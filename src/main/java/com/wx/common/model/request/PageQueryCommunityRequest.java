package com.wx.common.model.request;

import lombok.Data;

import java.util.List;

@Data
public class PageQueryCommunityRequest {

    private Long limit;

    private Long page;

}
