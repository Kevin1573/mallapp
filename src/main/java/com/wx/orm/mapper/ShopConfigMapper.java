package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.ShopConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ShopConfigMapper extends BaseMapper<ShopConfigDO> {

    @Select("select * from shop_config where from_mall = #{from}")
    ShopConfigDO selectByFrom(String from);
}
