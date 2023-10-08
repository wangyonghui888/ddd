package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import groovy.lang.Singleton;
import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :     赛果 & 进球大小
 * @date: 2020-03-30 16:31
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:13;2:13")
public class TotalAnd1X2 extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "13";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "1AndUnder,XAndUnder,2AndUnder,1AndOver,XAndOver,2AndOver";

    public TotalAnd1X2() {
        super(CATE_CODE, templateName);
    }

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 大小值 P. 当前玩法不需要处理 精确的等于 0, 因此考虑使用double ***/
        double p = getMarketValue(bean);

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();

        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投主胜&大 ***/
            case "1AndUnder":
                status = calculate1AndUnder(homeScore, awayScore, p);
                break;
            /*** 投平局&大  ***/
            case "XAndUnder":
                status = calculateXAndUnder(homeScore, awayScore, p);
                break;
            /*** 投客胜&大  ***/
            case "2AndUnder":
                status = calculate2AndUnder(homeScore, awayScore, p);
                break;
            /*** 投主胜&小 ***/
            case "1AndOver":
                status = calculate1AndOver(homeScore, awayScore, p);
                break;
            /*** 投平局&小  ***/
            case "XAndOver":
                status = calculateXAndOver(homeScore, awayScore, p);
                break;
            /*** 投客胜&小  ***/
            case "2AndOver":
                status = calculate2AndOver(homeScore, awayScore, p);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }

    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投主胜&大.  赢：M-N>0 && M+N-P>0; 输：M-N<=0||M+N-P<=0
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculate1AndOver(int m, int n, double p) {
        if (m > n && m + n > p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投平局&大.   赢：M-N==0 && M+N-P>0;  输：M-N!=0||M+N-P<=0
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculateXAndOver(int m, int n, double p) {
        if (m == n && m + n > p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投客胜&大.  赢：N-M>0 && M+N-P>0.  输：N-M<=0||M+N-P<=0.
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculate2AndOver(int m, int n, double p) {
        if (n > m && m + n > p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投主胜&小. 赢：M-N>0 && M+N-P<0;输：M-N<=0||M+N-P>=0
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculate1AndUnder(int m, int n, double p) {
        if (m > n && m + n < p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投平局&小. 赢：M-N==0 && M+N-P<0. 输：M-N!=0||M+N-P>=0
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculateXAndUnder(int m, int n, double p) {
        if (m == n && m + n < p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * @param m
     * @param n
     * @param p
     * @return int
     * @description 投客胜&小. 赢：N-M>0 && M+N-P<0; 输：N-M<=0||M+N-P>=0
     * @author dorich
     * @date 2020/3/30 16:41
     **/
    private int calculate2AndUnder(int m, int n, double p) {
        if (n > m && m + n < p) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
