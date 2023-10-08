package com.panda.sport.rcs.controller;


import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.customdb.mapper.DangerousRuleExtMapper;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.panda.sport.rcs.db.service.IUserProfileRuleService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.*;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * test
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
@Api(tags = "Test")
@RestController
@RequestMapping("/")
public class TestController {

    private Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private IOrderStaticsForIpService service1;


    @Autowired
    RedisService redisService;

    @Autowired
    IUserProfileRuleService service;

    @Autowired
    ITagService tagService;

    @Autowired
    IRuleService ruleService;

    @Autowired
    IUserVisitService userVisitService;

    @Autowired
    IDangerousService dangerousService;

    @Autowired
    IOrderStaticsService orderStaticsService;

    @Autowired
    DangerousRuleExtMapper dangerousRuleExtMapper;

    @Autowired
    IUserOrderHedgeAnalyzeService userOrderHedgeAnalyzeService;

    @Autowired
    IDangerousOrderService dangerousOrderService;

    @RequestMapping(value = "/userIpStatics", method = {RequestMethod.GET})
    public Object list() {
        try {
            userVisitService.userIpStatics(LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }

    @RequestMapping(value = "/redisTest1", method = {RequestMethod.GET})
    public Object redisTest1(String value) {
        try {
            redisService.set("redis1", value);
        } catch (Exception e) {
            log.info(e.getMessage() + e);
            e.printStackTrace();
        }
        return Result.succes();
    }
    @RequestMapping(value = "/redisTest2", method = {RequestMethod.GET})
    public Object redisTest2(String key) {
        try {
           String value =  redisService.get(key).toString();
            return Result.succes(value);
        } catch (Exception e) {
            log.info(e.getMessage() + e);
            e.printStackTrace();
        }
        return Result.succes(0);
    }
    @RequestMapping(value = "/redisTest3", method = {RequestMethod.GET})
    public Object redisTest3(String key) {
        try {
            String value =  redisService.getString(key);
            return Result.succes(value);
        } catch (Exception e) {
            log.info(e.getMessage() + e);
            e.printStackTrace();
        }
        return Result.succes(0);
    }




    @RequestMapping(value = "/staticsOrder", method = {RequestMethod.GET})
    public Object staticsOrder() {
        try {
            orderStaticsService.staticsOrderForUsers(1599235200000L,System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }


    @RequestMapping(value = "/dangerousService", method = {RequestMethod.GET})
    public Object dangerousService() {
        try {
//            dangerousOrderService.executeSnake();
            String runkey = RedisConstants.PREFIX + "dangerousOrderJob";
            if (!redisService.setIfAbsent(runkey, "1", 60 * 1000L)) {
                System.out.print("dangerousOrderJob 任务执行中...此次不处理");
            }

            String key = String.format("%s.%s", RedisConstants.PREFIX, "dangerous.order.lasttime");
            redisService.set(key, 1599095726000L);
            dangerousOrderService.execute(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }

    @RequestMapping(value = "/tag", method = {RequestMethod.GET})
    public Object tag() {
        try {
            Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            tagService.execute(time, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }

    @RequestMapping(value = "/log", method = {RequestMethod.GET})
    public Object log(Integer n) {
        try {
            for (int i = 0; i < n; i++) {
                log.info("上岛咖啡结算了的凯发聚少离多看飞机"+i);
                log.debug("上岛咖啡结算了的凯发聚少离多看飞机"+i);
                log.error("上岛咖啡结算了的凯发聚少离多看飞机"+i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }

    @RequestMapping(value = "/tuichong", method = {RequestMethod.GET})
    public Object tuichong() {
        try {
            Long beginTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
            beginTime = beginTime - LocalDateTimeUtil.dayMill;
            userOrderHedgeAnalyzeService.analyzeUserOrderHedge(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }

    @RequestMapping(value = "/orderStaticsForIPJob", method = {RequestMethod.GET})
    public void orderStaticsForIPJob(@RequestParam Long s,@RequestParam Long e) {
//        try {
////            s = 1609430400000L;
////            e = 1612022400000L;
//            //统计时间默认为上个小时
//            Long now = e / 1000 / (60 * 60);
//
//            //一共有多少個小時
//            Long count = now - s / 1000 / (60 * 60);
//
//            for(int i = 0; i<= count ; i++){
//                Long startTime = (s / 1000 / (60 * 60)) * 1000 * 60 * 60 + i * 1000 * 60 * 60;
//                Long endTime = startTime + 3600 * 1000;
//
//                if(!ObjectUtils.isEmpty(s)){
//                    //判断带过来的参数（时间戳）是否已存在，如果存在则清空目标数据然后重新统计
//                    List<RiskOrderStatisticsByIp> voList = service1.selectOneByStartTime(new RiskOrderStatisticsByIp(Long.valueOf(startTime)));
//                    if(!ObjectUtils.isEmpty(voList)){
//                        log.info("--------------清空数据后重新统计--------------");
//                        //清空数据
//                        LambdaQueryWrapper<RiskOrderStatisticsByIp> warpper = new LambdaQueryWrapper<>();
//                        warpper.eq(RiskOrderStatisticsByIp::getStartTime, startTime);
//                        service1.remove(warpper);
//                        log.info("--------------清空数据成功--------------");
//                        //赋值统计时间开始时间与结束时间，统计一小时内的数据
//                    }
//                    startTime = Long.valueOf(startTime);
//                    endTime = Long.valueOf(startTime) + 3600 * 1000;
//                }else {
//                    //判断带过来的参数（时间戳）是否已存在，如果存在则清空目标数据然后重新统计
//                    List<RiskOrderStatisticsByIp> voList = service1.selectOneByStartTime(new RiskOrderStatisticsByIp(startTime));
//                    if(!ObjectUtils.isEmpty(voList)){
//                        log.info("时间段{}-{}已自动统计,本次统计跳过",startTime,endTime);
//                    }
//                }
//
//                //累积投注
//                List<RiskOrderStatisticsByIp> productAmountTotalList = service1.queryProductAmountTotal(new RiskOrderStatisticsByIp(startTime, endTime));
//                //累积输赢
//                List<RiskOrderStatisticsByIp> profitAmountList = service1.queryProfitAmount(new RiskOrderStatisticsByIp(startTime, endTime));
//
//                productAmountTotalList.addAll(profitAmountList);
//
//                //判断是否查询出数据
//                if(!ObjectUtils.isEmpty(productAmountTotalList)){
//                    Map<String, RiskOrderStatisticsByIp> map = new HashMap<>();
//                    for (RiskOrderStatisticsByIp vo : productAmountTotalList) {
//                        //根据ip拼装对象，为"累积投注"和"累积输赢"赋值
//                        if(map.get(vo.getIp())!=null){
//                            if(vo.getBetAmount()==null){
//                                vo.setBetAmount(map.get(vo.getIp()).getBetAmount());
//                            }
//                            if(vo.getProfitAmount()==null){
//                                vo.setBetAmount(map.get(vo.getIp()).getProfitAmount());
//                            }
//                        }
//                        //存入map去重
//                        map.put(vo.getIp(), vo);
//                    }
//                    //合并"累积投注"和"累积输赢"后的数据
//                    List<RiskOrderStatisticsByIp> allList = map.values().stream().collect(Collectors.toList());
//
//                    //设置创建时间为当前系统时间
//                    for (RiskOrderStatisticsByIp vo : allList) {
////                        vo.setModifyTime(System.currentTimeMillis());
////                        vo.setStartTime(startTime);
////                        vo.setEndTime(endTime);
////                        if(ObjectUtils.isEmpty(vo.getBetAmount())){
////                            vo.setBetAmount(0L);
////                        }
////                        if(ObjectUtils.isEmpty(vo.getProfitAmount())){
////                            vo.setProfitAmount(0L);
////                        }
//                    }
//
//                    //存入"订单投注统计表"
//                    log.info("--------------数据正在统计中--------------");
//                    service1.saveBatch(allList);
//                    log.info("--------------数据已统计完成--------------");
//                    XxlJobLogger.log("--------------数据已统计完成--------------");
//                }else{
//                    log.info("--------------未查询到数据,任务执行完毕--------------");
//                    XxlJobLogger.log("--------------未查询到数据,任务执行完毕--------------");
//                }
//
//            }
//
//
//
//
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//            XxlJobLogger.log(ex.getMessage(), ex);
//        }
    }



}
