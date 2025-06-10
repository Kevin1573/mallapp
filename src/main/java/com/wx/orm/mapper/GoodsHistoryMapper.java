package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.GoodsHistoryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsHistoryMapper extends BaseMapper<GoodsHistoryDO> {

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete} where trade_no = #{tradeNo}")
    int updateByTradeNo(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} and is_complete = 1 and is_pay_success = 1")
    int updateByTradeNo1(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} and is_complete = 1 and is_pay_success = 2")
    int updateByTradeNo2(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} and is_pay_success = 2 and status = 7")
    int updateByTradeNo8(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} and is_pay_success = 2")
    int updateByTradeNo4(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} ")
    int updateByTradeNo6(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set is_pay_success = #{isPaySuccess}, is_complete = #{isComplete}, status = #{status} where trade_no = #{tradeNo} and is_pay_success = 2 and status = 6")
    int updateByTradeNo627(GoodsHistoryDO goodsHistoryDO);

    @Update("update goods_history set pay_way = #{payWay} where trade_no = #{tradeNo}")
    int updatePayWayByTradeNo(GoodsHistoryDO goodsHistoryDO);
}
