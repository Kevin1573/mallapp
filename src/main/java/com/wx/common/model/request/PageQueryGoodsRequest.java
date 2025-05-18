package com.wx.common.model.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Pattern;

@Data
public class PageQueryGoodsRequest {

    private Long page;

    private Long limit;

    private String typeId;

    // 排序字段
    private String sort;

    // 分类
    private String category;

    // 品牌
    private String brand;

    // 预算区间 0,100
    @Pattern(regexp = "^\\d+,\\d+$", message = "预算区间格式错误")
    private String budget;

    // 销量，1升序2降序
    @Range(min = 1, max = 2, message = "排序方式错误")
    private Integer inventory;
}
