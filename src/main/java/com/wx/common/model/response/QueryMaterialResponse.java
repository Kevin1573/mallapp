package com.wx.common.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class QueryMaterialResponse {

    private Long id;

    /**
     * 详情
     */
    private String content;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String cover;

    private String type;

    private String fileType;

}
