package com.wx.service;

import com.wx.orm.entity.OperationLog;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {
    @Async
    public void save(OperationLog log) {
        // 保存到数据库
    }
}