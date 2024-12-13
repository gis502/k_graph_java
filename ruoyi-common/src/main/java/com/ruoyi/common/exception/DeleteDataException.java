package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-12-10 10:34
 * @description: 删除数据时出错
 */


public class DeleteDataException extends RuntimeException {

    public DeleteDataException() {
    }

    public DeleteDataException(String msg) {
        super(msg);
    }

}
