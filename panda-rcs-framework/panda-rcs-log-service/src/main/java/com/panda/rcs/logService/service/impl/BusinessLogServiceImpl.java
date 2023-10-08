package com.panda.rcs.logService.service.impl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.rcs.logService.Enum.AoParameterEnum;
import com.panda.rcs.logService.Enum.SportIdEnum;
import com.panda.rcs.logService.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.rcs.logService.mapper.RcsSysUserMapper;
import com.panda.rcs.logService.mapper.TUserLevelMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.vo.*;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.enums.OperateLogEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 风控日志
 * @author Z9-jing
 */
@Slf4j
@Service
public class BusinessLogServiceImpl {
    @Resource
    private RcsQuotaBusinessLimitLogMapper rcsQuotaBusinessLimitLogMapper;
    @Resource
    private RcsSysUserMapper rcsSysUserMapper;

    @Resource
    private TUserLevelMapper tUserLevelMapper;

    private final  String  SET_CHANGE="动态风控设置";

    private final  String CHATS_SET="商户藏单设置";

    private final  String OPT="藏单阈值";


    private final  String OPEN_ON="球类开关";

    private final  String INSERT="新增";

    private final String UPDATE="编辑";

    private final String delete="删除";

    public void  addBusinessLog(String json) throws Exception {
        log.info("::业务操作誌消費開始-消费数据记录->{}", json);
        BusinessLogAllVo vo= JSONArray.parseArray(json, BusinessLogAllVo.class).get(0);
        switch (vo.getMethod()){
            case "editHideMoneyList" :
                editHideMoneyListName(vo);
                return;
            case "editHideStatusList" :
                editHideStatusListName(vo);
                return;
            case "insertRcsMerchantsHideRange":
                insertRcsMerchantsHideRange(vo);
            case "updateGoalWarnSetMethod":
                updateGoalWarnSetMethod(vo);
                return ;
            case "deleteGoalWarnSetMethod":
                deleteGoalWarnSetMethod(vo);
                return;
            case "addGoalWarnSetMethod":
                addGoalWarnSetMethod(vo);
                return;
                case "batchUpdateConfig":
                batchUpdateConfig(vo);
                    return;
            case "editSwitch":
                editSwitch(vo);
                return;
            default:
                throw new Exception("风控日誌-未有對應方法名稱");
        }


    }


    private void batchUpdateConfig(BusinessLogAllVo vo){
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        BusinessLogAllVo voCode= new ObjectMapper().convertValue(vo.getAfterString()[0],BusinessLogAllVo.class);
        vo.setLevelId(voCode.getLevelId());
        vo.setType(voCode.getType());
        vo.setId(voCode.getId());
        vo.setBqStatus(voCode.getBqStatus());
        vo.setQjStatus(voCode.getQjStatus());
        vo.setMaxMoney(voCode.getMaxMoney());
        vo.setMinMoney(voCode.getMinMoney());
        vo.setMerchantsId(voCode.getMerchantsId());
        vo.setVolumePercentage(voCode.getVolumePercentage());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        vo.setCreateTime(DateUtils.changeDateToString(new Date()));
        vo.setOperateCategory(AoParameterEnum.SET_DATA.getName());
        List<BusinessLogConfigAllVo> voList= JSONObject.parseArray(vo.getBeforeString(), BusinessLogConfigAllVo.class);
         if (null==voList){return;}
        if(voList.size()>=50) {
            //分批生成日志 字段太长
            int pointsDataLimit = 50;
            List<BusinessLogConfigAllVo> newList = new ArrayList<BusinessLogConfigAllVo>();
            for (int i = 0; i < voList.size(); i++) {//分批次处理
                newList.add(voList.get(i));
                if (pointsDataLimit == newList.size() || i == voList.size() - 1) {
                    inBatches(newList, vo);
                    newList.clear();
                }
            }
        }else{
            inBatches(voList, vo);
        }
    }

