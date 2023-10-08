package com.panda.sport.data.rcs.dto;

import lombok.Data;

/**
 * @Description   用户信息MQ消息接收类
 * @Param 
 * @Author  toney
 * @Date  17:07 2020/6/3
 * @return 
 **/
@Data
public class UserItem {
    /**
     * 版本号
     */
    private static final long serialVersionUID = -5591067906256831859L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户登录密码
     */
    private String password;


    private String realName;

    private String phone;


    /*
     * 币种(注册时必填)
     */
    private String currencyCode;

    private String email;

    private String createUser;

    private Long createTime;

    private Long modifyTime;




    /**
     * 用户等级
     */
    private Integer userLevel;


    private String ipAddress;

    private String merchantCode;

}
