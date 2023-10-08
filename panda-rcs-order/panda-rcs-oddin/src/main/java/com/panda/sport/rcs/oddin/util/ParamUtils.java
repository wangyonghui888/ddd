package com.panda.sport.rcs.oddin.util;

import com.panda.sport.rcs.oddin.enums.DataSourceEnum;
import org.apache.commons.lang3.StringUtils;


/**
 * 入参数据处理工具类
 *
 * @author Z9-conway
 */
public class ParamUtils {

    public static String montageOrderNo(String orderNo, Integer sourceId) {
        if (StringUtils.isBlank(orderNo)) {
            return "";
        }
        String source = DataSourceEnum.getValue(sourceId);
        if (StringUtils.isBlank(source)) {
            return "";
        }
        return source.concat("-").concat(orderNo);
    }

    public static String splitOrderNo(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return "";
        }
        return orderNo.split("-")[1];
    }
    //区分注单ID是体育还是电竞用于后续的需求
    public static String splitOrderNos(String orderNo) {
        if (StringUtils.isBlank(orderNo)) {
            return "";
        } return orderNo.split("-")[0];
    }
}
