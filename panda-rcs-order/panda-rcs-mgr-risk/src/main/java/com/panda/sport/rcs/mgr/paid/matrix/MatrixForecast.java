package com.panda.sport.rcs.mgr.paid.matrix;

import com.panda.sport.rcs.exeception.LogicException;
import com.panda.sport.rcs.mgr.paid.matrix.bean.CategoryList;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  最大赔付矩阵推算
 * @Date: 2019-10-04 10:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@org.springframework.stereotype.Service
public  class MatrixForecast extends AbstractMatrix  {

    @Autowired
    private CategoryList categoryList;

    /**
     * @Description  最大赔付矩阵推算
     * @Param [matrixForecastVoList]
     * @Author  max
     * @Date  11:06 2019/10/4
     * @return int
     **/
    public void MatrixForecastAmount(MatrixForecastVo matrixForecastVo){
        if(!MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType()))
        {
            log.error("::{}::不支持的盘口类型:{}!",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo.getMarketType());
            throw new LogicException("901", "不支持的盘口类型:"+matrixForecastVo.getMarketType());
        }
        log.info("::{}::MatrixForecast start...... marketOddsValue:{}",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo);
        matrixForecastVo.setMatrixArray(new Long[MatrixConstant.MATRIX_LINE_LENGTH ][MatrixConstant.MATRIX_COLUMN_LENGTH]);
        if(matrixForecastVo != null){
            MatrixConstant.MatrixCategoryType matrixCategoryType = queryCategoryType(matrixForecastVo);
            switch (matrixCategoryType){
                case MATRIX:
                    processMatrix(matrixForecastVo);
                    break;
                case EXHAUSTIVE:
                    matrixForecastVo.setCtype(MatrixConstant.MatrixCategoryType.EXHAUSTIVE);
                    break;
                case UNKNOWN:
                    log.error("::{}::当前玩法类型不支持最大赔付预测!",matrixForecastVo.getOddsFieldsTemplate());
                    throw new LogicException("902", "当前玩法类型不支持最大赔付预测,"+matrixForecastVo.getMarketCategoryId());
            }
        }
        else {
            log.warn("MatrixForecast Param is empty");
            return;
        }

        //printMatrixResult(matrixForecastVo);

        log.info("::{}::MatrixForecast end......{}",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo);
    }

    /**
     * @Description  查询当前玩法风控是否支持
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  11:45 2019/12/20
     * @return java.lang.Boolean
     **/
    public Boolean queryPlayTypeValidate(MatrixForecastVo matrixForecastVo){
        Boolean result = true;
        if(matrixForecastVo != null){
            MatrixConstant.MatrixCategoryType matrixCategoryType = queryCategoryType(matrixForecastVo);
            switch (matrixCategoryType){
                case UNKNOWN:
                    log.info("当前玩法类型不支持最大赔付预测!",matrixForecastVo);
                    result = false;
                    break;
            }
        }
        return result;
    }

    /**
     * @Description   查询玩法矩阵类型，0 比分矩阵 1用穷举法获
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  13:09 2019/10/4
     * @return void
     **/
    private MatrixConstant.MatrixCategoryType queryCategoryType(MatrixForecastVo matrixForecastVo){
        MatrixConstant.MatrixCategoryType matrixCategoryType;
        if(matrixForecastVo != null){
            matrixCategoryType = categoryList.queryMatrixCategory(matrixForecastVo.getMarketCategoryId().intValue());
        }
        else {
            matrixCategoryType = MatrixConstant.MatrixCategoryType.UNKNOWN;
        }

        return matrixCategoryType;
    }

    /**
     * @Description   //打印最大赔付矩阵
     * @Param []
     * @Author  max
     * @Date  16:41 2019/10/4
     * @return void
     **/
    private  void  printMatrixResult(MatrixForecastVo matrixForecastVo){
        System.out.println("打印最大赔付矩阵---");
        for(int i = 0 ;i < MatrixConstant.MATRIX_LINE_LENGTH ; ++i){
            for(int j =0; j<MatrixConstant.MATRIX_COLUMN_LENGTH; ++j){
                System.out.print(matrixForecastVo.getMatrixArray()[i][j] + ",");
            }
            System.out.println("\n");
        }
    }

    /**
     * @Description  比分矩阵计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    private void processMatrix(MatrixForecastVo matrixForecastVo){
        try {
            String code = categoryList.queryMatrixCodeById(matrixForecastVo.getMarketCategoryId().intValue());
            String templateCode = categoryList.queryMatrixTemplateCodeById(matrixForecastVo.getMarketCategoryId().intValue(), matrixForecastVo.getOddsFieldsTemplateName());
            matrixForecastVo.setIsNeedBenchmark(categoryList.getCategoryList().get(matrixForecastVo.getMarketCategoryId().intValue()).getBenchmark());

            IMatrixForecast matrixForecast = categoryList.getMapMatrixBeans().get(code);

            matrixForecast.processMatrix(matrixForecastVo, code, templateCode);
        }
        catch (Exception ex){
            log.error("::{}::MatrixForecast Error:{}",matrixForecastVo.getOddsFieldsTemplate(),ex.getMessage());
        }
    }

}