    public void inBatches(List<BusinessLogConfigAllVo> newList,BusinessLogAllVo vo){
        StringBuffer buffer=new StringBuffer();
        StringBuffer bufferCode=new StringBuffer();
        StringBuffer mBuffer=new StringBuffer();
        StringBuffer mBufferCode=new StringBuffer();

        for (BusinessLogConfigAllVo allVo: newList) {
            if(!vo.getQjStatus().equals(allVo.getQjStatus())
                    ||(vo.getVolumePercentage()==null?0:vo.getVolumePercentage().intValue())!=(allVo.getVolumePercentage()==null?0:allVo.getVolumePercentage().intValue())
                    ||!vo.getMinMoney().equals(allVo.getMinMoney())||!vo.getMaxMoney().equals(allVo.getMaxMoney())){
                if(!StringUtils.isEmpty(mBuffer.toString())){
                    mBuffer.append(";");
                    mBufferCode.append(";");
                }
                mBuffer.append(allVo.getMerchantsCode());
                mBufferCode.append(allVo.getVolumePercentage()==null?"0%":allVo.getVolumePercentage().intValue())
                        .append("%-").append(allVo.getMinMoney()).append("-")
                        .append(allVo.getMaxMoney()).append("-")
                        .append(allVo.getQjStatus()==1?"开":"关");
            }
            if(!vo.getBqStatus().equals(allVo.getBqStatus())||!contrast(vo.getLevelId(),allVo.getLevelId())){
                if(!StringUtils.isEmpty(buffer.toString())){
                    buffer.append(";");
                    bufferCode.append(";");
                }
                buffer.append(allVo.getMerchantsCode());
                String levelString=getLevelString(allVo.getLevelId());
                bufferCode.append(levelString).append("-").append(allVo.getBqStatus()==1?"开":"关");
            }
        }
        if(vo.getType()==1){
            if(null!=vo.getId()){
                vo.setOperateType(AoParameterEnum.SET.getName());
                vo.setParamName(AoParameterEnum.Single_Merchant_Settings.getName());
                vo.setId(null);
            }else{
                vo.setOperateType(AoParameterEnum.SET.getName());
                vo.setParamName(AoParameterEnum.Batch_Settings.getName());
            }

        }else if(vo.getType()==2){
            vo.setOperateType(AoParameterEnum.SET.getName());
            vo.setParamName(AoParameterEnum.Settings.getName());
        }
        else{
            vo.setOperateType(AoParameterEnum.SET.getName());
            vo.setParamName(AoParameterEnum.Exception_batch_settings.getName());
        }

        if(!StringUtils.isEmpty(mBuffer.toString())){
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            rcsQuotaBusinessLimitLog.setObjectId(mBuffer.toString());
            rcsQuotaBusinessLimitLog.setObjectName(AoParameterEnum.Global_missed_orders.getName());
            rcsQuotaBusinessLimitLog.setParamName(vo.getParamName()+"-"+AoParameterEnum.Global_missed_orders.getName());
            rcsQuotaBusinessLimitLog.setBeforeVal(mBufferCode.toString());
            rcsQuotaBusinessLimitLog.setAfterVal(new StringBuffer().
                    append(vo.getVolumePercentage()==null?"0%":vo.getVolumePercentage().intValue())
                    .append("%-").append(vo.getMinMoney()).append("-")
                    .append(vo.getMaxMoney()).append("-")
                    .append(vo.getQjStatus()==1?"开":"关").toString());
            rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);

        }if(!StringUtils.isEmpty(buffer.toString())){
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            rcsQuotaBusinessLimitLog.setObjectId(buffer.toString());
            rcsQuotaBusinessLimitLog.setObjectName(AoParameterEnum.Missing_label.getName());
            rcsQuotaBusinessLimitLog.setParamName(vo.getParamName()+"-"+AoParameterEnum.Missing_label.getName());
            rcsQuotaBusinessLimitLog.setBeforeVal(bufferCode.toString());
            rcsQuotaBusinessLimitLog.setAfterVal(getLevel(vo.getLevelId()).append(vo.getBqStatus()==1?"开":"关").toString());
            rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);
        }





    }

