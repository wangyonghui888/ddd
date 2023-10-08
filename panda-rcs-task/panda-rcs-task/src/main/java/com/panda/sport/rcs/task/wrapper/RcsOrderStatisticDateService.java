package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.report.RcsOrderStatisticDate;

import java.util.Date;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.trade.wrapper
 * @Description :  TODO
 * @Date: 2020-01-02 11:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsOrderStatisticDateService extends IService<RcsOrderStatisticDate> {

    void insertOneDate(Date date);
}
