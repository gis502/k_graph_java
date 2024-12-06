package com.ruoyi.common.exception;

/**
 * @author: xiaodemos
 * @date: 2024-12-03 23:47
 * @description: 文件下载失败异常类
 */


public class FileDownloadException extends RuntimeException{

    public FileDownloadException(){}

    public FileDownloadException(String msg){
        super(msg);
    }

}
