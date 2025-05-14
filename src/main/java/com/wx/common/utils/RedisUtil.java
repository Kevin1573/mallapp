package com.wx.common.utils;

import com.wx.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class RedisUtil {
    @Resource(type = StringRedisTemplate.class)
    private StringRedisTemplate stringRedisTemplate;

    /**
     * zset类型
     */
    // 数据进入有序队列
    public void zadd(String key, String value) {
        try {
            stringRedisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Redis zadd exception, key = {}, value = {}", key, value, e);
            throw new BizException("Redis zadd exception");
        }
    }

    public void lset(String key, List<String> value) {
        try {
            stringRedisTemplate.opsForList().leftPushAll(key, value);
        } catch (Exception e) {
            log.error("Redis Lset exception, key = {}, value = {}", key, value, e);
            throw new BizException("Redis Lset exception");
        }
    }

    public List<String> lrange(String key, long start, long end) {
        try {
            return stringRedisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis lrange exception, key = {}, start = {}, end = {}", key, start, end, e);
            throw new BizException("Redis lrange exception");
        }
    }

    // 批量添加数据
    public void zaddMulti(String key, Set<ZSetOperations.TypedTuple<String>> typedTuples) {
        try {
            stringRedisTemplate.opsForZSet().add(key, typedTuples);
        } catch (Exception e) {
            log.error("Redis zaddMulti exception, key = {}, value = {}", key, typedTuples, e);
            throw new BizException("Redis zaddMulti exception");
        }
    }


    /**
     * redis lock
     */
    public boolean setIfNotExist(String key, String value, Integer expire) {
        try {
            return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis setIfNotExist exception, key = {}, value = {}, expire = {}", key, value, expire, e);
            throw new BizException("Redis setIfNotExist exception");
        }
    }

    public Boolean expire(String key, Integer expire) {
        try {
            return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis expire exception, key = {}, expire = {}", key, expire, e);
            throw new BizException("Redis expire exception");
        }
    }

    public void eval(String script, String key, String lockId) {
        try {
            stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(key), lockId);
        } catch (Exception e) {
            log.error("Redis eval exception, key = {}, script = {}, lockId = {}", key, script, lockId, e);
            throw new BizException("Redis eval exception");
        }
    }


    /**
     * string 数据类型
     */
    public void setEx(String key, String value, Integer expire) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis setEx exception, key = {}, value = {}, expire = {}", key, value, expire, e);
            throw new BizException("Redis setEx exception");
        }
    }

    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get exception, key = {}", key, e);
            throw new BizException("Redis get exception");
        }
    }

    public void set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set exception, key = {}, value = {}", key, value, e);
            throw new BizException("Redis set exception");
        }
    }

    public void del(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete exception, key = {}", key);
            throw new BizException("Redis delete exception");
        }
    }

}

