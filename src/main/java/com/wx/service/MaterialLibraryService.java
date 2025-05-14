package com.wx.service;

import com.wx.common.model.request.SaveMaterialRequest;
import com.wx.common.model.response.QueryMaterialResponse;

import java.util.List;

public interface MaterialLibraryService {

    /**
     * 新增或编辑素材库配置
     *
     * @param request 配置内容
     */
    void saveOrUpdateMaterial(SaveMaterialRequest request);

    /**
     * 根据type查询素材库配置
     *
     * @param request 类型
     * @return 配置内容
     */
    List<QueryMaterialResponse> queryMaterialByType(SaveMaterialRequest request);

    /**
     * 删除配置
     *
     * @param request id
     */
    void deleteMaterialById(SaveMaterialRequest request);
}
