package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :   {主队}角球总数区间
 */

@Singleton
@MatrixCacl(configs = "1:226")
public class HomeGoalRange extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "226";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "";

    public HomeGoalRange() {
        super(CATE_CODE);
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

        if (templateCode.contains("+")) {
            status = calculateOther(m, n, templateCode);
        } else {
            status = calculate(m, n, templateCode);
        }

        return status;
    }

    /**
     * 计算 X-Y 的投注项 的矩阵
     *
     * @param
     * @return void
     * @description 赢：0 <=M+N<= 1;  输：M+N>1 ||M+N<0
     * @author dorich
     * @date 2020/3/18 9:52
     **/
    private int calculate(int m, int n, String templateCode) {
        String[] pList = templateCode.split("-");
        int total =   m;
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
        int total = m;
        if (p <= total) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /**  输：6 > M+N **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
