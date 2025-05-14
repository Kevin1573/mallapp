package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.Response;
import com.wx.common.model.request.SaveMaterialRequest;
import com.wx.common.model.request.SaveOrUpdateBannerRequest;
import com.wx.common.model.response.QueryMaterialResponse;
import com.wx.service.MaterialLibraryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/material")
@Slf4j
public class MaterialLibraryController {

    @Autowired
    private MaterialLibraryService materialLibraryService;

    @RequestMapping(value = "/saveOrUpdateMaterial", method = {RequestMethod.POST})
    public Response saveOrUpdateMaterial(@RequestBody SaveMaterialRequest request) {
        try {
            materialLibraryService.saveOrUpdateMaterial(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin saveOrUpdateMaterial exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin saveOrUpdateMaterial exception");
        }
    }

    @RequestMapping(value = "/queryMaterialByType", method = {RequestMethod.POST})
    public Response<List<QueryMaterialResponse>> queryMaterialByType(@RequestBody SaveMaterialRequest request) {
        try {
            return Response.success(materialLibraryService.queryMaterialByType(request));
        } catch (Exception e) {
            log.error("Admin queryMaterialByType exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin queryMaterialByType exception");
        }
    }

    @RequestMapping(value = "/deleteMaterialById", method = {RequestMethod.POST})
    public Response deleteMaterialById(@RequestBody SaveMaterialRequest request) {
        try {
            materialLibraryService.deleteMaterialById(request);
            return Response.success();
        } catch (Exception e) {
            log.error("Admin deleteMaterialById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin deleteMaterialById exception");
        }
    }

}
