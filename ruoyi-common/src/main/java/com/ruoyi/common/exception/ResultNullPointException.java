package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 11:33
 * @description: 数据空指针异常
 */


public class ResultNullPointException extends RuntimeException {

    public ResultNullPointException() {

    }

    public ResultNullPointException(String msg) {
        super(msg);
    }

}
