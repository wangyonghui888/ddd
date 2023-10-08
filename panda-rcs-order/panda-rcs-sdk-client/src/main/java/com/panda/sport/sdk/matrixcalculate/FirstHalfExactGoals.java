package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   上半场精确进球
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
@MatrixCacl(configs = "1:23")
public class FirstHalfExactGoals extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "23";


    /**
     * @Description 其它
     * @Param
     * @Author toney
     * @Date 15:02 2020/3/22
     * @return
     **/
    private static String other = "+";

    /**
     * @return
     * @Description 初始化
     * @Param []
     * @Author toney
     * @Date 15:41 2020/3/22
     **/
    public FirstHalfExactGoals() {
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
        if(templateCode.contains(other)){
            return  calculateOtherSelection(m, n, templateCode);
        }else {
            return calculateAccurateGoal(m, n, templateCode);
        }
    }

    /**
     * 精确比分玩法下, other选项的判断
     *
     * @param m
     * @param n
     * @param templateCode
     * @return int
     * @description  
     * @author dorich
     * @date 2020/3/20 17:45
     **/
    private int calculateAccurateGoal(int m, int n, String templateCode) {
        Integer p = Integer.parseInt(templateCode);

        /*** 比分相等  ***/
        if (m + n -  p == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 实际比分与下注比分不相同 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 精确比分玩法下, other选项的判断
     *
     * @param m
     * @param n
     * @return int
     * @description
     * @author dorich
     * @date 2020/3/20 17:45
     **/
    private int calculateOtherSelection(int m, int n,String templateCode) {
        Integer p = Integer.parseInt(templateCode.replace(other,""));
        /*** 实际比分在other之外的比分中  ***/
        if (m  + n - p >= 0 ) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 实际比分与不在other之外的比分中 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
}
