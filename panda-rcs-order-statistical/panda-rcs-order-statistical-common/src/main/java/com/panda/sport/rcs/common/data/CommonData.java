package com.panda.sport.rcs.common.data;

import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lithan
 * @date 2022-4-9 13:28:20
 */
public class CommonData {

    //待发送规则
    public static Map<String, RuleParameterVo> userRuledataMap = new ConcurrentHashMap<>();
}
