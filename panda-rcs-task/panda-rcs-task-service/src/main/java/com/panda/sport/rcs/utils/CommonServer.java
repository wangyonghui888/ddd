package com.panda.sport.rcs.utils;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.constants.RedisKey;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.enums.OrderStatusEnum;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.pojo.TOrderDetailExt;
import com.panda.sport.rcs.wrapper.OrderAcceptRejectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  公共处理ext定时任务方法
 * @Date: 2022-07-01 14:05
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CommonServer {
    private final OrderAcceptRejectService orderAcceptRejectService;

    private final RedisClient redisClient;

    private final TOrderDetailExtMapper tOrderDetailExtMapper;
    private static final Integer MAX_NUMBER = 100;

    @Resource(name = "asyncPoolTaskExecutor")
    protected ThreadPoolTaskExecutor asyncPoolTaskExecutor;


    public void commonMethod(List<TOrderDetailExt> list) {
        if (Objects.nonNull(list) && !list.isEmpty()) {
            Map<String, List<TOrderDetailExt>> orderMap = list.stream().collect(Collectors.groupingBy(TOrderDetailExt::getOrderNo));
            log.info("::1666需求扫描订单详情长度:{}::", orderMap.size());
            List<Map<String, List<TOrderDetailExt>>> tempList = mapChunk(orderMap, MAX_NUMBER);
            for (Map<String, List<TOrderDetailExt>> item : tempList) {
                asyncPoolTaskExecutor.execute(() -> {
                    List<String> acceptOrderList = new ArrayList<>();
                    List<String> rejectOrderList = new ArrayList<>();
                    for (Map.Entry<String, List<TOrderDetailExt>> entry : item.entrySet()) {
                        String orderNo = entry.getKey();
                        try {
                            boolean isLock = redisClient.setNX(String.format(RedisKey.LOCK_KEY, orderNo), String.valueOf(System.currentTimeMillis()), 30L);
                            //如果获取到了，表示这个订单已经被处理，直接返回
                            String lockValue = redisClient.get(String.format(RedisKey.LOCK_PROCESSED_KEY, orderNo));
                            if (!isLock || StringUtils.isNotBlank(lockValue)) {
                                continue;
                            }
                            orderAcceptRejectService.sendMessageNew(entry.getValue(), acceptOrderList, rejectOrderList);
                        } catch (Exception e) {
                            log.error("::{}::定时任务扫描EXT表出问题:{}", orderNo, e);
                        } finally {
                            redisClient.delete(String.format(RedisKey.LOCK_KEY, orderNo));
                        }
                    }
                    if (!acceptOrderList.isEmpty()) {
                        Integer updateNum = orderAcceptRejectService.updateOrderDetailExtStatusByOrderNoList(OrderStatusEnum.ORDER_ACCEPT.getCode(), acceptOrderList);
                        log.info("::{}::接单返回条数:{}", JSON.toJSONString(acceptOrderList),updateNum);
                    }
                    if (!rejectOrderList.isEmpty()) {
                        Integer updateNum = orderAcceptRejectService.updateOrderDetailExtStatusByOrderNoList(OrderStatusEnum.ORDER_REJECT.getCode(), rejectOrderList);
                        log.info("::{}::拒单返回条数:{}", JSON.toJSONString(rejectOrderList),updateNum);
                    }

                });
            }
        }
    }

    /**
     * 将map 拆分成多个map
     *
     * @param chunkMap 被拆的 map
     * @param chunkNum 每段的大小
     * @param <k>      map 的 key类 型
     * @param <v>      map 的value 类型
     * @return List
     */
    public static <k, v> List<Map<k, v>> mapChunk(Map<k, v> chunkMap, int chunkNum) {
        if (chunkMap == null || chunkNum <= 0) {
            List<Map<k, v>> list = new ArrayList<>();
            list.add(chunkMap);
            return list;
        }
        Set<k> keySet = chunkMap.keySet();
        Iterator<k> iterator = keySet.iterator();
        int i = 1;
        List<Map<k, v>> total = new ArrayList<>();
        Map<k, v> tem = new HashMap<>();
        while (iterator.hasNext()) {
            k next = iterator.next();
            tem.put(next, chunkMap.get(next));
            if (i == chunkNum) {
                total.add(tem);
                tem = new HashMap<>();
                i = 0;
            }
            i++;
        }
        if (!CollectionUtils.isEmpty(tem)) {
            total.add(tem);
        }
        return total;
    }

//    /**
//     * 将map 拆分成多个map
//     *
//     * @param chunkMap 被拆的 map
//     * @param chunkNum 每段的大小
//     * @param <k>      map 的 key类 型
//     * @param <v>      map 的value 类型
//     * @return List
//     */
//    public static <k, v> List<Map<k, v>> mapChunk(Map<k, v> chunkMap, int chunkNum) {
//        if (chunkMap == null || chunkNum <= 0) {
//            List<Map<k, v>> list = new ArrayList<>();
//            list.add(chunkMap);
//            return list;
//        }
//        Set<k> keySet = chunkMap.keySet();
//        Iterator<k> iterator = keySet.iterator();
//        int i = 1;
//        List<Map<k, v>> total = new ArrayList<>();
//        Map<k, v> tem = new HashMap<>();
//        while (iterator.hasNext()) {
//            k next = iterator.next();
//            tem.put(next, chunkMap.get(next));
//            if (i == chunkNum) {
//                total.add(tem);
//                tem = new HashMap<>();
//                i = 0;
//            }
//            i++;
//        }
//        if (!CollectionUtils.isEmpty(tem)) {
//            total.add(tem);
//        }
//        return total;
//    }
//
////    /**
////     * 计算切分次数
////     */
////    private static Integer countStep(Integer size) {
////        return (size + MAX_NUMBER - 1) / MAX_NUMBER;
////    }
//}
}
