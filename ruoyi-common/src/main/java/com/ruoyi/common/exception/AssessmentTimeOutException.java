package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2025-01-14 11:31
 * @description: 评估超时异常类
 */


public class AssessmentTimeOutException extends RuntimeException {


    public AssessmentTimeOutException(){}
    public AssessmentTimeOutException(String msg) {
        super(msg);
    }

}
