package com.wx.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.miniapp.entity.SessionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SessionMapper extends BaseMapper<SessionInfo> {
    /**
     * 根据token查找会话
     * @param token 会话token
     * @return 会话信息
     */
    SessionInfo findByToken(@Param("token") String token);

    /**
     * 根据token删除会话
     * @param token 会话token
     * @return 删除记录数
     */
    int deleteByToken(@Param("token") String token);

    /**
     * 根据openid删除会话
     * @param openid 用户openid
     * @return 删除记录数
     */
    int deleteByOpenid(@Param("openid") String openid);

    /**
     * 清理过期会话
     * @return 删除记录数
     */
    int cleanExpiredSessions();
}
