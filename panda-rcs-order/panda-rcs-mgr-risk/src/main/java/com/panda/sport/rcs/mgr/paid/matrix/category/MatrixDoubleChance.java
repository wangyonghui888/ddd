package com.panda.sport.rcs.mgr.paid.matrix.category;


import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  双重机会 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
public class MatrixDoubleChance extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C60001";


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
        //玩法 投注项 主/和局,主/客,和/客
        switch (templateCode){
            case "C600001":
                calculate1XMatrix(matrixForecastVo);
                break;
            case "C600002":
                calculate12Matrix(matrixForecastVo);
                break;
            case "C600003":
                calculateX2Matrix(matrixForecastVo);
                break;
            default:
                break;
        }

    }

    /**
     * @Description  矩阵值计算-- 主/和局
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate1XMatrix(MatrixForecastVo matrixForecastVo){
        for(int i = 0; i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
                if(i == j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
                if(i < j){
                    matrixForecastVo.getMatrixArray()[i][j] =  matrixForecastVo.getBetAmount();
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 和/客
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateX2Matrix(MatrixForecastVo matrixForecastVo){
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    matrixForecastVo.getMatrixArray()[i][j] = matrixForecastVo.getBetAmount();
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
                }
                if(i == j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
                if(i < j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 主/客
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate12Matrix(MatrixForecastVo matrixForecastVo){
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1 ;

                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
                if(i == j){
                    matrixForecastVo.getMatrixArray()[i][j] = matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 1L;
                }
                if(i < j){
                    matrixForecastVo.getMatrixArray()[i][j] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1 ;

                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 3L;
                }
            }
        }
    }
}
