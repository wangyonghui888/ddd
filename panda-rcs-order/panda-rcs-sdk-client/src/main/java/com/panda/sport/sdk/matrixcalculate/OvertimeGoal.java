package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :    加时赛是否进球
 * @Date: 2021-4-14 11:54:27
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:234")
public class OvertimeGoal extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "234";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Yes,No";

    public OvertimeGoal() {
        super(CATE_CODE, templateName);
    }


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
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" + templateName);
        }
        return status;
    }


    private Integer calculateYes(int m, int n) {
        /***  赢：N==0&&M>0 ***/
        if (m + n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：N>0||M==0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    private Integer calculateNo(int m, int n) {

        if (m + n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：N==0&&M>0  ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
}
