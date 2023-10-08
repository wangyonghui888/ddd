package com.panda.sport.rcs.mgr.paid.matrix.category;

import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import org.springframework.stereotype.Service;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  胜平负&进球数大小 矩阵计算
 * @Date: 2019-10-05 11:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class MatrixMatchBetAndTotals extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C13001";

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
     * @Description  胜平负&进球数大小计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
     public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        if(CATE_CODE.equalsIgnoreCase(code)){
            //玩法 投注项 1AndUnder, XAndUnder, 2AndUnder, 1AndOver, XAndOver, 2AndOver
            switch (templateCode){
                case "C130001":
                    calculate1AndUnderMatrix(matrixForecastVo);
                    break;
                case "C130002":
                    calculateXAndUnderMatrix(matrixForecastVo);
                    break;
                case "C130003":
                    calculate2AndUnderMatrix(matrixForecastVo);
                    break;
                case "C130004":
                    calculate1AndOverMatrix(matrixForecastVo);
                    break;
                case "C130005":
                    calculateXAndOverMatrix(matrixForecastVo);
                    break;
                case "C130006":
                    calculate2AndOverMatrix(matrixForecastVo);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @Description  胜平负&进球数大小--1AndUnder
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate1AndUnderMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                if(m > n && result < marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;

                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }

    /**
     * @Description  胜平负&进球数大小--XAndUnder
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateXAndUnderMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                if(m == n && result < marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }

    /**
     * @Description  胜平负&进球数大小--2AndUnder
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate2AndUnderMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                int result1 = m - n;
                if(result1 < 0 && result < marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;

                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }


    /**
     * @Description  胜平负&进球数大小--1AndUnder
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate1AndOverMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                int result1 = m  - n;
                if(result1 > 0 && result > marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }

    /**
     * @Description  胜平负&进球数大小--XAndOver
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateXAndOverMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                int result1 = m - n;
                if(result1 == 0 && result > marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;

                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }

    /**
     * @Description  胜平负&进球数大小--2AndOver
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculate2AndOverMatrix(MatrixForecastVo matrixForecastVo){
        //盘口值
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                if((m - n) < 0 && result > marketOddsValue){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount() * matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount())* -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] =  matrixForecastVo.getBetAmount() ;

                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }

}
