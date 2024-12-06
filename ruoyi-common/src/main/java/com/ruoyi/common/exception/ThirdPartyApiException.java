package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-12-04 17:30
 * @description: 第三方接口调用异常
 */


public class ThirdPartyApiException extends RuntimeException {

    public ThirdPartyApiException() {
    }

    public ThirdPartyApiException(String msg) {
        super(msg);
    }


}
