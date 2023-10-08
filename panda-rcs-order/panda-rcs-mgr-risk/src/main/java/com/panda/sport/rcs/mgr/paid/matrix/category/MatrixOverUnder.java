package com.panda.sport.rcs.mgr.paid.matrix.category;


import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  进球大小计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixOverUnder extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C20001,C18001,C26001,C34001";

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
        if(CATE_CODE.contains(code)){
            //玩法 投注项 大,小
            switch (templateCode){
                case "C200001":
                    calculateOverMatrix(matrixForecastVo);
                    break;
                case "C200002":
                    calculateUnderMatrix(matrixForecastVo);
                    break;
            }
        }
    }


    /**
     * @Description  矩阵值计算-- 大
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateOverMatrix(MatrixForecastVo matrixForecastVo){

        if(StringUtils.isEmpty(matrixForecastVo.getMarketOddsValue())){
            log.warn("::{}::盘口值不能为空!",matrixForecastVo.getOddsFieldsTemplate());
            return;
        }

        if(matrixForecastVo.getMarketOddsValue().contains(MatrixConstant.MARKET_ODDS_VALUE_SPLIT)){
            String[] marketOddsValue = matrixForecastVo.getMarketOddsValue().split(MatrixConstant.MARKET_ODDS_VALUE_SPLIT);
            if(marketOddsValue != null && marketOddsValue.length > 1) {
                double marketOddsValue1 = Double.parseDouble(marketOddsValue[0]);
                double marketOddsValue2 = Double.parseDouble(marketOddsValue[1]);

                for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                    for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                        int result = m + n;
                        int winHalf = 0;
                        int lostHalf = 0;
                        if(result >  marketOddsValue1){
                            winHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += (long) (matrixForecastVo.getBetAmount() /2 * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
                            //4 赢半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                        }
                        else if(result ==  marketOddsValue1){
                            matrixForecastVo.getMatrixArray()[m][n] += 0L;
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        }
                        else {
                            lostHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += matrixForecastVo.getBetAmount()/2;
                            //2：输半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                        }

                        if(result > marketOddsValue2){
                            winHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += (long) (matrixForecastVo.getBetAmount() /2 * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
                            //4 赢半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                        }
                        else if(result ==  marketOddsValue2){
                            matrixForecastVo.getMatrixArray()[m][n] += 0L;
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        }
                        else {
                            lostHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += matrixForecastVo.getBetAmount()/2;
                            //2：输半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                        }

                        //全赢或者全输,解决小数为精度丢失问题
                        if(winHalf == 2){
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()  * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                             //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                        }
                        if(lostHalf == 2){
                            matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        }
                    }
                }
            }
        }
        else {
            double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

            for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                    int result = m + n;
                    super.setHomeMatrix(matrixForecastVo,marketOddsValue,result,m,n);
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 小
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateUnderMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getMarketOddsValue())){
            log.error("::{}::盘口值不能为空!",matrixForecastVo.getOddsFieldsTemplate());
            return;
        }


        if(matrixForecastVo.getMarketOddsValue().contains(MatrixConstant.MARKET_ODDS_VALUE_SPLIT)){
            String[] marketOddsValue = matrixForecastVo.getMarketOddsValue().split(MatrixConstant.MARKET_ODDS_VALUE_SPLIT);
            if(marketOddsValue != null && marketOddsValue.length > 1) {
                double marketOddsValue1 = Double.parseDouble(marketOddsValue[0]);
                double marketOddsValue2 = Double.parseDouble(marketOddsValue[1]);


                for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                    for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                        int result = m + n;
                        int winHalf = 0;
                        int lostHalf = 0;
                        if(result >  marketOddsValue1){
                            lostHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += matrixForecastVo.getBetAmount()/2;
                            //2：输半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                        }
                        else if(result ==  marketOddsValue1){
                            matrixForecastVo.getMatrixArray()[m][n] += 0L;
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        }
                        else {
                            winHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += (long) (matrixForecastVo.getBetAmount() /2 * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
                            //4 赢半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                        }

                        if(result >  marketOddsValue2){
                            lostHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] += matrixForecastVo.getBetAmount()/2;
                            //2：输半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                        }
                        else if(result ==  marketOddsValue2){
                            matrixForecastVo.getMatrixArray()[m][n] += 0L;
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        }
                        else {
                            winHalf++;
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() /2 * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()/2) * -1;
                            //4 赢半
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                        }

                        //全赢或者全输,解决小数为精度丢失问题
                        if(winHalf == 2){
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()  * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                        }
                        if(lostHalf == 2){
                            matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        }
                    }
                }
            }
        }
        else {
            double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

            for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
                for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                    int result = i + j;

                    super.setAwayMatrix(matrixForecastVo,marketOddsValue,result,i,j);
                }
            }
        }
    }

}
