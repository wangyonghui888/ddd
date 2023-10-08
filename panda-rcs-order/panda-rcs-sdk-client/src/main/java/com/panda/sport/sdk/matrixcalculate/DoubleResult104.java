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
@MatrixCacl(configs = "1:104")
public class DoubleResult104 extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "104";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     * 这个地方选项很多
     **/
    private static final String templateName = "11,1X,12,X1,XX,X2,21,2X,22";

    public DoubleResult104() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        int status = 0;
        switch (templateCode) {
            case "11":
                status= calculate11(m, n);
                break;
            case "1X":
                status= calculate1X(m, n);
                break;
            case "12":
                status= calculate12(m, n);
                break;
            case "X1":
                status= calculateX1(m, n);
                break;
            case "XX":
                status= calculateXX(m, n);
                break;
            case "X2":
                status= calculateX2(m, n);
                break;

            case "21":
                status= calculate21(m, n);
                break;
            case "2X":
                status= calculate2X(m, n);
                break;
            case "22":
                status= calculate22(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        /**
         * 产品文档  投什么都一样的算法
         */
       // status = calculate(m, n);
        return status;
    }

    private int calculate11(int m, int n) {
        if (m - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculate1X(int m, int n) {
        if (m - n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculate12(int m, int n) {
        if (m - n < 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    private int calculateX1(int m, int n) {
        if (m - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculateXX(int m, int n) {
        if (m - n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculateX2(int m, int n) {
        if (m - n < 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    private int calculate21(int m, int n) {
        if (m - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculate2X(int m, int n) {
        if (m - n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    private int calculate22(int m, int n) {
        if (m - n < 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
