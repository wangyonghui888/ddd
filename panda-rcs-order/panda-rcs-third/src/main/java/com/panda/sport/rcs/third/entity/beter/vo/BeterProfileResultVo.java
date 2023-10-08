package com.panda.sport.rcs.third.entity.beter.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Beulah
 * @date 2023/3/20 17:08
 * @description 接收profile返回数据
 */
@Data
public class BeterProfileResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String playerId;
    private Map<String,String> currency;
    private List<Map<String,String>> currencies;
    private Map<String,List<String>> favorite;
    private String token;

}
