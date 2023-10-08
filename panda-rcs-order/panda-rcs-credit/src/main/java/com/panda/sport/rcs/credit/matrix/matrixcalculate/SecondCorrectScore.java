package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import com.panda.sport.rcs.credit.matrix.MatrixCacl;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import org.springframework.stereotype.Service;

/**
 * 下半场比分
 */

@Service
@MatrixCacl(configs = "1:74,342")
public class SecondCorrectScore extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    private static final String CATE_CODE = "74,342";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符。其他使用 other 标识
     **/
    private static String templateName = "0:0,0:1,0:2,1:0,1:1,1:2,2:0,2:1,2:2";

    protected static String other = "Other";

    public SecondCorrectScore() {
        super(CATE_CODE, templateName, other);
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
        /*** 处理不同的投注项,支持的精确比分 ***/
        if(templateCode.contains(other)){
//            return calculateOtherSelection(m, n,templateCode);
            return OrderSettleStatus.USER_WIN.getValue();
        }else{
            return calculateAccurateScore(m, n, templateCode);
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
        String[] pList = templateCode.split(":");
        /*** 比分相等  ***/
        if (m == Integer.parseInt(pList[0]) && n == Integer.parseInt(pList[1])) {
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
//    protected int calculateOtherSelection(int m, int n,String templateCode) {
//        String currentScore = "" + m + ":" + n;
//        if (!templateName.contains(currentScore)) {
//            return OrderSettleStatus.USER_WIN.getValue();
//        } else {
//            /*** 下注的other投注项, 但是却被支持的精确比分包含,则认为投注失败 ***/
//            return OrderSettleStatus.USER_LOSE.getValue();
//        }
//    }
}
