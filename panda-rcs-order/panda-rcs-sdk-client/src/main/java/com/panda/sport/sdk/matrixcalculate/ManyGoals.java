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
@MatrixCacl(configs = "1:28,30")
public class ManyGoals extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "28,30";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "1,None,2";

    public ManyGoals() {
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

        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            case "1":
                status = calculateHomeWin(homeScore, awayScore, x);
                break;
            case "None":
                status = calculateDraw(homeScore, awayScore);
                break;
            case "2":
                status = calculateAwayWin(homeScore, awayScore, x);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" + templateName);
        }
        return status;
    }


    /**
     * "只要主队有进球且全场达到X球就假设用户赢
     * 赢：M>0&&M+N>=X   输：M==0||M+N<X"
     */
    private int calculateHomeWin(double m, double n, int x) {
        if (m == 0 || m + n < x) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (m > 0 && m + n >= x) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * "投无进球
     * 赢：M+N==0
     * 输：M>0||N>0"
     **/
    private int calculateDraw(double m, double n) {
        if (m + n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * "只要客队有进球且全场达到X球就假设用户赢
     * 赢：N>0&&M+N>=X   输：N==0||M+N<X"
     **/
    private int calculateAwayWin(double m, double n, int x) {
        if (n == 0 || m + n < x) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (n > 0 && m + n >= x) {
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
