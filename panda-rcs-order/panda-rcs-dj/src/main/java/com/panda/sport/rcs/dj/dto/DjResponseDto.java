package com.panda.sport.rcs.dj.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DjResponseDto
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/19 20:33
 * @Version 1.0
 **/
@Data
public class DjResponseDto implements Serializable {


    private static final long serialVersionUID = -8359425443379431184L;
    //状态
    private String status;
    //返回编码
    private String code;
    //数据
    private String data;
    //注单映射关系
    private Map<String, List<Long>> id_mapping;
    //投注项以及对应的赔率
    private Map<String,Double> odd_mapping;
}
