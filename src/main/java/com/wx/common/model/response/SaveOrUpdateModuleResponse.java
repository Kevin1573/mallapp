package com.wx.common.model.response;

import com.wx.orm.entity.GoodsDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SaveOrUpdateModuleResponse {

    private Long id;

    private String title;

    private String banner;

    private List<GoodsDO> goodsList;

    private Date createTime;

    private Date modifyTime;
}
