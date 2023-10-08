package com.panda.sport.rcs.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.common
 * @Description :  LocalDateT常用操作集
 * @Date: 2019-10-07 15:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class LocalDateTimeUtils {
    /**
     *  获取肖前时间
     * @return
     */
    public static LocalDateTime getNow(){
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
}
