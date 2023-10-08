package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   总进球数区间
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:68,117,228;2:68")//"1:68,117;2:68"
public class GoalRange extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "68,117,228";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "0-1,2-3,4-5,6+";

    public GoalRange() {
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
            status=calculateOther(m, n, templateCode);
        } else {
            status=calculate(m, n, templateCode);
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
        /*** 赢：0 <=M+N<= 1; ***/
        int total = n + m;
        if (Integer.parseInt(pList[0]) <= total && total <= Integer.parseInt(pList[1])) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /**  输：M+N>1 ||M+N<0 **/
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
    private int calculateOther(int m, int n,String templateCode) {
        /*** 系统输钱数额(大于0),等于下注金额*赔率 ***/
        int p = Integer.parseInt(templateCode.replace("+",""));
        /***  赢：6 <= M+N ***/
        int total = n + m;
        if (p <= total) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /**  输：6 > M+N **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
