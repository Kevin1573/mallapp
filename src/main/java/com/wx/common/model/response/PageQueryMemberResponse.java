package com.wx.common.model.response;

import com.wx.common.model.request.UserProfileModel;
import lombok.Data;

import java.util.List;

@Data
public class PageQueryMemberResponse {

    private Long limit;

    private Long page;

    private Long total;

    private List<UserProfileModel> data;
}
