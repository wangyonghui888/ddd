package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 *
 * @author :  lithan
 * @description :  下半场准确进球数
 * @date: 2021-4-11 13:40:30
 */


@Service
@MatrixCacl(configs = "1:73")
public class SecondHalfGoals extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "73";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "0,1,2+";

    public SecondHalfGoals() {
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

        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            case "0":
                status = calculate0(homeScore, awayScore);
                break;
            case "1":
                status = calculate1(homeScore, awayScore);
                break;
            case "2+":
                status = calculate2(homeScore, awayScore);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" + templateName);
        }
        return status;
    }


    private int calculate0(double m, double n) {
        if (m + n != 0) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (m + n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    private int calculate1(double m, double n) {
        if (m + n != 1) {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
        if (m + n == 1) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    private int calculate2(double m, double n) {
        if (m + n >= 2) {
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
