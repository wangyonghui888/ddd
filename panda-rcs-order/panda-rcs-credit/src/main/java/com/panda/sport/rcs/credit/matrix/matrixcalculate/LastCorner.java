package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 *
 * @Description :  最后一个角球 最后进球队伍
 */


@Service
@MatrixCacl(configs = "1:112,149")
public class LastCorner extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "112,149";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1,None,2";

    public LastCorner() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        int status = 0;
        switch (templateCode) {

            case "1":
                status = calculateHome(m, n);
                break;

            case "2":
                status = calculateAway(m, n);
                break;

            case "None":
                status = calculateNotWin(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" + templateName);
        }
        return status;
    }


    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @return int 用户输赢情况
     * @description 投主队或客队  赢：M1-N1!=0  输：M1-N1==0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateHome(int m, int n) {
        if (m > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @return int 用户输赢情况
     * @description 投客队或平局: 赢：N1-M1>=0;  输：N1-M1<0
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateAway(int m, int n) {

        /*** 赢：N1-M1>=0;  ***/
        if (n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /***  输：N1-M1<0  ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * "只要主队有角球，都算用户赢
     * 赢：E>0    输：E<=0"
     **/
    private int calculateNotWin(int m, int n) {
        if (m == 0 && n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }

    }
}
