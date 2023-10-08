package com.panda.sport.rcs.dj.dto;

/**
 * @ClassName RegisterException
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/20 19:39
 * @Version 1.0
 **/
public class RegisterException extends RuntimeException{
    private static final long serialVersionUID = 2223281297335987970L;



    public RegisterException(){};

    public RegisterException(String message){
        super(message);
    }
}
