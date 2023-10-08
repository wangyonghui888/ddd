package com.panda.sport.rcs.credit.matrix.matrixcalculate;


import org.springframework.stereotype.Service;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * @author :  lithan
 * @Description :    下半场{客队}零失球
 * @Date: 2021-4-14 11:54:27
 * --------  ---------  --------------------------
 */


@Service
@MatrixCacl(configs = "1:99")
public class Competitor2Win extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "99";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符
     **/
    private static final String templateName = "Yes,No";

    public Competitor2Win() {
        super(CATE_CODE, templateName);
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
        Integer status = 0;
        /*** 处理不同的投注项 ***/
        switch (templateCode) {
            /*** 投 Yes  ***/
            case "Yes":
                status = calculateYes(m, n);
                break;
            /*** 投 No***/
            case "No":
                status = calculateNo(m, n);
                break;
            default:
                throw new RcsServiceException(-1, "不支持的投注项:" + templateCode + ".当前玩法支持的投注项列表:" +templateName);
        }
        return status;
    }


    /**
     * "赢：N2==0
     * 输：N2!=0"
     **/
    private Integer calculateYes(int m, int n) {
        /***  赢：N==0&&M>0 ***/
        if (0 == m) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /** 输：N>0||M==0 **/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }


    /**
     * "赢：N2!=0
     * 输：N2==0"
     **/
    private Integer calculateNo(int m, int n) {

        /***  赢：N>0||M==0 ***/
        if (m > 0) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            /*** 输：N==0&&M>0  ***/
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }
}
