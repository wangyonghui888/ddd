package com.panda.sport.rcs.credit.matrix;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.sdk.category
 * @Description :  TODO
 * @Date: 2020-03-03 18:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description   比分差
 * @Param
 * @Author  toney
 * @Date  18:56 2020/3/3
 * @return
 **/
public  class ScoreBenchmark {
    /**
     * @Description   比分
     * @Param 
     * @Author  toney
     * @Date  10:32 2020/3/7
     * @return 
     **/
    public static String SCOREBENCHMARKSPLIT=":";
    private Double x;
    private Double y;
    private static final Logger log = LoggerFactory.getLogger(ScoreBenchmark.class);

    public ScoreBenchmark(MatrixForecastVo matrixForecastVo) {
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            return;
        }
        String[] scoreBenchmarkList = matrixForecastVo.getScoreBenchmark().split(SCOREBENCHMARKSPLIT);
        //给默认值
        if(StringUtils.isEmpty(matrixForecastVo.getScoreBenchmark())){
            x=0.0;
            y=0.0;
        }
        if(scoreBenchmarkList.length == 2) {
            x = Double.parseDouble(scoreBenchmarkList[0]);
            y = Double.parseDouble(scoreBenchmarkList[1]);
        }else{
            log.error("ScoreBenchmark 基准比分有问题："+ JSONObject.toJSONString(matrixForecastVo));
            x=0.0;
            y=0.0;
        }
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}

