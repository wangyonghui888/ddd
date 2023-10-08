package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.rule.FinancialRuleVo;
import com.panda.sport.rcs.common.vo.rule.RuleParameterVo;

import java.math.BigDecimal;

/**
 * 规则逻辑实现  规则专用Service
 *
 * @author :  lithan
 * @date: 2020-06-28 09:50:05
 */
public interface IRuleService {
    /**
     * R1	用户盈亏金额	"一段时间（参数1天）内，用户盈亏金额位于[参数2,参数3)区间；盈亏金额=派彩金额-已结算注单的投注金额
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示盈亏金额>=参数2；例如只有参数3，则表示盈亏金额<参数3
     * 输出值：判断结果（1/0）；实际值（实际亏损金额）"
     *
     * @param vo
     * @return
     */
    public RuleResult<BigDecimal> r1(RuleParameterVo vo);

    /**
     * R2	盈利率标准	"一段时间（参数1天）内，用户盈利率位于[参数2,参数3)区间；
     * 盈利率=（派彩金额-已结算注单的投注金额）/已结算注单的投注金额
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示盈利率>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际盈利率）"
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r2(RuleParameterVo vo);

    /**
     * R3	投注笔数标准	"一段时间（参数1天）内，用户成功投注笔数位于[参数2,参数3)区间；
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示成功投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r3(RuleParameterVo vo);

    /**
     * R4	投注金额标准	"一段时间（参数1天）内，用户成功投注金额位于[参数2,参数3)区间；
     * 若某个参数不填，则所在区间方向不限制，例如只有参数3，则表示成功投注金额<参数3；
     * 输出值：判断结果（1/0）；实际值 "
     *
     * @param vo
     * @return
     */
    public RuleResult<BigDecimal> r4(RuleParameterVo vo);


    /**
     * 访问特征类	R5	代理登录判断标准	"一段时间（参数1天）内，出现单日访问IP来自>=参数2个城市的情况
     * 输出值：判断结果（1/0）；实际值（城市数量）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r5(RuleParameterVo vo);

    /**
     * 访问特征类	R6	危险IP判断标准	"一段时间（参数1天）内，访问IP为危险IP的数量>=参数2
     * 输出值：判断结果（1/0）；实际值（实际危险IP数量）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Integer> r6(RuleParameterVo vo);

    /**
     * 访问特征类	R7	一机多登判断标准	"一段时间（参数1天）内，出现同一个IP地址有>=参数2个账号登录的情况
     * 输出值：判断结果（1/0）；实际值（实际账号数量）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Integer> r7(RuleParameterVo vo);

    /**
     * R8	蛇单投注笔数标准	"一段时间（参数1天）内，标记为蛇单投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示蛇单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r8(RuleParameterVo vo);


    /**
     * R9	资讯单投注笔数标准	"一段时间（参数1天）内，标记为资讯投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示资讯单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r9(RuleParameterVo vo);


    /**
     * R10 水单投注笔数标准	"一段时间（参数1天）内，标记为打水投注的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示水单投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r10(RuleParameterVo vo);

    /**
     * R11	篮球打洞笔数标准	"一段时间（参数1天）内，标记为篮球打洞的注单笔数位于[参数2,参数3)区间
     * 若某个参数不填，则所在区间方向不限制，例如只有参数2，则表示篮球打洞投注笔数>=参数2；
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     *
     * @param vo
     * @return
     */
    public RuleResult<Long> r11(RuleParameterVo vo);

    /**
     * R12	蛇单投注比例标准	"一段时间（参数1天）内，蛇单投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r12(RuleParameterVo vo);

    /**
     * R13	资讯单投注比例标准	"一段时间（参数1天）内，资讯投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     * @param vo
     * @return
     */
    public RuleResult<String> r13(RuleParameterVo vo);

    /**
     * R14	水单投注比例标准	"一段时间（参数1天）内，打水投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     * @param vo
     * @return
     */
    public RuleResult<String> r14(RuleParameterVo vo);

    /**
     * R15	篮球打洞比例标准	"一段时间（参数1天）内，篮球打洞投注笔数/总投注笔数位于[参数2,参数3)区间
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     * @param vo
     * @return
     */
    public RuleResult<String> r15(RuleParameterVo vo);


    /**
     * R16	投注赛种比例	"一段时间（参数1天）内，某一赛种投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r16(RuleParameterVo vo);


    /**
     * 投投注特征类	R17	投注联赛比例	"一段时间（参数1天）内，某一联赛投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r17(RuleParameterVo vo);


    /***
     * R18	满额注单笔数	"一段时间（参数1天）内，满额注单笔数达到参数2
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际笔数）"
     **/
    RuleResult<Long> r18(RuleParameterVo vo);

    /**
     * R19	满额注单比例	"一段时间（参数1天）内，满额注单笔数/总投注笔数达到参数2
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）"
     **/
    RuleResult<String> r19(RuleParameterVo vo);


    /***
     * 大额注单笔数.
     * 一段时间（参数1天）内，达到指定金额（参数2）的注单笔数>=参数3
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际笔数）
     * @param vo   规则使用的参数    
     * @return com.panda.sport.rcs.common.bean.RuleResult<java.math.BigDecimal>
     * @Description
     * @Author dorich
     * @Date 9:34 2020/7/11
     **/
    RuleResult<BigDecimal> r20(RuleParameterVo vo);

    /***
     * 大额注单比例
     * 一段时间（参数1天）内，达到指定金额（参数2）的注单笔数/总投注笔数>=参数3
     * 注：统计所有状态的注单
     * 输出值：判断结果（1/0）；实际值（实际比例）
     * @param vo
     * @return com.panda.sport.rcs.common.bean.RuleResult<java.math.BigDecimal>
     * @Description
     * @Author dorich
     * @Date 9:35 2020/7/11
     **/
    RuleResult<String> r21(RuleParameterVo vo);

    /**
     * 一段时间（参数1天）内，某一玩法投注笔数/总投注笔数位于[参数2,参数3)区间
     * 注：仅统计成功状态的注单
     * 判断结果（1/0）；输出值：（赛种）玩法1-实际比例 | （赛种）玩法2-实际比例……
     * 注：输出的实际值，仅输出达到规则条件的玩法
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r22(RuleParameterVo vo);


    /**
     * "一段时间（参数1天）内，在位于[参数2，参数3)的联赛级别的盈利赛事（数量记为x）中，盈利金额除以“赛事联赛模板中用户单场限额”位于[参数4，参数5)内的赛事数量/x>=参数6
     * 判断结果（1/0）；输出值：参数1-(参数2-1)级联赛，参数4<=盈利金额/用户单场限额<参数5的赛事占全部盈利赛事的比例 = 实际比例值
     * 例如：1-5级联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%
     *            6级以下联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%    （参数3不填）
     *            5级以上联赛，盈利金额/用户单场限额<50%的盈利赛事占全部盈利赛事的比例 = 90%   （参数2不填）"
     *
     * @param vo
     * @return
     */
    public RuleResult<String> r23(RuleParameterVo vo);

    /**
     *"一段时间（参数1天）内，用户成功投注的赛事数量位于[参数2,参数3)区间；
     * 判断结果（1/0）；输出值：实际场次数量
     * 注：若参数4为空，输出的实际值中不包含“赛种-”"
     * @param vo
     * @return
     */
    public RuleResult<String> r24(RuleParameterVo vo);

}
