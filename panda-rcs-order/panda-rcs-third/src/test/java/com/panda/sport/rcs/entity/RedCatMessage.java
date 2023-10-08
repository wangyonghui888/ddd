package com.panda.sport.rcs.entity;

import lombok.Data;

import java.util.Date;

/**
 * 红猫测试缓存操作
 * @author vere
 * @date 2023-06-06
 * @version 1.0.0
 */
@Data
public class RedCatMessage {
    /**
     * 注单号
     */
    String orderNo;
    /**
     * 发送及接收时间
     */
    String times;
    /**
     * 消息内容
     */
    String message;
    /**
     * 过期时间
     */
    Long expiredTime;

}
