package com.panda.sport.sdk.service.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.category.IMatrixForecast;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.constant.SdkConstants;
import com.panda.sport.sdk.exception.LogicException;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  最大赔付矩阵推算
 * @Date: 2019-10-04 10:54
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixForecast extends AbstractMatrix {

    private static final Logger log = LoggerFactory.getLogger(MatrixForecast.class);

    @Inject
    private CategoryService categoryService;

    /**
     * @return int
     * @Description 最大赔付矩阵推算
     * @Param [matrixForecastVoList]
     * @Author max
     * @Date 11:06 2019/10/4
     **/
    public void MatrixForecastAmount(MatrixForecastVo matrixForecastVo) {
        if (!MatrixConstant.MarketTypeOptions.MARKET_TYPE_EUROPE.getMarketType().equalsIgnoreCase(matrixForecastVo.getMarketType())) {
            log.error("不支持的盘口类型:{}!", matrixForecastVo.getMarketType());
            throw new LogicException(SdkConstants.ORDER_ERROR_CODE_RISK, "不支持的盘口类型:" + matrixForecastVo.getMarketType());
        }
        log.info("MatrixForecast start...... marketOddsValue:{}", matrixForecastVo);
        matrixForecastVo.setMatrixArray(new Long[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_COLUMN_LENGTH]);
        if (matrixForecastVo != null) {
            MatrixConstant.MatrixCategoryType matrixCategoryType = queryCategoryType(matrixForecastVo);
            switch (matrixCategoryType) {
                case MATRIX:
                    processMatrix(matrixForecastVo);
                    break;
                case EXHAUSTIVE:
                    matrixForecastVo.setCtype(MatrixConstant.MatrixCategoryType.EXHAUSTIVE);
                    break;
                case UNKNOWN:
                    log.error("当前玩法类型不支持最大赔付预测!", matrixForecastVo);
                    //throw new LogicException("902", "当前玩法类型不支持最大赔付预测,"+matrixForecastVo.getMarketCategoryId());
            }
        } else {
            log.info("MatrixForecast Param is empty");
            return;
        }

        printMatrixResult(matrixForecastVo);

        log.info("MatrixForecast end......{}", matrixForecastVo);
    }

    /**
     * @return java.lang.Boolean
     * @Description 查询当前玩法风控是否支持
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 11:45 2019/12/20
     **/
    public Boolean queryPlayTypeValidate(MatrixForecastVo matrixForecastVo) {
        Boolean result = true;
        matrixForecastVo.setMatrixArray(new Long[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_COLUMN_LENGTH]);
        if (matrixForecastVo != null) {
            MatrixConstant.MatrixCategoryType matrixCategoryType = queryCategoryType(matrixForecastVo);
            switch (matrixCategoryType) {
                case UNKNOWN:
                    log.info("当前玩法类型不支持最大赔付预测!", matrixForecastVo);
                    result = false;
                    break;
            }
        }
        return result;
    }

    /**
     * @return void
     * @Description 查询玩法矩阵类型，0 比分矩阵 1用穷举法获
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 13:09 2019/10/4
     **/
    private MatrixConstant.MatrixCategoryType queryCategoryType(MatrixForecastVo matrixForecastVo) {
        MatrixConstant.MatrixCategoryType matrixCategoryType;
        if (matrixForecastVo != null) {
            matrixCategoryType = categoryService.queryMatrixCategory(matrixForecastVo.getMarketCategoryId().intValue());
        } else {
            matrixCategoryType = MatrixConstant.MatrixCategoryType.UNKNOWN;
        }

        return matrixCategoryType;
    }

    /**
     * @return void
     * @Description //打印最大赔付矩阵
     * @Param []
     * @Author max
     * @Date 16:41 2019/10/4
     **/
    private void printMatrixResult(MatrixForecastVo matrixForecastVo) {
        System.out.println("打印最大赔付矩阵---");
        for (int i = 0; i < MatrixConstant.MATRIX_LINE_LENGTH; ++i) {
            for (int j = 0; j < MatrixConstant.MATRIX_COLUMN_LENGTH; ++j) {
                System.out.print(matrixForecastVo.getMatrixArray()[i][j] + ",");
            }
            System.out.println("\n");
        }
    }

    /**
     * @return
     * @Description 比分矩阵计算
     * @Param
     * @Author max
     * @Date 13:13 2019/10/4
     **/
    private void processMatrix(MatrixForecastVo matrixForecastVo) {
        try {

            String code = categoryService.queryMatrixCodeById(matrixForecastVo.getMarketCategoryId().intValue());
            IMatrixForecast matrixForecast = categoryService.getMapMatrixBeans().get(code);
            matrixForecast.processMatrix(matrixForecastVo, code, matrixForecastVo.getOddsFieldsTemplateName());

            //      String code = categoryList.queryMatrixCodeById(matrixForecastVo.getMarketCategoryId().intValue());
            //      IMatrixForecast matrixForecast =  categoryList.getMapMatrixBeans().get(code);

            String templateCode = categoryService.queryMatrixTemplateCodeById(matrixForecastVo.getMarketCategoryId().intValue(), matrixForecastVo.getOddsFieldsTemplateName());
            matrixForecastVo.setIsNeedBenchmark(categoryService.getCategoryList().get(matrixForecastVo.getMarketCategoryId().intValue()).getBenchmark());
            categoryService.getMapMatrixBeans().get(code).processMatrix(matrixForecastVo, code, templateCode);
        } catch (Exception ex) {
            log.error("MatrixForecast Error:", ex);
        }
    }
}
