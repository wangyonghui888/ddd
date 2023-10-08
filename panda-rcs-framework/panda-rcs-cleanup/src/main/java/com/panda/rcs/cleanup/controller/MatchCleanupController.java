package com.panda.rcs.cleanup.controller;

import com.alibaba.fastjson.JSON;
import com.panda.rcs.cleanup.dto.ClearRedisHistoricalDataReqDto;
import com.panda.rcs.cleanup.job.ClearRedisHistoricalDataJob;
import com.panda.rcs.cleanup.job.OrderExtCleanupMongoDataJob;
import com.panda.rcs.cleanup.mapper.OrderMapper;
import com.panda.rcs.cleanup.mapper.SettleMapper;
import com.panda.rcs.cleanup.service.IRcsUserConfigNewService;
import com.panda.rcs.cleanup.service.MatchService;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.connection.CRC16;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(value = "赛事相关数据清理")
@RequestMapping(value = "clean")
@RestController
public class MatchCleanupController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SettleMapper settleMapper;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private OrderExtCleanupMongoDataJob orderExtCleanupMongoDataJob;
    @Autowired
    private ClearRedisHistoricalDataJob clearRedisHistoricalDataJob;

    @Autowired
    IRcsUserConfigNewService rcsUserConfigNewService;

    @ApiOperation(value = "UserConfig数据转换新表任务")
    @GetMapping(value = "/convertOldDataTask")
    public void convertOldDataTask() {
        rcsUserConfigNewService.convertOldDataTask();
    }


//    @ApiOperation(value = "UserConfig数据反向验证")
//    @GetMapping(value = "/verifySportIds")
//    public void verifySportIds() {
//        rcsUserConfigNewService.verifySportIds();
//    }


    @ApiOperation(value = "赛事关联表数据清理")
    @DeleteMapping(value = "/match")
    public void cleanMatch() {
        Long starTime = System.currentTimeMillis();
        matchService.cleanupMatchBusiData();
        log.info("::过期赛事关联数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
    }

    @ApiOperation(value = "不存在赛事关联数据清理")
    @DeleteMapping(value = "/no/match")
    public void cleanNotMatch() {
        Long starTime = System.currentTimeMillis();
        matchService.cleanupNotExistMatchLinkData();
        log.info("::不存在赛事关联数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
    }

    @ApiOperation(value = "订单表数据清理")
    @DeleteMapping(value = "/order")
    public void cleanOrder() {
        Long starTime = System.currentTimeMillis();
        orderMapper.deleteOrder(DataUtils.getTimestamp(7));
        orderMapper.deleteOrderDetail(DataUtils.getTimestamp(7));
        log.info("::订单表数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
    }

    @ApiOperation(value = "结算表数据清理")
    @DeleteMapping(value = "/settle")
    public void cleanSettle() {
        Long starTime = System.currentTimeMillis();
        settleMapper.deleteSettle(DataUtils.getTimestamp(7));
        settleMapper.deleteSettleDetail(DataUtils.getTimestamp(7));
        log.info("::结算表数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
    }

    @ApiOperation(value = "接拒单表数据清理")
    @DeleteMapping(value = "/order/ext")
    public void cleanOrderExt() {
        Long starTime = System.currentTimeMillis();
        orderMapper.deleteOrderDetailExt(DataUtils.getTimestamp(1));
        log.info("::接拒单表数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
    }

    @ApiOperation(value = "接拒单表mongo数据清理")
    @DeleteMapping(value = "/mongo/ext")
    public void cleanMongoOrderExt() throws Exception {
        orderExtCleanupMongoDataJob.execute("");
    }

    @ApiOperation(value = "清除Redis历史数据")
    @DeleteMapping("/clearRedisHistoricalData")
    public Boolean clearRedisHistoricalData(@RequestBody ClearRedisHistoricalDataReqDto reqDto) {
        try {
            clearRedisHistoricalDataJob.execute(JSON.toJSONString(reqDto));
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @ApiOperation(value = "清理赛事模板数据")
    @DeleteMapping(value="/clearMatchTemplate")
    public void clearMatchTemplate(){
//        matchService.cleanTemplateData();
    }

//    public static void main(String[] args) {
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410891724784}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410622672156}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013411096720842}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410608644741}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410846286838}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410638698626}".getBytes()) % 16384);
//
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410825220286}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410921982388}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013411073254456}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410555565245}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410803038771}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410978067471}".getBytes()) % 16384);
//
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410592384272}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410492154908}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410821383206}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013411080268612}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410625933250}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:auto:job:order:ext:{5013410746902483}".getBytes()) % 16384);
//
//        System.out.println("------------------------------------------------------------------");
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013410885247608}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013411104991021}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013410528241638}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013410504628225}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013410901558987}".getBytes()) % 16384);
//        System.out.println(CRC16.crc16("rcs:order:detai:ext:order:{5013410611821838}".getBytes()) % 16384);
//
//    }
}
