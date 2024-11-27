package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-11-26 3:18
 * @description: 数据保存异常
 */


public class DataSaveException extends RuntimeException {

    public DataSaveException() {
    }

    public DataSaveException(String msg) {
        super(msg);
    }

}
