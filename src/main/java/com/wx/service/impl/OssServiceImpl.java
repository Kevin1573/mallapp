package com.wx.service.impl;

import com.aliyun.sts20150401.Client;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.wx.common.model.request.OssRequest;
import com.wx.common.model.response.OssConfigResponse;
import com.wx.common.utils.Constants;
import com.wx.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private Client client;

    @Override
    public OssConfigResponse getOssConfig(OssRequest request) throws Exception {
        // 请求阿里云api，获取登录的临时凭证
        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
                .setRoleArn(Constants.ROLE_ARN)
                .setRoleSessionName(Constants.ROLE_SESSION_NAME);
        RuntimeOptions runtime = new RuntimeOptions();
        AssumeRoleResponse assumeRoleResponse = client.assumeRoleWithOptions(assumeRoleRequest, runtime);
        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = assumeRoleResponse.getBody().getCredentials();

        // 封装返回结果
        OssConfigResponse response = new OssConfigResponse();
        response.setAccessKeyId(credentials.getAccessKeyId());
        response.setAccessKeySecret(credentials.getAccessKeySecret());
        response.setStsToken(credentials.getSecurityToken());
        response.setRegion(Constants.REGION);
        response.setBucket(request.getBucket());
        return response;
    }


//    public static void main(String[] args) throws Exception {
//        // 请求阿里云api，获取登录的临时凭证
//        Client client = createClient();
//        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest()
//                .setRoleArn(Constants.ROLE_ARN)
//                .setRoleSessionName(Constants.ROLE_SESSION_NAME);
//        RuntimeOptions runtime = new RuntimeOptions();
//        AssumeRoleResponse assumeRoleResponse = client.assumeRoleWithOptions(assumeRoleRequest, runtime);
//        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = assumeRoleResponse.getBody().getCredentials();
//
//        // 封装返回结果
//        OssConfigResponse response = new OssConfigResponse();
//        response.setAccessKeyId(credentials.getAccessKeyId());
//        response.setAccessKeySecret(credentials.getAccessKeySecret());
//        response.setStsToken(credentials.getSecurityToken());
//        response.setRegion(Constants.REGION);
//        response.setBucket("0563headurl");
//        System.out.println(JSON.toJSONString(response));
//    }


}
