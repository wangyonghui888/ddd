package com.panda.sport.rcs.service;

import com.alibaba.fastjson.JSONArray;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.utils.TradeUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 危险联赛业务日志收集
 * */
@Slf4j
@Service
public class RiskTeamServiceImpl {
    private final String logTitle="危险球队池管理";
    private  final  String isYes="是";
    private  final  String isNo="否";
    private  final  String INSERT_NAME="新增危险球队";
    private  final  String DELETE_NAME="删除危险球队";

    private final  String FIELD_STATUS = "是否生效";

    private final  String FIELD_LEVEL = "危险级别";
    public static final String logCode="10060";

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    /**
     * 新增联赛日志
     * */
    public void insertTeamLog(RiskTeamSaveReqVo vo){
        List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
        try {
            //limitLogList.add(setTournamentLog(vo.getTeamId().intValue(),vo.getTeamNameCn(),INSERT_NAME,"",vo.getTeamNameCn()));
            limitLogList.add(setTournamentLogIP(vo.getTeamId().intValue(),vo.getTeamNameCn(),INSERT_NAME,"",vo.getTeamNameCn(),vo.getIp()));
            //新增球队危险等级
            limitLogList.add(setTournamentLogIP(vo.getTeamId().intValue(),vo.getTeamNameCn(),FIELD_LEVEL,"",vo.getRiskLevel()+"",vo.getIp()));
            //是否生效的日志
            limitLogList.add(setTournamentLogIP(vo.getTeamId().intValue(),vo.getTeamNameCn(),FIELD_STATUS,"",vo.getStatus()== 0 ? isNo:isYes+"",vo.getIp()));
            String arrString = JSONArray.toJSONString(limitLogList);
            log.info("危险联赛池管理->新增危险联赛池管理条数{}",limitLogList.size());
            producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, logCode,arrString);
        }catch (Exception e){
            log.info("::{}::危险联赛池管理->新增危险联赛池管异常{}",vo.getTeamId(),e.getMessage());
        }
    }
    /**
     * 删除联赛日志
     * */
    public void  deleteTeamLog(RiskTeamDelReqVo vo){
        List<RcsQuotaBusinessLimitLog> delLimitLogList=new ArrayList<>();
        try {
            //delLimitLogList.add(setTournamentLog(vo.getTeamId().intValue(),vo.getTeamName(),DELETE_NAME,vo.getTeamName(),""));
            delLimitLogList.add(setTournamentLogIP(vo.getTeamId().intValue(),vo.getTeamName(),DELETE_NAME,vo.getTeamName(),"",vo.getIp()));
            String arrString = JSONArray.toJSONString(delLimitLogList);
            log.info("危险联赛池管理->新增危险联赛池管理条数{}",delLimitLogList.size());
            producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, logCode,arrString);
        }catch (Exception e){
            log.info("::{}::危险联赛池管理->删除危险联赛池管异常{}",vo.getTeamId(),e.getMessage());
        }
    }
    /**
     * 修改联系日志
     * */
    public void  updateTeamLog(RiskTeamUpdateReqVo vo){
        List<RcsQuotaBusinessLimitLog> updateLogList=new ArrayList<>();
        try {
            updateLogList=setBusinessLimitLog(vo);
            if(updateLogList.size() >0){
                for(RcsQuotaBusinessLimitLog ip : updateLogList){
                    ip.setIp(vo.getIp());
                }
                String arrString = JSONArray.toJSONString(updateLogList);
                log.info("危险联赛池管理->新增危险联赛池管理条数{}",updateLogList.size());
                producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, logCode,arrString);
            }
        }catch (Exception e){
            log.info("::{}::危险联赛池管理->删除危险联赛池管异常{}",vo.getTeamId(),e.getMessage());
        }
    }
    /**
     * 修改信息
     * */
    private List<RcsQuotaBusinessLimitLog> setBusinessLimitLog(RiskTeamUpdateReqVo reqVo) throws Exception {
        List<RcsQuotaBusinessLimitLog> businessLimitLogs=new ArrayList<>();
        if(reqVo.getRiskLevel() != reqVo.getOldRiskLevel()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getRiskLevel()){
                afterVal = reqVo.getRiskLevel()+"";
            }
            if(null != reqVo.getOldRiskLevel()){
                beforeVal = reqVo.getOldRiskLevel()+"";
            }
            RcsQuotaBusinessLimitLog businessLimitLog = setTournamentLog(reqVo.getTeamId().intValue(),reqVo.getTeamName(),FIELD_LEVEL,beforeVal,afterVal);
            businessLimitLogs.add(businessLimitLog);
        }
        if(reqVo.getStatus() != reqVo.getOldStatus()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getStatus()){
                afterVal = reqVo.getStatus() == 0 ? isNo:isYes;
            }
            if(null != reqVo.getOldStatus()){
                beforeVal = reqVo.getOldStatus() == 0 ? isNo:isYes;
            }
            RcsQuotaBusinessLimitLog limitLog = setTournamentLog(reqVo.getTeamId().intValue(),reqVo.getTeamName(),FIELD_STATUS,beforeVal,afterVal);
            businessLimitLogs.add(limitLog);
        }
        return  businessLimitLogs;
    }

    private List<RcsQuotaBusinessLimitLog> setBusinessLimitLogIP(RiskTeamUpdateReqVo reqVo) throws Exception {
        List<RcsQuotaBusinessLimitLog> businessLimitLogs=new ArrayList<>();
        if(reqVo.getRiskLevel() != reqVo.getOldRiskLevel()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getRiskLevel()){
                afterVal = reqVo.getRiskLevel()+"";
            }
            if(null != reqVo.getOldRiskLevel()){
                beforeVal = reqVo.getOldRiskLevel()+"";
            }
            RcsQuotaBusinessLimitLog businessLimitLog = setTournamentLogIP(reqVo.getTeamId().intValue(),reqVo.getTeamName(),FIELD_LEVEL,beforeVal,afterVal,reqVo.getIp());
            businessLimitLogs.add(businessLimitLog);
        }
        if(reqVo.getStatus() != reqVo.getOldStatus()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getStatus()){
                afterVal = reqVo.getStatus() == 0 ? isNo:isYes;
            }
            if(null != reqVo.getOldStatus()){
                beforeVal = reqVo.getOldStatus() == 0 ? isNo:isYes;
            }
            RcsQuotaBusinessLimitLog limitLog = setTournamentLogIP(reqVo.getTeamId().intValue(),reqVo.getTeamName(),FIELD_STATUS,beforeVal,afterVal,reqVo.getIp());
            businessLimitLogs.add(limitLog);
        }
        return  businessLimitLogs;
    }
    /**
     *
     * */
    private RcsQuotaBusinessLimitLog setTournamentLog(Integer  teamId, String teamName, String paramName,
                                                      String beforeVal, String afterVal) throws Exception {

        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        String userId= TradeUserUtils.getUserId().toString();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(teamId.toString());
        limitLoglog.setObjectName(teamName);
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userId);
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
    private RcsQuotaBusinessLimitLog setTournamentLogIP(Integer  teamId, String teamName, String paramName,
                                                      String beforeVal, String afterVal,String IP) throws Exception {

        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        String userId= TradeUserUtils.getUserId().toString();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(teamId.toString());
        limitLoglog.setObjectName(teamName);
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userId);
        limitLoglog.setIp(IP);
        //limitLoglog.setUserName(linkedHashMap.get("userName").toString());
        return limitLoglog;
    }
}
