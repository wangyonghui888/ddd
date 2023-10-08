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
 * @Description :  总进球数计算矩阵
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@org.springframework.stereotype.Service
@Slf4j
public class MatrixTotalGoals extends AbstractMatrix implements IMatrixForecast {
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C14001,C23001";
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
     * @Description  总进球数计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        String name = categoryList.queryMatrixTemplateNameByCode(matrixForecastVo.getMarketCategoryId().intValue(),matrixForecastVo.getOddsFieldsTemplateName().toString());
        if(!StringUtils.isEmpty(name)){
            //玩法 投注项 0, 1, 2, 3, 4, 5, 6+
            if(name.contains(MatrixConstant.TOTAL_GOALS_MORE_SPLIT)){
                calculateTotalGoalsMatrix(matrixForecastVo,name);
            }
            else {
                calculateTotalGoalsMatrix(matrixForecastVo,Integer.parseInt(name));
            }
        }
        else {
            log.error("templateCode is error,can't find ! {0}",templateCode);
        }
    }

    /**
     * @Description  矩阵值计算-- 总进球数计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateTotalGoalsMatrix(MatrixForecastVo matrixForecastVo,int templateCode){
        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;
                if(result == templateCode){
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
     * @Description  矩阵值计算-- 总进球数计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateTotalGoalsMatrix(MatrixForecastVo matrixForecastVo,String name){
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                int result = m + n;

                if(result >= Integer.parseInt(name.replace("+",""))){
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
}
