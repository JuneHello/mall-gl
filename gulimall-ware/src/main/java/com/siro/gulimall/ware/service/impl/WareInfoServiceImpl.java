package com.siro.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;
import com.siro.common.utils.R;
import com.siro.gulimall.ware.dao.WareInfoDao;
import com.siro.gulimall.ware.entity.WareInfoEntity;
import com.siro.gulimall.ware.feign.MemberFeignService;
import com.siro.gulimall.ware.service.WareInfoService;
import com.siro.gulimall.ware.vo.FareVo;
import com.siro.gulimall.ware.vo.MemberAddressVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id",key).or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 根据用户收货地址计算运费
     * @param addrId
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();

        // 远程查询收货地址详细信息
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            //TODO 结合第三方快递、物流接口
            String phone = data.getPhone();
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal decimal = new BigDecimal(substring);
            fareVo.setAddress(data);
            fareVo.setFare(decimal);

            return fareVo;
        }
        return null;
    }

}