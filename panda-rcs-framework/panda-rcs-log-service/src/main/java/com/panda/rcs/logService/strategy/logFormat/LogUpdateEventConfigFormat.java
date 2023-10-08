package com.panda.rcs.logService.strategy.logFormat;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  koala
 * @Project Name :  panda-rcs-trade-group
 * @Package Name :  com.panda.sport.rcs.trade.strategy.logFormat
 * @Description :  接距配置编辑
 * @Date: 2023-03-11 16:40
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class LogUpdateEventConfigFormat extends LogFormatStrategy {
    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean req) {
        //LogBean基礎參數設置
        initialLogBean(rcsOperateLog, req);
        return null;
    }

    private Map<String, List<LogAllBean>> getMap(List<LogAllBean> list) {
        Map<String, List<LogAllBean>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)) {
            map = list.stream().collect(Collectors.groupingBy(e -> e.getEventType()));
        }
        if (!map.containsKey("safety")) {
            map.put("safety", new ArrayList<>());
        }
        if (!map.containsKey("danger")) {
            map.put("danger", new ArrayList<>());
        }
        if (!map.containsKey("closing")) {
            map.put("closing", new ArrayList<>());
        }
        if (!map.containsKey("reject")) {
            map.put("reject", new ArrayList<>());
        }
        return map;
    }

    private void initialLogBean(RcsOperateLog rcsOperateLog, LogAllBean config) {
        rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setBehavior("接拒事件配置");
        String rejectType = config.getRejectType() == 1 ? "常规接拒" : "提前结算接拒";
        rcsOperateLog.setObjectName(config.getCategorySetName() + "-" + rejectType);
        rcsOperateLog.setUserId(config.getUserId());
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setOperatePageCode(config.getOperatePageCode());
        //修改前
        Map<String, List<LogAllBean>> beforeMap = this.getMap(BaseUtils.mapList(config.getBeforeParams()));
        //修改前
        Map<String, List<LogAllBean>> afterMap = this.getMap(config.getEvents());
        for (Map.Entry<String, List<LogAllBean>> entry : afterMap.entrySet()) {
            String key = entry.getKey();
            List<LogAllBean> val = entry.getValue();
            List<LogAllBean> beforeEventCodeList = beforeMap.get(key);
            switch (key) {
                case "safety":
                    rcsOperateLog.setParameterName(OperateLogEnum.SAFETY_EVENT.getName());
                    break;
                case "danger":
                    rcsOperateLog.setParameterName(OperateLogEnum.DANGER_EVENT.getName());
                    break;
                case "closing":
                    rcsOperateLog.setParameterName(OperateLogEnum.CLOSING_EVENT.getName());
                    break;
                case "reject":
                    rcsOperateLog.setParameterName(OperateLogEnum.REJECT_EVENT.getName());
                    break;
                default:
                    break;
            }
            this.eventCodeCheck(rcsOperateLog, val, beforeEventCodeList);
        }
    }

    /**
     * 事件过滤
     */
    private void eventCodeCheck(RcsOperateLog rcsOperateLog, List<LogAllBean> afterEventCodeList, List<LogAllBean> beforeEventCodeList) {
        try {
            if (!equalList(afterEventCodeList, beforeEventCodeList)) {
                //修改后
                StringBuffer afterBuffer = new StringBuffer();
                //修改前
                StringBuffer beforeBuffer = new StringBuffer();
                afterEventCodeList.forEach(s -> {
                    afterBuffer.append(s.getEventName() + "\n");
                });
                beforeEventCodeList.forEach(s -> {
                    beforeBuffer.append(s.getEventName() +"\n");
                });
                String str1 = afterBuffer.length() > 0 ? afterBuffer.toString() : "";
                rcsOperateLog.setAfterVal(str1);
                String str2 = beforeBuffer.length() > 0 ? beforeBuffer.toString() : "";
                rcsOperateLog.setBeforeVal(str2);
                this.pushMessage(rcsOperateLog);
            }
        } catch (Exception e) {
            log.error("编辑接距配置异常:", e);
        }
    }


    private boolean equalList(List<LogAllBean> list1, List<LogAllBean> list2) {
        List<String> temp1 = list1.stream().map(LogAllBean::getEventCode).collect(Collectors.toList());
        List<String> temp2 = list2.stream().map(LogAllBean::getEventCode).collect(Collectors.toList());
        return list1.size() == list2.size() && temp1.containsAll(temp2);
    }

    private void pushMessage(RcsOperateLog rcsOperateLog) {
        rcsOperateLogMapper.insert(rcsOperateLog);
    }
}
