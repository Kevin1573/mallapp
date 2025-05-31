package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.ShopConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShopConfigMapper extends BaseMapper<ShopConfigDO> {

    @Select("select * from shop_config where from_mall = #{from}")
    ShopConfigDO selectByFrom(String from);

    @Select("select * from shop_config where id = #{id} or from_mall = #{from}")
    ShopConfigDO selectByIdOrFromMall(Long id, String from);

    @Update("UPDATE shop_config " +
            "SET best_sellers = #{bestSellers} " +
            "WHERE from_mall = #{fromMall}")
    int updateByFromMall(ShopConfigDO shopConfigDO);

    @Update("UPDATE shop_config " +
            "SET recommended_goods = #{recommendedGoods} " +
            "WHERE from_mall = #{fromMall}")
    int updateRecommendedGoodsByFromMall(ShopConfigDO shopConfigDO);

    @Select("select * from shop_config where from_mall = #{fromMall}")
    List<ShopConfigDO> selectListByFromMall(String fromMall);
}
