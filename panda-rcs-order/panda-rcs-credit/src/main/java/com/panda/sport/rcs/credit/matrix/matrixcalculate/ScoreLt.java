package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * 上/下半场进球数均大于{X.5}
 */

@Service
@MatrixCacl(configs = "1:110")//"1:2,18,114,122"
public class ScoreLt extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "110";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Yes,No";

    public ScoreLt() {
        super(CATE_CODE, templateName);
    }

    /**
     *
     **/
    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {

        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();

        /*** 投注项盘口值 ***/
        String marketValue = getMarketValueString(bean);
        int status = 0;
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** 投大于  ***/
            case "Yes":
                status = calculateYes(homeScore, awayScore, marketValue);
                break;
            /*** 投小于  ***/
            case "No":
                status = calculateNo(homeScore, awayScore, marketValue);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" + templateName);
        }
        return status;
    }

    /**
     * "只要全场进球数大于2P，都算用户赢
     * 赢：M+N-2P>0
     * 输：M+N-2P<=0"
     *
     * @param m
     * @param n
     * @param marketValue
     * @return
     */
    private int calculateYes(double m, double n, String marketValue) {
        double value = Double.valueOf(marketValue);
        value *= 2;
        if (m + n < value) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    private int calculateNo(double m, double n, String marketValue) {
        double value = Double.valueOf(marketValue);
        if (m + n > value) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


}
