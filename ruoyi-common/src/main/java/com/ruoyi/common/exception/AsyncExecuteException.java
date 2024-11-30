package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-11-27 2:08
 * @description: 异步执行任务时出错
 */


public class AsyncExecuteException extends RuntimeException{

    public AsyncExecuteException(){}
    public AsyncExecuteException(String msg){
        super(msg);
    }

}
