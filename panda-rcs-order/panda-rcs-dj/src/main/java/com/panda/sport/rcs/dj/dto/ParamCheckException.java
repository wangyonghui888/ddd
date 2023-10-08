package com.panda.sport.rcs.dj.dto;

/**
 * @ClassName ParamCheckException
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/17 21:37
 * @Version 1.0
 **/
public class ParamCheckException extends RuntimeException{


    public ParamCheckException(){};

    public ParamCheckException(String message){
        super(message);
    }
}
