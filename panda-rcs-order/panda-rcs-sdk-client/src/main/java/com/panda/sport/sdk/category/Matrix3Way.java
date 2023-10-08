package com.panda.sport.sdk.category;


import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.vo.MatrixForecastVo;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  全场胜平负 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class Matrix3Way  extends AbstractMatrix implements IMatrixForecast {

    /**
     * 当前矩阵服务支持的玩法id
     **/
    public static final String CATE_CODE = "C10001,C17001,C25001,C32001";

    // public static final String CATE_CODE = "1,17,25,32";
 

    /**
     * @Description   
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
                } else{
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
    private  void calculateAwayMatrix(MatrixForecastVo matrixForecastVo,String code) {
        for (int i = 0; i < MatrixConstant.MATRIX_LINE_LENGTH; ++i) {
            for (int j = 0; j < MatrixConstant.MATRIX_COLUMN_LENGTH; ++j) {
                if (i < j) {
                    setWinAmount(matrixForecastVo, i, j);
                } else {
                    {
                        setLostAmount(matrixForecastVo, i, j);
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
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                if(i == j){
                    setWinAmount(matrixForecastVo,i,j) ;
                }else{
                    setLostAmount(matrixForecastVo,i,j);
                }
            }
        }
    }
}
