package com.panda.sport.rcs.controller;


import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;
import com.panda.sport.rcs.customdb.mapper.DangerousRuleExtMapper;
import com.panda.sport.rcs.db.service.IUserProfileRuleService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    ProducerSendMessageUtils producerSendMessageUtils;


    @ApiOperation(value = "send")
    @RequestMapping(value = "/send", method = {RequestMethod.POST})
    public Object send() {
        try {
            producerSendMessageUtils.sendMessage("panda0207", "gagaga");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }



    @ApiOperation(value = "test")
    @RequestMapping(value = "/test", method = {RequestMethod.POST})
    public Object list() {
        try {
            RuleParameterVo vo = new RuleParameterVo();
            vo.setTime(1600000000000L);
            vo.setParameter1("3");
            vo.setParameter2("3");
            vo.setParameter3("3");
            vo.setParameter4("3");
            vo.setUserId(1L);
            RuleResult result = ruleService.r19(vo);
            System.out.println(JSONObject.toJSONString(result));
//            tagService.execute();


//            List<OrderDetailVo> list = dangerousRuleExtMapper.getOrderByBetTime(0L, 1600000000000L);
//            DangerousRuleParameterVo vo = new DangerousRuleParameterVo();
//            vo.setOrderDetailVo(list.get(0));
//            vo.setUserId(1L);
//            vo.setParameter1("3");
//            vo.setParameter2("3");
//            vo.setParameter3("3");
//            vo.setParameter4("3");
//            dangerousService.d1(vo);
//            dangerousService.d2(vo);
//            dangerousService.d3(vo);
//            dangerousService.d4(vo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.succes();
    }
 
}
