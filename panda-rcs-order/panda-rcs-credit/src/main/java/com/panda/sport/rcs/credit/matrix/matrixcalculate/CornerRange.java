package com.panda.sport.rcs.credit.matrix.matrixcalculate;/*
package com.panda.sport.sdk.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

*/
/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :  角球总数区间
 * @date: 2020-04-04 14:26
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 *//*

@Service
@MatrixCacl(configs = "1:117")
public class CornerRange extends AbstractMatrix {

    */
/**
     * 玩法编码
     **//*

    public static final String CATE_CODE = "117";

    */
/**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **//*

    private static final String templateName = "0-8,9-11,12+";

    public CornerRange() {
        super(CATE_CODE, templateName);
    }

    */
/**
     * 获取当前比分计算的结果
     * 1 输 2:输半  3 :赢  4：赢半  5:平
     *
     * @param homeScore 主队比分
     * @param awayScore 客队比分
     * @param bean
     * @return
     *//*

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {
        Integer status = 0;
        String selectionItem = bean.getItemBean().getPlayOptions();
        */
/*** 处理不同的投注项 ***//*

        switch (selectionItem) {
            */
/*** 0-8 ***//*

            case "0-8":
                status = calculateUnder8(homeScore, awayScore);
                break;
            */
/*** 9-11  ***//*

            case "9-11":
                status = calculateUnder11(homeScore, awayScore);
                break;
            */
/*** 12+  ***//*

            case "12+":
                status = calculateUnderOver11(homeScore, awayScore);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    */
/**
     * 计算客场赢球不失分的投注项矩阵
     *
     * @param m
     * @param n
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/20 17:17
     **//*

    private int calculateUnder8(int m, int n) {
        int total = m + n;
        if (0 <= total && total <= 8) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    */
/**
     * 计算客场赢球不失分的投注项矩阵
     *
     * @param m
     * @param n
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/20 17:17
     **//*

    private int calculateUnder11(int m, int n) {
        int total = m + n;
        if (9 <= total && total <= 11) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    */
/**
     * 计算客场赢球不失分的投注项矩阵
     *
     * @param m
     * @param n
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/20 17:17
     **//*

    private int calculateUnderOver11(int m, int n) { 
        int total = m + n;
        if (12 <= total) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
*/
