package com.panda.sport.rcs.credit.matrix.matrixcalculate;

import org.springframework.stereotype.Service;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.enums.OrderSettleStatus;
import com.panda.sport.rcs.credit.matrix.MatrixCacl;

/**
 * 点球大战-净胜分
 */

@Service
@MatrixCacl(configs = "1:238")
public class PenaltyMargin extends AbstractMatrix {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "238";

    /**
     * 投注项名称全部按顺序写在该字符串中,使用 逗号作为分隔符.
     * 投注项跟产品确认如下:
     * 前面的1，2是主客队.  后面的1，2，3+，6+是分值.  1And2 代表 主队胜2分. 2And1, 2And2, 2And3+  分别代表客队 胜 1分 ，2分，33分及以上.
     * X代表足球的其他选项. Other代表篮球的其他选项
     **/
    private final String templateName = "1And1,1And2,1And3+,2And1,2And2,2And3+";

    /**
     * 其它
     */
    private String other= "+";
    /**
     * 主队
     */
    private String home="1";
    /**
     * 平
     */
    private String draw="X";
    /**
     * 客队
     */
    private String away= "2";
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
        int status = 0;
        String selectionItem = bean.getItemBean().getPlayOptions();
        /**   平局***/
        if(selectionItem.equals(draw)){
            status = calculateDraw(homeScore, awayScore);
            return status;
        }

        selectionItem = selectionItem.substring(4);
        String flag = bean.getItemBean().getPlayOptions().substring(0,1);
        /** 主队**/
        if(home.equals(flag)){
            if(selectionItem.contains(other)){
                status = calculateHomeOtherBasketball(homeScore, awayScore,selectionItem);
            }else {
                status = calculateHome(homeScore, awayScore, selectionItem);
            }
        }else{
            /** 客队**/
            if(selectionItem.contains(other)){
                status = calculateAwayOtherBasketball(homeScore, awayScore,selectionItem);
            }else {
                status = calculateAway(homeScore, awayScore, selectionItem);
            }
        }


        return status;
    }


    /**
     * 主队胜1球的投注项 矩阵计算
     *
     * @return void
     * @description 赢：M-N==P;  输：M-N!=P;
     * @author dorich
     * @date 2020/3/19 11:30
     **/
    private int calculateHome(int m, int n, String templateCode) {
        Integer p = Integer.parseInt(templateCode);
        /*** 赢：M-N==P ***/
        boolean whetherUserWin = (m - n == p);
        /*** 更新矩阵当前的数据 ***/
        /*** 返回输赢情况 ***/
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 客队
     * @param m
     * @param n
     * @param templateCode
     * @return
     */
    private int calculateAway(int m, int n, String templateCode) {
        Integer p = Integer.parseInt(templateCode);
        /*** 赢：M-N==P ***/
        boolean whetherUserWin = (n - m == p);
        /*** 更新矩阵当前的数据 ***/
        /*** 返回输赢情况 ***/
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }



    /**
     * 计算篮球其他比分的场景
     *
     * @return void
     * @description
     * @author dorich
     * @date 2020/3/19 13:03
     **/
    private int calculateHomeOtherBasketball(int m, int n,String templateCode) {
        Integer p =Integer.parseInt(templateCode.replace("+",""));
        /*** 赢：M-N==P ***/
        boolean whetherUserWin = (m - n >= p);
        /*** 更新矩阵当前的数据 ***/
        /*** 返回输赢情况 ***/
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * 客队更多
     * @param m
     * @param n
     * @param templateCode
     * @return
     */
    private int calculateAwayOtherBasketball(int m, int n,String templateCode) {
        Integer p =Integer.parseInt(templateCode.replace("+",""));
        /*** 赢：M-N==P ***/
        boolean whetherUserWin = (n - m >= p);
        /*** 更新矩阵当前的数据 ***/
        /*** 返回输赢情况 ***/
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

    /**
     * @Description   平局
     * @Param [m, n, templateCode]
     * @Author  toney
     * @Date  16:41 2020/4/15
     * @return int
     **/
    private int calculateDraw(int m, int n) {
       
        /*** 赢：M-N==P ***/
        boolean whetherUserWin = (n - m == 0);
        /*** 更新矩阵当前的数据 ***/
        /*** 返回输赢情况 ***/
        if (whetherUserWin) {
            return OrderSettleStatus.USER_WIN.getValue();
        } else {
            return OrderSettleStatus.USER_LOSE.getValue();
        }
    }

}
