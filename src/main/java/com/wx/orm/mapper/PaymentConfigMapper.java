package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.PaymentConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentConfigMapper extends BaseMapper<PaymentConfig> {

    @Select("SELECT * FROM payment_config WHERE mch_id = #{mchId} AND pay_channel = #{channel} AND status = 1")
    PaymentConfig findActiveConfig(@Param("mchId") String mchId, @Param("channel") String channel);

    @Select("SELECT * FROM payment_config WHERE status = #{status}")
    List<PaymentConfig> selectByStatus(@Param("status") Integer status);
    
    // 使用 QueryWrapper 的链式写法（推荐）
    default List<PaymentConfig> selectActiveConfigs() {
        return selectList(new LambdaQueryWrapper<PaymentConfig>()
            .eq(PaymentConfig::getStatus, PaymentConfig.Status.ENABLED.getCode()));
    }
}
