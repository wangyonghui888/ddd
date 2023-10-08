package com.panda.sport.sdk.category;

import com.google.inject.Singleton;
import com.panda.sport.rcs.profit.utils.ProfitRoleUtil;
import com.panda.sport.rcs.profit.utils.ProfitUtil;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.util.StringUtil;
import com.panda.sport.sdk.vo.MatrixForecastVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  让球比分矩阵计算
 * @Date: 2019-10-05 13:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Singleton
public class MatrixHandicap extends AbstractMatrix implements IMatrixForecast {
    private static final Logger log = LoggerFactory.getLogger(MatrixHandicap.class);
    /**
     * 玩法编码
     **/
    public static final String CATE_CODE = "C30001,C40001,C19001,C33001";

    /**
     * @Description 亚盘让球 有走水
     * @Param
     * @Author max
     * @Date 10:50 2019/10/25
     * @return
     **/
    public static final String DRAW_CODE = "C40001,C19001,C33001";

    /**
     * @return void
     * @Description //TODO
     * @Param 获取玩法编码
     * @Author max
     * @Date 12:50 2019/10/10
     **/
    @Override
    public String queryCateCode() {
        return CATE_CODE;
    }

    /**
     * @return
     * @Description 比分矩阵计算
     * @Param
     * @Author max
     * @Date 13:13 2019/10/4
     **/
    @Override
    public void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode) {
        //玩法 投注项 1,x,2
        switch (templateCode) {
            case "C100001":
                calculateHandicapHomeMatrix(matrixForecastVo, code);
                break;
            case "C100002":
                calculateDrawMatrix(matrixForecastVo);
                break;
            case "C100003":
                calculateHandicapAwayMatrix(matrixForecastVo, code);
                break;
        }
    }




    /**
     * @return void
     * @Description 矩阵值计算-- 主
     * @Param [matrixForecastVo, code]
     * @Author toney
     * @Date 11:53 2020/3/2
     * 基准分
     **/
    private void calculateHandicapHomeMatrix(MatrixForecastVo matrixForecastVo, String code) {
        String marketValue = matrixForecastVo.getMarketOddsValue();
        if (!StringUtils.isEmpty(matrixForecastVo.getMarketOddsValueNew())) {
            marketValue = matrixForecastVo.getMarketOddsValueNew();
        }

        if (StringUtils.isNotBlank(marketValue)) {
            for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
                for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                    Integer result = ProfitRoleUtil.HandicapHomeMatrix1(m, n, marketValue);
                    //1:赢  2：赢半  3:输 4：输半 5：走水
                    if (result == 1) {
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        setWinAmount(matrixForecastVo, m, n);
                    } else if (result == 2) {
                        setWinHalfAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                    } else if (result == 3) {
                        setLostAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                    } else if (result == 4) {
                        setLostHalfAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                    } else if (result == 5) {
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        matrixForecastVo.getMatrixArray()[m][n] += 0L;
                    } else {
                        log.error("处理数据异常：" + matrixForecastVo.toString());
                    }
                }
            }
        }
    }

    /**
     * @return void
     * @Description 矩阵值计算-- 客
     * @Param [matrixForecastVo, code]
     * @Author toney
     * @Date 13:19 2020/3/2
     **/
    private void calculateHandicapAwayMatrix(MatrixForecastVo matrixForecastVo, String code) {
        String marketValue = matrixForecastVo.getMarketOddsValue();
        if (!StringUtils.isEmpty(matrixForecastVo.getMarketOddsValueNew())) {
            marketValue = matrixForecastVo.getMarketOddsValueNew();
        }

        if (StringUtils.isNotBlank(marketValue)) {
            for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
                for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                    Integer result = ProfitRoleUtil.HandicapAwayMatrix1(m, n, marketValue, matrixForecastVo.getScoreBenchmark());
                    //1:赢  2：赢半  3:输 4：输半 5：走水
                    if (result == 1) {
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 1L;
                        setWinAmount(matrixForecastVo, m, n);
                    } else if (result == 2) {
                        setWinHalfAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 2L;
                    } else if (result == 3) {
                        setLostAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 3L;
                    } else if (result == 4) {
                        setLostHalfAmount(matrixForecastVo, m, n);
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 4L;
                    } else if (result == 5) {
                        matrixForecastVo.getMatrixStatusArray()[m][n] = 5L;
                        matrixForecastVo.getMatrixArray()[m][n] += 0L;
                    } else {
                        log.error("处理数据异常：" + matrixForecastVo.toString());
                    }
                }
            }
        }
    }




    /**
     * @return void
     * @Description 矩阵值计算-- 平局
     * @Param [matrixForecastVo]
     * @Author max
     * @Date 16:16 2019/10/4
     **/
    public void calculateDrawMatrix(MatrixForecastVo matrixForecastVo) {
        String marketValue = matrixForecastVo.getMarketOddsValue();
        if (!StringUtils.isEmpty(matrixForecastVo.getMarketOddsValueNew())) {
            marketValue = matrixForecastVo.getMarketOddsValueNew();
        }
        double marketOddsValue = Double.parseDouble(marketValue);
        for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
            for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                if (m + marketOddsValue - n == 0) {
                    setWinAmount(matrixForecastVo, m, n);
                } else if (m + marketOddsValue - n != 0) {
                    setLostAmount(matrixForecastVo, m, n);
                }
            }
        }
    }
}
