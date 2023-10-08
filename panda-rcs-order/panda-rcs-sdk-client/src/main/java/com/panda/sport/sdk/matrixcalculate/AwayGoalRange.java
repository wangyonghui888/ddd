package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :   {客队}角球总数区间
 */

@Singleton
@MatrixCacl(configs = "1:227")
public class AwayGoalRange extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "227";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "";

    public AwayGoalRange() {
        super(CATE_CODE);
    }

    /**
     *
     **/
    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        int status = 0;
        /*** 处理不同的投注项 ***/

        if (templateCode.contains("+")) {
            status = calculateOther(m, n, templateCode);
        } else {
            status = calculate(m, n, templateCode);
        }

        return status;
    }

    /**
     *
     **/
    private int calculate(int m, int n, String templateCode) {
        String[] pList = templateCode.split("-");
        int total = n;
        if (Integer.parseInt(pList[0]) <= total && total <= Integer.parseInt(pList[1])) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算 X+ 的投注项 的矩阵
     *
     * @param
     * @return void
     * @description 赢：6 <= M+N ;  输：6 > M+N
     * @author dorich
     * @date 2020/3/18 16:35
     **/
    private int calculateOther(int m, int n, String templateCode) {
        int p = Integer.parseInt(templateCode.replace("+", ""));
        int total = n;
        if (p <= total) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /**  输：6 > M+N **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
