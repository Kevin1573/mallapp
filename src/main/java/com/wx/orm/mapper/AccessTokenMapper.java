package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.AccessTokenDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessTokenMapper extends BaseMapper<AccessTokenDO> {
}
