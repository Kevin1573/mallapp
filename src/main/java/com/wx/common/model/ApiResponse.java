package com.wx.common.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 统一响应基类（非分页）
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    // 成功静态方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "操作成功", data);
    }

    // 失败静态方法
    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    // 分页专用方法
    public static <T> PageResponse<T> page(IPage<T> page) {
        return new PageResponse<>(page);
    }

    public static <T> PageResponse<T> page(IPage<T> page, BigDecimal totalAmount) {
        return new PageResponse<>(page, totalAmount);
    }


    public static <T> PageResponse<T> failPage(int code, String message) {
        return new PageResponse<>(code, message, new Page<>());
    }

}

