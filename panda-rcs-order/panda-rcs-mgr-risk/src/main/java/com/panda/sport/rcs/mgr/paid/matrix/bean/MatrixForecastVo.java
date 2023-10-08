package com.panda.sport.rcs.mgr.paid.matrix.bean;

import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.pojo.TOrderDetail;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  max
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  最大赔付矩阵推算Vo
 * @Date: 2019-10-04 11:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatrixForecastVo {
    /**
     * 盘口值
     * 让分 0.0/+0.5 以 / 分割
     * 负数表示让球方
     * 正数表示被让球方
     */
    private String marketOddsValue;

    /**
     * 盘口类型(EU:欧盘 HK:香港盘 US:美式盘 ID:印尼盘 MY:马来盘 GB:英式盘）
     */
    private String marketType;

    /**
     * 玩法ID
     */
    private Long marketCategoryId;

    /**
     * 玩法名称
     */
    private String marketCategoryName;

    /**
     * 投注项ID
     */
    private Long oddsFieldsTemplate;

    /**
     * 投注项名称
     */
    private String oddsFieldsTemplateName;

    /**
     * 交易项赔率。单位：0.0001
     */
    private Double fieldOddsValue;

    /**
     * 下注金额
     */
    private Long betAmount;

    /**
     * @Description 基准分
     * @Param
     * @Author max
     * @Date 17:59 2019/10/5
     * @return
     **/
    private String scoreBenchmark;


    /**
     * @Description 玩法是否需要基准分
     * 0 不需要
     * 1 需要基准分
     **/
    private Integer isNeedBenchmark;

    public void setMatrixArray(Long[][] matrixArray) {
        for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
            for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                matrixArray[m][n] = 0L;
            }
        }
        this.matrixArray = matrixArray;
    }

    /**
     * @Description
     * @Param 玩法计算矩阵
     * @Author max
     * @Date 11:23 2019/10/10
     * @return
     **/
    private Long[][] matrixArray = new Long[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_COLUMN_LENGTH];


    /*
     * @Description
     *  //1 输，//2：输半，//3：赢，// 4：赢半， //5：走水
     **/
    private Long[][] matrixStatusArray = new Long[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_COLUMN_LENGTH];

    /*static {
        for(int m = 0 ;m < MatrixConstant.MATRIX_LINE_LENGTH ; ++m) {
            for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                matrixArray[m][n] = 0L;
            }
        }
    }*/

    /**
     * @Description
     * @Param 玩法推算类型
     * 0 矩阵
     * 1 穷举法
     * @Author max
     * @Date 11:27 2019/10/10
     * @return
     **/
    private MatrixConstant.MatrixCategoryType ctype;


    public Long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(Long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public String getMarketCategoryName() {
        return marketCategoryName;
    }

    public void setMarketCategoryName(String marketCategoryName) {
        this.marketCategoryName = marketCategoryName;
    }

    public Long getOddsFieldsTemplate() {
        return oddsFieldsTemplate;
    }

    public void setOddsFieldsTemplate(Long oddsFieldsTemplate) {
        this.oddsFieldsTemplate = oddsFieldsTemplate;
    }

    public String getOddsFieldsTemplateName() {
        return oddsFieldsTemplateName;
    }

    public void setOddsFieldsTemplateName(String oddsFieldsTemplateName) {
        this.oddsFieldsTemplateName = oddsFieldsTemplateName;
    }

    /**
     * @return java.lang.Long
     * @Description 赔率转换double
     * @Param []
     * @Author max
     * @Date 15:30 2019/10/5
     **/
    public double getFieldOddsValue() {
        return fieldOddsValue.doubleValue();
    }

    public void setFieldOddsValue(Double fieldOddsValue) {
        this.fieldOddsValue = fieldOddsValue;
    }

    public Long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Long betAmount) {
        this.betAmount = betAmount;
    }

    public String getMarketOddsValue() {
        return marketOddsValue;
    }

    public void setMarketOddsValue(String marketOddsValue) {
        this.marketOddsValue = marketOddsValue;
    }

    public String getScoreBenchmark() {
        return scoreBenchmark;
    }

    public void setScoreBenchmark(String scoreBenchmark) {
        this.scoreBenchmark = scoreBenchmark;
    }

    public Integer getIsNeedBenchmark() {
        return isNeedBenchmark;
    }

    public void setIsNeedBenchmark(Integer isNeedBenchmark) {
        this.isNeedBenchmark = isNeedBenchmark;
    }

    public static MatrixForecastVo getMatrixForecastBean(Object object) {
        MatrixForecastVo matrixForecastVo = null;
        OrderItem item = null;
        TOrderDetail detail = null;
        if (object instanceof OrderItem) {
            item = (OrderItem) object;
        } else if (object instanceof TOrderDetail) {
            detail = (TOrderDetail) object;
        } else {
            return null;
        }
        Integer playId = item != null ? item.getPlayId() : detail.getPlayId();
        Double oddsValue = item != null ? item.getHandleAfterOddsValue() : detail.getOddsValue();
        matrixForecastVo = new MatrixForecastVo();
        matrixForecastVo.setMarketCategoryName(item != null ? item.getPlayName() : detail.getPlayName());
        matrixForecastVo.setMarketCategoryId(playId != null ? playId.longValue() : null);
        matrixForecastVo.setOddsFieldsTemplate(item != null ? item.getPlayOptionsId().longValue() : detail.getPlayOptionsId().longValue());
        matrixForecastVo.setOddsFieldsTemplateName(item.getPlayOptions());
        matrixForecastVo.setFieldOddsValue(oddsValue != null ? oddsValue : null);
        matrixForecastVo.setBetAmount(item != null ? item.getBetAmount() : detail.getBetAmount());
        matrixForecastVo.setScoreBenchmark(item != null ? item.getScoreBenchmark() : detail.getScoreBenchmark());
        matrixForecastVo.setMarketType(item != null ? item.getMarketType() : detail.getMarketType());
        matrixForecastVo.setMarketOddsValue(item != null ? item.getMarketValue() : detail.getMarketValue());
        return matrixForecastVo;
    }

    public Long[][] getMatrixArray() {
        return matrixArray;
    }


    public MatrixConstant.MatrixCategoryType getCtype() {
        return ctype;
    }

    public void setCtype(MatrixConstant.MatrixCategoryType ctype) {
        this.ctype = ctype;
    }

    public Long[][] getMatrixStatusArray() {
        return this.matrixStatusArray;
    }

    public String queryMatrixStatus() {
        String compressionStr = "";
        for (int m = 0; m < MatrixConstant.MATRIX_LINE_LENGTH; ++m) {
            for (int n = 0; n < MatrixConstant.MATRIX_COLUMN_LENGTH; ++n) {
                if (matrixStatusArray[m][n] != null) {
                    compressionStr += matrixStatusArray[m][n];
                }
            }
        }
        compressionStr = compressionStr + "9";
        compressionStr = compression(compressionStr);
        return compressionStr;
    }

    public void setMatrixStatusArray(Long[][] matrixStatusArray) {
        this.matrixStatusArray = matrixStatusArray;
    }

    public static void main(String args[]) {
        OrderItem item = new OrderItem();
        item.setBetNo(1 + "");

        System.out.println(compression("12451214251354"));
        System.out.println(unCompression(compression("12451214251354")));

        System.out.println();
    }

    private static char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static Map<Integer, Character> keySet = new HashMap<>();

    static {
        for (int i = 0; i < charSet.length; i++) {
            keySet.put(i, charSet[i]);
        }
    }

    /**
     * @return java.lang.String
     * @Description 压缩
     * @Param [status]
     * @Author max
     * @Date 11:18 2020/1/22
     **/
    public static String compression(String status) {
        String result = "";
        int length = 0;
        for (int i = 0; i < (status.length() / 2); i++) {
            String index = status.substring(length, length + 2);
            Character character = keySet.get(Integer.parseInt(index));
            result += character + ",";
            length += 2;
        }
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }

        System.out.println("length:" + result.length() + "," + result);
        return result;
    }

    /**
     * @return java.lang.String
     * @Description 解压缩
     * @Param [number]
     * @Author max
     * @Date 11:18 2020/1/22
     **/
    public static String unCompression(String status) {
        String result = "";
        status = status.replaceAll(",", "");
        int length = 0;
        for (int i = 0; i < status.length(); i++) {
            String character = status.substring(length, length + 1);

            for (Map.Entry<Integer, Character> entry : keySet.entrySet()) {
                if (entry.getValue().toString().equals(character)) {
                    result += entry.getKey().toString();
                    break;
                }
            }
            length += 1;
        }

        System.out.println("length:" + result.length() + "," + result);

        return result;
    }

}
