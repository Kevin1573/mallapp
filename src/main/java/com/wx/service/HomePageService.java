package com.wx.service;

import com.wx.common.model.request.HomePageRequest;
import com.wx.common.model.request.QueryGoodsByIdRequest;
import com.wx.common.model.response.CompanyConfigResponse;
import com.wx.common.model.response.HomePageResponse;
import com.wx.common.model.response.QueryRecondDetailByUnitResponse;

import java.util.List;

/**
 * 首页配置
 */
public interface HomePageService {

    /**
     * 查看首页配置
     *
     * @param request
     * @return
     */
    HomePageResponse queryPage(HomePageRequest request);

    /**
     * 查询公司信息
     *
     * @param request
     * @return
     */
    CompanyConfigResponse queryCompanyConfig(HomePageRequest request);

    /**
     * 根据unit查询首页组合商品详情
     *
     * @param request
     * @return
     */
    List<QueryRecondDetailByUnitResponse> queryRecondDetail(QueryGoodsByIdRequest request);
}
