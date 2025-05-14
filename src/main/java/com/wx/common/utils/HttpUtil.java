package com.wx.common.utils;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

    private static final OkHttpClient client = new OkHttpClient();

    public static String get(String url, Map<String, String> param) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (param != null) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : param.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), "utf-8"))
                        .append("=")
                        .append(entry.getValue()).append("&");
            }
        }
        Request request = new Request.Builder().get().url(urlBuilder.toString()).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String post(String url, Map<String, Object> param) throws IOException {
        String json = "";
        if (param != null) {
            json = JSON.toJSONString(param);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().post(requestBody).url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String postWithHeader(String url, Map<String, Object> param, Map<String, String> header) throws IOException {
        String json = "";
        if (param != null) {
            json = JSON.toJSONString(param);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request.Builder builder = new Request.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Request request = builder.post(requestBody).url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
