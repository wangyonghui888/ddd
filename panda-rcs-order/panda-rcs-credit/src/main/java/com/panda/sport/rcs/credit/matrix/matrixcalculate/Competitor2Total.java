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
 * @Description :   客队 进球大小盘
 * @Date: 2020-03-17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */


@Service
@MatrixCacl(configs = "1:11,97,98,116,124;2:11,97,98")
public class Competitor2Total extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "11,97,98,116,124";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Over,Under";

    public Competitor2Total() {
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
        Integer status = 0;
        /*** 获取投注项编码 ***/
        String selectionItem = bean.getItemBean().getPlayOptions();
        /*** 大小值 P ***/
        double p = getMarketValue(bean);
        /*** 处理不同的投注项 ***/
        switch (selectionItem) {
            /*** Under 小***/
            case "Under":
                status = calculateUnder(homeScore, awayScore, p);
                break;
            /*** Over  大***/
            case "Over":
                status = calculateOver(homeScore, awayScore, p);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + selectionItem + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
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
        return null;
    }


    /**
     * 计算主场大小球 在m:n情况下  的用户输赢情况
     *Over大
     * @param m
     * @param n
     * @param p
     * @return int
     * @description
     * @author dorich
     * @date 2020/3/20 17:12
     **/
    private int calculateOver(int m, int n, double p) {
        /*** 赢：N1-P>0; ***/
        if (n - p > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (n - p < 0) {
            /** 输: N1-P<0; **/
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /*** 走水：N1-P==0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }


    /**
     * 计算主场大小球 在m:n情况下  的用户输赢情况
     *Over小
     * @param m
     * @param n
     * @param p
     * @return int
     * @description
     * @author dorich
     * @date 2020/3/20 17:12
     **/
    private int calculateUnder(int m, int n, double p) {
        /*** 系统输钱数额(大于0),等于下注金额*赔率 ***/

        /***  赢：N1-P<0; ***/
        if (n - p < 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else if (n - p > 0) {
            /***  输：N1-P>0; ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        } else {
            /***  走水: N1-P==0 ***/
            return OrderSettleStatus.USER_BACK.getValue();
        }
    }
}
