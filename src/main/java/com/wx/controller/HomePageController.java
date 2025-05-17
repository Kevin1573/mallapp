//package com.wx.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.wx.common.model.Response;
//import com.wx.common.model.request.SaveOrUpdateBannerRequest;
//import com.wx.common.model.request.SaveOrUpdateModuleRequest;
//import com.wx.common.model.response.SaveOrUpdateModuleResponse;
//import com.wx.service.HomePageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping(value = "/page")
//@Slf4j
//public class HomePageController {
//
//    @Autowired
//    private HomePageService homePageService;
//
//    @RequestMapping(value = "/saveOrUpdateBanner", method = {RequestMethod.POST})
//    public Response uploadGoods(@RequestBody SaveOrUpdateBannerRequest request) {
//        try {
//            homePageService.saveOrUpdateBanner(request);
//            return Response.success();
//        } catch (Exception e) {
//            log.error("Admin saveOrUpdateBanner exception, request = {}", JSON.toJSONString(request), e);
//            return Response.failure("Admin saveOrUpdateBanner exception");
//        }
//    }
//
//    @RequestMapping(value = "/queryBanner", method = {RequestMethod.POST})
//    public Response<String> queryBanner() {
//        try {
//            return Response.success(homePageService.queryBanner());
//        } catch (Exception e) {
//            log.error("Admin queryBanner exception", e);
//            return Response.failure("Admin queryBanner exception");
//        }
//    }
//
//    @RequestMapping(value = "/saveOrUpdateModule", method = {RequestMethod.POST})
//    public Response saveOrUpdateModule(@RequestBody SaveOrUpdateModuleRequest request) {
//        try {
//            homePageService.saveOrUpdateModule(request);
//            return Response.success();
//        } catch (Exception e) {
//            log.error("Admin saveOrUpdateModule exception, request = {}", JSON.toJSONString(request), e);
//            return Response.failure("Admin saveOrUpdateModule exception");
//        }
//    }
//
//    @RequestMapping(value = "/queryModule", method = {RequestMethod.POST})
//    public Response<List<SaveOrUpdateModuleResponse>> queryModule() {
//        try {
//            return Response.success(homePageService.queryModule());
//        } catch (Exception e) {
//            log.error("Admin queryModule exception", e);
//            return Response.failure("Admin queryModule exception");
//        }
//    }
//
//    @RequestMapping(value = "/deleteModuleById", method = {RequestMethod.POST})
//    public Response deleteModuleById(@RequestBody SaveOrUpdateModuleRequest request) {
//        try {
//            homePageService.deleteModuleById(request);
//            return Response.success();
//        } catch (Exception e) {
//            log.error("Admin deleteModuleById exception, request = {}", JSON.toJSONString(request), e);
//            return Response.failure("Admin deleteModuleById exception");
//        }
//    }
//}
