package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   上/下半场两队都进球
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:108;2:12,24,108")//,76
public class BothTeamsScoreResult extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "108";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "YesYes,YesNo,NoNo,NoYes";

    public BothTeamsScoreResult() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {

        Integer lostStatus = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            case "YesYes":
                lostStatus = calculateYesYes(m, n);
                break;
            case "YesNo":
                lostStatus = calculateOther(m, n);
                break;
            case "NoNo":
                lostStatus = calculateOther(m, n);
                break;
            case "NoYes":
                lostStatus = calculateOther(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return lostStatus;
    }


    /**
     **/
    private Integer calculateYesYes(int m, int n) {
        /*** 赢:  M1>0&&N1>0;  ***/
        if (m > 0 && n > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输: M1==0||N1==0***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     **/
    private Integer calculateOther(int m, int n) {
            return OrderSettleStatus.USER_WIN.getValue();
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
