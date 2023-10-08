package com.panda.sport.rcs.dj.dto;

/**
 * @ClassName QueryLimitAmoutException
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/21 11:34
 * @Version 1.0
 **/
public class QueryLimitAmoutException extends RuntimeException{
    private static final long serialVersionUID = -4085901740318531773L;


    public QueryLimitAmoutException(){};

    public QueryLimitAmoutException(String message){
        super(message);
    }
}
