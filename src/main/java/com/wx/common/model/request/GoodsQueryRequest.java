package com.wx.common.model.request;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wx.orm.entity.GoodsDO;
import lombok.Data;

@Data
public class GoodsQueryRequest {
    private String startTime;
    private String endTime;

    private Integer page = 1;
    private Integer pageSize = 10;
    public Page<GoodsDO> toPage() {
        return new Page<>(page, pageSize);
    }
}
