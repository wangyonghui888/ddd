package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  双重机会, 两队都进球. 根据对其他玩法的分析发现：矩阵的横轴下表对应 主队进球数; 矩阵的纵轴下表对应 客队进球。 也就是说m:n中m 对应第一层循环;n 对应第二层循环.
 * matrixStatusArray: 保存的是用户的输赢状态; matrixArray: 保存系统的输赢金额;输钱为负值;赢球为正值。
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:107;2:107")
public class DoubleChanceBothTeamScore extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "107";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "1XAndYes,12AndYes,X2AndYes,1XAndNo,12AndNo,X2AndNo";

    public DoubleChanceBothTeamScore() {
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
            /*** 投主胜/平局 & 是(双方都进球)  ***/
            case "1XAndYes":
                status = calculateAwayNotWin(homeScore, awayScore, true);
                break;
            /*** 投主胜/客胜 & 是(双方都进球)  ***/
            case "12AndYes":
                status = calculateHomeAwayWin(homeScore, awayScore, true);
                break;
            /*** 投客胜/平局 & 是(双方都进球)  ***/
            case "X2AndYes":
                status = calculateHomeNotWin(homeScore, awayScore, true);
                break;
            /*** 投主胜/平局 & 否(某个队不会进球)  ***/
            case "1XAndNo":
                status = calculateAwayNotWin(homeScore, awayScore, false);
                break;
            /*** 投主胜/客胜 &是(某个队不会进球)  ***/
            case "12AndNo":
                status = calculateHomeAwayWin(homeScore, awayScore, false);
                break;
            /*** 投客胜/平局  &是(某个队不会进球)  ***/
            case "X2AndNo":
                status = calculateHomeNotWin(homeScore, awayScore, false);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }

    /**
     * 计算主胜客胜场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 9:52
     **/
    private int calculateHomeAwayWin(int m, int n, Boolean bothScore) {
        /*** 两队都进球的投注项。  投主胜/客胜 & 是 ***/
        if (bothScore) {
            /*** 赢：M-N!=0&&M>0&&N>0 ***/
            boolean whetherUserWin = (m - n != 0) && (m > 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N==0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 两队都进球的投注项。 投主胜/客胜 & 否 ***/
        else {
            /*** 赢：M-N!=0&&(M==0||N==0) ***/
            boolean whetherUserWin = (m - n != 0) && (0 == n || 0 == m);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N==0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }

    /**
     * 计算客胜或者平局场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 10:52
     **/
    private int calculateHomeNotWin(int m, int n, Boolean bothScore) {
        /*** 两队都进球的投注项。  投客胜/平局&是 ***/
        if (bothScore) {
            /*** 赢：M-N<=0&&M>0 ***/
            boolean whetherUserWin = (m - n <= 0) && (m > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N==0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 至少一个队没有进球的投注项。 投客胜/平局 & 否 ***/
        else {
            /*** 赢：M-N<=0&&M==0 ***/
            boolean whetherUserWin = (m - n <= 0) && (m == 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N<0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }

    /**
     * 计算主胜或者平局场景下的投注项订单
     *
     * @param bothScore 是否双方都进球
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/17 11:52
     **/
    private int calculateAwayNotWin(int m, int n, Boolean bothScore) {


        /*** 两队都进球的投注项。  投主胜/平局&是***/
        if (bothScore) {
            /*** 赢：M-N>=0&&N>0 ***/
            boolean whetherUserWin = (m - n >= 0) && (n > 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N<0||M==0||N==0 ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
        /*** 至少一个队没有进球的投注项。 投主胜/平局&否 ***/
        else {
            /*** 赢：M-N>=0&&N==0 ***/
            boolean whetherUserWin = (m - n >= 0) && (n == 0);
            if (whetherUserWin) {
                return OrderSettleStatus.USER_WIN.getValue();
            } else {
                /*** 输：M-N<0||(M>0&&N>0) ***/
                return OrderSettleStatus.USER_LOSE.getValue();
            }
        }
    }
}
