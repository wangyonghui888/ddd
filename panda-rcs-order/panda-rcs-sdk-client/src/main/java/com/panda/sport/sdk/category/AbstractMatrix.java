package com.panda.sport.sdk.category;


import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import com.panda.sport.sdk.vo.OddsFieldsTemplateVo;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  TODO
 * @Date: 2019-10-05 11:43
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public abstract class AbstractMatrix {
    /**
     * @Description   比分差
     * @Param 
     * @Author  toney
     * @Date  18:59 2020/3/3
     * @return 
     **/
    protected ScoreBenchmark scoreBenchmark;

    /**
     * 保存,当前玩法的投注项,key是:code. OddsFieldsTemplateVo 的 code  和 name 是同一个值
     */
    protected Map<String, OddsFieldsTemplateVo> oddsFieldsTemplateVoMap = new HashMap<>();

    /**
     * @Description  设置矩阵主队
     * @Param [matrixForecastVo, marketOddsValue, result]
     * @Author  max
     * @Date  14:00 2019/10/5
     * @return void
     **/

    public void setHomeMatrix(MatrixForecastVo matrixForecastVo, double marketOddsValue, int result, int i, int j){
        if(result >  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
            //3：赢
            matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
        }

        if(result <  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = matrixForecastVo.getBetAmount() ;
            //1 输
            matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
        }

        if(result ==  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = Long.valueOf(0);
            //5：走水
            matrixForecastVo.getMatrixStatusArray()[i][j] = 5L;
        }
    }

    /**
     * @Description   设置矩阵客队
     * @Param [matrixForecastVo, marketOddsValue, result]
     * @Author  max
     * @Date  14:01 2019/10/5
     * @return void
     **/

    public void setAwayMatrix(MatrixForecastVo matrixForecastVo,double marketOddsValue,int result,int i,int j){
        if(result >  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = matrixForecastVo.getBetAmount() ;
            //1 输
            matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
        }

        if(result <  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1 ;
            //3 赢
            matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
        }

        if(result ==  marketOddsValue){
            matrixForecastVo.getMatrixArray()[i][j] = Long.valueOf(0);
            //5：走水
            matrixForecastVo.getMatrixStatusArray()[i][j] = 5L;
        }
    }


    /**
     * @Description   设置赢 矩阵值
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  10:39 2019/10/22
     * @return void
     **/
    public void setWinAmount(MatrixForecastVo matrixForecastVo,int i,int j){
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType()) || StringUtils.isEmpty(matrixForecastVo.getMarketType())){
            matrixForecastVo.getMatrixArray()[i][j] += (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;

            //赢
            matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
        }

        /**
         * 马来盘 计算公式
         * 赔率正数是和欧洲盘一致
         * 负数 输少赢足
         * 举例 如果您下注100元，赔率为-0.80的话，如果您赢了，您的派彩为100元，那您的账户中就有200元，如果您没有赢，您的账户中就剩下100-100*0.80=20元         *
         **/
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_MALAY.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType())){
            if(matrixForecastVo.getFieldOddsValue() < 0){
                matrixForecastVo.getMatrixArray()[i][j] += matrixForecastVo.getBetAmount() * -1;
            }
            else {
                matrixForecastVo.getMatrixArray()[i][j] += (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
            }
        }
    }

    /**
     * @Description   设置赢半 矩阵值
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  10:39 2019/10/22
     * @return void
     **/
    public void setWinHalfAmount(MatrixForecastVo matrixForecastVo,int i,int j){
        // 4：赢半
        matrixForecastVo.getMatrixStatusArray()[i][j] = 4L;

        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType()) || StringUtils.isEmpty(matrixForecastVo.getMarketType())){
            matrixForecastVo.getMatrixArray()[i][j] += (long) ((matrixForecastVo.getBetAmount() / 2) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
        }

        /**
         * 马来盘 计算公式
         * 赔率正数是和欧洲盘一致
         * 负数 输少赢足
         * 举例 如果您下注100元，赔率为-0.80的话，如果您赢了，您的派彩为100元，那您的账户中就有200元，如果您没有赢，您的账户中就剩下100-100*0.80=20元         *
         **/
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_MALAY.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType())){
            if(matrixForecastVo.getFieldOddsValue() < 0){
                matrixForecastVo.getMatrixArray()[i][j] += (matrixForecastVo.getBetAmount() / 2) * -1;
            }
            else {
                matrixForecastVo.getMatrixArray()[i][j] += (long) ((matrixForecastVo.getBetAmount() / 2) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
            }
        }
    }

    /**
     * @Description   设置输 矩阵值
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  10:39 2019/10/22
     * @return void
     **/
    public void setLostAmount(MatrixForecastVo matrixForecastVo,int i,int j){
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType()) || StringUtils.isEmpty(matrixForecastVo.getMarketType())){
            matrixForecastVo.getMatrixArray()[i][j] += matrixForecastVo.getBetAmount();

            //1 输
            matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
        }

        /**
         * 马来盘 计算公式
         * 赔率正数是和欧洲盘一致
         * 负数 输少赢足
         * 举例 如果您下注100元，赔率为-0.80的话，如果您赢了，您的派彩为100元，那您的账户中就有200元，如果您没有赢，您的账户中就剩下100-100*0.80=20元         *
         **/
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_MALAY.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType())){
            if(matrixForecastVo.getFieldOddsValue() < 0){
                matrixForecastVo.getMatrixArray()[i][j] += (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount());
            }
            else {
                matrixForecastVo.getMatrixArray()[i][j] += matrixForecastVo.getBetAmount();
            }
        }
    }


    /**
     * @Description   设置输半 矩阵值
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  10:39 2019/10/22
     * @return void
     **/
    public void setLostHalfAmount(MatrixForecastVo matrixForecastVo,int i,int j){
        //2：输半
        matrixForecastVo.getMatrixStatusArray()[i][j] = 2L;
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType()) || StringUtils.isEmpty(matrixForecastVo.getMarketType())){
            matrixForecastVo.getMatrixArray()[i][j] += matrixForecastVo.getBetAmount() / 2;
        }

        /**
         * 马来盘 计算公式
         * 赔率正数是和欧洲盘一致
         * 负数 输少赢足
         * 举例 如果您下注100元，赔率为-0.80的话，如果您赢了，您的派彩为100元，那您的账户中就有200元，如果您没有赢，您的账户中就剩下100-100*0.80=20元         *
         **/
        if(MatrixConstant.MarketTypeOptions.MARKET_TYPE_MALAY.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType())){
            if(matrixForecastVo.getFieldOddsValue() < 0){
                matrixForecastVo.getMatrixArray()[i][j] += (long) (matrixForecastVo.getBetAmount()/2 * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount() / 2);
            }
            else {
                matrixForecastVo.getMatrixArray()[i][j] += matrixForecastVo.getBetAmount() / 2;
            }
        }
    }

}
