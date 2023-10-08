package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  客队零失球(全场,上半场)
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Service
@MatrixCacl(configs = "1:79,100;2:79,100")
public class Competitor2CleanSheet extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "79,100";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Yes,No";

    public Competitor2CleanSheet() {
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
            /*** 投大&是  ***/
            case "Yes":
                status = calculateCleanSheet(m, n);
                break;
            /*** 投小&是 ***/
            case "No":
                status = calculateNotClean(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @param m 主队比分
     * @param n 客队比分
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateCleanSheet(int m, int n) {
        /*** 赢：M1==0 ***/
        boolean whetherUserWin = (m == 0);
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1!=0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @param m 主队比分
     * @param n 客队比分
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateNotClean(int m, int n) {
        /*** 赢：M1>0 ***/
        boolean whetherUserWin = (m > 0);
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1==0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
