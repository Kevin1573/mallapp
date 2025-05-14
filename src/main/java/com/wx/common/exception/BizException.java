package com.wx.common.exception;

/**
 * 业务异常处理
 *
 * @author gengzijin
 * @date 2022/12/12
 */
public class BizException extends RuntimeException {

    private int code;

    public BizException(String msg) {
        super(msg);
    }

    public BizException(String msg, Throwable ex) {
        super(msg, ex);
    }

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
    }
}
