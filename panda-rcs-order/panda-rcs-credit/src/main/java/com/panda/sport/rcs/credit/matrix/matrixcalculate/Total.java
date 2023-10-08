package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :  TODO
 * @date: 2020-03-30 10:35
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:2,18,26,114,122,127,134")//"1:2,18,114,122"
public class Total extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "2,18,26,114,122,122,127,134";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Over,Under";

    public Total() {
        super(CATE_CODE, templateName);
    }

    /**
     * 根据当前主客比分和 投注项编码,计算当前输赢结果
     *
     * @param homeScore 主队比分
     * @param awayScore 客队比分
     * @param bean      订单
     * @return java.lang.Integer  1 输 2:输半  3 :赢  4：赢半  5:平
     * @description
     * @author dorich
     * @date 2020/3/20 16:02
     **/
    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();

        /*** 投注项盘口值 ***/
        String marketValue = getMarketValueString(bean);
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投大于  ***/
            case "Over":
                status = calculateTotalSelection(homeScore, awayScore, marketValue, true);
                break;
            /*** 投小于  ***/
            case "Under":
                status = calculateTotalSelection(homeScore, awayScore, marketValue, false);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }
 
    public int calculateSelect(int m, int n, String marketValue, boolean over) {

        /*** 大小值 P. 当前玩法不需要处理 精确的等于 0, 因此考虑使用double ***/
        double p = getMarketValue(marketValue);

        /*** Over 投注项 ***/
        if (over) {

            if (m + n - p > 0) {
                /***赢：M+N-P>0***/
                return OrderSettleStatus.USER_WIN.getValue();
            } else if (m + n - p < 0) {
                /***输：M+N-P<0***/
                return OrderSettleStatus.USER_LOSE.getValue();
            } else {
                /*** 走水：M+N-P==0***/
                return OrderSettleStatus.USER_BACK.getValue();
            }
        } else {

            if (m + n - p < 0) {
                /***  赢：M+N-P<0***/
                return OrderSettleStatus.USER_WIN.getValue();
            } else if (m + n - p > 0) {
                /***    输：M+N-P>0***/
                return OrderSettleStatus.USER_LOSE.getValue();
            } else {
                /*** 走水：M+N-P==0***/
                return OrderSettleStatus.USER_BACK.getValue();
            }
        }
    }

}
