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
 * @Description :  主队精确进球数/客队精确进球数/主队进球数-上半场/客队进球数-上半场
 * @Date: 2019-10-05 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixHalfTimeTeam1TotalGoals extends AbstractMatrix implements IMatrixForecast {
    private static final Logger log = LoggerFactory.getLogger(MatrixHalfTimeTeam1TotalGoals.class);
    /**
     * 玩法编码
     *
     **/
    public static final String CATE_CODE = "C80001,C90001,C21001,C22001";

    /**
     * @Description
     * @Param 主队精确进球数/上半场
     * @Author  max
     * @Date  10:45 2019/10/11
     * @return
     **/

    public static final String Team1TotalGoalsExact = "C80001,C21001";

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
     * @Description  上半场比分计算
     * @Param
     * @Author  max
     * @Date  13:13 2019/10/4
     * @return
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode){
        String name = categoryService.queryMatrixTemplateNameByCode(matrixForecastVo.getMarketCategoryId().intValue(),matrixForecastVo.getOddsFieldsTemplateName().toString());
        if(!StringUtils.isEmpty(name)){
            //玩法 投注项 0, 1, 2, 3, 4, 5, 6+
            if(name.contains(MatrixConstant.TOTAL_GOALS_MORE_SPLIT)){
                calculateHalfTimeTeam1Matrix(matrixForecastVo,name,code);
            }
            else {
                calculateHalfTimeTeam1Matrix(matrixForecastVo,Integer.parseInt(name),code);
            }
        }
        else {
            log.error("templateCode is error,can't find ! {0}",templateCode);
        }
    }

    /**
     * @Description  矩阵值计算-- 上半场比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeTeam1Matrix(MatrixForecastVo matrixForecastVo,int templateCode,String code){
        for(int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                if(Team1TotalGoalsExact.contains(code)){
                    if(m == templateCode){
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
                else {
                    if(n == templateCode){
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

    /**
     * @Description  矩阵值计算-- 上半场比分计算
     * @Param [matrixForecastVo]
     * @Author  max
     * @Date  16:16 2019/10/4
     * @return void
     **/
    private void calculateHalfTimeTeam1Matrix(MatrixForecastVo matrixForecastVo,String name,String code){
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m){
            for(int n =0; n<MatrixConstant.MATRIX_COLUMN_LENGTH; ++n){
                if(Team1TotalGoalsExact.contains(code)){
                    if(m >= Integer.parseInt(name.replace("+",""))){
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
                else {
                    if(n >= Integer.parseInt(name.replace("+",""))){
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
}
