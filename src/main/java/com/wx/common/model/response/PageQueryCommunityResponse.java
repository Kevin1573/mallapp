package com.wx.common.model.response;

import lombok.Data;

import java.util.List;

@Data
public class PageQueryCommunityResponse {

    private Long limit;

    private Long page;

    private Long total;

    private List<CommunityModel> data;
}
