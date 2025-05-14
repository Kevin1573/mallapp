package com.wx.controller;

import com.alibaba.fastjson.JSON;
import com.wx.common.model.Response;
import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;
import com.wx.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/oss")
@Slf4j
public class OssController {

    @Autowired
    private OssService ossService;

    @RequestMapping(value = "/getConfig", method = {RequestMethod.POST})
    public Response<OssConfigResponse> orderRepair(@RequestBody OssRequest request) {
        try {
            return Response.success(ossService.getOssConfig(request));
        } catch (Exception e) {
            log.error("Oss getConfig exception, request = {}", JSON.toJSONString(request), e);
            return Response.failure("Oss getConfig exception");
        }
    }
}
