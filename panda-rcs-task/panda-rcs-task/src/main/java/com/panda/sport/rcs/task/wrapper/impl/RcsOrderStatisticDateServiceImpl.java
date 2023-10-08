package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.mapper.RcsOrderStatisticDateMapper;
import com.panda.sport.rcs.pojo.report.RcsOrderStatisticDate;
import com.panda.sport.rcs.task.wrapper.RcsOrderStatisticDateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.trade.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-01-02 11:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsOrderStatisticDateServiceImpl extends ServiceImpl<RcsOrderStatisticDateMapper, RcsOrderStatisticDate> implements RcsOrderStatisticDateService {

    @Autowired
    RcsOrderStatisticDateMapper rcsOrderStatisticDateMapper;

    @Override
    public void insertOneDate(Date date) {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        QueryWrapper<RcsOrderStatisticDate> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsOrderStatisticDate::getOrderDay, dateStr);
        RcsOrderStatisticDate rcsOrder = rcsOrderStatisticDateMapper.selectOne(wrapper);
        if (rcsOrder == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            //Calendar里取出来的month比实际的月份少1，所以要加上
            int mon = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            int totalDays = DateUtils.whichDay(year, mon, day);
            int phase = 0;
            int week = 0;
            if (totalDays % 28 == 0) {
                phase = totalDays / 28;
                week = 4;
            } else {
                phase = (totalDays / 28) + 1;

                if ((totalDays % 28) % 7 == 0) {
                    week = (totalDays % 28) / 7;
                } else {
                    week = ((totalDays % 28) / 7) + 1;
                }
            }
            String phaseStr = "第" + phase + "期";
            String weekStr = "第" + week + "周";
            RcsOrderStatisticDate rcsOrderStatisticDate = new RcsOrderStatisticDate();
            rcsOrderStatisticDate.setOrderDay(dateStr);
            rcsOrderStatisticDate.setOrderYear(year + "");
            rcsOrderStatisticDate.setOrderPhase(phaseStr);
            rcsOrderStatisticDate.setOrderWeek(weekStr);
            rcsOrderStatisticDateMapper.insert(rcsOrderStatisticDate);
        }

    }
}
