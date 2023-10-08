package com.panda.rcs.logService.strategy.logFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.panda.rcs.logService.Enum.AoParameterEnum;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.Enum.SportIdEnum;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.vo.AoParameterTemplateVo;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *  Ao参数修改
 * @author Z9-jing
 */

@Component
@Slf4j
public class LogAoCsChangeFormat extends LogFormatStrategy {
    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean aoParameterTemplateReq) {
        StandardMatchInfo info=getStandardMatchInfo(aoParameterTemplateReq.getMatchId());
        List<AoParameterTemplateVo> aoParameterTemplateVoList =
                JSON.parseArray(aoParameterTemplateReq.getAoConfigValue(), AoParameterTemplateVo.class);
        String aoConfig=aoParameterTemplateReq.getBeforeParams().get("aoConfigValue")==null?
                aoParameterTemplateReq.getBeforeString():
                aoParameterTemplateReq.getBeforeParams().get("aoConfigValue").toString();
        List<AoParameterTemplateVo> beforeVoList =
                JSON.parseArray(aoConfig, AoParameterTemplateVo.class);
        log.info("aoParameterTemplateVoList:{} code:{}",beforeVoList,aoParameterTemplateVoList);
        String matchName =montageEnAndZsIs(aoParameterTemplateReq.getTeamList(),aoParameterTemplateReq.getMatchId());
        if(MatchTypeEnum.EARLY.getId().equals(info.getMatchStatus())){
            rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId());
        }else {
            rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        }
        rcsOperateLog.setMatchId(aoParameterTemplateReq.getMatchId());
        rcsOperateLog.setObjectIdByObj(aoParameterTemplateReq.getTemplateId());
        rcsOperateLog.setObjectNameByObj(matchName);
        rcsOperateLog.setExtObjectIdByObj(info.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(matchName);
        insertRcsOperateLog(rcsOperateLog,aoParameterTemplateVoList,beforeVoList);
        return null;
    }

    /**
     *  对比数据生成日志
     * @param rcsOperateLog
     * @param afterList
     * @param beforeList
     */
    public void insertRcsOperateLog(RcsOperateLog rcsOperateLog, List<AoParameterTemplateVo> afterList,
                                    List<AoParameterTemplateVo> beforeList){
        Map<String, AoParameterTemplateVo> mapList=listConvertMap(afterList);
        int type=0;
        int typeEx=0;
        for (AoParameterTemplateVo  vo:beforeList) {
            AoParameterTemplateVo afterVo=mapList.get(vo.getTempType());
            log.info("aoParameterTemplateVoList:{}:code{}:值:{}",vo,afterVo,vo.equals(afterVo));
            if(!vo.equals(afterVo)){
                if(vo.getTempType().contains(AoParameterEnum.EX.getName())){
                    type++;
                    insertContrast(rcsOperateLog,afterVo,vo,type);
                }else{
                    typeEx++;
                    insertContrast(rcsOperateLog,afterVo,vo,typeEx);
                }

            }
        }

    }

    /**
     *  对比值生成日志
     * @param afterVo
     * @param before
     * @param type
     */
    public void insertContrast(RcsOperateLog rcsOperateLog, AoParameterTemplateVo afterVo,AoParameterTemplateVo before,Integer type ){
         if(!afterVo.getFtDrawAdj().equals(before.getFtDrawAdj())){
             assignmentInsert(rcsOperateLog,afterVo.getTempType(), afterVo.getFtDrawAdj().toString(),
                     before.getFtDrawAdj().toString(), AoParameterEnum.ftDrawAdj.getCode());
         }if(!afterVo.getHtDrawAdj().equals(before.getHtDrawAdj())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getHtDrawAdj().toString(),
                    before.getHtDrawAdj().toString(), AoParameterEnum.htDrawAdj.getCode());
        }if(!afterVo.getHtSix().equals(before.getHtSix())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getHtSix().toString(),
                    before.getHtSix().toString(), AoParameterEnum.htSix.getCode());
        }if(!afterVo.getOneFiveThree().equals(before.getOneFiveThree())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getOneFiveThree().toString(),
                    before.getOneFiveThree().toString(), AoParameterEnum.oneFiveThree.getCode());
        }if(!afterVo.getSevenFiveFt().equals(before.getSevenFiveFt())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getSevenFiveFt().toString(),
                    before.getSevenFiveFt().toString(), AoParameterEnum.sevenFiveFt.getCode());
        }if(!afterVo.getSixSevenFive().equals(before.getSixSevenFive())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getSixSevenFive().toString(),
                    before.getSixSevenFive().toString(), AoParameterEnum.sixSevenFive.getCode());
        }if(!afterVo.getThreeHt().equals(before.getThreeHt())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getThreeHt().toString(),
                    before.getThreeHt().toString(), AoParameterEnum.threeHt.getCode());
        }if(!afterVo.getZeroOneFive().equals(before.getZeroOneFive())){
            assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getZeroOneFive().toString(),
                    before.getZeroOneFive().toString(), AoParameterEnum.zeroOneFive.getCode());
        }if(type==0){
            if(!afterVo.getOneInjTime().equals(before.getOneInjTime())){
                assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getOneInjTime().toString(),
                        before.getOneInjTime().toString(), AoParameterEnum.oneInjTime.getCode());
            }  if(!afterVo.getTwoInjTime().equals(before.getTwoInjTime())){
                assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getTwoInjTime().toString(),
                        before.getTwoInjTime().toString(), AoParameterEnum.twoInjTime.getCode());
            } if(!afterVo.getRefresh().equals(before.getRefresh())){
                assignmentInsert(rcsOperateLog,afterVo.getTempType(),afterVo.getRefresh().toString(),
                        before.getRefresh().toString(), AoParameterEnum.refresh.getCode());
            }

        }


    }

    /**
     *  赋值生成日志
     * @param rcsOperateLog
     * @param tempType
     * @param after
     * @param before
     * @param name
     */
    public void assignmentInsert(RcsOperateLog rcsOperateLog,String tempType,String after,String before,String name ){
        log.info("aoParameterTemplateVoList:{}",rcsOperateLog);
        rcsOperateLog.setParameterName(AoParameterEnum.getCodeByName(tempType)+name);
        rcsOperateLog.setExtObjectName(getParameterName(tempType));
        rcsOperateLog.setAfterVal(after);
        rcsOperateLog.setBeforeVal(before);
        rcsOperateLogMapper.insert(rcsOperateLog);

    }

    /**
     *  对应的操作对象名称
     * @param type
     * @return
     */
    public String getParameterName(String type){
        if(type.contains(AoParameterEnum.EX.getName())){
            return AoParameterEnum.AO_Extra.getName();
        }else{
            return AoParameterEnum.AO_Regular.getName();
        }

    }


    /**
     *  list转换成map
     * @param afterList
     * @return
     */
    public Map<String, AoParameterTemplateVo> listConvertMap(List<AoParameterTemplateVo> afterList){
        Map<String, AoParameterTemplateVo> mapList=new HashMap<>(afterList.size());
        for (AoParameterTemplateVo  vo:afterList) {
            mapList.put(vo.getTempType(),vo);
        }
        return mapList;
    }

    /**
     * 获取赛事类型 1 ：早盘 ，2： 滚球盘， 3： 冠军盘
     */
    private String convertMatchType(Integer matchType, Integer sportId) {
        String objectName = null;
        if (1 == matchType) {
            objectName = "早盘";
        } else if (2 == matchType) {
            objectName = "滚球盘";
        } else if (3 == matchType) {
            objectName = "冠军盘";
        }
        return SportIdEnum.getBySportId(Long.valueOf(sportId)).getName() + "-" + objectName;
    }
}



