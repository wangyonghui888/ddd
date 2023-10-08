package com.panda.sport.rcs.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-07-23 17:42
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class BusinessSingleBetAndPlayVo {
    private List<BusinessSingleBetVo> businessSingleBetVoList;
    private Map<Long, Map<String, String>> longI18nBeanMap;
}
