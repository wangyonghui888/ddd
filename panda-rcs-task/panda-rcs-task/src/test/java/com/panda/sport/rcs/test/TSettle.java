package com.panda.sport.rcs.test;/*
package com.panda.sport.rcs.test;

import com.panda.sport.rcs.mapper.RcsOrderStatisticBetTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticMatchTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticSettleTimeMapper;
import com.panda.sport.rcs.mapper.TSettleMapper;
import com.panda.sport.rcs.pojo.report.CalcSettleItem;
import com.panda.sport.rcs.pojo.report.RcsOrderStatisticBetTime;
import com.panda.sport.rcs.pojo.report.RcsOrderStatisticMatchTime;
import com.panda.sport.rcs.pojo.report.RcsOrderStatisticSettleTime;
import com.panda.sport.rcs.task.RcsTaskApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/

import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mapper.RcsOrderStatisticBetTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticMatchTimeMapper;
import com.panda.sport.rcs.mapper.RcsOrderStatisticSettleTimeMapper;
import com.panda.sport.rcs.mapper.TSettleMapper;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.wrapper.RcsOrderStatisticDateService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.test
 * @Description :  TODO
 * @Date: 2019-12-26 11:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class TSettle {
    @Autowired
    private RcsOrderStatisticBetTimeMapper rcsOrderStatisticBetTimeMapper;
    @Autowired
    private RcsOrderStatisticMatchTimeMapper rcsOrderStatisticMatchTimeMapper;
    @Autowired
    private RcsOrderStatisticSettleTimeMapper rcsOrderStatisticSettleTimeMapper;
    @Autowired
    private TSettleMapper tSettleMapper;
    @Autowired
    private RcsOrderStatisticDateService rcsOrderStatisticDateService;


    @Test
    public void getx1() {
        long beginTime = DateUtils.getBeginTime(1578110400000L);
        long endTime = DateUtils.getEndTime(1578110400000L);

        List<Date> dates = new ArrayList<>();
//        SimpleDateFormat dateFormatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        for (int x = 0; x < 1000; x++) {
            Date time = calendar.getTime();
            dates.add(time);
            rcsOrderStatisticDateService.insertOneDate(time);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}
