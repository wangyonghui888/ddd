package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  dorich
 * @project Name :  rcs-parent
 * @package Name :  com.panda.sport.sdk.matrixcalculate
 * @description :   {主队}准确进球数
 * @date: 2020-03-30 13:33
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Service
@MatrixCacl(configs = "1:8,21;2:8,21")
public class Competitor1ExactGoals extends AbstractMatrix {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "8,21";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "0,1,2,3+";

    public Competitor1ExactGoals() {
        super(CATE_CODE, templateName);
    }


    @Override
    public Integer getSettleResult(int m, int n, String templateCode) {
        Integer lostStatus = 0;
        if(templateCode.contains("+")){
            lostStatus = calculateOther(m, n , templateCode);
        }else{
            lostStatus = calculateCorrect(m, n, templateCode);
        }


        return lostStatus;
    }


    /**
     *
     * @description            赢：M1-P==0, 输：M1-P!=0
     * @param m                主队进球
     * @param n                客队进球
     * @return java.lang.Integer
     * @author dorich
     * @date 2020/3/30 14:00
     **/
    private Integer calculateOther(int m, int n,String templateCode) {
        Integer p = Integer.parseInt(templateCode.replace("+",""));
        /*** 赢：M1-P==0 ***/
        if (m - p >= 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-P!=0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     *
     * @description            赢：M1-P==0, 输：M1-P!=0
     * @param m                主队进球
     * @param n                客队进球
     * @param templateCode     用户投注项
     *
     * @return java.lang.Integer
     * @author dorich
     * @date 2020/3/30 13:58
     **/
    private Integer calculateCorrect(int m, int n, String templateCode) {
        Integer selectItem = Integer.parseInt(templateCode);
        /*** 赢：M1-P==0 ***/
        if (selectItem == m) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：M1-P!=0 ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
