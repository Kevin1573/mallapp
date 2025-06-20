package com.wx.miniapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.miniapp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE openid = #{openid}")
    User selectByOpenid(String openid);
}
