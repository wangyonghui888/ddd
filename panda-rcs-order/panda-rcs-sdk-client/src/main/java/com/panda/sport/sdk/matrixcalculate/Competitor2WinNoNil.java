package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :    客队 零失球获胜
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:80;2:80")
public class Competitor2WinNoNil extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "80";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Yes,No";

    public Competitor2WinNoNil() {
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
        Integer status = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 Yes  ***/
            case "Yes":
                status = calculateYes(m, n);
                break;
            /*** 投 No***/
            case "No":
                status = calculateNo(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }

    /**
     * 计算客场赢球不失分的投注项矩阵
     *
     * @param m
     * @param n
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/20 17:17
     **/
    private int calculateYes(int m, int n) {

        /** 输：M>0||N==0**/
        if (m > 0 || 0 == n) {
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /***  赢：M==0&&N>0 ***/
            return OrderSettleStatus.USER_WIN.getValue();
        }
    }


    /**
     * 计算客场 非 赢球不失分 的投注项矩阵
     *
     * @param m
     * @param n
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateNo(int m, int n) {
        /*** 输：M==0 && N>0  ***/
        if (0 == m && 0 < n) {
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /***  赢：M>0||N==0 ***/
            return OrderSettleStatus.USER_WIN.getValue();
        }
    }

}
