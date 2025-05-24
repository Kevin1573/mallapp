package com.wx.common.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PasswordUpdateRequest {
    @NotBlank(message = "token不能为空")
    private String token;
    
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
             message = "密码必须包含字母和数字，且至少8位")
    private String newPassword;

    // getters & setters
}