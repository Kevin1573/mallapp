package com.wx.common.model.request;

import lombok.Data;

@Data
public class EditPasswordRequest {

    private String userName;

    private String oldPassword;

    private String newPassword;
}
