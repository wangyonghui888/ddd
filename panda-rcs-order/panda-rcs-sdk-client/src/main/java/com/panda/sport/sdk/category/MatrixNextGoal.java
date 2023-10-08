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
 * @Description :  下一个进球 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixNextGoal extends AbstractMatrix implements IMatrixForecast {

    private static final Logger log = LoggerFactory.getLogger(MatrixNextGoal.class);
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C28001,C30101";
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
    /*private  void calculateHomeMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.error("下一个进球 矩阵计算 基准比分不能为空!");
            return;
        }
        else
        {
            String[] scoreBench = matrixForecastVo.getScoreBenchmark().split(":");
            int m1 = Integer.parseInt(scoreBench[0]);
            int n1 = Integer.parseInt(scoreBench[1]);
            for(int m2 = 0; m2 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m2){
                for(int n2 =0; n2<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n2){
                   if(m2 >= m1 ){
                       if(m2 > m1 && n2 == n1){
                           matrixForecastVo.getMatrixArray()[m2][n2] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                           //3 赢
                           matrixForecastVo.getMatrixStatusArray()[m2][n2] = 3L;
                       }
                       else if(m2 == m1 && n2 == n1){
                           matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                           //5：走水
                           matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
                       }
                       else {
                           matrixForecastVo.getMatrixArray()[m2][n2] =  matrixForecastVo.getBetAmount() ;

                           //1 输
                           matrixForecastVo.getMatrixStatusArray()[m2][n2] = 1L;
                       }
                   }
                   else {
                       matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                       //5：走水
                       matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
                   }
                }
            }
        }
    }*/

    /**
     * @Description  矩阵值计算-- 主胜
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateHomeMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            log.error("下一个进球 矩阵计算 基准比分不能为空!");
            return;
        }
        else
        {
            scoreBenchmark = new ScoreBenchmark(matrixForecastVo);


            Double m1 = scoreBenchmark.getX();
            Double n1 = scoreBenchmark.getY();
            for(int m2 = 0; m2 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m2){
                for(int n2 =0; n2<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n2){
                    if(m2 >= m1 ){
                        if(m2 > m1 && n2 == n1){
                            matrixForecastVo.getMatrixArray()[m2][n2] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 3L;
                        }
                        else if(m2 == m1 && n2 == n1){
                            matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m2][n2] =  matrixForecastVo.getBetAmount() ;

                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 1L;
                        }
                    }
                    else {
                        matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
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
            log.error("下一个进球 矩阵计算 基准比分不能为空!");
            return;
        }
        else
        {
            String[] scoreBench = matrixForecastVo.getScoreBenchmark().split(":");
            int m1 = Integer.parseInt(scoreBench[0]);
            int n1 = Integer.parseInt(scoreBench[1]);
            for(int m2 = 0; m2 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m2){
                for(int n2 =0; n2<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n2){
                    if(m2 >= m1 ){
                        if(m2 == m1 && (n2 - n1) > 0){
                            matrixForecastVo.getMatrixArray()[m2][n2] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 3L;
                        }
                        else if(m2 == m1 && n2 == n1){
                            matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                            //5：走水
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m2][n2] =  matrixForecastVo.getBetAmount() ;
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m2][n2] = 1L;
                        }
                    }
                    else {
                        matrixForecastVo.getMatrixArray()[m2][n2] =  Long.valueOf(0);
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[m2][n2] = 5L;
                    }
                }
            }
        }
    }

}
