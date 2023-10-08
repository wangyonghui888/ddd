package com.panda.sport.rcs.mgr.paid.matrix.category;

import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  让球比分矩阵计算
 * @Date: 2019-10-05 13:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixHandicap extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C30001,C40001,C19001,C33001";

    /**
     * @Description  亚盘让球 有走水
     * @Param
     * @Author  max
     * @Date  10:50 2019/10/25
     * @return
     **/
    public static final String DRAW_CODE = "C40001,C19001,C33001";

    /**
     * @Description   //TODO
     * @Param 获取玩法编码
     * @Author  max
     * @Date  12:50 2019/10/10
     * @return void
     **/
    @Override
    public String queryCateCode() {
        return CATE_CODE;
    }
    /**
     * @Description  比分矩阵计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        //玩法 投注项 1,x,2
        switch (templateCode){
            case "C100001":
                calculateHandicapHomeMatrix(matrixForecastVo,code);
                break;
            case "C100002":
                calculateDrawMatrix(matrixForecastVo);
                break;
            case "C100003":
                calculateHandicapAwayMatrix(matrixForecastVo,code);
                break;
        }
    }


    /**
     * @Description  矩阵值计算-- 主
     * @Param [matrixForecastVo]
     * -0/0.5
     * 0/0.5
     * 盘口值负数主让客
     * 盘口值正数 客让主
     * 亚盘让球滚球需要有基准分
     * 赛前盘基准分为0:0
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHandicapHomeMatrix(MatrixForecastVo matrixForecastVo,String code){
        if(matrixForecastVo.getMarketOddsValue().contains(MatrixConstant.MARKET_ODDS_VALUE_SPLIT)){
            String[] marketOddsValue = matrixForecastVo.getMarketOddsValue().split(MatrixConstant.MARKET_ODDS_VALUE_SPLIT);
            if(marketOddsValue != null && marketOddsValue.length > 1){
                double marketOddsValue1 = Double.parseDouble(marketOddsValue[0]);
                double marketOddsValue2 =  Double.parseDouble(marketOddsValue[1]);
                for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                    for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                        if(marketOddsValue1 < 0){
                            int result = m - n;
                            if(result > Math.abs(marketOddsValue1)){
                                setWinHalfAmount(matrixForecastVo,m,n);
                            }
                            else if(result == Math.abs(marketOddsValue1) || result == Math.abs(marketOddsValue2)){
                                setLostHalfAmount(matrixForecastVo,m,n);
                            }
                            else
                            {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                            if(result > Math.abs(marketOddsValue2)){
                                setWinHalfAmount(matrixForecastVo,m,n);
                            }

                            //2个盘口都赢 全赢
                            if(result > Math.abs(marketOddsValue1) && result > Math.abs(marketOddsValue2)){
                                matrixForecastVo.getMatrixArray()[m][n] =  (long) ((matrixForecastVo.getBetAmount()) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                                //3 赢
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                            }
                        }
                        else {
                            int lostHalf = 0;
                            int winHalf = 0;
                            double result = m + marketOddsValue1;
                            if(result > n ){
                                setWinHalfAmount(matrixForecastVo,m,n);
                                winHalf++;
                            }
                            else if (result == n)
                            {
                                if(DRAW_CODE.contains(code)){
                                    matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                }else {
                                    setLostHalfAmount(matrixForecastVo,m,n);
                                    lostHalf++;
                                }
                            }
                            else
                            {
                                setLostHalfAmount(matrixForecastVo,m,n);
                                lostHalf++;
                            }

                            if( (m + marketOddsValue2) > n){
                                setWinHalfAmount(matrixForecastVo,m,n);
                                winHalf++;
                            }
                            else if ((m + marketOddsValue2) == n)
                            {
                                if(DRAW_CODE.contains(code)){
                                    matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                    //5：走水
                                    matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                                }else {
                                    setLostHalfAmount(matrixForecastVo,m,n);
                                    lostHalf++;
                                }
                            }
                            else
                            {
                                setLostHalfAmount(matrixForecastVo,m,n);
                                lostHalf++;
                            }

                            //全输或者全赢
                            if(lostHalf == 2){
                                matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();

                                //1 输
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                            }
                            if(winHalf == 2){
                                matrixForecastVo.getMatrixArray()[m][n] = (long) ((matrixForecastVo.getBetAmount() ) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                                //3 赢
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                            }
                        }
                    }
                }
            }
            else {
                log.error("盘口值格式不正确!",matrixForecastVo);
            }
        }
        else{
            double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

            for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                    if(marketOddsValue < 0){
                        int result = m - n;
                        if(result > Math.abs(marketOddsValue)){
                            setWinAmount(matrixForecastVo,m,n);
                        }
                        else if(result == Math.abs(marketOddsValue)){
                            if(DRAW_CODE.contains(code)){
                                matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                //5：走水
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                            }
                            else {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                        }
                        else
                        {
                           setLostAmount(matrixForecastVo,m,n);
                        }
                    }
                    else {
                        double result = m + marketOddsValue;
                        if(result > n){
                            setWinAmount(matrixForecastVo,m,n);
                        }
                        else if(result == n){
                            if(DRAW_CODE.contains(code)){
                                matrixForecastVo.getMatrixArray()[m][n] += 0L;
                            }
                            else {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                        }
                        else
                        {
                            setLostAmount(matrixForecastVo,m,n) ;
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 客
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateHandicapAwayMatrix(MatrixForecastVo matrixForecastVo,String code){
        if(matrixForecastVo.getMarketOddsValue().contains(MatrixConstant.MARKET_ODDS_VALUE_SPLIT)){
            String[] marketOddsValue = matrixForecastVo.getMarketOddsValue().split(MatrixConstant.MARKET_ODDS_VALUE_SPLIT);
            if(marketOddsValue != null && marketOddsValue.length > 1){
                double marketOddsValue1 = Double.parseDouble(marketOddsValue[0]);
                double marketOddsValue2 =  Double.parseDouble(marketOddsValue[1]);
                for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                    for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                        if(marketOddsValue1 < 0){
                            int result = m - n;
                            if(result < Math.abs(marketOddsValue1)){
                                setWinHalfAmount(matrixForecastVo,m,n);
                            }
                            else if(result == Math.abs(marketOddsValue1) || result == Math.abs(marketOddsValue2)){
                                setLostHalfAmount(matrixForecastVo,m,n);
                            }
                            else
                            {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                            if(result < Math.abs(marketOddsValue2)){
                                setWinHalfAmount(matrixForecastVo,m,n);
                            }

                            //2个盘口都赢 全赢
                            if(result < Math.abs(marketOddsValue1) && result < Math.abs(marketOddsValue2)){
                                matrixForecastVo.getMatrixArray()[m][n] =  (long) ((matrixForecastVo.getBetAmount()) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;

                                //3 赢
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                            }
                        }
                        else {
                            int lostHalf = 0;
                            int winHalf = 0;
                            double result = m + marketOddsValue1;
                            if(result < n ){
                                setWinHalfAmount(matrixForecastVo,m,n);
                                winHalf++;
                            }
                            else if (result == n)
                            {
                                if(DRAW_CODE.contains(code)){
                                    matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                    //5：走水
                                    matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                                }else {
                                    setLostHalfAmount(matrixForecastVo,m,n);
                                    lostHalf++;
                                }
                            }
                            else
                            {
                                setLostHalfAmount(matrixForecastVo,m,n);
                                lostHalf++;
                            }

                            if( (m + marketOddsValue2) < n){
                                setWinHalfAmount(matrixForecastVo,m,n);
                                winHalf++;
                            }
                            else if ((m + marketOddsValue2) == n)
                            {
                                if(DRAW_CODE.contains(code)){
                                    matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                    //5：走水
                                    matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                                }else {
                                    setLostHalfAmount(matrixForecastVo,m,n);
                                    lostHalf++;
                                }
                            }
                            else
                            {
                                setLostHalfAmount(matrixForecastVo,m,n);
                                lostHalf++;
                            }

                            //全输或者全赢
                            if(lostHalf == 2){
                                matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                                //1 输
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                            }
                            if(winHalf == 2){
                                matrixForecastVo.getMatrixArray()[m][n] = (long) ((matrixForecastVo.getBetAmount() ) * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;

                                //3 赢
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                            }
                        }
                    }
                }
            }
            else {
                log.error("::{}::盘口值格式不正确!",matrixForecastVo.getOddsFieldsTemplate());
            }
        }
        else{
            double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

            for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                    if(marketOddsValue < 0){
                        int result = m - n;
                        if(result < Math.abs(marketOddsValue)){
                            setWinAmount(matrixForecastVo,m,n);
                        }
                        else if(result == Math.abs(marketOddsValue)){
                            if(DRAW_CODE.contains(code)){
                                matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                //5：走水
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                            }
                            else {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                        }
                        else
                        {
                            setLostAmount(matrixForecastVo,m,n);
                        }
                    }
                    else {
                        double result = m + marketOddsValue;
                        if(result < n){
                            setWinAmount(matrixForecastVo,m,n);
                        }
                        else if(result == n){
                            if(DRAW_CODE.contains(code)){
                                matrixForecastVo.getMatrixArray()[m][n] += 0L;
                                //5：走水
                                matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                            }
                            else {
                                setLostAmount(matrixForecastVo,m,n);
                            }
                        }
                        else
                        {
                            setLostAmount(matrixForecastVo,m,n) ;
                        }
                    }
                }
            }
        }

    }

    /**
     * @Description  矩阵值计算-- 平局
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    public  void calculateDrawMatrix(MatrixForecastVo matrixForecastVo){
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                // 负数 主让客
                if(marketOddsValue < 0){
                    int result = m - n;
                    if(result == Math.abs(marketOddsValue)){
                        setWinAmount(matrixForecastVo,m,n);
                    }
                    else{
                        setLostAmount(matrixForecastVo,m,n);
                    }
                }
                else {
                    if((n - m) == Math.abs(marketOddsValue)){
                       setWinAmount(matrixForecastVo,m,n);
                    }
                    else{
                        setLostAmount(matrixForecastVo,m,n);
                    }
                }
            }
        }
    }
}
