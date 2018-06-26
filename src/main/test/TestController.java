import com.lumi.Application;
import com.lumi.domain.RedisModel;
import com.lumi.service.IRedisService;
import com.lumi.service.impl.RedisServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/3/1 14:55.
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class TestController {
    @Autowired
    private IRedisService<RedisModel> service;

    @Test
    public void aa(){
        System.out.println("d");
    }
    @Test
    public void test() {
        System.out.println("start.....");
        RedisModel m = new RedisModel();
        m.setName("张三");
        m.setTel("1111");
        m.setAddress("深圳1");
        m.setRedisKey("zhangsanKey01");
        service.put(m.getRedisKey(), m, -1);

        RedisModel m2 = new RedisModel();
        m2.setName("张三2");
        m2.setTel("2222");
        m2.setAddress("深圳");
        m2.setRedisKey("zhangsanKey02");
        service.put(m2.getRedisKey(), m2, -1);

        RedisModel m3 = new RedisModel();
        m3.setName("张三3");
        m3.setTel("2222");
        m3.setAddress("深圳2");
        m3.setRedisKey("zhangsanKey03");
        service.put(m3.getRedisKey(), m3, -1);

        System.out.println("add success end...");
    }

    //查询所有对象
    @Test
    public void getAll() {
        List<RedisModel> o =  service.getAll();
        System.out.println(o);

    }

    //查询所有key
    @Test
    public void getKeys() {
         Set<String> keys = service.getKeys();
        System.out.println(keys);
    }

    //根据key查询
    @Test
    public void get() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey02");
        RedisModel redisModel = service.get(m.getRedisKey());
        System.out.println(redisModel);
    }

    //删除
    @Test
    public void remove() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey01");
        service.remove(m.getRedisKey());
    }

    //判断key是否存在
    @Test
    public void isKeyExists() {
        RedisModel m = new RedisModel();
        m.setRedisKey("zhangsanKey01");
        boolean flag = service.isKeyExists(m.getRedisKey());
        System.out.println("zhangsanKey01 是否存在: "+flag);
    }

    //查询当前缓存的数量
//    @Test
//    public Object count() {
//        return service.count();
//    }

    //清空所有key
    @Test
    public void empty() {
        service.empty();
    }

}