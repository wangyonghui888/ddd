package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.rule.DangerousRuleParameterVo;

import java.math.BigDecimal;

/**
 * 危险投注  规则专用Service
 *
 * @author :  lithan
 * @date: 2020-07-10 09:04:49
 */
public interface IDangerousService {

    /**
     * "赛前阶段，同时满足以下2个条件的注单，标记为“蛇单投注”：
     * 1、注单金额>=1000元（参数1）
     * 2、下注10秒内（参数2）赔率跳水到Y，然后60秒内（参数3）赔率没有回到原值X"
     */
    public void d1(DangerousRuleParameterVo vo);


    /**
     * "滚球阶段，同时满足以下6个条件的注单，标记为“蛇单投注”：
     * 1、注单金额>=1000元（参数1）
     * 2、大小类玩法，Pdid：2、10、11、13、18、26、34、87、88、97、98、102、109、110、114、115、116、122、123、124、127、134、217、233
     * 3、下注10秒内（参数2）赔率跳水到Y，然后30秒内（参数3）赔率没有回到原值X
     * 4、注单欧赔>1.9（参数4）
     * 5、注单欧赔>=supremacy（supremacy=球头-(1-马来赔)   ） 【注：若马来赔为负，则supremacy=球头-(1+马来赔)】
     * 6、注单赔率> SR赔率"
     * @param vo
     */
    public void d2(DangerousRuleParameterVo vo);
    /**
     * D2	打水投注	"满足以下任一条件的均为打水投注：
     * 1.单账户的同投注项的两笔或多笔注单，间隔时间少于2秒（参数1）
     * 2.投注项1出现蛇单时，5秒内（参数2）购买投注项2的注单（无论是否投注成功）
     * 3.同一赛事注单达到参数3笔，且至少参数3笔注单的下注时间间隔相似（差异在参数4秒内），这些下注时间间隔相似的注单标记标记为打水投注"
     */
    public void d3(DangerousRuleParameterVo vo);


    /**
     * D3	资讯投注	注单（无论是否投注成功）投注时间之后的5秒内（参数1）发生了进球、红牌事件
     */
    public void d4(DangerousRuleParameterVo vo);

    /**
     * D4	篮球打洞	篮球投注时，在同一赛事相同玩法的不同盘口之间交叉下注（无论是否投注成功），相应注单标记为篮球打洞。
     * 涉及玩法PDID：2（常规赛总分）、10（常规赛{主队}总分）、11（常规赛{客队}总分）、18（上半场总分）、26（下半场总分）、38（总分）、45（第1节总分）、
     * 51（第2节总分）、57（第3节总分）、63（第4节总分）、87（上半场{主队}总分）、88（下半场{主队}总分）、97（上半场{客队}总分）、98（下半场{客队}总分）、
     * 145（第{X}节{主队}总分）、146（第{X}节{客队}总分）
     */
    public void d5(DangerousRuleParameterVo vo);
    /**
     * D5	风控拒单	"满足以下任一条件的均标记为风控拒单：
     * 1.自动操盘被拒绝的注单
     * 2.手动模式拒绝的注单"
     */
    public void d6(DangerousRuleParameterVo vo);
}
