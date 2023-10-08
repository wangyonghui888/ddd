package com.panda.sport.sdk.matrixcalculate;

import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * 矩阵  28
 *
 * @author :  lithan
 * @description :  第{X}个进球
 * @date: 2021-4-11 13:40:30
 */


@Singleton
@MatrixCacl(configs = "1:148")
public class GoalScorerMan extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "148";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "";

    public GoalScorerMan() {
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
        //盘口值
        int x = Integer.valueOf(bean.getItemBean().getMarketValue());

        status = calculateHomeWin(homeScore, awayScore, x);
        return status;
    }


    private int calculateHomeWin(double m, double n, int x) {
        if (m + n >= x) {
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
