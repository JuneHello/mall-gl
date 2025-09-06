package com.siro.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.constant.OrderStatusEnum;
import com.siro.common.constant.WareStatusEnum;
import com.siro.common.to.mq.OrderTo;
import com.siro.common.to.mq.StockDetailTo;
import com.siro.common.to.mq.StockLockedTo;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;
import com.siro.common.utils.R;
import com.siro.gulimall.ware.dao.WareSkuDao;
import com.siro.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.siro.gulimall.ware.entity.WareOrderTaskEntity;
import com.siro.gulimall.ware.entity.WareSkuEntity;
import com.siro.common.exception.NoStockException;
import com.siro.gulimall.ware.feign.OrderFeignService;
import com.siro.gulimall.ware.feign.ProductFeignService;
import com.siro.gulimall.ware.service.WareOrderTaskDetailService;
import com.siro.gulimall.ware.service.WareOrderTaskService;
import com.siro.gulimall.ware.service.WareSkuService;
import com.siro.gulimall.ware.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据采购单的数据进行入库操作
     * @param skuId  sku_id
     * @param wareId 仓库id
     * @param skuNum 库存数
     */
    @Override
    public void saveStock(Long skuId, Long wareId, Integer skuNum) {
        // 如果没有这个库存记录就新增
        List<WareSkuEntity> entities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // TODO 远程查询sku名称；如果失败，整个事务无需回滚
            // 1、自己catch处理
            // 2、还可以用什么办法让异常出现后不回滚？
            try {
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String, Object> spuInfo = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) spuInfo.get("skuName"));
                }
            } catch (Exception e) {

            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            // 否则就更新
            wareSkuDao.updateStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询sku是否有库存
     * @param skuIds
     * @return
     */
    @Override
    public List<HasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<HasStockVo> collect = skuIds.stream().map(skuId -> {
            HasStockVo hasStockVo = new HasStockVo();
            //查询当前sku的总库存量
            Long count = baseMapper.getSkuStock(skuId);
            hasStockVo.setSkuId(skuId);
            hasStockVo.setHasStock(count == null ? false : count > 0);
            return hasStockVo;
        }).collect(Collectors.toList());
        return collect;
    }

    /**
     * 为某个订单锁定库存
     *
     * @param wareSkuLockVo
     * @return
     */
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        // 保存库存工作单的详情
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        // 按照下单的收获地址，找到一个就近仓库，锁定库存
        // 1、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> orderItemVos = wareSkuLockVo.getLocks();
        List<SkuWareHasStock> collect = orderItemVos.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪里有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        //锁定库存
        for (SkuWareHasStock hasStock : collect) {
            Boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            // 减库存
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                if (count == 1) {
                    //成功
                    skuStocked = true;
                    // 保存库存工作单详情
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
                    wareOrderTaskDetailEntity.setSkuId(skuId);
                    wareOrderTaskDetailEntity.setSkuNum(hasStock.getNum());
                    wareOrderTaskDetailEntity.setTaskId(wareOrderTaskEntity.getId());
                    wareOrderTaskDetailEntity.setWareId(wareId);
                    wareOrderTaskDetailEntity.setLockStatus(WareStatusEnum.LOCK_WARE.getCode());
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);

                    //将库存锁定成功的消息发给消息队列
                    StockLockedTo stockLockedTo = new StockLockedTo();
                    stockLockedTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
                    stockLockedTo.setDetail(stockDetailTo);
                    //告诉MQ库存锁定成功
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",stockLockedTo);
                    break;
                } else {
                    //失败，当前仓库锁定失败,重试下一个仓库
                }
            }
            if (skuStocked == false) {
                //当前商品所有仓库都没锁住
                throw new NoStockException(skuId);
            }
        }
        // 全部锁定成功
        return true;
    }

    /**
     * 库存解锁的场景：
     * 1、下订单成功，订单过期没有支付被系统自动取消，被用户手动取消、都要解锁库存。
     * 2、下订单成功，库存锁定成功，但是接下来的业务调用失败，导致订单回滚，之前锁定的库存就要解锁。
     *
     * 只要解锁库存的消息失败，一定要告诉服务解锁失败。【启用手动ack】
     */
    @Override
    public void unlockStock(StockLockedTo stockLockedTo) {
        StockDetailTo detail = stockLockedTo.getDetail();
        Long detailId = detail.getId();
        /**
         * 去库存锁定工作单详情查询数据库关于这个订单的锁定库存信息
         *     如果没有这个信息，说明库存锁定失败了，这个商品的库存锁定回滚了，就不需要解锁。
         *     如果有这个信息，说明这个商品的库存锁定成功了，由于其他业务的失败导致订单回滚了
         */
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = wareOrderTaskDetailService.getById(detailId);
        if(wareOrderTaskDetailEntity!=null){
            /**
             * 解锁：判断订单情况
             *    没有这个订单，必须解锁
             *    有这个订单，判断订单状态：
             *          订单状态为已取消，解锁库存，
             *          没取消订单，不用解锁
             */
            // 库存工作单id
            Long id = stockLockedTo.getId();
            WareOrderTaskEntity wareOrderTaskEntity = wareOrderTaskService.getById(id);
            //订单号
            String orderSn = wareOrderTaskEntity.getOrderSn();
            //根据订单号查询订单的状态
            R r = orderFeignService.getOrderStatus(orderSn);
            if(r.getCode() == 0){
                OrderVo data = r.getData(new TypeReference<OrderVo>() {});
                if(data == null || data.getStatus() == OrderStatusEnum.CANCLED.getCode()){
                    //订单不存在 或 订单已经被取消，解锁库存
                    if(wareOrderTaskDetailEntity.getLockStatus() == WareStatusEnum.LOCK_WARE.getCode()){
                        //当前库存工作单详情，状态1已锁定 但是未解锁才可以解锁
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            }else{
                //消息拒绝之后重新放到队列，让别人继续消费解锁
                throw new RuntimeException("远程服务失败");
            }
        }else{
            //不需要解锁
        }
    }

    /**
     * P298 防止订单服务卡顿，导致订单状态消息一直改不了，库存优先到期，查订单状态新建状态，什么都不处理
     * 导致卡顿的订单，永远都不能解锁库存
     * @param orderTo
     */
    @Transactional
    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        //查一下最新库存的状态，防止重复解锁库存
        WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = task.getId();
        //按照工作单找到所有 没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", id)
                        .eq("lock_status", WareStatusEnum.LOCK_WARE.getCode()));
        //Long skuId, Long wareId, Integer num, Long taskDetailId
        for (WareOrderTaskDetailEntity entity : entities) {
            unLockStock(entity.getSkuId(), entity.getWareId() ,entity.getSkuNum(), entity.getId());
        }
    }

    /**
     * 解锁库存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    private void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        //解锁库存
        wareSkuDao.unLockStock(skuId, wareId, num);

        //更新工作单的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        taskDetailEntity.setLockStatus(WareStatusEnum.UNLOCK_WARE.getCode());//变为已解锁
        wareOrderTaskDetailService.updateById(taskDetailEntity);
    }

}