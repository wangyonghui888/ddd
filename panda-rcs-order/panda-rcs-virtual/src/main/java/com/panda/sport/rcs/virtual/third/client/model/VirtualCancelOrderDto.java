package com.panda.sport.rcs.virtual.third.client.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName VirtualCancelOrderDto
 * @Description TODO
 * @Author zerone
 * @Date 2021/9/12 11:54
 * @Version 1.0
 **/
@Data
public class VirtualCancelOrderDto {


    private String linkId;
    private List<Data> data;

    @lombok.Data
    public  static class Data {

        private String orderNo;

        private Integer status;

        private String msgInfo;

        private Integer cancelStatus;
    }
}
