package com.wx.miniapp.entity;

import lombok.Data;

import java.util.Date;

/**
 * 会话信息实体类
 */
@Data
public class SessionInfo {
    private String token;
    private String openid;
    private String sessionKey;
    private Date createTime;
    private Date expireTime;

}
