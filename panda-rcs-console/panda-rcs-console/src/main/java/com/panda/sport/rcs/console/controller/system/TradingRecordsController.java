package com.panda.sport.rcs.console.controller.system;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.console.common.utils.ExcelListener;
import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsMatchConfigLogsService;
import com.panda.sport.rcs.console.service.RcsMatchMarketConfigLogsService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.controller.system
 * @Description :  TODO
 * @Date: 2020-02-10 11:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Controller
@RequestMapping("tradingRecords")
@Slf4j
public class TradingRecordsController {
    @Autowired
    private RcsMatchMarketConfigLogsService rcsMatchMarketConfigLogsService;
    @Autowired
    private RcsMatchConfigLogsService rcsMatchConfigLogsService;
    @Autowired
    private RedisClient redisClient;

    /**
     * @return java.lang.String
     * @Description //进入操盘记录主页
     * @Param []
     * @Author kimi
     * @Date 2020/2/10
     **/
    @RequestMapping("tradingRecordsManage")
    public String tradingRecordsManage() {
        log.info("进入记录查询");
        return "tradingRecords/tradingRecordsManage";
    }

    @ResponseBody
    @RequestMapping(value = "/getRcsMatchMarketConfigLogs", method = RequestMethod.POST)
    public PageDataResult getRcsMatchMarketConfigLogs(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize,
                                                      Integer matchId, Long marketId) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) {
                pageNum = 1;
            }
            if (null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
//            if (matchId != null && marketId == null) {
//                pdr = rcsMatchConfigLogsService.getStatusList(matchId, pageNum, pageSize);
//            } else {
//                pdr = rcsMatchMarketConfigLogsService.getStatusList(matchId, marketId, pageNum, pageSize);
//            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("pageNum", pageNum);
            params.put("pageSize", pageSize);
            if (matchId != null) params.put("matchId", matchId);
            if (marketId != null) params.put("marketId", marketId);

            pdr = rcsMatchConfigLogsService.queryTradeLogList(params);

            log.info("StatusList查询=pdr:" + pdr);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pdr;
    }

    @PostMapping("/importExcel")
    public String importExcel(@RequestParam("file") MultipartFile file) {
        long startOrigin = System.currentTimeMillis();
        String linkId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("linkId", linkId);
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (!".xls".equalsIgnoreCase(suffix) && !".xlsx".equalsIgnoreCase(suffix)) {
            log.error("::{}::动态藏单比例数据导入异常-excel格式不正确", linkId);
            MDC.remove("linkId");
            return "/error";
        }
        log.info("::{}::动态藏单比例数据excle导入开始", linkId);
        ExcelListener listener = new ExcelListener();
        List<ExcelVO> collect = new ArrayList<>();
        InputStream inputStream = null;
        ExcelReader excelReader = null;
        try {
            inputStream = file.getInputStream();
            excelReader = new ExcelReader(inputStream, suffix.equals("xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX, null, listener);
            excelReader.read(new Sheet(1, 1, ExcelVO.class));
            List<Object> list = listener.getDatas();
            if (list.size() > 1) {
                collect = list.stream().map(m -> (ExcelVO) m).filter(f -> !f.getUserId().equals("1") && !f.getUserId().equals("2")).collect(Collectors.toList());
                listener.clearDatas();
            }
            if (!CollectionUtils.isEmpty(collect)) {
                Iterator<ExcelVO> iterators = collect.iterator();
                while (iterators.hasNext()) {
                    ExcelVO vo = iterators.next();
                    String userTag = redisClient.hGet("risk:trade:rcs_limit_tag", vo.getUserId());
                    if ("115".equals(userTag) || "116".equals(userTag)) {
                        iterators.remove();
                    }
                }
                List<List<ExcelVO>> lists = Lists.partition(collect, 20000);

                CountDownLatch countDownLatch = new CountDownLatch(lists.size());
                log.info("::{}::动态藏单比例数据入库开始，处理线程数:{}", linkId, lists.size());
                long start = System.currentTimeMillis();
                for (List<ExcelVO> listSub : lists) {
                    rcsMatchConfigLogsService.insertUserConfig(listSub, countDownLatch);
                }
                try {
                    countDownLatch.await(); //保证之前的所有的线程都执行完成，才会走下面的；
                } catch (Exception e) {
                    log.error("::{}::动态藏单比例数据入库阻塞异常:{}", linkId, e.getMessage());
                }
                long end = System.currentTimeMillis();
                log.info("::{}::动态藏单比例数据入库结束，耗时:{}.开始异步刷新缓存", linkId, end - start);
                final Map<String, List<ExcelVO>> excelVOGroupByUserIdList = collect.stream().collect(Collectors.groupingBy(ExcelVO::getUserId));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //更新缓存
                        String key = "risk:trade:rcs_user_sport_type_bet_limit_config:%s";
                        excelVOGroupByUserIdList.forEach((k, v) -> {
                            String betFormat = String.format(key, k);
                            for (ExcelVO vo : v) {
                                redisClient.hSet(betFormat, vo.getSportId(), String.valueOf(vo.getPercentAge()));
                            }
                        });
                    }
                }).start();
            }
        } catch (Exception e) {
            log.error("::{}::动态藏单比例数据excle导入异常:{}", linkId, e.getMessage());
            return "/error";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) {
                    log.error("::{}::动态藏单比例数据excle导入,关闭输入流异常:{}", linkId, ex.getMessage());
                }
            }
            if (excelReader != null) {
                excelReader.finish();
            }
            MDC.remove("linkId");
        }
        long endOrigin = System.currentTimeMillis();
        log.info("::{}::动态藏单比例数据excle导入成功,总计耗时：{}", linkId, endOrigin - startOrigin);
        return tradingRecordsManage();
    }

    @PostMapping("/clearUserBetConfig")
    public String clearUserBetConfig() {
        String linkId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("linkId", linkId);
        //获取到所有配置百分比的用户
        List<String> userList = null;
        try {
            userList = rcsMatchConfigLogsService.getUserBetRate();
        } catch (Exception ex) {
            log.error("::{}::动态藏单比例数据清空-获取获取出错:{}", linkId, ex.getMessage());
            MDC.remove("linkId");
            return "/error";
        }
        //清掉缓存

        if (!CollectionUtils.isEmpty(userList)) {
            final List<String> userIds = userList;
            new Thread(() -> {
                String key = "risk:trade:rcs_user_sport_type_bet_limit_config:%s";
                userIds.forEach(u -> {
                    redisClient.delete(String.format(key, u));
                });
            }).start();
        }
        //清空表数据
        log.info("::{}::动态藏单比例数据清空-表数据清空开始", linkId);
        try {
            rcsMatchConfigLogsService.deleteUserBetRate();
        } catch (Exception ex) {
            log.error("::{}::动态藏单比例数据清空-表数据清空出错:{}", linkId, ex.getMessage());
            MDC.remove("linkId");
            return "/error";
        }
        log.info("::{}::动态藏单比例数据清空-表数据清空结束", linkId);
        MDC.remove("linkId");
        return tradingRecordsManage();
    }


}
