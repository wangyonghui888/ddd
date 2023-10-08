package com.panda.rcs.push.entity.vo;


import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;


@Data
public class RcsAutoOddsRequest<T>  extends RcsBaseEntity<com.panda.rcs.push.entity.vo.RcsAutoOddsRequest> {

    private String linkId;

    private T data;

    private Long matchId;

    private Object dataTime;

    private String dataSourceCode;
}
