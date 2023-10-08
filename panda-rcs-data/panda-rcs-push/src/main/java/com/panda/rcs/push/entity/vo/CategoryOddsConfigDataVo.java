package com.panda.rcs.push.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CategoryOddsConfigDataVo implements Serializable {

    private  String matchId;

    private Integer matchType;

    private Map<String, Object> playDataSource;

}
