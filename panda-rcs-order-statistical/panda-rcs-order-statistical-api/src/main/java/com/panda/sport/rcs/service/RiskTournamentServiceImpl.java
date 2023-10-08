package com.panda.sport.rcs.service;

import com.alibaba.fastjson.JSONArray;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.common.vo.api.request.TyRiskAddRiskTournamentReqVo;
import com.panda.sport.rcs.common.vo.api.request.TyRiskDelRiskTournamentReqVo;
import com.panda.sport.rcs.common.vo.api.request.TyRiskUpdateLevelTournamentReqVo;
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
public class RiskTournamentServiceImpl {
    private final String logTitle="危险联赛池管理";
    private  final  String isYes="是";
    private  final  String isNo="否";
    private  final  String INSERT_NAME="新增危险联赛";
    private  final  String DELETE_NAME="删除危险联赛";

    private final  String FIELD_STATUS = "是否生效";

    private final  String FIELD_LEVEL = "危险级别";
    public static final String logCode="10050";

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    /**
     * 新增联赛日志
     * */
    public void insertTournamentLog(TyRiskAddRiskTournamentReqVo vo){
        try {
            List<RcsQuotaBusinessLimitLog> limitLogList=new ArrayList<>();
            //limitLogList.add(setTournamentLog(vo.getId(),vo.getTournamentNameCn(),INSERT_NAME,"",vo.getTournamentNameCn()));
            limitLogList.add(setTournamentLogIP(vo.getId(),vo.getTournamentNameCn(),INSERT_NAME,"",vo.getTournamentNameCn(),vo.getIp()));
            //新增联赛时，增加危险等级、是否生效日志
            limitLogList.add(setTournamentLogIP(vo.getId(),vo.getTournamentNameCn(),FIELD_LEVEL,"",vo.getRiskLevel()+"",vo.getIp()));
            limitLogList.add(setTournamentLogIP(vo.getId(),vo.getTournamentNameCn(),FIELD_STATUS,"",Integer.parseInt(vo.getStatus())== 0 ? isNo:isYes+"",vo.getIp()));
            String arrString = JSONArray.toJSONString(limitLogList);
            log.info("危险联赛池管理->新增危险联赛池管理条数{}",limitLogList.size());
            producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, logCode,arrString);
        }catch (Exception e){
            log.info("::{}::危险联赛池管理->新增危险联赛池管异常{}",vo.getId(),e.getMessage());
        }
    }
    /**
     * 删除联赛日志
     * */
    public void  deleteTournamentLog(TyRiskDelRiskTournamentReqVo vo){
        List<RcsQuotaBusinessLimitLog> delLimitLogList=new ArrayList<>();
        try {
            //delLimitLogList.add(setTournamentLog(vo.getId().intValue(),vo.getName(),DELETE_NAME,vo.getName(),""));
            delLimitLogList.add(setTournamentLogIP(vo.getId().intValue(),vo.getName(),DELETE_NAME,vo.getName(),"",vo.getIp()));
            String arrString = JSONArray.toJSONString(delLimitLogList);
            log.info("危险联赛池管理->新增危险联赛池管理条数{}",delLimitLogList.size());
            producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,null, logCode,arrString);
        }catch (Exception e){
            log.info("::{}::危险联赛池管理->删除危险联赛池管异常{}",vo.getId(),e.getMessage());
        }
    }
    /**
     * 修改联系日志
     * */
    public void  updateTournamentLog(TyRiskUpdateLevelTournamentReqVo vo){
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
            log.info("::{}::危险联赛池管理->删除危险联赛池管异常{}",vo.getId(),e.getMessage());
        }
    }
    /**
     * 修改信息
     * */
    private List<RcsQuotaBusinessLimitLog> setBusinessLimitLog(TyRiskUpdateLevelTournamentReqVo reqVo) throws Exception {
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
            RcsQuotaBusinessLimitLog businessLimitLog = setTournamentLog(reqVo.getId().intValue(),reqVo.getName(),FIELD_LEVEL,beforeVal,afterVal);
            businessLimitLogs.add(businessLimitLog);
        }
        if(reqVo.getStatus() != reqVo.getOldStatus()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getStatus()){
                afterVal = reqVo.getStatus() == 0 ? isNo:isYes;
            }
            if(null != reqVo.getOldStatus()){
                beforeVal = reqVo.getOldStatus() ==0 ? isNo:isYes;
            }
            RcsQuotaBusinessLimitLog limitLog = setTournamentLog(reqVo.getId().intValue(),reqVo.getName(),FIELD_STATUS,beforeVal,afterVal);
            businessLimitLogs.add(limitLog);
        }
        return  businessLimitLogs;
    }

    private List<RcsQuotaBusinessLimitLog> setBusinessLimitLogIP(TyRiskUpdateLevelTournamentReqVo reqVo) throws Exception {
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
            RcsQuotaBusinessLimitLog businessLimitLog = setTournamentLogIP(reqVo.getId().intValue(),reqVo.getName(),FIELD_LEVEL,beforeVal,afterVal,reqVo.getIp());
            businessLimitLogs.add(businessLimitLog);
        }
        if(reqVo.getStatus() != reqVo.getOldStatus()){
            String beforeVal="";
            String afterVal ="";
            if(null != reqVo.getStatus()){
                afterVal = reqVo.getStatus() == 0 ? isNo:isYes;
            }
            if(null != reqVo.getOldStatus()){
                beforeVal = reqVo.getOldStatus() ==0 ? isNo:isYes;
            }
            RcsQuotaBusinessLimitLog limitLog =  setTournamentLogIP(reqVo.getId().intValue(),reqVo.getName(),FIELD_LEVEL,beforeVal,afterVal,reqVo.getIp());
            businessLimitLogs.add(limitLog);
        }
        return  businessLimitLogs;
    }
    /**
     *
     * */
    private RcsQuotaBusinessLimitLog setTournamentLog(Integer  tournamentId, String tournamentName, String paramName,
                                                      String beforeVal, String afterVal) throws Exception {

        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        String userId= TradeUserUtils.getUserId().toString();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tournamentId.toString());
        limitLoglog.setObjectName(tournamentName);
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

    private RcsQuotaBusinessLimitLog setTournamentLogIP(Integer  tournamentId, String tournamentName, String paramName,
                                                      String beforeVal, String afterVal,String IP) throws Exception {

        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        String userId= TradeUserUtils.getUserId().toString();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(tournamentId.toString());
        limitLoglog.setObjectName(tournamentName);
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
