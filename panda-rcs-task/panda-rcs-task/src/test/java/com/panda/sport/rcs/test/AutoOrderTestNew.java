/*
package com.panda.sport.rcs.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.task.RcsTaskApplication;
import com.panda.sport.rcs.task.job.orderMode.AutoOrderJobHandler;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

*/
/**
 * @author :  toney
 * @Project Name :  panda-rcs-task
 * @Package Name :  com.panda.sport.rcs.test
 * @Description :  TODO
 * @Date: 2020-08-15 15:51
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 *//*

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcsTaskApplication.class)
public class AutoOrderTestNew {
    @Autowired
    private AutoOrderJobHandler autoOrderJobHandler;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Test
    public void test(){
        LambdaQueryWrapper<StandardMatchInfo> lambdaQueryWrapper = new QueryWrapper<StandardMatchInfo>().lambda();
        lambdaQueryWrapper.eq(StandardMatchInfo::getId, 3);
        lambdaQueryWrapper.select(StandardMatchInfo::getId, StandardMatchInfo::getStandardTournamentId,StandardMatchInfo::getSportId);
        StandardMatchInfo info = standardMatchInfoMapper.selectOne(lambdaQueryWrapper);

        autoOrderJobHandler.getTourTemplate(info,0);
    }
}
*/
