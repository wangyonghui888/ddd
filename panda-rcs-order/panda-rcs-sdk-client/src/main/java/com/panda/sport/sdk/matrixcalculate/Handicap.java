package com.panda.sport.sdk.matrixcalculate;

import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.sdk.matrixcalculate
 * @Description :  让球
 * @Date: 2020-04-12 13:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
@MatrixCacl(configs = "1:4,19,113,121,128,130,143")//"1:4,19,113,121"
public class Handicap extends AbstractMatrix{
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "4,19,113,121,113,121,128,130,143";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1,2";

    public Handicap() {
        super(CATE_CODE, templateName);
    }

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();
        /*** 投注项盘口值 ***/
        String marketValue = getMarketValueString(bean);
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投 主队胜 ***/
            case "1":
                status = calculateTotalSelection(homeScore, awayScore, marketValue, true);
                break;
            /*** 投 客队  ***/
            case "2":
                status = calculateTotalSelection(homeScore, awayScore, marketValue, false);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     *
     * @description
     * @param m                  主队
     * @param n                  客队
     * @param marketValue   盘口值
     * @param home          true:主场;false:客场
     * @return int
     * @author dorich
     * @date 2020/4/5 13:50
     **/
    @Override
    public int calculateSelect(int m, int n, String marketValue, boolean home) {

        /*** 大小值 P. 当前玩法不需要处理 精确的等于 0, 因此考虑使用double ***/
        /*** p 在 4 19 玩法中才会有。其他玩法下不会有值,而默认值为0,因此不影响 ***/
        double p = getMarketValue(marketValue);

        /*** Over 投注项 ***/
        if (home) {

            /***  赢：(M-X)+P-(N-Y)>0  ***/
            if (m + p  - n> 0) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else if (m + p - n < 0 ) {
                /***  输：(M-X)+P-(N-Y)<0;  ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            } else {
                /*** 走水：(M-X)+P-(N-Y)==0 ***/
                return OrderSettleStatus.USER_BACK.getValue();
            }
        } else {
            /***  用户赢：n - p > m  ***/
            if (n - p > m) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else if ( n - p < m ) {
                /*** 用户输：n - p < m ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            } else {
                /** 走水：(N-Y)+P-(M-X)==0   **/
                return OrderSettleStatus.USER_BACK.getValue();
            }
        }
    }
}
