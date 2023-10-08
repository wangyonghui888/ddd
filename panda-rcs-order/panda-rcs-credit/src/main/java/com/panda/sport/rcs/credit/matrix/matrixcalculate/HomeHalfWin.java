package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :   {主队}上/下半场全胜
 * @Date: 2021-4-14 11:39:51
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:83")
public class HomeHalfWin extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "83";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符. 其他选项不保存,直接使用
     **/
    private static String templateName = "Yes,No";

    public HomeHalfWin() {
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
     *"只要主队赢，就算用户赢
     * 赢：M>N
     * 输：M<=N"
     **/
    private Integer calculateYes(int m, int n) {
        /*** 赢:  M1>0&&N1>0;  ***/
        if (m > n) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输: M1==0||N1==0***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     *只要投注这一项，都算用户赢
     **/
    private Integer calculateNo(int m, int n) {
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
