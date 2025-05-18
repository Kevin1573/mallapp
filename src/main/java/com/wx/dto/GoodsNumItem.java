package com.wx.dto;

import com.wx.orm.entity.GoodsDO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoodsNumItem {

    private GoodsDO goodsDO;
    private Long num;

}
