package com.wx.orm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String operationType; // 操作类型
    private String className;     // 类名
    private String methodName;    // 方法名
    private String requestParams; // 请求参数
    private String responseData;  // 响应结果
    private String errorMsg;      // 错误信息
    private Boolean success;      // 是否成功
    private Long operatorId;      // 操作人ID
    private String operatorName;  // 操作人名称
    private Long costTime;        // 耗时(ms)
    private Date createTime;      // 创建时间
}