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
 * @Description :  进球数单双计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixEvenOddTotal extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C15001";

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
     * @Description  进球数单双计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        if(CATE_CODE.equalsIgnoreCase(code)){
            //玩法 投注项 单双
            switch (templateCode){
                case "C150001":
                    calculateOddMatrix(matrixForecastVo);
                    break;
                case "C150002":
                    calculateEvenMatrix(matrixForecastVo);
                    break;
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 进球数单双计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateEvenMatrix(MatrixForecastVo matrixForecastVo){
        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = (m + n)%2;
                if(result == 0){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }


    /**
     * @Description  矩阵值计算-- 进球数单双计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateOddMatrix(MatrixForecastVo matrixForecastVo){
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = (m + n)%2;

                if(result == 0){
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
            }
        }
    }
}
