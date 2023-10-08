package com.panda.sport.sdk.category;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  主队进球数大小计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixHomeTeamGoals extends AbstractMatrix implements IMatrixForecast {
    private static final Logger log = LoggerFactory.getLogger(MatrixHomeTeamGoals.class);
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C10111";

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
        if(CATE_CODE.equalsIgnoreCase(code)){
            //玩法 投注项 大,小
            switch (templateCode){
                case "C200001":
                    calculateOverMatrix(matrixForecastVo);
                    break;
                case "C200002":
                    calculateUnderMatrix(matrixForecastVo);
                    break;
            }
        }
    }


    /**
     * @Description  矩阵值计算-- 大
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateOverMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getMarketOddsValue())){
            log.error("盘口值不能为空!",matrixForecastVo);
            return;
        }
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m;
                super.setHomeMatrix(matrixForecastVo,marketOddsValue,result,m,n);
            }
        }
    }

    /**
     * @Description  矩阵值计算-- 小
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateUnderMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getMarketOddsValue())){
            log.error("盘口值不能为空!",matrixForecastVo);
            return;
        }
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m;

                super.setAwayMatrix(matrixForecastVo,marketOddsValue,result,m,n);
            }
        }
    }

}
