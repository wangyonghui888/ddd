package com.panda.sport.sdk.matrixcalculate;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.category.ScoreBenchmark;
import com.panda.sport.sdk.service.impl.matrix.MatrixCaclApi;
import com.panda.sport.sdk.vo.OddsFieldsTemplateVo;

import io.netty.util.internal.StringUtil;
import lombok.Data;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  TODO
 * @Date: 2019-10-05 11:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public abstract class AbstractMatrix implements MatrixCaclApi {

    /**
     * @Description 比分差
     * @Param
     * @Author toney
     * @Date 18:59 2020/3/3
     * @return
     **/
    protected ScoreBenchmark scoreBenchmark;

    /**
     * 玩法矩阵类型; 0 矩阵计算; 1 穷举计算。
     **/
    protected Integer matrixType = 0;

    /**
     * 玩法是否需要基准分. * 0 不需要; * 1 需要基准分
     **/
    protected Integer benchmark = 0;

    /**
     * 盘口值分隔符
     **/
    protected static String MARKET_ODDS_VALUE_SPLIT = "/";

    /**
     * 保存,当前玩法的投注项,key是:code. OddsFieldsTemplateVo 的 code  和 name 是同一个值(就是投注项的内容). 支持的投注项 都必须在该map中保存
     */
    protected Map<String, OddsFieldsTemplateVo> oddsFieldsTemplateVoMap = new HashMap<>();

    /**
     * 获取当前比分计算的结果
     * 1 输 2:输半  3 :赢  4：赢半  5:平
     *
     * @param homeScore 主队比分
     * @param awayScore 客队比分
     * @param bean
     * @return
     */
    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {
        String selectionItem = bean.getItemBean().getPlayOptions();
        return getSettleResult(homeScore, awayScore, selectionItem);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        return null;
    }


    /**
     * 玩法编码
     **/
    protected String cateCode;

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    protected String templateName;

    /**
     * 初始化
     */
    public AbstractMatrix() {
    }

    /**
     * @return
     * @Description 初始化
     * @Param [cateCode, templateName]
     * @Author toney
     * @Date 14:54 2020/3/22
     **/
    public AbstractMatrix(String cateCode, String templateName) {
        this.cateCode = cateCode;
        this.templateName = templateName;

        this.init();
    }

    /**
     * 初始化
     * @param cateCode
     */
    public AbstractMatrix(String cateCode){
        this.cateCode = cateCode;
    }

    /**
     * 初始化
     *
     * @param cateCode
     * @param templateName
     */
    public AbstractMatrix(String cateCode, String templateName, String otherCode) {
        this.cateCode = cateCode;
        this.templateName = templateName;

        init2(otherCode);
    }

    /**
     * @return void
     * @Description 初始化
     * @Param []
     * @Author toney
     * @Date 14:05 2020/3/22
     **/
    protected void init() {
        String[] cateIds = cateCode.split(",");
        Integer cateId = Integer.parseInt(cateIds[0]);
        String[] templateNames = templateName.split(",");
        int i = 0;
        for (String name : templateNames) {
            i = i + 1;
            OddsFieldsTemplateVo oddsFieldsTemplateVo = new OddsFieldsTemplateVo();
            oddsFieldsTemplateVo.setId(cateId + i);
            oddsFieldsTemplateVo.setCid(cateId);
            oddsFieldsTemplateVo.setName(name);
            oddsFieldsTemplateVo.setCode(name);
            oddsFieldsTemplateVoMap.put(oddsFieldsTemplateVo.getName(), oddsFieldsTemplateVo);
        }
    }

    public void init2(String otherCode) {
        String[] cateIds = cateCode.split(",");

        Integer cateId = Integer.parseInt(cateIds[0]);
        int i = 0;
        String[] templateNames = templateName.split(",");
        for (String name : templateNames) {
            i = i + 1;
            OddsFieldsTemplateVo oddsFieldsTemplateVo = new OddsFieldsTemplateVo();
            oddsFieldsTemplateVo.setId(cateId + i);
            oddsFieldsTemplateVo.setCid(cateId);
            oddsFieldsTemplateVo.setName(name);
            oddsFieldsTemplateVo.setCode(name);
            oddsFieldsTemplateVoMap.put(oddsFieldsTemplateVo.getName(), oddsFieldsTemplateVo);
        }

        /*** 把 other投注项写入到map中 ***/
        OddsFieldsTemplateVo oddsFieldsTemplateVo = new OddsFieldsTemplateVo();
        i++;
        String name = otherCode;//"other";
        oddsFieldsTemplateVo.setId(cateId + i);
        oddsFieldsTemplateVo.setCid(cateId);
        oddsFieldsTemplateVo.setName(name);
        oddsFieldsTemplateVo.setCode(name);
        oddsFieldsTemplateVoMap.put(oddsFieldsTemplateVo.getName(), oddsFieldsTemplateVo);
    }


    /**
     * 玩法共用的取注单盘口值的方法
     *
     * @param bean
     * @return double
     * @description
     * @author dorich
     * @date 2020/4/2 15:21
     **/
    protected double getMarketValue(ExtendBean bean) {
        String marketValue = getMarketValueString(bean);
        double p = 0;
        if (!StringUtils.isEmpty(marketValue) && NumberUtils.isNumber(marketValue)) {
            p = Double.parseDouble(marketValue);
        }
        return p;
    }

    protected double getMarketValue(String marketValue) {
        double p = 0;
        if (!StringUtils.isEmpty(marketValue) && NumberUtils.isNumber(marketValue)) {
            p = Double.parseDouble(marketValue);
        }
        return p;
    }

    /**
     * 玩法共用的取注单盘口值的(适配那种可能是1/1.5的盘口值)
     *
     * @param bean
     * @return double
     * @description
     * @author dorich
     * @date 2020/4/2 15:21
     **/
    protected String getMarketValueString(ExtendBean bean) {
        /*** 如果是滚球,则取 getMarketValueNew***/
        String value ="";
        if(bean ==null || bean.getItemBean()==null || StringUtil.isNullOrEmpty(bean.getItemBean().getMarketValue())){
            value = "";
        }else {
            value = bean.getItemBean().getMarketValue();
        }
        if ("1".equals(bean.getIsScroll())) {
            if(StringUtils.isNotEmpty(bean.getItemBean().getMarketValueNew())) {
                value = bean.getItemBean().getMarketValueNew();
            }
        }
        if (null == value) {
            value = "";
        }
        return value;
    }
    @Override
    public int calculateSelect(int m, int n, String marketValue, boolean home) {
        return 0;
    }


    /**
     * 该方法抽象出来用于计算存在1/4盘的玩法
     *
     * @param m           主队比分
     * @param n           客队比分
     * @param marketValue 盘口值
     * @param upper       true: Over投注项; false:Under投注项
     * @return int
     * @description
     * @author dorich
     * @date 2020/4/5 13:18
     **/
    protected int calculateTotalSelection(int m, int n, String marketValue, boolean upper) {
        /*** 保存遍历结果 ***/
        Map<String, Integer> times = new HashMap<>();
        times.put("1", 0);
        times.put("3", 0);
        times.put("5", 0);
        String[] marketOddsValues = marketValue.split(MARKET_ODDS_VALUE_SPLIT);

        /*** 遍历每个盘口 ***/
        for (String marketOddsValueString : marketOddsValues) {
            String paraMarketValue = marketOddsValueString;
            /*** 如果盘口值为负值 ***/
            if ("-".equals(marketValue.substring(0, 1))) {
                /*** 当前非1/4盘口, 则需要将当前不带-号的字符串加上-号***/
                if(!"-".equals(paraMarketValue.substring(0, 1))) {
                    paraMarketValue = "-" + paraMarketValue;
                }
            }
            /*** 计算当前  盘口的输赢结果.并将结构转换为字符串,然后作为map的key.  ***/
            String result = "" + calculateSelect(m, n, paraMarketValue, upper);

            /*** 之前已经有值,则+1,这种情况下全赢或者全输场景 的值应该是2 ***/
            times.put(result, times.get(result) + 1); 
        }

        /*** 全赢 ***/
        if (marketOddsValues.length == times.get("3")) {
            return OrderSettleStatus.USER_WIN.getValue();
        }
        /***  全输 ***/
        if (marketOddsValues.length == times.get("1")) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        /*** 1个1/2盘赢,一个走水 才是半赢 ***/
        if (1 == times.get("3") && 1 == times.get("5")) {
            return OrderSettleStatus.USER_HALF_WIN.getValue();
        }
        /*** 1个1/2盘输,一个走水 才是半输 ***/
        if (1 == times.get("1") && 1 == times.get("5")) {
            return OrderSettleStatus.USER_HALF_LOSE.getValue();
        }
        /*** 走水 ***/
        if (marketOddsValues.length == times.get("5")) {
            return OrderSettleStatus.USER_BACK.getValue();
        }
        return OrderSettleStatus.USER_BACK.getValue();
    }
}
