package com.panda.sport.rcs.enums;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @Description : 乒乓球
 * @Author : Kwon
 * @Date : 2022年9月28日15:19:47
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface PingPong {

    /**
     * 获取占位符玩法
     *
     * @return
     */
    static List<Long> getPlaceholderPlayIds() {
        return Lists.newArrayList(175L, 176L, 177L, 178L, 179L, 203L);
    }

    /**
     * 是否是占位符玩法
     *
     * @param playId
     * @return
     */
    static boolean isPlaceholderPlay(Long playId) {
        return getPlaceholderPlayIds().contains(playId);
    }

    /**
     * 是否支持M模式构建盘口
     * @param playId 玩法ID
     * @return
     */
    static boolean isExistPlay(Long playId) {
        //153-全场独赢（Margin）、172-全场让分（spread）、173-全场总分（spread）
        if(Arrays.asList(153L,172L,173L).contains(playId)){
            return true;
        }
        return false;
    }

    /**
     * 是否是大小玩法
     * @param playId
     * @return
     */
    static boolean BsPlays(Long playId) {
        //173L-全场总分 归属于大小玩法
        if(Arrays.asList(173L).contains(playId)){
            return true;
        }
        return false;
    }

    static boolean isManualBuildMarket(Long playId) {
        //172-全场让分（spread）、173-全场总分（spread）
        if(Arrays.asList(172L,173L).contains(playId)){
            return true;
        }
        return false;
    }
}
