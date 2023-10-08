package com.panda.sport.rcs.mgr.paid.matrix.category;

import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  最多进球的半场 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class MatrixHighestScoringHalf extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C16001";

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
        //玩法 投注项 FirstHalf，SecondHalf，equals
        switch (templateCode){
            case "C160001":
                calculateFirstHalfMatrix(matrixForecastVo);
                break;
            case "C160002":
                calculateSecondHalfMatrix(matrixForecastVo);
                break;
            case "C160003":
                calculateEqualsMatrix(matrixForecastVo);
                break;
            default:
                break;
        }
    }

    /**
     * @Description  矩阵值计算-- 上半场
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateFirstHalfMatrix(MatrixForecastVo matrixForecastVo){
        List<Integer> list = new ArrayList<>();
        for(int m3 = 0; m3 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m3){
            for(int n3 =0; n3<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n3){
                int result = m3 + n3;
                if(list.contains(result) == false){
                    list.add(result);
                }
            }
        }

        for(Integer res : list){
            for(int m4 = 0 ;m4 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m4) {
                for(int n4 = 0; n4 < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n4) {
                    //上半场和下半场比分计算
                    int result = res - (m4+n4);
                    if(result > 0){
                        matrixForecastVo.getMatrixArray()[m4][n4] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                        //3 赢
                        matrixForecastVo.getMatrixStatusArray()[m4][n4] = 3L;
                    }
                    else {
                        matrixForecastVo.getMatrixArray()[m4][n4] =  matrixForecastVo.getBetAmount();
                        //1 输
                        matrixForecastVo.getMatrixStatusArray()[m4][n4] = 1L;
                    }
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 下半场
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateSecondHalfMatrix(MatrixForecastVo matrixForecastVo){
        for(int m3 = 0 ;m3 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m3){
            for(int n3 =0; n3<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n3){

                for(int m4 = 0 ;m4 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m4) {
                    for (int n4 = 0; n4 < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n4) {

                        //上半场和下半场比分计算
                        int result = (m3 + n3) - (m4+n4);
                        if(result < 0){
                            matrixForecastVo.getMatrixArray()[m4][n4] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;

                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m4][n4] = 3L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m4][n4] =  matrixForecastVo.getBetAmount();
                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m4][n4] = 1L;
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 一样多
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateEqualsMatrix(MatrixForecastVo matrixForecastVo){
        for(int m3 = 0 ;m3 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m3){
            for(int n3 =0; n3<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n3){

                for(int m4 = 0 ;m4 < MatrixConstant.MATRIX_LINE_LENGTH ; ++m4) {
                    for (int n4 = 0; n4 < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n4) {

                        //上半场和下半场比分计算
                        int result = (m3 + n3) - (m4+n4);
                        if(result == 0){
                            matrixForecastVo.getMatrixArray()[m4][n4] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                            //3 赢
                            matrixForecastVo.getMatrixStatusArray()[m4][n4] = 3L;
                        }
                        else {
                            matrixForecastVo.getMatrixArray()[m4][n4] =  matrixForecastVo.getBetAmount();

                            //1 输
                            matrixForecastVo.getMatrixStatusArray()[m4][n4] = 1L;
                        }
                    }
                }
            }
        }
    }
}
