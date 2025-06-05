package com.wx.common.model.request;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.orm.entity.GoodsDO;
import com.wx.service.TokenCarrier;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class GoodsRequest implements TokenCarrier {
    private Long id;

    private String keyword;
    private Integer status;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "价格必须大于0")
    @Digits(integer = 10, fraction = 1, message = "价格格式不正确")
    private Double price;

    @Min(value = 0, message = "库存不能为负数")
    private long inventory;

    private String category;
    private Double minPrice;
    private Double maxPrice;

    private String brand;
    private String token;
    private Integer page;
    private Integer pageSize;


    @Override
    public String getToken() {
        return token;
    }

    public Page<GoodsDO> toPage() {
        return new Page<>(page, pageSize);
    }
}