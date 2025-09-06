package com.siro.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.siro.gulimall.product.dao.AttrGroupDao;
import com.siro.gulimall.product.dao.SkuSaleAttrValueDao;
import com.siro.gulimall.product.entity.BrandEntity;
import com.siro.gulimall.product.service.BrandService;
import com.siro.gulimall.product.service.CategoryService;
import com.siro.gulimall.product.vo.SpuItemAttrGroupVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Test
    public void test() {
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(1L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    /**
     * springboot整合cache 简化缓存开发
     * 1. 引入spring-boot-starter-cache、spring-boot-starter-redis依赖
     * 2. 写配置
     *   配置使用redis作为缓存
     *      spring.cache.type=redis
     * 3. 测试使用缓存
     *  @cacheable：触发将数据保存到缓存的操作
     *  @CacheEvict：触发将数据从缓存中删除的操作
     *  @CachePut：不影响方法执行更新缓存
     *  @Caching：组合以上多个操作
     *  @CacheConfig：在类级别共享缓存的相同配置
     *  1）开启缓存功能：@EnableCaching
     *  2）只需要注解就能完成缓存操作
     * 4. 原理
     *      CacheAutoConfiguration -> RedisCacheConfiguration ->
     *      自动配置了缓存管理器RedisCacheManager -> 初始化所有的缓存 -> 每个缓存解决使用什么配置
     *      -> 如果redisCacheConfiguration有就用已有的，没有就用默认配置
     *      -> 想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可
     *      -> 就会应用到当前 RedisCacheManager管理的所有缓存分区中
     */

    /**
     * springboot整合redisson
     * 1. 引入redisson依赖
     * 2. 编写配置类
     */
    @Test
    public void redisson() {
        System.out.println(redissonClient);
    }

    /**
     * springboot整合redis
     * 1. 引入data-redis-starter
     * 2. 简单配置redis的host信息
     * 3. 使用springboot自动配置好的StringRedisTemplate来操作redis
     */
    @Test
    public void testRedisTemplate() {
        ValueOperations<String, String> forValue = stringRedisTemplate.opsForValue();
        // 保存数据
        forValue.set("hello","world_"+ UUID.randomUUID().toString());
        //查询数据
        String hello = forValue.get("hello");
        System.out.println("之前保存的数据：" + hello);
    }

    @Test
    public void testFindPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}", Arrays.asList(catelogPath));
    }

    @Test
    public void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        //brandEntity.setName("小米");
        //brandService.save(brandEntity);
        //System.out.println("保存成功");

        //brandEntity.setBrandId(1L);
        //brandEntity.setDescript("小米");
        //brandService.updateById(brandEntity);

        List<BrandEntity> lists = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        lists.forEach(System.out::println);
    }
}
