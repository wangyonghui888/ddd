package com.panda.sport.data.rcs.dto.dj;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DJCancelOrderResVo
 * @Description TODO
 * @Author Administrator
 * @Date 2021/10/20 10:38
 * @Version 1.0
 **/
@Data
public class DJCancelOrderResVo implements Serializable {
    private static final long serialVersionUID = 3809752496317960269L;

    /**
     *  成功:true,失败:false
     */
    private String status;

    /**
     *  错误编码
     */
    private String code;

    /**
     *  提示信息
     */
    private String data;
}
