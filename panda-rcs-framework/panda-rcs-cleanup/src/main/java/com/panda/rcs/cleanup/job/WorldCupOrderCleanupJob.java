package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.OrderMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@JobHandler(value = "worldCupOrderCleanupJob")
public class WorldCupOrderCleanupJob extends IJobHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        if(s != null && !"".equals(s)){
            List<String> matchIds = Arrays.asList(s.split(","));
            for (String matchId : matchIds){
                List<String> orderNoLists = orderMapper.getOrderNoByMatchId(Long.parseLong(matchId));
                if(orderNoLists != null && orderNoLists.size() > 0){
                    orderNoLists = orderNoLists.stream().distinct().collect(Collectors.toList());
                    int tempOPage = 0;
                    for (int oi = 0; oi < orderNoLists.size(); oi ++){
                        boolean isOEnd = (oi == (orderNoLists.size() - 1));
                        if (oi != 0 && oi % 5000 == 0 || isOEnd) {
                            List<String> pageOData = orderNoLists.subList(tempOPage * 5000, isOEnd ? orderNoLists.size() : oi);
                            int rowsO = orderMapper.deleteOrderByOrderNo(pageOData);
                            log.info("订单-第{}批->t_order数据清理::，本次清理数据->{}", tempOPage, rowsO);
                            tempOPage ++;
                            Thread.sleep(500);
                        }
                    }
                }

                int orderDetailRows = 0;
                do{
                    orderDetailRows = orderMapper.deleteOrderDetailByMatchIdByLimit(Long.parseLong(matchId), 10000);
                    Thread.sleep(1000);
                } while(orderDetailRows != 0);
            }
        }
        return ReturnT.SUCCESS;
    }

}
