package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  双重机会,
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:6,70,72;2:6,70,72")
public class DoubleChance extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "6,70";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1X,12,X2";

    public DoubleChance() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投主胜/平局   ***/
            case "1X":
                status= calculateAwayNotWin(m, n);
                break;
            /*** 投主胜/客胜    ***/
            case "12":
                status= calculateHomeAwayWin(m, n);
                break;
            /*** 投客胜/平局   ***/
            case "X2":
                status= calculateHomeNotWin(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @return int 用户输赢情况
     * @description 投主队或客队  赢：M1-N1!=0  输：M1-N1==0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateHomeAwayWin(int m, int n) {
        /*** 系统输钱数额(大于0),等于下注金额*赔率 ***/

        /***赢：M1-N1!=0   ***/
        boolean whetherUserWin = (m - n != 0);
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @return int 用户输赢情况
     * @description 投客队或平局: 赢：N1-M1>=0;  输：N1-M1<0
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateHomeNotWin(int m, int n) {

        /*** 赢：N1-M1>=0;  ***/
        if (n >= m) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /***  输：N1-M1<0  ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算主胜或者平局场景下的投注项订单
     *
     * @return int 用户输赢情况
     * @description 投主队或平局 赢：M1-N1>=0; 输：M1-N1<0
     * @author dorich
     * @date 2020/3/17 11:52
     **/
    private int calculateAwayNotWin(int m, int n) {


        /*** 赢：M1-N1>=0;  ***/
        boolean whetherUserWin = (m - n >= 0);
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1<0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }

    }
}
