package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   两队都进球
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
@MatrixCacl(configs = "1:12,24,76;2:12,24")
public class BothTeamsScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "12,24,76";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "Yes,No";

    public BothTeamsScore() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {

        Integer lostStatus = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 是  ***/
            case "Yes":
                lostStatus = calculateYes(m, n);
                break;
            /*** 投 否 ***/
            case "No":
                lostStatus = calculateNo(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return lostStatus;
    }


    /**
     * 两队都进球的结算
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return java.lang.Integer
     * @description 赢：M1>0&&N1>0;  输：M1==0||N1==0
     * @author dorich
     * @date 2020/3/20 15:46
     **/
    private Integer calculateYes(int m, int n) {
        /*** 赢:  M1>0&&N1>0;  ***/
        if (m > 0 && n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输: M1==0||N1==0***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 非 两队都进球的结算
     *
     * @param m 主队比分,
     * @param n 客队比分
     * @return void
     * @description 赢：M1==0 || N1==0; 输：M1>0 && N1>0
     * @author dorich
     * @date 2020/3/17 11:52
     **/
    private Integer calculateNo(int m, int n) {
        /*** 赢：M1==0||N1==0;  ***/
        if (m == 0 || n == 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1>0&&N1>0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

//    /**
//     * 获取当前比分计算的结果
//     * 1 输 2:输半  3 :赢  4：赢半  5:平
//     *
//     * @param homeScore 主队比分
//     * @param awayScore 客队比分
//     * @param bean
//     * @return
//     */
//    @Override
//    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {
//        String selectionItem = bean.getItemBean().getPlayOptions();
//        return getSettleResult(homeScore, awayScore, selectionItem);
//    }
}
