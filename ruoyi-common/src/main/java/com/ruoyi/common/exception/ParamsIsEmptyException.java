package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 3:18
 * @description: 参数为空异常
 */


public class ParamsIsEmptyException extends RuntimeException {

    public ParamsIsEmptyException() {
    }

    public ParamsIsEmptyException(String msg) {
        super(msg);
    }

}
