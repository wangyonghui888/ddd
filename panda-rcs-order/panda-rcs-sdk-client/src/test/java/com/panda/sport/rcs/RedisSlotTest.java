package com.panda.sport.rcs;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.data.rcs.dto.limit.enums.LimitDataTypeEnum;
import com.panda.sport.sdk.constant.LimitRedisKeys;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import redis.clients.util.JedisClusterCRC16;

import java.util.ArrayList;
import java.util.List;

/**
 * redis槽位计算测试
 */
public class RedisSlotTest {


    @Data
    public static class SlotBT {
        private int start;
        private int end;
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 16; i++) {
//            System.out.print(i + "=" + JedisClusterCRC16.getSlot(getLimitKey(1, i, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT)));
//            System.out.println("       ," + i + "=" + JedisClusterCRC16.getSlot(getLimitKey(2, i, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT)));
//        }
//
//        for (int i = 1; i < 11; i++) {
//            System.out.println(i * 1638);
//        }

        SlotBT slotBT1 = new SlotBT();
        slotBT1.setStart(1);
        slotBT1.setEnd(1367);
        SlotBT slotBT2 = new SlotBT();
        slotBT2.setStart(1368);
        slotBT2.setEnd(3276);
        SlotBT slotBT3 = new SlotBT();
        slotBT3.setStart(3277);
        slotBT3.setEnd(4914);
        SlotBT slotBT4 = new SlotBT();
        slotBT4.setStart(4915);
        slotBT4.setEnd(6553);
        SlotBT slotBT5 = new SlotBT();
        slotBT5.setStart(6554);
        slotBT5.setEnd(8191);
        SlotBT slotBT6 = new SlotBT();
        slotBT6.setStart(8192);
        slotBT6.setEnd(9829);
        SlotBT slotBT7 = new SlotBT();
        slotBT7.setStart(9830);
        slotBT7.setEnd(11468);
        SlotBT slotBT8 = new SlotBT();
        slotBT8.setStart(11469);
        slotBT8.setEnd(13106);
        SlotBT slotBT9 = new SlotBT();
        slotBT9.setStart(13107);
        slotBT9.setEnd(14745);
        SlotBT slotBT10 = new SlotBT();
        slotBT10.setStart(14746);
        slotBT10.setEnd(16383);
        List<SlotBT> slotBTS = new ArrayList<>();
        slotBTS.add(slotBT1);
        slotBTS.add(slotBT2);
        slotBTS.add(slotBT3);
        slotBTS.add(slotBT4);
        slotBTS.add(slotBT5);
        slotBTS.add(slotBT6);
        slotBTS.add(slotBT7);
        slotBTS.add(slotBT8);
        slotBTS.add(slotBT9);
        slotBTS.add(slotBT10);

        String [] matchIds = "2334836,2336913,2336915,2337436,2339409,2339410,2339412,2341967,2345182,2345416,2345543,2345544,2345541,2345552,2336545,2341971,2344786,2345485,2345549,2339414,2340424,2340425,2340426,2344803,2345558,2345557,2337437,2345556,2337439,2337879,2338183,2339743,2342893,2345211,2345341,2345475,2345608,2345621,2345589,2337440,2337936,2337937,2345164,2345646,2295303,2334285,2334839,2337441,2337443,2337444,2337446,2337447,2337448,2337450,2337451,2337910,2339744,2340063,2341540,2341966,2341968,2342895,2344539,2345261,2345264,2345286,2345398,2333380,2336467,2341454,2302266,2302286,2337452,2337453,2345480,2333160,2313132,2329564,2333159,2335883,2337445,2337455,2337457,2337458,2337459,2338423,2338971,2339415,2339745,2341453,2341972,2344540,2345238,2345267,2345348,2337460,2337461,2337462,2337464,2341452,2335862,2335908,2337456,2337465,2337466,2337845,2338425,2341973,2341974,2345292,2345293,2329521,2336916,2337467,2337939,2339282,2339416,2341975,2342711,2342978,2342979,2342984,2342985,2342986,2342987,2342988,2345265,2345604,2337468,2344918,2330122,2333381,2333485,2333501,2335884,2337469,2339286,2339417,2341451,2342989,2342992,2345290,2295304,2296557,2302875,2311015,2311016,2311017,2311018,2311019,2311020,2311021,2311022,2311023,2311024,2327398,2327408,2330194,2334515,2335656,2335662,2335885,2335886,2335887,2335888,2335889,2335890,2335943,2336870,2336871,2336872,2336873,2336874,2336875,2336876,2336877,2336879,2336880,2336882,2336883,2336886,2337470,2337938,2337945,2339118,2339177,2339178,2339179,2339180,2339316,2339374,2339418,2339419,2339420,2339421,2339423,2339424,2339425,2339426,2339427,2339428,2339431,2339432,2339434,2339435,2339436,2339437,2339438,2341039,2341040,2341041,2341977,2341979,2341981,2343837,2343838,2343839,2343840,2343841,2343842,2343843,2343844,2343858,2302265,2302267,2302268,2302284,2302285,2302287,2302288,2324657,2333503,2333540,2333541,2333542,2337471,2341982,2343846,2335942,2345792,2345789,2345790,2335891,2329530,2345835,2345837,2337946,2345861,2343847,2330921,2335701,2337196,2337474,2337475,2337476,2339746,2341983,2326463,2335894,2337478,2337479,2337481,2338454,2339040,2339439,2332375,2333341,2333504,2336623,2338219,2338232,2338431,2338435,2338437,2338440,2338441,2338442,2338444,2338445,2338446,2338447,2338448,2338449,2338451,2338452,2342709,2343234,2332376,2345550,2332365,2332367,2338455,2338457,2338459,2339747,2343848,2345551,2345760,2345796,2345797,2346104,2346140,2326464,2338458,2333408,2338233,2338460,2338461,2338462,2338464,2338469,2344736,2345257,2333390,2333391,2340683,2345767,2326465,2346206,2339749,2332026,2346253,2346254,2346259,2346275,2346272,2346265,2339750,2346234,2346276,2346301,2346332,2346378,2346260,2346335,2346361,2346264,2332027,2332028,2346381,2346392,2346412,2346454,2346237,2346293,2346433,2346458,2346456,2346486,2338722,2339361,2346232,2346285,2346286,2345836,2346541,2346537,2346527,2346545,2332029,2345245,2346025,2346538,2346298,2332030,2332031,2338723,2344541,2345838,2345840,2346300,2346562,2338724,2340486,2341984,2341985,2346563,2335896,2336254,2339366,2340256,2341265,2341986,2341992,2344542,2344804,2345798,2345813,2346388,2346494,2346495,2346496,2346552,2345799,2346544,2345814,2345246,2346030,2340166,2340487,2345818,2346619,2346620,2345832,2346627,2332032,2337962,2339441,2341042,2344007,2344543,2346497,2346498,2346555,2346622,2346625,2346650,2340205,2346617,2340488,2344008,2346499,2346556,2340489,2340490,2340491,2303304,2338277,2339751,2340492,2340493,2340497,2340498,2340499,2341357,2341987,2344805,2345833,2345841,2345843,2345844,2346185,2346258,2346419,2346452,2346560,2346602,2346603,2345834,2345842,2340500,2340501,2337967,2346680,2302315,2302328,2302329,2302331,2310395,2336256,2336490,2338934,2339442,2340504,2340506,2341988,2345845,2344806,2334841,2334842,2338245,2338310,2338725,2339443,2339752,2339753,2345846,2346597,2346647,2346657".split(",");

        Integer[] result1 = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Integer[] result2 = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int times = 500;


        for (int i = 0; i < matchIds.length; i++) {
            for (int j = 1; j < 100; j++) {
                String matchType ="0";
                String sportId = "1";
                String matchId = matchIds[i];
                String limitKey = LimitRedisKeys.getMatchSingleBetPlayLimitKey(1, LimitDataTypeEnum.USER_SINGLE_BET_LIMIT, matchId, matchType, String.valueOf(j));
                String limitKeyKoala = LimitRedisKeys.getSinglePlayLimitKey(sportId.toString(), matchId, matchType, String.valueOf(j));
                String redisKey = String.format("rcs:task:match:event:%s", matchIds[i]);
                int a1 = JedisClusterCRC16.getSlot(limitKeyKoala);
                int a2 = JedisClusterCRC16.getSlot(limitKey + ":singlePay");
                int a3 = JedisClusterCRC16.getSlot(limitKey + ":playTotal");
                int a4 = JedisClusterCRC16.getSlot(limitKey + ":singleBet");
                for (int jj = 0; jj < slotBTS.size(); jj++) {
                    SlotBT slotBT = slotBTS.get(jj);
                    if (a1 >= slotBT.getStart() && a1 <= slotBT.getEnd()) {
                        result1[jj] = result1[jj] + 1;
                    }

                    if (a2 >= slotBT.getStart() && a2 <= slotBT.getEnd()) {
                        result1[jj] = result1[jj] + 1;
                    }

                    if (a3 >= slotBT.getStart() && a3 <= slotBT.getEnd()) {
                        result1[jj] = result1[jj] + 1;
                    }

                    if (a4 >= slotBT.getStart() && a4 <= slotBT.getEnd()) {
                        result1[jj] = result1[jj] + 1;
                    }
                }
            }
        }
        System.out.println(JSONObject.toJSONString(result1));
//
//        result1 = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        result2 = new Integer[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//        for (int i = 0; i < times; i++) {
//            int a1 = JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:" + ( i) + ":matchType:0");
//
//            int a2 = JedisClusterCRC16.getSlot(( i) + ":rcs:redis:mts:contact:config:matchId:matchType:0");
//
//            for (int j = 0; j < slotBTS.size(); j++) {
//                SlotBT slotBT = slotBTS.get(j);
//                if (a1 >= slotBT.getStart() && a1 <= slotBT.getEnd()) {
//                    result1[j] = result1[j] + 1;
//                }
//
//                if (a2 >= slotBT.getStart() && a2 <= slotBT.getEnd()) {
//                    result2[j] = result2[j] + 1;
//                }
//            }
//        }
//        System.out.println(times+"次数据key散列在末尾的槽位计算情况：    rcs:redis:mts:contact:config:matchId:？:matchType:0");
//        System.out.println(JSONObject.toJSONString(result1));
//        System.out.println(times+"次数据key散列在开始的槽位计算情况：    ？:rcs:redis:mts:contact:config:matchId:matchType:0");
//        System.out.println(JSONObject.toJSONString(result2));
////
//        System.out.println("--------------------------");
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267758:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267758:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267759:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267759:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267757:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267757:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267762:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("rcs:redis:mts:contact:config:matchId:2267762:matchType:1"));
//        System.out.println("--------------------------");
//        System.out.println(JedisClusterCRC16.getSlot("2267758:rcs:redis:mts:contact:config:matchId:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("2267758:rcs:redis:mts:contact:config:matchId:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("2267759:rcs:redis:mts:contact:config:matchId:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("2267759:rcs:redis:mts:contact:config:matchId:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("2267757:rcs:redis:mts:contact:config:matchId:matchType:1"));
//        System.out.println(JedisClusterCRC16.getSlot("2267757:rcs:redis:mts:contact:config:matchId:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("2267762:rcs:redis:mts:contact:config:matchId:matchType:0"));
//        System.out.println(JedisClusterCRC16.getSlot("2267762:rcs:redis:mts:contact:config:matchId:matchType:1"));
//        System.out.println("---------------------------");
//        System.out.println(JedisClusterCRC16.getSlot("2_2267757"));
//        System.out.println(JedisClusterCRC16.getSlot("2_2267762"));
//        System.out.println(JedisClusterCRC16.getSlot("2_2267758"));
//        System.out.println(JedisClusterCRC16.getSlot("2_2267759"));
    }

}
