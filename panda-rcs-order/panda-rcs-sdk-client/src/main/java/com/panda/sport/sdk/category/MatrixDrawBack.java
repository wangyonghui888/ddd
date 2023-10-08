package com.panda.sport.sdk.category;


import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.vo.MatrixForecastVo;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  平局返还
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class MatrixDrawBack extends AbstractMatrix implements IMatrixForecast {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C50001";

    public static final String CATE_CODE_DRAW_NO_BET = "C50001";

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
     public void processMatrix(MatrixForecastVo matrixForecastVo, String code , String templateCode){
         //玩法 投注项 1 X 2
         switch (templateCode){
             case "C100001":
                 calculateHomeMatrix(matrixForecastVo,code);
                 break;
             case "C100003":
                 calculateAwayMatrix(matrixForecastVo,code);
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
    private  void calculateHomeMatrix(MatrixForecastVo matrixForecastVo,String code){
        for(int i = 0; i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    setWinAmount(matrixForecastVo,i,j);
                }

                if(i == j){
                    matrixForecastVo.getMatrixArray()[i][j] = Long.valueOf(0);
                    //5：走水
                    matrixForecastVo.getMatrixStatusArray()[i][j] = 5L;
                }
                if(i < j){
                    setLostAmount(matrixForecastVo,i,j) ;
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
    private  void calculateAwayMatrix(MatrixForecastVo matrixForecastVo,String code){
        for(int m= 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                if(m > n){
                    setLostAmount(matrixForecastVo,m,n) ;
                }
                if(m == n){
                    matrixForecastVo.getMatrixArray()[m][n] = Long.valueOf(0);
                    //5 走水
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                }
                if(m < n){
                    setWinAmount(matrixForecastVo,m,n);
                }
            }
        }
    }
}
