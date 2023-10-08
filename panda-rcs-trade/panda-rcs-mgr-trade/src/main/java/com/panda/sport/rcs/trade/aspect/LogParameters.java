package com.panda.sport.rcs.trade.aspect;

import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogParameters implements Serializable {
    private String methodName;

    private Object[] args;

    private RcsOperateLog log;

}
