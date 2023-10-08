package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   客队获胜退款
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:77;2:77")
public class Competitor1NoBet extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "77";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "2,X";

    public Competitor1NoBet() {
        super(CATE_CODE, templateName);
    }

    /**
     * 获取结果
     * @param m
     * @param n
     * @param templateCode
     * @return
     */
    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        Integer lostStatus = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 客队  ***/
            case "2":
                lostStatus = calculateAwayWin(m, n);
                break;
            /*** 投 平 ***/
            case "X":
                lostStatus = calculateDraw(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return lostStatus;
    }


    /**
     * 计算主场大小球 大的矩阵
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return void
     * @description 赢：N1-P>0;输：N1-P<0;走水：N1-P==0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateAwayWin(int m, int n) {
        /***  赢：M-N<0 ***/
        if (m < n) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (m > n) {
            /*** 走水：M-N>0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        } else {
            /** 输：M-N==0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算主场大小球 小的矩阵
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return void
     * @description 赢：N1-P<0;输：N1-P>0;走水：N1-P==0
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateDraw(int m, int n) {
        /***  赢：M-N==0 ***/
        if (m == n) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (m - n < 0) {
            /***  输：M-N<0  ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /***  走水：M-N>0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }
}
