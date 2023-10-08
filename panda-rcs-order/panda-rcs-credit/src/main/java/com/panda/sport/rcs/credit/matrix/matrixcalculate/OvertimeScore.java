package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @Description :   加时赛-正确比分
 */

@Service
@MatrixCacl(configs = "1:236,241")
public class OvertimeScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "236,241";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符。其他使用 other 标识
     **/
    private static String templateName = "";//0:0,1:0,2:0,3:0,0:1,1:1,2:1,0:2,1:2,0:3 不固定
    private static String other = "Other";

    public OvertimeScore() {
        super(CATE_CODE, templateName, other);
    }

    @Override
    public Integer getScoreResult(Integer homeScore, Integer awayScore, ExtendBean bean) {
//        String selectionItem = bean.getItemBean().getPlayOptions();
        String selectionItem = bean.getItemBean().getPlayOptions();
        /*** 处理不同的投注项,支持的精确比分 ***/
        if (selectionItem.contains(other)) {
            return calculateOtherSelection(homeScore, awayScore);
        } else {
            return calculateAccurateScore(homeScore, awayScore, selectionItem);
        }
    }


    protected int calculateAccurateScore(int m, int n, String templateCode) {
        String currentScore = "" + m + ":" + n;
        /*** 比分相等  ***/
        if (templateCode.equals(currentScore)) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 实际比分与下注比分不相同 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    protected int calculateOtherSelection(int m, int n) {
        String currentScore = "" + m + ":" + n;
        if (!templateName.contains(currentScore)) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 下注的other投注项, 但是却被支持的精确比分包含,则认为投注失败 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
