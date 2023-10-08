package com.panda.sport.rcs.mgr.paid.matrix;

import com.panda.sport.rcs.mgr.paid.matrix.bean.MatrixForecastVo;

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
      * @Description   获取玩法编码
      * @Param []
      * @Author  max
      * @Date  12:49 2019/10/10
      * @return void
      **/
     String queryCateCode();
}
