package com.wx.common.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class Response<T> implements Serializable {

    /**
     * 返回结果
     */
    private T data;

    /**
     * httpCode，正常请求为200
     */
    private Integer httpCode;

    /**
     * errorCode
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String errorMessage;
    /**
     * 是否成功
     */
    private Boolean success;


    public Response() {
    }

    public static <T> Response<T> success() {
        Response<T> result = new Response<>();
        result.setSuccess(true);
        result.setHttpCode(200);
        return result;
    }

    public static <T> Response<T> success(T data) {
        Response<T> result = new Response<>();
        result.setSuccess(true);
        result.setData(data);
        result.setHttpCode(200);
        return result;
    }

    public static <T> Response<T> failure(String errorMessage) {
        Response<T> result = new Response<>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setErrorCode("500");
        result.setHttpCode(200);
        return result;
    }

}
