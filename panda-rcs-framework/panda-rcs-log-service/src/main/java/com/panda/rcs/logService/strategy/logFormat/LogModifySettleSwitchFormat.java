package com.panda.rcs.logService.strategy.logFormat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.mapper.StandardMatchTeamRelationMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 操盤日誌(modifySettleSwitch)
 * 設置-提前結算開關
 */
@Component
public class LogModifySettleSwitchFormat extends LogFormatStrategy {
    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog,LogAllBean param ) {
        if(BaseUtils.isTrue(param.getBeforeParams())){
            return null;
        }
        //根據不同操作頁面組裝不同格式
        switch (param.getOperatePageCode()) {
            case 14:
                //早盘操盘-设置
                rcsOperateLog.setOperatePageCode(110);
                break;
            case 17:
                //滚球操盘-设置
                rcsOperateLog.setOperatePageCode(111);
                break;
        }
        rcsOperateLog.setMatchId(param.getMatchId());

        return modifySettleFormat(rcsOperateLog, param);
    }

    private RcsOperateLog modifySettleFormat(RcsOperateLog rcsOperateLog, LogAllBean param) {
        if (Objects.nonNull(param.getMatchPreStatus()) &&
                !param.getMatchPreStatus().equals(param.getBeforeParams().get("matchPreStatus"))) {


            //提前结算是开启状态
            if(param.getMatchPreStatus()==1&&Objects.nonNull(param.getBeforeParams().get("earlySettStr"))){
                //SR AO 切换生成日志
                setRcsOperateLog(rcsOperateLog, param);

            }
            rcsOperateLog.setObjectIdByObj(param.getMatchManageId());
            rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
            rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
            rcsOperateLog.setBeforeValByObj(getStatusName(Integer.parseInt(param.getBeforeParams().get("matchPreStatus").toString())));
            rcsOperateLog.setAfterValByObj(getStatusName(param.getMatchPreStatus()));
            rcsOperateLog.setSportId(param.getSportId());
            rcsOperateLogMapper.insert(rcsOperateLog);
        }
        return null;
    }


    /**
     * 轉換狀態碼
     *
     * @param status
     * @return
     */
    private String getStatusName(Integer status) {
        switch (status) {
            case 0:
                return "关";
            case 1:
                return "开";
            default:
                return "";
        }
    }

    /**
     * 提前结算切换生成日志
     * @param rcsOperateLog
     * @param param
     */
    private void setRcsOperateLog(RcsOperateLog rcsOperateLog, LogAllBean param){
        Map<String, String> jsonMap = JSON.parseObject(param.getBeforeParams().get("earlySettStr").toString(),
                new TypeReference<HashMap<String, String>>() {
        });
        rcsOperateLog.setObjectIdByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setExtObjectIdByObj(param.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(montageEnAndZsIs(param.getTeamList(),param.getMatchId()));
        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        Set set= jsonMap.entrySet();
        for(Object key:set){
            Map.Entry entry = (Map.Entry) key;
            rcsOperateLog.setObjectNameByObj(entry.getKey());
            rcsOperateLog.setAfterValByObj(getStatusName(Integer.parseInt(entry.getValue().toString())));
            rcsOperateLogMapper.insert(rcsOperateLog);
        }


    }


}
