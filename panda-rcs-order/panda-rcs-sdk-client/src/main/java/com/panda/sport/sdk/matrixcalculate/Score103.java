package com.panda.sport.sdk.matrixcalculate;

import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;
import org.apache.commons.lang3.StringUtils;

/**
 * @author :  lithan
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :  剩余时间获胜  上半场剩余时间获胜
 * @date: 2020-12-4 12:06:15
 */


@Singleton
@MatrixCacl(configs = "1:103")//"1:1,3,17,27,29,69,111,119"
public class Score103 extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "103";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "";

    public Score103() {
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
        int status = calculateHomeWin(homeScore, awayScore, selectionItem);
        return status;
    }


    /**
     * @param m 主队比分  1:4
     * @param n 客队比分
     * @return
     */
    private int calculateHomeWin(double m, double n, String selectionItem) {
        String arr[] = selectionItem.split(" ");
        String score = arr[1];
        if (score.contains("+")) {
            score = score.replace("+", "");
            int scoreNum = Integer.valueOf(score);
            if (m >= scoreNum || n >= scoreNum) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }else {
            String scoreArr[] = arr[0].split(":");
            int home = Integer.valueOf(scoreArr[0]);
            int away = Integer.valueOf(scoreArr[1]);
            if (m == home && n == away) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                return OrderSettleStatus.USER_LOSE.getValue();
            }
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
