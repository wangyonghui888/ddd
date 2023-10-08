package com.panda.sport.rcs.third.factory;

import com.panda.sport.rcs.third.service.third.ThirdOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Beulah
 * @date 2023/3/27 19:57
 * @description 三方api策略工厂
 */
@Service
@Slf4j
public class ThirdStrategyFactory {

    private static final Map<String, ThirdOrderService> thirdStrategy = new ConcurrentHashMap<>();

    /**
     * 获取对应策略
     *
     * @param third 三方标志
     * @return 三方api实现
     */
    public static ThirdOrderService getThirdStrategy(String third) {
        return thirdStrategy.get(third);
    }


    /**
     * 检查
     *
     * @return 注册列表
     */
    public static Map<String, ThirdOrderService> getThirdStrategyList() {
        return thirdStrategy;
    }


    /**
     * 策略注册
     *
     * @param third    三方标志
     * @param thirdApi 三方api实现
     */
    public static void register(String third, ThirdOrderService thirdApi) {
        if (third == null || thirdApi == null) {
            return;
        }
        thirdStrategy.put(third, thirdApi);
    }

}
