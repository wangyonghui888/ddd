package com.panda.rcs.pending.order.utils;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.pending.order.vo.dto.ThirdMatchDTO;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.trade.util
 * @Description :  TODO
 * @Date: 2022-03-06 21:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
public class CommonUtil {
    public static String getId(String jsonArray, String dataSourceCode) {
        try {
            if (StringUtils.isBlank(jsonArray) || StringUtils.isBlank(dataSourceCode)) {
                return null;
            }
            String aoId = null;
            log.info("获取数据源ID信息:{},{}",jsonArray,dataSourceCode);
            List<ThirdMatchDTO> thirdMatchDTOS = JsonFormatUtils.fromJsonArray(jsonArray, ThirdMatchDTO.class);
            for (ThirdMatchDTO thirdMatchDTO : thirdMatchDTOS) {
                if (thirdMatchDTO.getDataSourceCode().equals(dataSourceCode)) {
                    aoId = thirdMatchDTO.getThirdMatchSourceId();
                }
            }
            return aoId;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getDataSourceCode(String earlySettStr){
        if(StringUtils.isNotBlank(earlySettStr)){
            Map<String, Integer> sourceCodeMap = JSON.parseObject(earlySettStr, Map.class);
            for (Map.Entry<String, Integer> entry : sourceCodeMap.entrySet()) {
                if (entry.getValue() == 1) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

}
