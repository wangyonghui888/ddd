package com.panda.sport.sdk.category;

import com.panda.sport.sdk.vo.MatrixForecastVo;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.rpc.paid.matrix
 * @Description :  矩阵计算接口
 * @Date: 2019-10-09 14:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface IMatrixForecast {

     /**
      * @Description   比分矩阵计算
      * @Param [matrixForecastVo, code, templateCode]
      * @Author  max
      * @Date  15:46 2019/10/9
      * @return void
      **/
     void processMatrix(MatrixForecastVo matrixForecastVo, String code, String templateCode);

     /**
      * 查询支持的投注项编码
      * @description
      * @param
      * @return java.lang.String
      * @author dorich
      * @date 2020/3/18 17:00
      **/
     String queryCateCode();
}
