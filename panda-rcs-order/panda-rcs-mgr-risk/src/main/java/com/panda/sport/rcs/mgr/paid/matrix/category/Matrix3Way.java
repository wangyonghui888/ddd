package com.panda.sport.rcs.mgr.paid.matrix.category;


import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  全场胜平负 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
public class Matrix3Way  extends AbstractMatrix implements IMatrixForecast {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C10001,C50001,C17001,C25001,C32001";

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
             case "C100002":
                 calculateDrawMatrix(matrixForecastVo);
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
                    //平局返回
                    if(code.equalsIgnoreCase(CATE_CODE_DRAW_NO_BET)){
                        matrixForecastVo.getMatrixArray()[i][j] = Long.valueOf(0);
                        //5：走水
                        matrixForecastVo.getMatrixStatusArray()[i][j] = 5L;
                    }
                    else {
                        setLostAmount(matrixForecastVo,i,j);
                    }
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
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    setLostAmount(matrixForecastVo,i,j) ;
                }
                if(i == j){
                    //平局返回
                    if(code.equalsIgnoreCase(CATE_CODE_DRAW_NO_BET)){
                        matrixForecastVo.getMatrixArray()[i][j] = Long.valueOf(0);
                        //5 走水
                        matrixForecastVo.getMatrixStatusArray()[i][j] = 5L;
                    }
                    else {
                        setLostAmount(matrixForecastVo,i,j);
                    }
                }
                if(i < j){
                    setWinAmount(matrixForecastVo,i,j);
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
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i > j){
                    setLostAmount(matrixForecastVo,i,j) ;
                }
                if(i == j){
                    setWinAmount(matrixForecastVo,i,j) ;
                }
                if(i < j){
                    setLostAmount(matrixForecastVo,i,j);
                }
            }
        }
    }
}
