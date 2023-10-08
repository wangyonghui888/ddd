package com.panda.sport.rcs;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mgr.RiskBootstrap;
import com.panda.sport.rcs.mgr.service.impl.odds.api.OddsPublicMethodApi;
import com.panda.sport.rcs.mgr.wrapper.impl.TOrderServiceImpl;
import com.panda.sport.rcs.pojo.RcsLabelSportVolumePercentage;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaMerchantSingleFieldLimitVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs
 * @Description :  TODO
 * @Date: 2020-08-15 16:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RiskBootstrap.class)
@Slf4j
public class OrderServiceTest {
    @Autowired
    private TOrderServiceImpl orderService;
    @Autowired
    private RedisClient redisClient;;
    @Autowired
    private OddsPublicMethodApi oddsPublicMethodApi;
//    @Test
    public void test(){
        oddsPublicMethodApi.getMsg(1946065L,4680369933231946065L,39L, null);
        RcsQuotaMerchantSingleFieldLimitVo rcsQuotaMerchantSingleFieldLimitVo=new RcsQuotaMerchantSingleFieldLimitVo();
        rcsQuotaMerchantSingleFieldLimitVo.setCompensationLimitBase(1L);
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList=new ArrayList<>();
        RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit=new RcsQuotaMerchantSingleFieldLimit();
        rcsQuotaMerchantSingleFieldLimitList.add(rcsQuotaMerchantSingleFieldLimit);
        rcsQuotaMerchantSingleFieldLimitVo.setRcsQuotaMerchantSingleFieldLimitList(rcsQuotaMerchantSingleFieldLimitList);
        Object o = JSON.toJSONString(rcsQuotaMerchantSingleFieldLimitVo);
//        orderService.getTourTemplate(1,3L,1837L,0);
    }
//    @Test
    public void testLabelLimitConfig(){
        RcsLabelSportVolumePercentage list=orderService.setLabelLimitConfig(251,1);
        System.out.println(list);
    }
}
