package com.wx.common.model.request;

import lombok.Data;

@Data
public class SaveOrUpdateModuleRequest {

    private Long id;

    private String title;

    private String banner;

    private String goodsList;
}
