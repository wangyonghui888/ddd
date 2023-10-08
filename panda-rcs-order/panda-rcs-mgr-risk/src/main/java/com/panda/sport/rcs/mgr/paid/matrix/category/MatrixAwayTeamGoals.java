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
 * @Description :  客队进球数大小计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixAwayTeamGoals extends AbstractMatrix implements IMatrixForecast {

    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C11001";

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


    /**
     * @Description  矩阵值计算-- 大
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private  void calculateOverMatrix(MatrixForecastVo matrixForecastVo){
        if(StringUtils.isEmpty(matrixForecastVo.getMarketOddsValue())){
            log.warn("::{}::盘口值{}不能为空!",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo.getMarketOddsValue());
            return;
        }
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                super.setHomeMatrix(matrixForecastVo,marketOddsValue,n,m,n);
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
            log.warn("::{}::盘口值{}不能为空!",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo.getMarketOddsValue());
            return;
        }
        double marketOddsValue = Double.parseDouble(matrixForecastVo.getMarketOddsValue());

        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                super.setAwayMatrix(matrixForecastVo,marketOddsValue,n,m,n);
            }
        }
    }

}
