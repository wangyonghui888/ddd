package com.panda.rcs.cleanup.utils;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Map;

/**
 * @author :  sean
 * @Project Name :  panda-rcs-cleanup
 * @Package Name :  com.panda.rcs.cleanup.utils
 * @Description :  TODO
 * @Date: 2022-03-02 11:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class MarketPlaceUtils {

    public static List<Long> MARKET_BALANCE_SPORT = Lists.newArrayList(1L,4L,11L,12L,13L,14L,15L,16L);
    public static List<Long> PLACE_BALANCE_SPORT = Lists.newArrayList(2L,3L,5L,6L,7L,8L,9L,10L);
    /**
     * @Description   //根据子玩法来给盘口分组
     * @Param [map]
     * @Author  sean
     * @Date   2022/3/1
     * @return java.lang.String
     **/
    public static String matchPlaySubPlayGroupKey(Map<String,String> map){
        StringBuffer buffer = new StringBuffer();
        if (ObjectUtils.isNotEmpty(map)){
            buffer.append(map.get("match_id")).append("_").append(map.get("play_id")).append("_").append(map.get("sub_play_id"));
        }
        return buffer.toString();
    }
//    /**
//     * @Description   //赛事，种类开赛时间分组
//     * @Param [map]
//     * @Author  sean
//     * @Date   2022/3/2
//     * @return java.lang.String
//     **/
//    public static Long matchSportBeginTimeGroupKey(Map<Long,Long> map){
//        Long match = NumberUtils.LONG_ZERO;
//        if (ObjectUtils.isNotEmpty(map)){
//            match = map.get("id");
//        }
//        return match;
//    }
}
