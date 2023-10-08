package com.panda.sport.rcs.credit.matrix;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.data.rcs.dto.OrderBean;
import com.panda.sport.data.rcs.dto.OrderItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.service.impl
 * @Description :  订单参数效验
 * @Date: 2019-12-10 21:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class ParamValidateService {

    /**
     * @return com.panda.sport.data.rcs.dto.ExtendBean
     * @Description 根据orderItem 获取扩展 orderBean
     * @Param [bean, item]
     * @Author max
     * @Date 11:15 2019/12/11
     **/
    public ExtendBean buildExtendBean(OrderBean bean, OrderItem item) {
        ExtendBean extend = new ExtendBean();
        extend.setSeriesType(bean.getSeriesType());
        extend.setItemId(item.getBetNo());
        extend.setOrderId(item.getOrderNo());
        extend.setBusId(String.valueOf(bean.getTenantId()));
        extend.setHandicap(item.getMarketValue());
        extend.setCurrentScore(item.getScoreBenchmark());
        //item  1 ：早盘 ，2： 滚球盘， 3： 冠军盘
        extend.setIsScroll(String.valueOf(item.getMatchType()).equals("2") ? "1" : "0");
        //冠军盘标识
        extend.setIsChampion(item.getMatchType().intValue() == 3 ? 1 : 0);
        extend.setMatchId(String.valueOf(item.getMatchId()));
        extend.setPlayId(item.getPlayId() + "");
        extend.setSelectId(String.valueOf(item.getPlayOptionsId()));
        extend.setSportId(String.valueOf(item.getSportId()));
        extend.setUserId(String.valueOf(item.getUid()));
        extend.setOdds(String.valueOf(item.getHandleAfterOddsValue()));
        extend.setMarketId(item.getMarketId().toString());
        //阶段  冠军玩法走mts 可以不设置此字段
        if (item.getMatchType() != 3) {
            //extend.setPlayType(rcsPaidConfigService.getPlayProcess(String.valueOf(item.getSportId()), String.valueOf(item.getPlayId())));
        }
        if (item.getBetAmount() != null) {
            extend.setOrderMoney(item.getBetAmount());
            extend.setCurrentMaxPaid(new BigDecimal(item.getBetAmount()).multiply((new BigDecimal(extend.getOdds()).subtract(new BigDecimal(1)))).longValue());
        } else {
            extend.setOrderMoney(0L);
            extend.setCurrentMaxPaid(0L);
        }
        extend.setTournamentLevel(item.getTurnamentLevel());
        extend.setTournamentId(item.getTournamentId());
        extend.setDateExpect(item.getDateExpect());
        extend.setDataSourceCode(item.getDataSourceCode());

        extend.setItemBean(item);

        if (StringUtils.isBlank(extend.getHandicap())) {
            extend.setHandicap("0");
        }
        if (StringUtils.isBlank(extend.getCurrentScore())) {
            extend.setCurrentScore("0:0");
        }
        return extend;
    }
}
