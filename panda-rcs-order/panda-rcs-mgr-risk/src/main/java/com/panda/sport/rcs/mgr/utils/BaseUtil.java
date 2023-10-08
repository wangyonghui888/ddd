package com.panda.sport.rcs.mgr.utils;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseUtil {

   private static String[] weekDays = {"星期日", "星期六"};

    public static boolean getHoliday(Date date) {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        String strDate=dateFm.format(date);
        if(weekDays[0].equals(strDate) || weekDays[1].equals(strDate))
            return true;
        else
            return  false;
    }
    public static BigDecimal getVolumeByDeviceType(Integer deviceType) {

        BigDecimal volumePercentage;
        switch (deviceType){
            case 1:  // 1h5
                volumePercentage=new BigDecimal("0.6");
                break;
            case 2: // 2pc
                volumePercentage=new BigDecimal("1");
                break;
            default: // 3app
                volumePercentage=new BigDecimal("0.3");
                break;
        }
        return  volumePercentage;
    }
}
