package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   全场(上半场) 进球单/双
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:15,42,75,118,229,240")//"1:15,42,118"
public class OddEven extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "15,42,118,75,118";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Odd,Even";

    public OddEven() {
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
        int status = 0;
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
     * 计算主场对奇数球的矩阵
     *
     * @return void
     * @description 赢：(M1+N1)% 2;输：(M1+N1)% 2，余数=0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateOdd(int m, int n) {

        /*** 赢：(M1+N1)% 2 ***/
        if (((m + n) % 2) == 1) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /**  输：(M1+N1)% 2，余数=0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算主场对偶数的矩阵
     *
     * @return void
     * @description 赢：(M1+N1)%2，余数=0; 输：(M1+N1)%2，余数=1
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateEven(int m, int n) {

        /*** 赢：(M1+N1)%2，余数=0 ***/
        if (((m + n) % 2) == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /***  输：(M1+N1)%2，余数=1 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
