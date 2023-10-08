package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.profit.utils.ProfitRoleUtil;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   平局退款
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Singleton
@MatrixCacl(configs = "1:5,43")
public class DrawNoBet extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "5,43";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1,2";

    public DrawNoBet() {
        super(CATE_CODE, templateName);
    }

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {
        String selectionItem = bean.getItemBean().getPlayOptions();
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投 主队胜 ***/
            case "1":
                status = calculateHomeSelection(homeScore, awayScore);
                break;
            /*** 投 客队  ***/
            case "2":
                status = calculateAwaySelection(homeScore, awayScore);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * 主队
     * @param m
     * @param n
     * @return
     */
    private int calculateHomeSelection(int m, int n) {
        if (m  - n  > 0 ) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if(m - n < 0) {
            /*** 实际比分与不在other之外的比分中 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }else{
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }

    /**
     * 客队
     * @param m
     * @param n
     * @return
     */
    private int calculateAwaySelection(int m, int n) {
        if (m  - n  < 0 ) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if(m - n > 0) {
            /*** 实际比分与不在other之外的比分中 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }else{
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }

}
