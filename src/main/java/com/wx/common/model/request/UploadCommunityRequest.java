package com.wx.common.model.request;

import lombok.Data;

import java.util.List;

@Data
public class UploadCommunityRequest {

    private List<String> communityNameList;
}
