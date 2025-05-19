package com.wx.common.model.response;

import com.wx.common.model.request.ShopModel;
import lombok.Data;

import java.util.List;

@Data
public class HomePageResponse {

    private String shopName;

    private String shopNameEng;

    private String homePage;

    /**
     * 热销商品列表
     */
    private List<ShopModel> bestSellersModels;

    /**
     * 合作品牌列表
     */
    private List<ShopModel> brandModels;

    private String aboutUsText;

    private String aboutUsPic;

    /**
     * 组合推荐列表
     */
    private List<ShopModel> reconModels;
}
