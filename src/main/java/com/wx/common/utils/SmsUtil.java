package com.wx.common.utils;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class SmsUtil {

    @Autowired
    private static Client smsClient;

    /**
     * 发送短信
     *
     * @throws Exception
     */
    public static void sendMessage( ) throws Exception {
        smsClient = createClient();
        List<String> phoneList = new ArrayList<>();
        phoneList.add("18098515271");
        List<String> signList = new ArrayList<>();
        signList.add("0563同城速修");
        SendBatchSmsRequest sendBatchSmsRequest = new SendBatchSmsRequest();
        sendBatchSmsRequest.setPhoneNumberJson(JSON.toJSONString(phoneList));
        sendBatchSmsRequest.setSignNameJson(JSON.toJSONString(signList));
        sendBatchSmsRequest.setTemplateCode("SMS_464215579");
        smsClient.sendBatchSmsWithOptions(sendBatchSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());

        List<String> phoneList1 = new ArrayList<>();
        phoneList1.add("18098442991");
        sendBatchSmsRequest.setPhoneNumberJson(JSON.toJSONString(phoneList1));
        smsClient.sendBatchSmsWithOptions(sendBatchSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());

        List<String> phoneList2 = new ArrayList<>();
        phoneList2.add("15856337220");
        sendBatchSmsRequest.setPhoneNumberJson(JSON.toJSONString(phoneList2));
        smsClient.sendBatchSmsWithOptions(sendBatchSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());

        List<String> phoneList3 = new ArrayList<>();
        phoneList3.add("15005639226");
        sendBatchSmsRequest.setPhoneNumberJson(JSON.toJSONString(phoneList3));
        smsClient.sendBatchSmsWithOptions(sendBatchSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
    }

    public static com.aliyun.dysmsapi20170525.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(Constants.ACCESS_KEY_ID)
                .setAccessKeySecret(Constants.ACCESS_KEY_SECRET);
        return new com.aliyun.dysmsapi20170525.Client(config);
    }
    public static void main(String[] args) {
        try {
            sendMessage();
        } catch (Exception e) {
            System.out.print(e);
        }
    }

}
