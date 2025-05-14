//package com.wx.task;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.wx.common.utils.TimeUtil;
//import com.wx.orm.entity.GoodsHistoryDO;
//import com.wx.orm.mapper.GoodsHistoryMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Calendar;
//import java.util.Date;
//
///**
// * 用户订单三天之内自动完成
// */
//@Component
//@Slf4j
//public class CompleteOrderTask {
//
//    @Autowired
//    private GoodsHistoryMapper goodsHistoryMapper;
//
//    @Scheduled(cron = "0 0 * * * ?")
//    private void process() {
//        try {
//            log.info("The task CompleteOrderTask is start, time = {}", System.currentTimeMillis());
//            handle();
//            log.info("The task CompleteOrderTask is end, time = {}", System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("CompleteOrderTask exception", e);
//        }
//    }
//
//    private void handle() {
//        LambdaQueryWrapper<GoodsHistoryDO> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(GoodsHistoryDO::getIsComplete, 1);
//        queryWrapper.le(GoodsHistoryDO::getCreateTime, TimeUtil.getDateAfterTime(new Date(), -3, Calendar.DATE));
//        queryWrapper.eq(GoodsHistoryDO::getIsPack, 2);
//        GoodsHistoryDO goodsHistoryDO = new GoodsHistoryDO();
//        goodsHistoryDO.setIsComplete(2);
//        goodsHistoryMapper.update(goodsHistoryDO, queryWrapper);
//    }
//
//
//}
