package com.siro.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siro.common.utils.PageUtils;
import com.siro.common.utils.Query;
import com.siro.gulimall.product.dao.CategoryDao;
import com.siro.gulimall.product.entity.CategoryEntity;
import com.siro.gulimall.product.service.CategoryBrandRelationService;
import com.siro.gulimall.product.service.CategoryService;
import com.siro.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询所有分类以及子分类，以树形结构组装起来
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        // 查询所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        // 组装父子的树形结构
        // 找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
            categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildren(menu,entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    /**
     * 删除菜单
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 根据所属分类id查询完整路径
     * 格式：[父/子/孙]
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        // 手机：[2,25,225]
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        // 逆序显示
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 更新本表及关联表，保证冗余字段的数据一致性
     * @CacheEvict：失效模式
     * 1. 同时进行多种缓存操作：@Caching
     * 2. 指定删除某个分区下的所有数据：@CacheEvict(value = "category",allEntries = true)
     * 3. 存储同一个类型的数据，都可以指定成同一个分区。分区名默认就是缓存的前缀。
     * @param category
     */
//    @Caching(evict = {
//        @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
//        @CacheEvict(value = "category",key = "'getCatalogJson'")
//    })
    @CacheEvict(value = "category",allEntries = true)//失效模式
//    @CachePut()//双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            // 同步更新其他关联表中的数据
            categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
            // TODO 更新其他关联表
        }
    }

    /**
     * 查询所有一级分类
     * 1. 每一个需要缓存的数据我们都来指定要放到那个名字的缓存。【缓存的分区（按照业务划分）】
     * 2. @Cacheable({"category"})：表示当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有，会调用方法，并将方法的结果放入缓存。
     * 3. 默认行为
     *      1）如果缓存中有，方法不用调用
     *      2）key默认自动生成：缓存的名字::SimpleKey []（自动生成key的值）
     *      3）缓存的value的值：默认使用jdk序列换机制。将序列化后的数据存到redis。
     *      4）默认ttl时间是-1。
     * 4. 自定义
     *      1）指定生成缓存使用的key： key属性指定，接收一个SpEL表达式
     *          SpEL语法详细：https://docs.spring.io/spring-framework/docs/5.3.19-SNAPSHOT/reference/html/integration.html#cache
     *      2）指定缓存数据的存活时间： 配置文件中修改ttl
     *      3）将数据保存为json格式：
     *          查看源码，自定义RedisCacheConfiguration配置类进行修改
     * 5. Spring Cache的不足：
     *      1）读模式
     *          缓存穿透：查询一个null数据。解决方案：缓存空数据，可通过spring.cache.redis.cache-null-values=true
     *          缓存击穿：大量并发进来同时查询一个正好过期的数据。解决方案：加锁 ? 默认是无加锁的;
     *              使用sync = true来解决击穿问题
     *          缓存雪崩：大量的key同时过期。解决：加随机时间。可通过spring.cache.redis.time-to-live=3600000
     *      2）写模式
     *          读写加锁。【适用于读多写少】
     *          引入Canal，感知到MySQL的更新去更新Redis
     *          读多写多，直接去数据库查询就行
     *      3）总结
     *          常规数据（读多写少，即时性，一致性要求不高的数据，完全可以使用Spring-Cache）
     *          写模式（只要缓存的数据有过期时间就足够了）
     *          特殊数据：特殊设计
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys....");
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 整合Cache缓存，查询二级分类和三级分类 P138
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        System.out.println("查询了数据库.....");
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
        // 封装数据
        Map<String, List<Catelog2Vo>> listMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> level2Catelog = getParent_cid(selectList,v.getCatId());
            // 封装上面的结果集
            List<Catelog2Vo> catelog2Vos = null;
            if (level2Catelog != null) {
                catelog2Vos = level2Catelog.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    // 找到当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            // 封装成指定格式
                            Catelog2Vo.catelog3Vo catelog3Vo = new Catelog2Vo.catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return listMap;
    }

    /**
     * 整合Redis缓存/整合Redisson缓存，查询二级分类和三级分类 P138
     * TODO 产生堆外内存溢出：outOfDirectMemoryError
     * 1. SpringBoot2.0以后默认使用lettuce作为操作redis的客户端。它使用netty进行网络通信。
     * 2. lettuce的bug导致netty堆外内存溢出 VM Option = -Xmx300m；netty如果没有指定堆外内存，默认使用-Xmx300m
     * 解决方案：不能使用 -Dio.netty.maxDirectMemory 只去调大堆外内存。
     * 1. 升级lettuce客户端
     * 2. 切换使用jedis
     *
     * lettuce、jedis操作redis的底层客户端。Spring再次封装redisTemplate。
     * @return
     */
