package com.wx.miniapp.service;

import com.wx.miniapp.entity.SessionInfo;

public interface SessionService {
    /**
     * 创建会话
     *
     * @param openid     用户openid
     * @param sessionKey 微信session_key
     * @param token
     * @return 创建的会话信息
     */
    SessionInfo createSession(String openid, String sessionKey, String token);

    /**
     * 获取会话信息
     * @param token 用户token
     * @return 会话信息
     */
    SessionInfo getSession(String token);

    /**
     * 删除会话
     * @param token 用户token
     */
    void deleteSession(String token);

    /**
     * 清理过期会话
     * @return 删除的会话数量
     */
    int cleanExpiredSessions();

    boolean validateSession(String token);
}
