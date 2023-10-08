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
 * @Description :  剩余时间胜平负 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@org.springframework.stereotype.Service
public class MatrixRestOfTheMatchWinner extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C27001,C29001";
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
        //玩法 投注项 1 X 2
        switch (templateCode){
            case "C100001":
                calculateHomeMatrix(matrixForecastVo);
                break;
            case "C100002":
                calculateDrawMatrix(matrixForecastVo);
                break;
            case "C100003":
                calculateAwayMatrix(matrixForecastVo);
                break;
            default:
                break;
        }
    }

    /**
     * @Description  矩阵值计算-- 主胜
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateHomeMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.error("::{}::剩余时间胜平负 矩阵计算 基准比分不能为空!",matrixForecastVo.getOddsFieldsTemplate());
            return;
        }
        else
        {
            String[] scoreBench = matrixForecastVo.getScoreBenchmark().split(":");
            int m1 = Integer.parseInt(scoreBench[0]);
            int n1 = Integer.parseInt(scoreBench[1]);

            for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                    if(m < m1 || n <  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = 0L;
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                    }
                    else if(m == m1 && n ==  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                    else {
                        int result = (m - n);
                        if(result > 0){
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 客胜
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateAwayMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.warn("::{}::剩余时间胜平负 矩阵计算 基准比分不能为空!",matrixForecastVo.getOddsFieldsTemplate());
            return;
        }
        else
        {
            String[] scoreBench = matrixForecastVo.getScoreBenchmark().split(":");
            int m1 = Integer.parseInt(scoreBench[0]);
            int n1 = Integer.parseInt(scoreBench[1]);

            for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                    if(m < m1 || n <  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = 0L;
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                    }
                    else if(m == m1 && n ==  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                    else {
                        int result = (m - n);
                        if(result < 0){
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
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
    private  void calculateDrawMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.warn("::{}::剩余时间胜平负 矩阵计算 基准比分不能为空!",matrixForecastVo.getOddsFieldsTemplate());
            return;
        }
        else
        {
            String[] scoreBench = matrixForecastVo.getScoreBenchmark().split(":");
            int m1 = Integer.parseInt(scoreBench[0]);
            int n1 = Integer.parseInt(scoreBench[1]);

            for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
                for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){

                    if(m < m1 || n <  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = 0L;
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                    }
                    else if(m == m1 && n ==  n1){
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                    else {
                        int result = (m - n);
                        if(result == 0){
                            matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        }
                    }
                }
            }
        }
    }
}