public String getLevelString(String level){
    StringBuffer buffer=new StringBuffer();
    if (!StringUtils.isEmpty(level)) {
        String[] arr = level.split(",");
        for (String s : arr) {
            TUserLevel tUserLevel=tUserLevelMapper.selectById(Long.parseLong(s));
            if(null!=tUserLevel){
                buffer.append(tUserLevel.getLevelName()).append(";");
            }
        }
    }

    return buffer.toString();
}
    public StringBuffer getLevel(List<Long> list){
        StringBuffer buffer=new StringBuffer();
        for (Long s : list) {
                TUserLevel tUserLevel=tUserLevelMapper.selectById(s);
                if(null!=tUserLevel){
                    buffer.append(tUserLevel.getLevelName()).append(";");
                }
            }
        return buffer;
    }






    private Boolean contrast(List<Long> list,String string){
        if(null==list||list.size()==0){
            return StringUtils.isEmpty(string);
        }
        if(StringUtils.isEmpty(string)){
           return (null==list||list.size()==0);
        }
        List<Long> levelIds = Arrays.stream(string.split(","))
                .filter(num -> !StringUtils.isEmpty(num) && org.apache.commons.lang3.StringUtils.isNumeric(num))
                .map(num -> Long.parseLong(num))
                .collect(Collectors.toList());
        return list.containsAll(levelIds)&&levelIds.containsAll(list);
    }
    private void editSwitch(BusinessLogAllVo vo){
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        BusinessLogAllVo voCode= new ObjectMapper().convertValue(vo.getAfterString()[0],BusinessLogAllVo.class);
        vo.setStatus(voCode.getStatus());
        vo.setVolumePercentage(voCode.getVolumePercentage());
        RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
        BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
        rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
        rcsQuotaBusinessLimitLog.setOperateCategory(AoParameterEnum.SET_DATA.getName());
        rcsQuotaBusinessLimitLog.setBeforeVal(getStatusValueCode(vo.getStatus()));
        rcsQuotaBusinessLimitLog.setAfterVal(getStatusValueTypeCode(vo.getStatus()));
        rcsQuotaBusinessLimitLog.setParamName(AoParameterEnum.main_switch.getName());
        rcsQuotaBusinessLimitLog.setOperateType(AoParameterEnum.SET.getName());
        rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);
    }


    private void updateGoalWarnSetMethod(BusinessLogAllVo vo){
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        List<BusinessLogVo> param= JSONObject.parseArray(vo.getAfterString()[0].toString(),BusinessLogVo.class);
        log.info("进球点设置改变值:{}",param);
        vo.setOperateType(UPDATE);
        vo.setParamName(getGoalWarnName());
        vo.setCreateTime(DateUtils.changeDateToString(new Date()));
        vo.setOperateCategory(AoParameterEnum.Goal_point_warning_settings.getName());
        List<BusinessLogVo> beforeBean= JSONObject.parseArray(vo.getBeforeString(),BusinessLogVo.class);
        log.info("进球点设置初始值:{}",beforeBean);
        Map<String,BusinessLogVo> map= new HashMap<>();
        if(null!=beforeBean && !beforeBean.isEmpty()) {
            for (BusinessLogVo voCodeMap : beforeBean) {
                map.put(vo.getId() + "", voCodeMap);
            }
        }
        for (BusinessLogVo voCode: param) {
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            if(map.get(voCode.getGoalWarnSetId().toString())!=null){
                BusinessLogVo before= map.get(voCode.getGoalWarnSetId().toString());
                rcsQuotaBusinessLimitLog.setBeforeVal(getGoalWarnValue(before));
            }else{
                rcsQuotaBusinessLimitLog.setBeforeVal(OperateLogEnum.NONE.getName());
            }
            rcsQuotaBusinessLimitLog.setAfterVal(getGoalWarnValue(voCode));
            rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);
        }



    }
    private void deleteGoalWarnSetMethod(BusinessLogAllVo vo){
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
        BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
        rcsQuotaBusinessLimitLog.setOperateType(delete);
        if(!StringUtils.isEmpty(vo.getBeforeString())){
            BusinessLogVo before=  JSONObject.parseObject(vo.getBeforeString()
                    ,BusinessLogVo.class);
            rcsQuotaBusinessLimitLog.setBeforeVal(getGoalWarnValue(before));
        }else{
            rcsQuotaBusinessLimitLog.setBeforeVal(OperateLogEnum.NONE.getName());
        }
        rcsQuotaBusinessLimitLog.setParamName(getGoalWarnName());
        rcsQuotaBusinessLimitLog.setAfterVal(OperateLogEnum.NONE.getName());
        rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
        rcsQuotaBusinessLimitLog.setOperateCategory(AoParameterEnum.Goal_point_warning_settings.getName());
        rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);


    }
    private void addGoalWarnSetMethod(BusinessLogAllVo vo){
        BusinessLogVo voCode= new ObjectMapper().convertValue(vo.getAfterString()[0],BusinessLogVo.class);
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
        BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
        rcsQuotaBusinessLimitLog.setOperateType(INSERT);
        rcsQuotaBusinessLimitLog.setBeforeVal(OperateLogEnum.NONE.getName());
        rcsQuotaBusinessLimitLog.setParamName(getGoalWarnName());
        rcsQuotaBusinessLimitLog.setAfterVal(getGoalWarnValue(voCode));
        rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
        rcsQuotaBusinessLimitLog.setOperateCategory(AoParameterEnum.Goal_point_warning_settings.getName());
        rcsQuotaBusinessLimitLogMapper.insert(rcsQuotaBusinessLimitLog);
    }

    private void insertRcsMerchantsHideRange(BusinessLogAllVo vo){
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        List<BusinessLogAllVo> voList= JSONObject.parseArray(vo.getBeforeString(), BusinessLogAllVo.class);
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        for (BusinessLogAllVo allVo: voList) {
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            rcsQuotaBusinessLimitLog.setObjectId(allVo.getMerchantsId().toString());
            rcsQuotaBusinessLimitLog.setObjectName(allVo.getMerchantsCode());
            rcsQuotaBusinessLimitLog.setBeforeVal(OperateLogEnum.NONE.getName());
            rcsQuotaBusinessLimitLog.setParamName(getSportIdString(allVo.getSportId())+INSERT);
            String value=allVo.getHideMoney()==null?"":allVo.getHideMoney().toString();
            rcsQuotaBusinessLimitLog.setAfterVal(value+" - "+getStatusValue(allVo.getStatus()));
            rcsQuotaBusinessLimitLog.setOperateType(CHATS_SET);
            rcsQuotaBusinessLimitLog.setOperateCategory(SET_CHANGE);
            rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
            if(!allVo.getStatus().equals(vo.getStatus())) {
                list.add(rcsQuotaBusinessLimitLog);
            }

        }
        rcsQuotaBusinessLimitLogMapper.bathInserts(list);
    }

    private String getSportIdString(Integer sportId){
      return  SportIdEnum.getBySportId(Long.parseLong(sportId.toString())).getName()+"-";
    }
    private String getStatusValueCode(Integer value){
        if(null==value){
            return "-";
        }
        switch (value) {
            case 2:
                return "开";
            case 1:
                return "关";
        }
        return  "_";
    }
    private String getStatusValueTypeCode(Integer value){
        if(null==value){
            return "-";
        }
        switch (value) {
            case 1:
                return "开";
            case 2:
                return "关";
        }
        return  "_";
    }



  private String getStatusValue(Integer value){
        if(null==value){
            return "-";
        }
      switch (value) {
          case 0:
              return "开";
          case 1:
              return "关";
      }
      return  "_";
  }

    private String getStatusValueType(Integer value){
        if(null==value){
            return "-";
        }
        switch (value) {
            case 0:
                return "开";
            case 1:
                return "关";
        }
        return  "_";
    }

    private String getStatusValueTypeOpen(Integer value){
        if(null==value){
            return "-";
        }
        switch (value) {
            case 0:
                return "关";
            case 1:
                return "开";
        }
        return  "_";
    }

    /**
     *  组合修改记录
     * @param vo
     * @return
     */
  private String getGoalWarnValue(BusinessLogVo vo){
       StringBuffer buff=new StringBuffer();
        buff.append("联赛名称:").append(vo.getStandardMatchName()).append("<br>")
                .append("投注人数:").append(vo.getBetUserNum()).append("<br>")
                .append("单笔投注额:").append(vo.getMaxAmount()).append("<br>")
                .append("进球前秒数:").append(vo.getBeforeGoalSeconds());
        return buff.toString();
    }

    /**
     *  组合修改记录
     * @param
     * @return
     */
    private String getGoalWarnName(){
        StringBuffer buff=new StringBuffer();
        buff.append("联赛名称:").append("<br>")
                .append("投注人数:").append("<br>")
                .append("单笔投注额:").append("<br>")
                .append("进球前秒数:");
        return buff.toString();
    }









    private void editHideMoneyListName(BusinessLogAllVo vo){
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        List<BusinessLogAllVo> voList= JSONObject.parseArray(vo.getBeforeString(), BusinessLogAllVo.class);
        for (BusinessLogAllVo allVo: voList) {
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            rcsQuotaBusinessLimitLog.setObjectId(allVo.getMerchantsId().toString());
            rcsQuotaBusinessLimitLog.setObjectName(allVo.getMerchantsCode());
            rcsQuotaBusinessLimitLog.setBeforeVal(allVo.getHideMoney().toString());
            rcsQuotaBusinessLimitLog.setParamName(getSportIdString(allVo.getSportId())+OPT);
            rcsQuotaBusinessLimitLog.setAfterVal(vo.getHideMoney()==null?"_":vo.getHideMoney().toString());
            rcsQuotaBusinessLimitLog.setOperateType(CHATS_SET);
            rcsQuotaBusinessLimitLog.setOperateCategory(SET_CHANGE);
            rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
            if(!rcsQuotaBusinessLimitLog.getAfterVal().equals(rcsQuotaBusinessLimitLog.getBeforeVal())){
                list.add(rcsQuotaBusinessLimitLog);
            }
        }
        rcsQuotaBusinessLimitLogMapper.bathInserts(list);
    }


    private void editHideStatusListName(BusinessLogAllVo vo){
        List<RcsQuotaBusinessLimitLog> list=new ArrayList<>();
        List<BusinessLogAllVo> voList= JSONObject.parseArray(vo.getBeforeString(), BusinessLogAllVo.class);
        RcsSysUser sysUser=rcsSysUserMapper.selectById(vo.getUserId()==null?0:vo.getUserId());
        vo.setUserName(sysUser==null?"-":sysUser.getUserCode());
        for (BusinessLogAllVo allVo: voList) {
            RcsQuotaBusinessLimitLog rcsQuotaBusinessLimitLog=new RcsQuotaBusinessLimitLog();
            BeanUtils.copyProperties(vo,rcsQuotaBusinessLimitLog);
            rcsQuotaBusinessLimitLog.setObjectId(allVo.getMerchantsId().toString());
            rcsQuotaBusinessLimitLog.setObjectName(allVo.getMerchantsCode());
            rcsQuotaBusinessLimitLog.setBeforeVal(getStatusValueType(allVo.getStatus()));
            rcsQuotaBusinessLimitLog.setParamName(getSportIdString(allVo.getSportId())+OPEN_ON);
            rcsQuotaBusinessLimitLog.setAfterVal(getStatusValueTypeOpen(allVo.getStatus()));
            rcsQuotaBusinessLimitLog.setOperateType(CHATS_SET);
            rcsQuotaBusinessLimitLog.setOperateCategory(SET_CHANGE);
            rcsQuotaBusinessLimitLog.setCreateTime(DateUtils.changeDateToString(new Date()));
            if(!allVo.getStatus().equals(vo.getStatus())) {
                list.add(rcsQuotaBusinessLimitLog);
            }

        }
        rcsQuotaBusinessLimitLogMapper.bathInserts(list);
    }

}
