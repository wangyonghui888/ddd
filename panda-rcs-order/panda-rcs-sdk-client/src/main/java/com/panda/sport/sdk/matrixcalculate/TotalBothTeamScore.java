package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  进球大小 & 两队都进球
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
@MatrixCacl(configs = "1:102;2:102")
public class TotalBothTeamScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "102";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "OverAndYes,OverAndNo,UnderAndYes,UnderAndNo";

    public TotalBothTeamScore() {
        super(CATE_CODE, templateName);
    }


    /**
     * 根据当前主客比分和 投注项编码,计算当前输赢结果
     *
     * @param m            主队比分
     * @param n            客队比分
     * @param templateCode 投注项id
     * @return java.lang.Integer
     * @description
     * @author dorich
     * @date 2020/3/20 16:02
     **/
    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        return null;
    }

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 大小值 P. 当前玩法不需要处理 精确的等于 0, 因此考虑使用double ***/
        double p = getMarketValue(bean);

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投大&是  ***/
            case "OverAndYes":
                status = calculateOver(homeScore, awayScore, p, true);
                break;
            /*** 投小&是 ***/
            case "UnderAndYes":
                status = calculateUnder(homeScore, awayScore, p, true);
                break;
            /*** 投大&否   ***/
            case "OverAndNo":
                status = calculateOver(homeScore, awayScore, p, false);
                break;
            /*** 投小&否  ***/
            case "UnderAndNo":
                status = calculateUnder(homeScore, awayScore, p, false);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }

    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateOver(int m, int n, double p, Boolean bothScore) {
        /*** 投大&是 ***/
        if (bothScore) {
            /*** 赢：M+N-P>0&&M>0&&N>0 ***/
            boolean whetherUserWin = (m + n - p > 0) && (m > 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M+N-P<=0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 投大&否 ***/
        else {
            /*** 赢：M+N-P>0&&(M==0||N==0) ***/
            boolean whetherUserWin = (m + n - p > 0) && (0 == m || 0 == n);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M+N-P<=0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }

    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateUnder(int m, int n, double p, Boolean bothScore) {
        /*** 投小&是 ***/
        if (bothScore) {
            /*** 赢：M+N-P<0&&M>0&&N>0 ***/
            boolean whetherUserWin = (m + n - p < 0) && (m > 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /***输：M+N-P>=0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 投小&否 ***/
        else {
            /*** 赢：M+N-P<0&&(M==0||N==0) ***/
            boolean whetherUserWin = (m + n - p < 0) && (n == 0 || m == 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M+N-P>=0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }

}
