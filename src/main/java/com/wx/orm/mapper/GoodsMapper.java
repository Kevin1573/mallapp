package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.GoodsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsDO> {

    @Update("UPDATE goods SET inventory = inventory - #{quantity} " +
            "WHERE id = #{goodsId} AND inventory >= #{quantity}")
    int reduceInventory(@Param("goodsId") Long goodsId,
                        @Param("quantity") Integer quantity);
}
