package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :   两队都进球 223玩法
 * @Date: 2020-09-11
 */

@Service
@MatrixCacl(configs = "1:223")
public class WhoWillScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "223";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "Both,Only1,Only2,None";

    public WhoWillScore() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {

        Integer lostStatus = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 Both ***/
            case "Both":
                lostStatus = calculateBoth(m, n);
                break;
            /*** 投 Only1  ***/
            case "Only1":
                lostStatus = calculateOnly1(m, n);
                break;
            /*** 投 Only2  ***/
            case "Only2":
                lostStatus = calculateOnly2(m, n);
                break;
            /*** 投 None  ***/
            case "None":
                lostStatus = calculateNone(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return lostStatus;
    }
    /**
     * 两队都进球 both
     * @param m 主队比分,
     * @param n 客队比分
     **/
    private Integer calculateBoth(int m, int n) {
        if (m > 0 && n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 只有主队进球 only1
     * @param m 主队比分,
     * @param n 客队比分
     **/
    private Integer calculateOnly1(int m, int n) {
        if (m > 0 && n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
    /**
     * 只有客队进球 only2
     * @param m 主队比分,
     * @param n 客队比分
     **/
    private Integer calculateOnly2(int m, int n) {
        if (m == 0 & n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 都不进球 none
     * @param m 主队比分,
     * @param n 客队比分
     **/
    private Integer calculateNone(int m, int n) {
        if (m == 0 & n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
