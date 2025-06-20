package com.wx.miniapp.service.impl;

import com.wx.miniapp.entity.SessionInfo;
import com.wx.miniapp.mapper.SessionMapper;
import com.wx.miniapp.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private SessionMapper sessionMapper;

    @Override
    @Transactional
    public SessionInfo createSession(String openid, String sessionKey) {
        // 清理旧的会话
        sessionMapper.deleteByOpenid(openid);

        // 创建新会话
        SessionInfo session = new SessionInfo();
        session.setToken(generateToken());
        session.setOpenid(openid);
        session.setSessionKey(sessionKey);
        session.setCreateTime(new Date());
        session.setExpireTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7天过期

        sessionMapper.insert(session);
        return session;
    }

    @Override
    public SessionInfo getSession(String token) {
        return sessionMapper.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteSession(String token) {
        sessionMapper.deleteByToken(token);
    }

    @Override
    @Transactional
    public int cleanExpiredSessions() {
        return sessionMapper.cleanExpiredSessions();
    }

    @Override
    public boolean validateSession(String token) {
        SessionInfo session = sessionMapper.findByToken(token);
        return session != null && session.getExpireTime().after(new Date());
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
