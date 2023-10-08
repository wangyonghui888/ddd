package com.panda.sport.sdk.matrixcalculate;

import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * 点球大战-净胜分
 */

@Singleton
@MatrixCacl(configs = "1:239")
public class PenaltyScoreScope extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "239";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符.
     * 投注项跟产品确认如下:
     * 前面的1，2是主客队.  后面的1，2，3+，6+是分值.  1And2 代表 主队胜2分. 2And1, 2And2, 2And3+  分别代表客队 胜 1分 ，2分，33分及以上.
     * X代表足球的其他选项. Other代表篮球的其他选项
     **/
    private final String templateName = "0-4,5,6,7,8,9,10+";

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
        int status = 0;
        String selectionItem = bean.getItemBean().getPlayOptions();
        status = calculate(homeScore, awayScore, selectionItem);
        return status;
    }


    private int calculate(int m, int n, String templateCode) {
        if (templateCode.equals("0-4")) {
            if (m + n >= 0 && m + n <= 4) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        if (templateCode.equals("10+")) {
            if (m + n >= 10) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }

        if (m + n == Integer.valueOf(templateCode)) {
            return OrderSettleStatus.USER_WIN.getValue();
        }
        return OrderSettleStatus.USER_LOSE.getValue();
    }

}
