package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import groovy.lang.Singleton;
import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;
import org.apache.commons.lang3.StringUtils;

/**
 * @author :  lithan
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :  剩余时间获胜  上半场剩余时间获胜
 * @date: 2020-12-4 12:06:15
 */


@Service
@MatrixCacl(configs = "1:27,29")//"1:1,3,17,27,29,69,111,119"
public class WinBehind extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "27,29";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "1,X,2";

    public WinBehind() {
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

        int status = 0;

        //基准分
        String scoreBenchmark = bean.getItemBean().getScoreBenchmark();
        if (StringUtils.isBlank(scoreBenchmark)) {
            scoreBenchmark = "0:0";
        }
        String arr[] = scoreBenchmark.split(":");
        double beforeM = Double.valueOf(arr[0]);
        double beforeN = Double.valueOf(arr[1]);

        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            case "1":
                status = calculateHomeWin(homeScore, awayScore, beforeM, beforeN);
                break;
            case "X":
                status = calculateDraw(homeScore, awayScore, beforeM, beforeN);
                break;
            case "2":
                status = calculateAwayWin(homeScore, awayScore, beforeM, beforeN);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * @param m 主队比分  1:4
     * @param n 客队比分
     * @return
     */
    private int calculateHomeWin(double m, double n ,double beforeM,double beforeN) {
        m = m - beforeM;
        n = n - beforeN;
        if (m < 0 || n < 0) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (m - n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 计算平局的矩阵
     *
     * @param
     * @return void
     * @author dorich
     * @date 2020/3/18 17:43
     **/
    private int calculateDraw(double m, double n, double beforeM, double beforeN) {
        m = m - beforeM;
        n = n - beforeN;
        if (m < 0 || n < 0) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (m - n == 0) {
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
     * @author dorich
     * @date 2020/3/18 17:17
     **/
    private int calculateAwayWin(double m, double n, double beforeM, double beforeN) {
        m = m - beforeM;
        n = n - beforeN;
        if (m < 0 || n < 0) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (n - m > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
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
