package com.panda.sport.sdk.category;


import org.apache.commons.lang3.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.service.impl.CategoryService;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  上半场准确比分/全场比分/波胆
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Singleton
public class MatrixHalfTimeCorrectScore extends AbstractMatrix implements IMatrixForecast {
    private static final Logger log = LoggerFactory.getLogger(MatrixHalfTimeCorrectScore.class);
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C20101,C70001";

    @Inject
    CategoryService categoryService ;

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
     * @Description  上半场准确比分计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        String name = categoryService.queryMatrixTemplateNameByCode(matrixForecastVo.getMarketCategoryId().intValue(),matrixForecastVo.getOddsFieldsTemplateName()
                .toString());
        if(!StringUtils.isEmpty(name)){
            //玩法 投注项  0:0,1:0 其它
            if(name.contains(MatrixConstant.HALFTIME_SCORE_OTHER)){
                calculateHalfTimeScoreOtherMatrix(matrixForecastVo);
            }
            else {
                calculateHalfTimeScoreMatrix(matrixForecastVo,name);
            }
        }
        else {
            log.error("templateCode is error,can't find ! {}",matrixForecastVo.getMarketCategoryId());
        }
    }

    /**
     * @Description  矩阵值计算-- 上半场准确比分计算--常规选项
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    /*private void calculateHalfTimeScoreMatrix(MatrixForecastVo matrixForecastVo,String templateCode){
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                String result = m + ":"+ n;
                if(result.equalsIgnoreCase(templateCode)){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }*/
    /**
     * @Description  矩阵值计算-- 上半场准确比分计算--常规选项
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeScoreMatrix(MatrixForecastVo matrixForecastVo,String templateCode){
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                String result = m  + ":"+ n;
                if(result.equalsIgnoreCase(templateCode)){
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount() ;
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
            }
        }
    }
    /**
     * @Description  矩阵值计算-- 上半场准确比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    /*private void calculateHalfTimeScoreOtherMatrix(MatrixForecastVo matrixForecastVo){
        String names = categoryList.queryMatrixTemplateNameById(matrixForecastVo.getMarketCategoryId());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                String result = m + ":"+ n;
                if(names.contains(result)){
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
            }
        }
    }*/
    /**
     * @Description  矩阵值计算-- 上半场准确比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeScoreOtherMatrix(MatrixForecastVo matrixForecastVo){
        String names = categoryService.queryMatrixTemplateNameById(matrixForecastVo.getMarketCategoryId());
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                String result = m + ":"+ n;
                if(names.contains(result)){
                    matrixForecastVo.getMatrixArray()[m][n] = matrixForecastVo.getBetAmount();
                    //1 输
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                }
                else{
                    matrixForecastVo.getMatrixArray()[m][n] = (long) (matrixForecastVo.getBetAmount()* matrixForecastVo.getFieldOddsValue() - matrixForecastVo.getBetAmount()) * -1;
                    //3 赢
                    matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                }
            }
        }
    }
}
