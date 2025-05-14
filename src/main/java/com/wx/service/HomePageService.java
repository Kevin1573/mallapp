package com.wx.service;

import com.wx.common.model.request.SaveOrUpdateBannerRequest;
import com.wx.common.model.request.SaveOrUpdateModuleRequest;
import com.wx.common.model.response.SaveOrUpdateModuleResponse;

import java.util.List;

/**
 * 首页配置
 */
public interface HomePageService {

    /**
     * 新增或者更新首页banner列表
     *
     * @param request banner列表
     */
    void saveOrUpdateBanner(SaveOrUpdateBannerRequest request);

    /**
     * 查看首页banner列表
     *
     * @return banner列表
     */
    String queryBanner();

    /**
     * 新增或者编辑首页模块
     *
     * @param request 模块信息
     */
    void saveOrUpdateModule(SaveOrUpdateModuleRequest request);

    /**
     * 查看首页模块列表
     *
     * @return 模块列表信息
     */
    List<SaveOrUpdateModuleResponse> queryModule();

    /**
     * 根据id删除模块
     *
     * @param request id
     */
    void deleteModuleById(SaveOrUpdateModuleRequest request);


}
