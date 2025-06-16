package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.GoodsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsDO> {

    @Update("UPDATE goods SET inventory = inventory - #{quantity} " +
            "WHERE id = #{goodsId} AND inventory >= #{quantity}")
    int reduceInventory(@Param("goodsId") Long goodsId,
                        @Param("quantity") Integer quantity);

    @Select("SELECT * FROM goods WHERE id = #{id} FOR UPDATE")
    GoodsDO selectByIdForUpdate(@Param("id") Long goodsId);

    @Update("UPDATE goods SET sales = sales - #{num}, inventory = inventory + #{num} WHERE id = #{id}")
    int updateInventoryWithReturn(@Param("id") Long goodsId, @Param("num") Integer num);

    @Select("select sum(pay_amount) as totalAmount from goods_history where create_time between #{startTime} and #{endTime}")
    Double calculateTotalAmount(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
