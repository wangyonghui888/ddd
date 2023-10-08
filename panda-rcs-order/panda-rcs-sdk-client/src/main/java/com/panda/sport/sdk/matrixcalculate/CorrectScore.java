package com.panda.sport.sdk.matrixcalculate;


import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.sdk.service.impl.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :   全场精确比分(波胆玩法),
 * @Date: 2020-03-18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
@MatrixCacl(configs = "1:7;2:7")
public class CorrectScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "7";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符。其他使用 other 标识
     **/
    private static String templateName = "0:0,1:0,2:0,3:0,4:0,0:1,1:1,2:1,3:1,4:1,0:2,1:2,2:2,3:2,4:2,0:3,1:3,2:3,3:3,4:3,0:4,1:4,2:4,3:4,4:4";
    private static String other = "Other";

    public CorrectScore() {
        super(CATE_CODE, templateName, other);
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
//        String selectionItem = bean.getItemBean().getPlayOptions();
    	String selectionItem = bean.getItemBean().getPlayOptions();
        /*** 处理不同的投注项,支持的精确比分 ***/
        if(selectionItem.contains(other)){
            return calculateOtherSelection(homeScore, awayScore);
        }else{
            return calculateAccurateScore(homeScore, awayScore, selectionItem);
        }
    }

    /**
     * 计算精确比分投注项中 指定了比分的投注项
     *
     * @return void
     * @description 投常规比分; 赢：M1==X&&N1==Y; 输：M1!=X||N1!=Y
     * @author dorich
     * @date 2020/3/17 9:52
     **/
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


    /**
     * 精确比分玩法下, other选项的判断
     *
     * @return void
     * @description 投其他（这里的P表示常规投注项的最大比分项的值）;赢：M1>P||N1>P;输：M1<=P&&N1<=P;
     * @author dorich
     * @date 2020/3/17 11:52
     **/
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
