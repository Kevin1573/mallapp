package com.wx.common.model;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

// 分页响应子类
@EqualsAndHashCode(callSuper = true)
@Data
public class PageResponse<T> extends ApiResponse<List<T>> {
    private long total;
    private long size;
    private long current;
    private long pages;
    private BigDecimal totalAmount;

    public PageResponse(IPage<T> page) {
        super(true, 200, "查询成功", page.getRecords());
        this.total = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    public PageResponse(IPage<T> page, BigDecimal totalAmount) {
        super(true, 200, "查询成功", page.getRecords());
        this.total = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
        this.totalAmount = totalAmount;
    }

    public PageResponse(int code, String message, IPage<T> page) {
        super(true, code, message, page.getRecords());
        this.total = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    // getters 省略...
}
