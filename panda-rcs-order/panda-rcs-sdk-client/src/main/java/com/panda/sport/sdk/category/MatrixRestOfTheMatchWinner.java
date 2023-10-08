package com.panda.sport.sdk.category;

import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  剩余时间胜平负 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixRestOfTheMatchWinner extends AbstractMatrix implements IMatrixForecast {
    private static final Logger log = LoggerFactory.getLogger(MatrixRestOfTheMatchWinner.class);
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C27001,C29001";

    /**
     * @return void
     * @Description //TODO
     * @Param 获取玩法编码
     * @Author max
     * @Date 12:50 2019/10/10
     **/
    @Override
    public String queryCateCode() {
        return CATE_CODE;
    }

    /**
     * @return
     * @Description 比分矩阵计算
     * @Param
     * @Author max
     * @Date 13:13 2019/10/4
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode) {
        //玩法 投注项 1 X 2
        switch (templateCode) {
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
     * @return void
     * @Description 矩阵值计算-- 主胜
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 16:16 2019/10/4
     **/
    private void calculateHomeMatrix(MatrixForecastVo matrixForecastVo) {
        scoreBenchmark = new ScoreBenchmark(matrixForecastVo);

        if (StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())) {
            log.error("剩余时间胜平负 矩阵计算 基准比分不能为空!");
            return;
        } else {
            for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
                for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                    if ((m - scoreBenchmark.getX()) - (n - scoreBenchmark.getY()) > 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                        //3 赢
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                    } else if ((m - scoreBenchmark.getX()) - (n - scoreBenchmark.getY()) < 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @Description 矩阵值计算-- 客胜
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 16:16 2019/10/4
     **/
    private void calculateAwayMatrix(MatrixForecastVo matrixForecastVo) {
        scoreBenchmark = new ScoreBenchmark(matrixForecastVo);
        if (StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())) {
            log.error("剩余时间胜平负 矩阵计算 基准比分不能为空!");
            return;
        } else {
            for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
                for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                    if ((n - scoreBenchmark.getY()) - (m - scoreBenchmark.getX()) > 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                        //3 赢
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                    } else if ((n - scoreBenchmark.getY()) - (m - scoreBenchmark.getX()) < 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                }
            }
        }
    }
    /**
     * @Description 矩阵值计算-- 平局
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 16:16 2019/10/4
     * @return void
     **/
    /*private  void calculateDrawMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.error("剩余时间胜平负 矩阵计算 基准比分不能为空!");
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
    }*/


    /**
     * @return void
     * @Description 矩阵值计算-- 平局
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 16:16 2019/10/4
     **/
    private void calculateDrawMatrix(MatrixForecastVo matrixForecastVo) {

        if (StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())) {
            log.error("剩余时间胜平负 矩阵计算 基准比分不能为空!");
            return;
        } else {
            scoreBenchmark = new ScoreBenchmark(matrixForecastVo);

            for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
                for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                    if ((m - scoreBenchmark.getX()) - (n - scoreBenchmark.getY()) == 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                        //3 赢
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                    } else if ((m - scoreBenchmark.getX()) - (n - scoreBenchmark.getY()) != 0) {
                        matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                    }
                }
            }
        }
    }
}
