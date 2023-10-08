package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  赛果(上半场赛果) & 上半场两队都进球
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
@MatrixCacl(configs = "1:101,105,106;2:101,105,106")
public class Match1X2BothTeamScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "101,105,106";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1AndYes,XAndYes,2AndYes,1AndNo,XAndNo,2AndNo";

    public Match1X2BothTeamScore() {
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
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投主胜&是  ***/
            case "1AndYes":
                status = calculateHomeWin(m, n, true);
                break;
            /*** 投平局&是 ***/
            case "XAndYes":
                status = calculateDraw(m, n, true);
                break;
            /*** 投客胜&是   ***/
            case "2AndYes":
                status = calculateAwayWin(m, n, true);
                break;
            /*** 投主胜&否   ***/
            case "1AndNo":
                status = calculateHomeWin(m, n, false);
                break;
            /*** 投平局&否 ***/
            case "XAndNo":
                status = calculateDraw(m, n, false);
                break;
            /*** 投客胜&否  ***/
            case "2AndNo":
                status = calculateAwayWin(m, n, false);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
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
    private int calculateHomeWin(int m, int n, Boolean bothScore) {

        /*** 投主胜&是  ***/
        if (bothScore) {
            /*** 赢： M1-N1>0&&N1>0 ***/
            boolean whetherUserWin = (m - n > 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M1-N1<=0||M1==0||N1==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 投主胜&否 ***/
        else {
            /*** 赢：M1-N1>0&&N1==0 ***/
            boolean whetherUserWin = (m - n > 0) && (0 == n);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M1-N1<=0||(M1>0&&N1>0) ***/
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
    private int calculateDraw(int m, int n, Boolean bothScore) {


        /*** 投平局&是 ***/
        if (bothScore) {
            /*** 赢：M1-N1==0 && N1>0 ***/
            boolean whetherUserWin = (m - n == 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N==0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 投平局&否 ***/
        else {
            /*** 赢：M1-N1==0 && N1==0 ***/
            boolean whetherUserWin = (m - n == 0) && (n == 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N<0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }

    /**
     * 计算主胜或者平局场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 11:52
     **/
    private int calculateAwayWin(int m, int n, Boolean bothScore) {

        /*** 投客胜&是 ***/
        if (bothScore) {
            /*** 赢：M1-N1<0&&M1>0 ***/
            boolean whetherUserWin = (m - n < 0) && (m > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M1-N1>=0||M1==0||N1==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 投客胜&否  ***/
        else {
            /*** 赢：M1-N1<0&&M1==0 ***/
            boolean whetherUserWin = (m - n < 0) && (m == 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M1-N1>=0||(M1>0&&N1>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }
}
