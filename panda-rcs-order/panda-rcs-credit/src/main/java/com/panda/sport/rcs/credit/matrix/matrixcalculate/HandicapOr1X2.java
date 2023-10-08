package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :  让球赛果矩阵计算结果     半场比分 M1:N1; 让球值 P (全场半场赛果中P 为 0)  胜平负的
 * @date: 2020-03-18 16:55
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Service
@MatrixCacl(configs = "1:1,3,17,69,25,71,111,119,126,129,132")//"1:1,3,17,27,29,69,111,119"
public class HandicapOr1X2 extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "1,3,17,69,111,119,25,111,119,126,129,132";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "1,X,2";

    public HandicapOr1X2() {
        super(CATE_CODE, templateName);
    }

    /**
     * 获取当前比分计算的结果
     * 1 输 2:输半  3 :赢  4：赢半  5:平
     *
     * @param homeScore 主队比分
     * @param awayScore 客队比分
     * @param bean
     * @return
     */
    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();

        /*** 大小值 P ***/
        double p = getMarketValue(bean);

        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            case "1":
                status = calculateHomeWin(homeScore, awayScore, p);
                break;
            case "X":
                status = calculateDraw(homeScore, awayScore, p);
                break;
            case "2":
                status = calculateAwayWin(homeScore, awayScore, p);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * 计算主队赢球的矩阵
     *
     * @param
     * @return void
     * @description 赢：M1+P-N1>0; 输：M1+P-N1<=0
     * @author dorich
     * @date 2020/3/18 17:17
     **/
    private int calculateHomeWin(double m, double n, double p) {

        /***  赢：M1+P-N1>0; ***/
        if (m + p - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：M1+P-N1<=0  **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算平局的矩阵
     *
     * @param
     * @return void
     * @description 赢：M1+P-N1==0, 输：M1+P-N1!=0
     * @author dorich
     * @date 2020/3/18 17:43
     **/
    private int calculateDraw(double m, double n, double p) {
        /***  赢：M1+P-N1==0 ***/
        if (m + p - n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：M1+P-N1!=0  **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * 计算客队赢球的矩阵
     *
     * @param
     * @return void
     * @description 赢：N1+P-M1>0; 输：N1+P-M1<=0
     * @author dorich
     * @date 2020/3/18 17:17
     **/
    private int calculateAwayWin(double m, double n, double p) {

        /***  赢：N1+P-M1>0; 投注客队时, P可能是负值  ***/
        if (n - p - m > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：N1+P-M1<=0   **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
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
        return null;
    }
}
