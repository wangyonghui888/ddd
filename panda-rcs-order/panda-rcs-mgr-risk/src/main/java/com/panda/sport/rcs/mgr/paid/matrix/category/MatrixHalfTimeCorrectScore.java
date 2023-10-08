package com.panda.sport.rcs.mgr.paid.matrix.category;


import com.panda.sport.rcs.mgr.paid.matrix.AbstractMatrix;
import com.panda.sport.rcs.mgr.paid.matrix.IMatrixForecast;
import com.panda.sport.rcs.mgr.paid.matrix.bean.CategoryList;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixConstant;
import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  上半场准确比分计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixHalfTimeCorrectScore extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C20101,C70001";

    @Autowired
    private CategoryList categoryList;

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
        String name = categoryList.queryMatrixTemplateNameByCode(matrixForecastVo.getMarketCategoryId().intValue(),matrixForecastVo.getOddsFieldsTemplateName()
                .toString());
        if(!StringUtils.isEmpty(name)){
            //玩法 投注项  0:0,1:0
            if(name.contains(MatrixConstant.HALFTIME_SCORE_OTHER)){
                calculateHalfTimeScoreOtherMatrix(matrixForecastVo);
            }
            else {
                calculateHalfTimeScoreMatrix(matrixForecastVo,name);
            }
        }
        else {
            log.warn("::{}::templateCode is error,can't find ! {}",matrixForecastVo.getOddsFieldsTemplate(),matrixForecastVo.getMarketCategoryId());
        }
    }

    /**
     * @Description  矩阵值计算-- 上半场准确比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeScoreMatrix(MatrixForecastVo matrixForecastVo,String templateCode){
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
    }

    /**
     * @Description  矩阵值计算-- 上半场准确比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeScoreOtherMatrix(MatrixForecastVo matrixForecastVo){
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
    }
}
