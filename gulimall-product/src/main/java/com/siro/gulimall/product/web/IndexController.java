package com.siro.gulimall.product.web;

import com.siro.gulimall.product.entity.CategoryEntity;
import com.siro.gulimall.product.service.CategoryService;
import com.siro.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 前端首页Controller
 * @author Starsea
 * @date 2022-03-30 21:17
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 首页跳转 并查询一级分类
     * @param model
     * @return
     */
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {
        // 查询所有一级分类
        List<CategoryEntity> categoryEnties = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",categoryEnties);
        // 视图解析器进行拼串
        // classpath:/templates/ + 返回值 + .html
        return "index";
    }

    /**
     * 查询二级分类和三级分类
     * @return
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    /**
     * redisson分布式锁：可重入锁（Reentrant Lock）
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        //获取一把锁，只要锁的名字相同，就是同一把锁
        RLock lock = redisson.getLock("my-lock");
        //加锁
        lock.lock();//阻塞式等待，默认加的锁都是30s的时间
        //1）锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s。不用担心业务时间长，锁自动过期被删除。
        //2）加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s之后自动删除。

//        lock.lock(10, TimeUnit.SECONDS);//10s自动解锁，自动解锁时间一定要大于业务执行时间。
        //问题：lock.lock(10, TimeUnit.SECONDS);在锁时间到了以后，不会自动续期。
        //1. 如果我们传递了超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        //2. 如果我们未指定超时时间，就使用30 * 1000【lockWatchdogTimeout看门狗的默认时间】
        //    只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】
        //    internalLockLeaseTime【看门狗的的时间】 / 3 ，也就是10s。每隔10s都会自动再次续期，续成30s

        //最佳实战：lock.lock(30, TimeUnit.SECONDS);省掉了整个续期操作，手动操作。将解锁时间设大一些 为30s
        try {
            System.out.println("加锁成功，指定业务代码...." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {

        } finally {
            //解锁  假设解锁代码没有运行，redisson会不会出现死锁。 结果是不会。
            System.out.println("释放锁..." + Thread.currentThread().getId() );
            lock.unlock();
        }
        return "hello";
    }

    /**
     * redisson分布式锁：读写锁（ReadWriteLock）
     * 保证一定能读到最新数据，修改期间，写锁是一个排他锁（互斥锁 / 独享锁），读锁是一个共享锁
     * 写锁没有释放 读锁就必须等待
     * 读 + 读：相当于无锁，并发读只会在redis中记录好所有当前的读锁，他们都会同时加锁成功
     * 写 + 读：等待写锁释放
     * 写 + 写：阻塞方式
     * 读 + 写：有读锁，写锁也需要等待
     * 总结：只要有写锁的存在，都必须要等待。
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("wr-lock");
        String s = "";
        RLock rLock = lock.writeLock();//改数据加写锁
        try {
            rLock.lock();
            System.out.println("写锁加锁成功..." + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放成功..." + Thread.currentThread().getId());
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("wr-lock");
        String s = "";
        RLock rLock = lock.readLock();//读数据加读锁
        try {
            rLock.lock();
            System.out.println("读锁加锁成功..." + Thread.currentThread().getId());
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("读锁释放成功..." + Thread.currentThread().getId());
        }
        return s;
    }

    /**
     * redisson分布式锁：信号量（Semaphore）
     * 举例：车库停车 3个车位
     * @return
     */
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.acquire();//获取一个信号，获取一个值

        //信号量 可以用于分布式限流。
//        boolean b = park.tryAcquire();
//        if (b){
//            //执行业务
//        } else {
//            return "error";
//        }

        return "停车...";
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();//释放一个信号
        return "开走...";
    }

    /**
     * redisson分布式锁：闭锁（CountDownLatch）
     * 举例：放假锁门
     * 5个班全部走完，我们就可以锁门
     */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch latch = redisson.getCountDownLatch("door");
        latch.trySetCount(5);
        latch.await();
        return "放假了...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch latch = redisson.getCountDownLatch("door");
        latch.countDown();//计数减一
        return id + "班的人都走了...";
    }

}
