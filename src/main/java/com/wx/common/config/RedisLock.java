package com.wx.common.config;

import com.wx.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisLock {

    @Autowired
    private RedisUtil redisUtil;

    public boolean lock(String key, String requestId, int expired) {
        return redisUtil.setIfNotExist(key, requestId, expired);
    }

    public boolean expire(String key, int expired) {
        return redisUtil.expire(key, expired);
    }

    public void unlock(String key, String requestId) {
        String script = "";
        redisUtil.eval(script, key, requestId);
    }
}
