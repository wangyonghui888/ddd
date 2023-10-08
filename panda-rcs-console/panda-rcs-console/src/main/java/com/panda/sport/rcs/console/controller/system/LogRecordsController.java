package com.panda.sport.rcs.console.controller.system;

import com.alibaba.fastjson.JSON;
import com.panda.sport.rcs.console.pojo.LogRecord;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.LogBeanService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.panda.sport.rcs.console.mq.impl.LogHttpConsumer.RCSCACHE_LOG_HEADER;

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
@RequestMapping("logRecords")
@Slf4j
public class LogRecordsController {
    @Autowired
    private LogBeanService logBeanService;
    @Autowired
    private RedisClient redisClient;

    /**
     * @return java.lang.String
     * @Description //进入操盘记录主页
     * @Param []
     * @Author kimi
     * @Date 2020/2/10
     **/
    @RequestMapping("log")
    public String tradingRecordsManage() {
        log.info("进入记录查询");
        return "logRecords/log";
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public PageDataResult getRcsMatchMarketConfigLogs(@RequestParam("pageNum") Integer pageNum,
                                                      @RequestParam("pageSize") Integer pageSize,
                                                      LogRecord bean) {
        PageDataResult result = new PageDataResult();
        try {
            if (null == pageNum) {
                pageNum = 1;
            }
            if (null == pageSize) {
                pageSize = 10;
            }
            result = logBeanService.getLogRecords(bean, pageNum, pageSize);

            log.info("StatusList查询=result:" + result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getHeader", method = RequestMethod.POST)
    public Map<String, Object> getHeader(String code) {
        if (StringUtils.isNotBlank(code)) {
            String title = redisClient.get(String.format(RCSCACHE_LOG_HEADER, code));
            //String abc = "{'currentPage': '当前查询页', 'pageSize': '数据数量', 'sportId': '体育类型'}";
            Map mapType = JSON.parseObject(title, Map.class);
            return mapType;
        }
        return new HashMap<>();
    }
}
