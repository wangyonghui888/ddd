package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   客队 进球单/双
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:92;2:92")
public class Competitor2OddEven extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "92";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Odd,Even";

    public Competitor2OddEven() {
        super(CATE_CODE, templateName);
    }

    /**
     * 根据当前主客比分和 投注项编码,计算当前输赢结果
     * c
     *
     * @param templateCode 投注项id
     * @return java.lang.Integer
     * @description
     * @author dorich
     * @date 2020/3/20 16:02
     **/
    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        Integer status = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投奇数  ***/
            case "Odd":
                status = calculateOdd(m, n);
                break;
            /*** 投偶数 ***/
            case "Even":
                status = calculateEven(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * 计算客场对奇数球的矩阵
     *
     * @param m 主队比分
     * @param n 客队比分
     * @return void
     * @description 赢：N%2，余数=1; 输：N%2，余数=0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateOdd(int m, int n) {
        /*** 系统输钱数额(大于0),等于下注金额*赔率 ***/

        /*** 赢：N%2，余数=1 ***/
        if ((n % 2) == 1) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：N%2，余数=0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算客场对偶数的矩阵
     *
     * @param m 主队比分
     * @param n 客队比分
     * @return void
     * @description 赢：N%2，余数=0,输：N/2，余数=1
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateEven(int m, int n) {
        /***  赢：N%2，余数=0 ***/
        if ((n % 2) == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /***  输：N/2，余数=1 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
