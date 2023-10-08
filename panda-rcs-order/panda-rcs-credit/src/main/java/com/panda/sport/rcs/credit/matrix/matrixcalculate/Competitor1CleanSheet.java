package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  主队零失球(全场,上半场)
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Service
@MatrixCacl(configs = "1:81,90;2:81,90")
public class Competitor1CleanSheet extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "81,90";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static String templateName = "Yes,No";

    public Competitor1CleanSheet() {
        super(CATE_CODE, templateName);
    }

    /**
     * 根据投注项计算订单的矩阵
     *
     * @param m            主队比分,
     * @param n            客队比分
     * @param templateCode 配置文件中的玩法编码
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 9:38
     **/

    public Integer getSettleResult(int m, int n, String templateCode) {
        Integer lostStatus = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 是  ***/
            case "Yes":
                lostStatus = calculateCleanSheet(m, n);
                break;
            /*** 投 否 ***/
            case "No":
                lostStatus = calculateNotClean(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return lostStatus;
    }

    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private Integer calculateCleanSheet(int m, int n) {
        /*** 系统输钱数额(大于0),等于下注金额*赔率 ***/

        /*** 赢：N==0 ***/
        if (n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：N!=0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private Integer calculateNotClean(int m, int n) {
        /*** 赢：N1!=0 ***/
        if (n != 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /***  输：N1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