//    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        /**
         * 1.空结果缓存：解决缓存穿透
         * 2. 设置过期时间（加随机值）：结果缓存雪崩
         * 3. 加锁：解决缓存击穿
         */

        // 加入缓存逻辑，缓存中存的数据是json字符串
        // json是跨语言，跨平台兼容的。
        String catelogJson = redisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            // 缓存中没有，查询数据库
            System.out.println("缓存不命中...将要查询数据库...");
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            return catalogJsonFromDb;
        }
        System.out.println("缓存命中....直接返回....");
        // 给缓存中放json字符串，拿到的json字符串，还要逆转为能用的对象类型。【序列化与反序列化】
        // 转为我们指定的对象返回
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
        return result;
    }

    /**
     * 改造：使用redisson分布式锁
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        /**
         * 问题：缓存数据一致性：缓存里面的数据如何和数据库中数据保持一致
         * 1）双写模式：写数据库后，更新缓存。
         * 出现问题：由于卡顿等原因，导致写缓存2在最前，写缓存1在后面，就出现数据不一致。【脏数据问题】
         * 解决方案：① 整个操作加锁。②看业务允不允许数据暂时不一致问题，但是数据要设置过期时间。
         * 2）失效模式：写数据库后，删除缓存。
         * 也会出现脏数据问题。
         *
         * 无论是双写模式还是失效模式，都会存在缓存不一致的问题。即多个实例同时更新会出事，怎么办？
         * 1.如果是用户维度数据（订单数据，用户数据），这种并发几率非常小，不用考虑这个问题，缓存数据加上过期时间，每隔一段时间触发读的主动更新即可。
         * 2. 如果是菜单，商品介绍等基础数据，也可以去使用canal订阅binlog方式。
         * 3. 缓存数据 + 过期时间 也足够解决大部分业务对缓存的要求。
         * 4. 通过加锁保证并发读写，写写的时候按顺序排好队，读读无所谓。所以适合使用读写锁。（业务不关系脏数据，允许临时脏数据可忽略）
         *
         * 我们系统最终解决方案：
         * 1. 缓存的所有数据都有过期时间，数据过期下一次查询出发主动更新。
         * 2. 读写数据的时候，加上分布式读写锁。
         */

        // 注意锁的名字。锁的粒度，越细越快。
        // 锁的粒度：具体缓存的是某个数据，11-号商品：product-11-lock
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }

    /**
     * 改造：使用redis分布式锁
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        /**
         * 阶段一：
         * 问题：setnx占好了位，业务代码异常或者程序在页面过程中宕机。没有执行删除锁逻辑，这就造成了死锁。
         * 解决：设置锁的过期时间，即使没有删除，也会自动删除。
         * 阶段二：
         * 问题：setnx设置好，正好去设置过期时间，宕机。又死锁了。
         * 解决：设置过期时间和占位必须是原子的。redis支持使用setnx ex命令。
         * 阶段三：
         * 问题：删除锁直接删除？？
         * 如果由于业务时间很长，锁自己过期了，我们直接删除，有可能把别人正在持有的锁删除了。
         * 解决：占锁的时候，值指定为uuid，每个人匹配是自己的锁才删除。
         * 阶段四：
         * 问题：如果正好判断是当前值，正要删除锁的时候，锁已经过期，别人已经设置到了新的值。那么我们删除的是别人的锁。
         * 解决：删除锁必须保证原子性。使用redis+Lua脚本完成。
         * 阶段五：
         * 保证加锁【占位+过期时间】和删除锁【判断+删除】的原子性。更难的事情，锁的自动续期。
         */
        // 1.占分布式锁。去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,300,TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功...");
            //加锁成功...执行业务
            // 2.设置过期时间，必须和加锁是同步的，保证原子的。
            //redisTemplate.expire("lock",30,TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDb;
            try {
                //查询数据库
                dataFromDb = getDataFromDb();
            } finally {
                //获取值对比 + 对比成功删除 = 原子操作
//                String lockValue = redisTemplate.opsForValue().get("lock");
//                if (uuid.equals(lockValue)) {
//                    //删除我自己的锁
//                    redisTemplate.delete("lock");
//                }

                //官网文档说明：http://redis.cn/commands/set.html
                //使用 lua脚本解锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            return dataFromDb;
        } else {
            System.out.println("获取分布式锁失败...重试");
            //加锁失败...重试... 和 synchronized ()类似
            //睡眠200ms
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock();// 自旋的方式
        }
    }

    /**
     * 改造：使用本地锁
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        /**
         * 加锁：只要是同一把锁，就能锁住需要这个锁的所有线程。
         * 1. synchronized (this) ：springboot所有的组件在容器中都是单例的。
         * 本地锁：synchronized，JUC（Lock）只能锁住当前进程；
         * 在分布式情况下，想要锁住所有，必须使用分布式锁。
         */
        synchronized (this) {
            return getDataFromDb();
        }
    }

    /**
     * 从数据库查询直接返回数据，比较耗时。
     * @return
     */
    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        // 得到锁以后，我们应该再去查询缓存中确定一次，如果没有才需要继续查询数据库
        String catelogJson = redisTemplate.opsForValue().get("catelogJson");
        if (!StringUtils.isEmpty(catelogJson)) {
            // 缓存不为null直接返回
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }
        System.out.println("查询了数据库....");
        // 优化：将数据库的多次查询变为一次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
        // 封装数据
        Map<String, List<Catelog2Vo>> listMap = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> level2Catelog = getParent_cid(selectList,v.getCatId());
            // 封装上面的结果集
            List<Catelog2Vo> catelog2Vos = null;
            if (level2Catelog != null) {
                catelog2Vos = level2Catelog.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                    // 找到当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            // 封装成指定格式
                            Catelog2Vo.catelog3Vo catelog3Vo = new Catelog2Vo.catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        // 将查询到的数据在放入缓存，将对象转为json放入缓存
        // 注意：查询数据库后，将结果放入缓存，应该在同一把锁中，保证原子操作，否则会出现锁时序问题
        String s = JSON.toJSONString(listMap);
        redisTemplate.opsForValue().set("catelogJson",s,1, TimeUnit.DAYS);
        return listMap;
    }

    // 抽取方法
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    // [225,25,2]
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归获取所有菜单的子菜单
     * @param root 当前菜单对象
     * @param all 所有分类数据
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity,all));
            return categoryEntity;
        }).sorted((menu1,menu2) -> {
            return (menu1.getSort() == null ? 0:menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}