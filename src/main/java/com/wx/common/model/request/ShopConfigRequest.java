package com.wx.common.model.request;

import com.wx.service.TokenCarrier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopConfigRequest implements TokenCarrier {
    private Long id;
    private String token;
    private String from;
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    @NotBlank(message = "联系人不能为空")
    private String contactPerson;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;
    private String businessStatus;

    private String fromMall;

    private String shopNameEng;

    private String homePage;

    private String bestSellers;

    private String aboutUsText;

    private String aboutUsPic;

    private String companyName;

    private String companyPhone;

    private String companyAddr;

    private String programUrl;

    private String wechatUrl;

    private Date createTime;

    private Date modifyTime;

    private double freight;

    private String email;
    private Integer page;
    private Integer pageSize;

    @Override
    public String getToken() {
        return token;
    }
}
