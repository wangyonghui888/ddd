package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;
import com.panda.sport.sdk.vo.OddsFieldsTemplateVo;
import lombok.Data;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   客队获胜退款
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */



@Singleton
@MatrixCacl(configs = "1:91;2:91")
public class Competitor2NoBet extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "91";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1,X";

    public Competitor2NoBet() {
        super(CATE_CODE,templateName);
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
            /*** 投 客队  ***/
            case "1":
                status = calculateAwayWin(m, n);
                break;
            /*** 投 平 ***/
            case "X":
                status = calculateDraw(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }

    /**
     * 计算客场大小球 大的矩阵
     *
     * @param m            主队比分
     * @param n            客队比分
     * @return void
     * @description 赢：N1-P>0;输：N1-P<0;走水：N1-P==0
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateAwayWin(int m, int n) {
        /***  赢：M-N>0 ***/
        if (m - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (m - n == 0) {
            /** 输：M-N==0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /*** 走水：M-N<0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }


    /**
     * 计算主场大小球 小的矩阵
     *
     * @param m            主队比分
     * @param n            客队比分
     * @return void
     * @description 赢：N1-P<0;输：N1-P>0;走水：N1-P==0
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateDraw(int m, int n) {
        /***  赢：M-N==0 ***/
        if (m - n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (m - n > 0) {
            /***  输：M-N>0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /***  走水：M-N<0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }

}
