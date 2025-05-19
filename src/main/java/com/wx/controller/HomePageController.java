package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.Response;
import com.wx.common.model.request.HomePageRequest;
import com.wx.common.model.request.SaveOrUpdateBannerRequest;
import com.wx.common.model.request.SaveOrUpdateModuleRequest;
import com.wx.common.model.response.CompanyConfigResponse;
import com.wx.common.model.response.HomePageResponse;
import com.wx.common.model.response.SaveOrUpdateModuleResponse;
import com.wx.service.HomePageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/page")
@Slf4j
public class HomePageController {

    @Autowired
    private HomePageService homePageService;

    @RequestMapping(value = "/queryPage", method = {RequestMethod.POST})
    public Response<HomePageResponse> queryPage(@RequestBody HomePageRequest request) {
        try {
            return Response.success(homePageService.queryPage(request));
        } catch (Exception e) {
            log.error("Admin queryModule exception", e);
            return Response.failure("Admin queryModule exception");
        }
    }

    @RequestMapping(value = "/queryCompanyConfig", method = {RequestMethod.POST})
    public Response<CompanyConfigResponse> queryCompanyConfig(@RequestBody HomePageRequest request) {
        try {
            return Response.success(homePageService.queryCompanyConfig(request));
        } catch (Exception e) {
            log.error("Admin deleteModuleById exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Admin deleteModuleById exception");
        }
    }
}
