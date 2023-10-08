package com.panda.sport.sdk.service.impl.matrix;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.panda.sport.rcs.enums.OrderSettleStatus;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.sdk.annotation.AutoInitMethod;
import com.panda.sport.sdk.bean.MatrixBean;
import com.panda.sport.sdk.constant.MatrixConstant;
import com.panda.sport.sdk.exception.RcsServiceException;
import com.panda.sport.sdk.scan.ClasspathPackageScanner;
import com.panda.sport.sdk.util.MatrixDataZip;

/**
 * 矩阵计算适配器
 */
@Singleton
@AutoInitMethod(init = "init")
public class MatrixAdapter {

    private Logger log = LoggerFactory.getLogger(MatrixAdapter.class);

    @Inject
    ClasspathPackageScanner scanner;

    Map<String, Map<String, MatrixCaclApi>> handleApiMap = new HashMap<String, Map<String, MatrixCaclApi>>();

    Map<String, Integer[][]> matrixResultCache = new ConcurrentHashMap<String, Integer[][]>();

    private static List<String> scoreMatrixPlayIds = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "27", "28", "31", "32", "33", "34", "35", "36", "68", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "91", "92", "93", "94", "95", "96", "101", "102", "103", "104", "107", "108", "109", "110", "135", "136", "137", "141", "144", "148", "149", "150", "151", "152", "222", "223", "363", "364", "365", "366");

    public void init() {
        List<String> list = scanner.getAllMatchByAnnotion(MatrixCacl.class);
        if (list == null || list.size() <= 0) return;

        for (String clazz : list) {
            try {
                MatrixCaclApi api = (MatrixCaclApi) Class.forName(clazz).newInstance();
                MatrixCacl anno = api.getClass().getAnnotation(MatrixCacl.class);
                if (anno == null || StringUtils.isBlank(anno.configs())) {
                    log.warn("当前class注解没有对应配置：{}", clazz);
                    continue;
                }
                String configs = anno.configs();
                for (String playConfig : configs.split(";")) {
                    String sportId = playConfig.split(":")[0];
                    String playIds = playConfig.split(":")[1];
                    if (!handleApiMap.containsKey(sportId)) {
                        handleApiMap.put(sportId, new HashMap<String, MatrixCaclApi>());
                    }
                    Map<String, MatrixCaclApi> apiAll = handleApiMap.get(sportId);
                    for (String playId : playIds.split(",")) {
                        if (apiAll.containsKey(playId)) {
                            log.warn("玩法已经有一个矩阵配置：忽略当前：{},playId:{},重复配置：{}", clazz, playId, apiAll.get(playId).getClass());
                            continue;
                        }

                        apiAll.put(playId, api);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public MatrixBean process(String sportId, String playId, ExtendBean bean) {
        //默认不支持矩阵
        MatrixBean matrixBean = new MatrixBean(1);
        if (!handleApiMap.containsKey(sportId)) {
            return matrixBean;
        }

        if (!handleApiMap.get(sportId).containsKey(playId)) {
            return matrixBean;
        }

        if ("2".equals(sportId)) {
            return matrixBean;
        }

        matrixBean.setRecType(3);
        if (scoreMatrixPlayIds.contains(playId)) {
            matrixBean.setRecType(0);
        }

        matrixBean.setMatrixValueArray(new Long[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_LINE_LENGTH]);
        matrixBean.setMatrixStatusArray(new Integer[MatrixConstant.MATRIX_LINE_LENGTH][MatrixConstant.MATRIX_LINE_LENGTH]);
        MatrixCaclApi api = handleApiMap.get(sportId).get(playId);

        Map<Integer, Long> allResultMap = getAllResultMap(bean.getOrderMoney(), bean.getOdds());

        boolean isCache = false;
        for (int home = 0; home < matrixBean.getMatrixValueArray().length; home++) {
            for (int away = 0; away < matrixBean.getMatrixValueArray()[home].length; away++) {
                Integer result = -1;
                if (isCache) {
                    result = matrixBean.getMatrixStatusArray()[home][away];
                } else {
                    try {
                        result = api.getScoreResult(home, away, bean);
                    } catch (Exception e) {
                        log.error("::{}::矩阵计算,异常:{}", bean.getOrderId() == null ? bean.getUserId() : bean.getOrderId(), e);
                        result = OrderSettleStatus.USER_WIN.getValue();
                    }
                }
                if (!allResultMap.containsKey(result)) {
                    throw new RcsServiceException("矩阵计算,当前结果未知,请核对数据:" + result);
                }
                matrixBean.getMatrixStatusArray()[home][away] = result;
                matrixBean.getMatrixValueArray()[home][away] = allResultMap.get(result);
            }
        }
        String statusStr = MatrixDataZip.queryMatrixStatus(matrixBean.getMatrixStatusArray());
        matrixBean.setStatusZip(statusStr);
        return matrixBean;
    }


    public Map<String, Map<String, MatrixCaclApi>> getHandleApiMap() {
        return handleApiMap;
    }

    private Map<Integer, Long> getAllResultMap(Long betAount, String odds) {
        Map<Integer, Long> map = new HashMap<Integer, Long>();
        // 输
        map.put(1, betAount);
        // 输半
        map.put(2, betAount / 2);
        map.put(3, new BigDecimal(String.valueOf(betAount)).multiply(new BigDecimal(String.valueOf(odds)).subtract(new BigDecimal("1"))).multiply(new BigDecimal("-1")).longValue());// 赢
        map.put(4, new BigDecimal(String.valueOf(betAount)).divide(new BigDecimal("2")).multiply(new BigDecimal(String.valueOf(odds)).subtract(new BigDecimal("1"))).multiply(new BigDecimal("-1")).longValue());// 赢半
        // 平
        map.put(5, 0l);
        return map;
    }

    public static void main(String[] args) {
        System.out.println(scoreMatrixPlayIds.contains("1"));
    }
}
