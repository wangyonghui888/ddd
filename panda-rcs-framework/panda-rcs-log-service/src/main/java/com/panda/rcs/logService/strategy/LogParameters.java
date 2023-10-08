package com.panda.rcs.logService.strategy;

import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Z9-jing
 */
@Data
public class LogParameters implements Serializable {
    private String methodName;

    private Object[] args;

    private RcsOperateLog log;

    private String beforeString;

    private  Map<String,Object> map;

}
