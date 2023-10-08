package com.panda.rcs.logService.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.Enum.SportIdEnum;
import com.panda.rcs.logService.LogServiceBootstrap;
import com.panda.rcs.logService.Enum.TradeStatusEnum;
import com.panda.rcs.logService.mapper.*;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogParameters;
import com.panda.rcs.logService.strategy.logFormat.*;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.*;
import com.panda.sport.rcs.enums.OperateLogEnum;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class LogDataChangeServiceImpl {
    @Autowired
    private LogAoCsChangeFormat logAoCsChangeFormat;
    @Autowired
    private LogOddsModelChangeFormat logOddsModelChangeFormat;
    @Autowired
    private LogSubOddsChangeFormat logSubOddsChangeFormat;
    @Autowired
    private LogOddsChangeFormat  logOddsChangeFormat;
    @Autowired
    private LogDataSourceChangeFormat logDataSourceChangeFormat;
    @Autowired
    private LogUpdateMarketConfigFormat logUpdateMarketConfigFormat;
    @Autowired
    private LogMarketDisableFormat logMarketDisableFormat;
    @Autowired
    private LogUpdateMarketTradeTypeFormat  logUpdateMarketTradeTypeFormat;
    @Autowired
    private LogUpdateMarketValueFormat logUpdateMarketValueFormat;
    @Autowired
    private LogUpdateTournamentLevelFormat logUpdateTournamentLevelFormat;
    @Autowired
    private LogTemplateSelectionFormat logTemplateSelectionFormat;
    @Autowired
    private LogTemplateDeleteFormat  logTemplateDeleteFormat;
    @Autowired
    private LogTemplateUpdateFormat logTemplateUpdateFormat;
    @Autowired
    private LogUpdatePlayOddsConfigFormat  logUpdatePlayOddsConfigFormat;
    @Autowired
    private LogModifyBaiJiaConfigFormat logModifyBaiJiaConfigFormat;
    @Autowired
    private LogModifyMargainRefFormat logModifyMargainRefFormat;
    @Autowired
    private LogRemoveMargainRefFormat logRemoveMargainRefFormat;
    @Autowired
    private LogUpdateMarketWaterFormat logUpdateMarketWaterFormat;
    @Autowired
    private LogChangeStatusSourceFormat logChangeStatusSourceFormat;
    @Autowired
    private LogChangeEventSourceFormat logChangeEventSourceFormat;
    @Autowired
    private LogUpdateMarketStatusFormat logUpdateMarketStatusFormat;
    @Autowired
    private LogModifyMatchPayValFormat logModifyMatchPayValFormat;
    @Autowired
    private LogModifySettleSwitchFormat logModifySettleSwitchFormat;
    @Autowired
    private LogModifyScoreSourceFormat logModifyScoreSourceFormat;
    @Autowired
    private LogModifyTemplateEventFormat logModifyTemplateEventFormat;
    @Autowired
    private LogModifyPlayMargainFormat logModifyPlayMargainFormat;
    @Autowired
    private LogModifyMatchTempByLevelTempFormat logModifyMatchTempByLevelTempFormat;
    @Autowired
    private LogModifySpecialIntervalFormat logModifySpecialIntervalFormat;
    @Autowired
    private LogUpdateEventAndTimeConfigFormat logUpdateEventAndTimeConfigFormat;
    @Autowired
    private LogCopyEventAndTimeConfigFormat logCopyEventAndTimeConfigFormat;
    @Autowired
    private LogUpdateEventConfigFormat logUpdateEventConfigFormat;
    @Autowired
    private LogUpdateAutoOpenMarketStatusFormat logUpdateAutoOpenMarketStatusFormat;
    @Autowired
    private LogMatchSpecEventQuitFormat logMatchSpecEventQuitFormat;
    @Autowired
    private LogUpdateMatchSpecEventProbFormat logUpdateMatchSpecEventProbFormat;
    @Autowired
    private LogMatchSpecEventFormat logMatchSpecEventFormat;
    @Autowired
    private LogUpdateMarketHeadGapFormat logUpdateMarketHeadGapFormat;
    @Autowired
    private LogReductionWaterFormat logReductionWaterFormat;
    @Autowired
    private LogWaterDiffRelevanceFormat logWaterDiffRelevanceFormat;
    @Autowired
    private LogConfigChangeWeightFormat logConfigChangeWeightFormat;
    @Autowired
    private LogUpdateShowFormat logUpdateShowFormat;
    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;
    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Autowired
    private MarketCategorySetMapper marketCategorySetMapper;

    @Autowired
    private RcsTournamentTemplateMapper rcsTournamentTemplateMapper;

    private  final String TYPE="type";
    private final String STATUS="status";
    private  final String DIS_PLAY_SORT="displaySort";
    private  final String NAME="name";
    private final String LANGUAGE="language";

    /**
     * 根據方法名稱判斷要導向的 Format
     *
     * @param name 方法名稱
     * @return 對應的 LogFormatStrategy
     * @throws Exception 若給予的方法找不到對應的 LogFormatStrategy
     */
   public RcsOperateLog filterMethod(String name,RcsOperateLog rcsOperateLog, LogAllBean bean) throws Exception {
       log.info("转换后的值:{}:name:{}",bean,name);
       switch (name) {
            case "updateAoParameterTemplate":
                //AO参数调整赛制
                return logAoCsChangeFormat.formatLogBean(rcsOperateLog,bean);
            case "updateShow":
                return logUpdateShowFormat.formatLogBean(rcsOperateLog,bean);
            case "updateOddsMode":
                //次要玩法赔率模式调整赔率
                return logOddsModelChangeFormat.formatLogBean(rcsOperateLog,bean);
            case "updateOddsValue":
                //次要玩法调整赔率
                return logSubOddsChangeFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMarketOddsValue":
            case "updateMarketAutoRatio": // 篮球马来赔
                //調整賠率
                return logOddsChangeFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyPlayOddsConfig":
                //切换数据源
                return logDataSourceChangeFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMatchMarketConfig":
                //調價窗口參數調整
                return logUpdateMarketConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "marketDisable":
                //設置-盘口弃用
                return logMarketDisableFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMarketTradeType":
                //切换操盘模式
                return logUpdateMarketTradeTypeFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMatchMarketValue":
                //新增/調整盤口
                return logUpdateMarketValueFormat.formatLogBean(rcsOperateLog,bean);
            case "updateTournamentLevel":
                //聯賽模板-联赛属性
                return logUpdateTournamentLevelFormat.formatLogBean(rcsOperateLog,bean);
            case "updateTournamentTemplate":
                //联赛模板日志需求-模板选择
                return logTemplateSelectionFormat.formatLogBean(rcsOperateLog,bean);
            case "removeSpecialTemplate":
                //联赛模板日志需求-模板删除
                return logTemplateDeleteFormat.formatLogBean(rcsOperateLog,bean);
            case "update":
                //联赛模板日志需求-模板修改
                return logTemplateUpdateFormat.formatLogBean(rcsOperateLog,bean);
            case "updatePlayOddsConfig":
                //聯賽模板-玩法賠率源設置
                return logUpdatePlayOddsConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyBaiJiaConfig":
                //更新赛事百家赔数据
                return logModifyBaiJiaConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyMargainRef":
                //分時節點-调整操盘参数
                return logModifyMargainRefFormat.formatLogBean(rcsOperateLog,bean);
            case "removeMargainRef":
                //分時節點-模板刪除
                return logRemoveMargainRefFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMarketWater":
                //調水差 獨贏玩法
                return logUpdateMarketWaterFormat.formatLogBean(rcsOperateLog,bean);
            case "matchChangeStatusSource":
                //切数据源-赛事状态源
                return logChangeStatusSourceFormat.formatLogBean(rcsOperateLog,bean);
            case "changeStatusSource":
                //切数据源-赛事状态源,篮球
                return logChangeStatusSourceFormat.formatLogBean(rcsOperateLog,bean);
            case "matchChangeEventSource":
                //切数据源-实时事件源
                return logChangeEventSourceFormat.formatLogBean(rcsOperateLog,bean);
            case "changeEventSource":
                //切数据源-实时事件源,篮球
                return logChangeEventSourceFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMarketStatus":
            case "marketStatus":
                //修改操盘状态
                return logUpdateMarketStatusFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyMatchPayVal":
                //操盤設置-商戶/用戶 單場賠付限額
                return logModifyMatchPayValFormat.formatLogBean(rcsOperateLog,bean);
            case "modifySettleSwitch":
                //操盤設置-提前結算開關
                return logModifySettleSwitchFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyTemplate":
                //操盤設置-比分源
                return logModifyScoreSourceFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyTemplateEvent":
                //操盤設置-誰先開球/角球/進球/事件
                return logModifyTemplateEventFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyPlayMargain":
                //操盤設置-盤口參數調整-最大盤口數/盤口賠付預警/支持串關/賠率(水差)變動幅度/自動關盤時間設置
                return logModifyPlayMargainFormat.formatLogBean(rcsOperateLog,bean);
            case "modifyMatchTempByLevelTemp":
                //操盤設置-同步联赛模板
                return logModifyMatchTempByLevelTempFormat.formatLogBean(rcsOperateLog,bean);
            case "modifySpecialInterval":
                //操盤設置-盤口參數調整-特殊抽水
                return logModifySpecialIntervalFormat.formatLogBean(rcsOperateLog,bean);
            case "updateEventAndTimeConfig":
                //聯賽模板-自动接拒设置-保存
                return logUpdateEventAndTimeConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "copyEventAndTimeConfig":
                //联赛模板日志-接拒单玩法集事件复制功能
                return logCopyEventAndTimeConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "updateEventConfig":
                //修改接距配置
                return logUpdateEventConfigFormat.formatLogBean(rcsOperateLog,bean);
            case "updateAutoOpenMarketStatus":
                //修改AO自动开关
                return logUpdateAutoOpenMarketStatusFormat.formatLogBean(rcsOperateLog,bean);
            case "exitMatchSpecEvent":
                //操盘手手动确认退出赛事特殊事件
                return logMatchSpecEventQuitFormat.formatLogBean(rcsOperateLog,bean);
            case "updateSpecEventProbByMatchId":
                //根据赛事id及事件编码修改赔率
                return logUpdateMatchSpecEventProbFormat.formatLogBean(rcsOperateLog,bean);
            case "changeMatchSpecEvent":
                //操盘手手动确认修改赛事特殊事件
                return logMatchSpecEventFormat.formatLogBean(rcsOperateLog,bean);
            case "updateMarketHeadGap":
                //篮球操盘+/-
                return logUpdateMarketHeadGapFormat.formatLogBean(rcsOperateLog,bean);
            case "reductionWater":
                //篮球操盘赔率返回
                return logReductionWaterFormat.formatLogBean(rcsOperateLog,bean);
            case "waterDiffRelevance":
                //篮球操盘锁联动
                return logWaterDiffRelevanceFormat.formatLogBean(rcsOperateLog,bean);
            case "configChangeWeight":
                //数据源权重设置
                return logConfigChangeWeightFormat.formatLogBean(rcsOperateLog,bean);
                //
                case "modifyMtsSwitchConfig":
                return modifyMtsSwitchConfig(rcsOperateLog,bean);
            case "modifyDistanceSwitch":
                return modifyDistanceSwitch(rcsOperateLog,bean);
            case "modifyMatchPendingOrderParam":
                return modifyMatchPendingOrderParam(rcsOperateLog,bean);
            case "modifyPendingOderStauts" :
                return modifyPendingOderStauts(rcsOperateLog,bean);
            case "balanceToZero":
                return balanceToZero(rcsOperateLog,bean);
            case "matchConfirmMarketCategorySell":
                return matchConfirmMarketCategorySell(rcsOperateLog,bean);
            case "updateCategorySetAndCategory":
                return updateCategorySetAndCategory(rcsOperateLog,bean);
            case "changePersonLiablea":
                    return changePersonLiable(rcsOperateLog,bean);
            case "addCategorySetAndCategory" :
                return addCategorySetAndCategory(rcsOperateLog,bean);
                case "firstHalfJointControl" :
                return firstHalfJointControl(rcsOperateLog,bean);
            case "fullFieldJointControl" :
                return fullFieldJointControl(rcsOperateLog,bean);

                default:
                throw new Exception("操盤日誌-未有對應方法名稱");
        }

    }

    private RcsOperateLog fullFieldJointControl(RcsOperateLog rcsOperateLog, LogAllBean bean){
        switchConfig(rcsOperateLog,bean,bean.getMatchId());
        rcsOperateLog.setBehavior(OperateLogOneEnum.MARKET_STATUS.getName());
        rcsOperateLog.setMatchId(bean.getMatchId());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_GQCP.getId():MatchTypeEnum.OPERATE_PAGE_ZPCP.getId());
        rcsOperateLog.setObjectNameByObj(OperateLogOneEnum.Full_half_goal.getLangJson());
        rcsOperateLog.setUserId("-2");
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setOperatePageName(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setAfterVal(getTradeStatusName(bean.getMarketStatus()));
       return rcsOperateLog;
    }

    private  RcsOperateLog firstHalfJointControl(RcsOperateLog rcsOperateLog, LogAllBean bean){
        switchConfig(rcsOperateLog,bean,bean.getMatchId());
        rcsOperateLog.setMatchId(bean.getMatchId());
        rcsOperateLog.setBehavior(OperateLogOneEnum.MARKET_STATUS.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_GQCP.getId():MatchTypeEnum.OPERATE_PAGE_ZPCP.getId());
        rcsOperateLog.setOperatePageName(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setObjectNameByObj(OperateLogOneEnum.first_half_goal.getLangJson());
        rcsOperateLog.setUserId("-2");
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setAfterVal(getTradeStatusName(bean.getMarketStatus()));
        return rcsOperateLog;
    }


    /**
     * 根據盤口狀態碼轉換名稱
     *
     * @param stateCode
     * @return
     */
    public static String getTradeStatusName(Integer stateCode) {
        switch (stateCode) {
            case 0:
                return TradeStatusEnum.OPEN.getName();
            case 2:
                return TradeStatusEnum.CLOSE.getName();
            case 1:
                return TradeStatusEnum.SEAL.getName();
            case 11:
                return TradeStatusEnum.LOCK.getName();
            case 12:
                return TradeStatusEnum.DISABLE.getName();
            case 13:
                return TradeStatusEnum.END.getName();
            default:
                return OperateLogEnum.NONE.getName();
        }
    }

    /**
     * 删除玩法集 添加操盘操作日志
     * @param rcsOperateLog
     * @param
     * @return
     */
    public  void deleteCategorySet(RcsOperateLog rcsOperateLog, Integer id){
        RcsMarketCategorySet marketCategorySet= marketCategorySetMapper.selectById(id);
        if(null!=marketCategorySet){
            QueryWrapper<RcsLanguageInternation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(RcsLanguageInternation::getNameCode, marketCategorySet.getNameCode());
            RcsLanguageInternation  language=  rcsLanguageInternationMapper.selectOne(queryWrapper);
            rcsOperateLog.setOperatePageCode(getBehavior(marketCategorySet.getSportId().toString()));
            rcsOperateLog.setParameterName(language==null?"-":language.getText());
        }else{
            rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_WFJGL_ZQ.getId());
            rcsOperateLog.setParameterName("-");
        }

        rcsOperateLog.setBehavior(OperateLogOneEnum.Delete_game_set.getName());
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setAfterVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLogMapper.insert(rcsOperateLog);
    }



    /**
     *  新增玩法集
     * @param joinPoint
     */
    public void addCategory(LogParameters joinPoint){
        LogAllBean bean=  BaseUtils.setObject(joinPoint.getArgs()[0]);
          addCategorySetAndCategory(joinPoint.getLog(), bean);

    }
    public void updateCategory(LogParameters joinPoint){

        LogAllBean bean=  BaseUtils.setObject(joinPoint.getArgs()[0]);
        bean.setBeforeParams(joinPoint.getMap());
        updateCategorySetAndCategory(joinPoint.getLog(), bean);

    }





    /**
     *  新增玩法集
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog addCategorySetAndCategory(RcsOperateLog rcsOperateLog, LogAllBean bean){
        //Map<String, String> map=BaseUtils.jsonStringMap(bean.getMarketCategorySet());
        RcsMarketCategorySet map=bean.getMarketCategorySet();
        rcsOperateLog.setObjectNameByObj(JSON.toJSONString(map.getLanguage()));
        Integer sportId=map.getSportId().intValue();
        rcsOperateLog.setOperatePageCode(getBehavior(sportId.toString()));
        rcsOperateLog.setSportId(sportId);
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setBehavior(OperateLogOneEnum.New_game_play_set.getName());
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        List<RcsOperateLog> list=new ArrayList<>();
        insertRcsOperateLog(list,rcsOperateLog,OperateLogOneEnum.Play_Set_Type.getName(),setTypeString(map.getType()+"",1));
        insertRcsOperateLog(list,rcsOperateLog,OperateLogOneEnum.Play_Set_Status.getName(),setTypeString(map.getStatus()+"",2));
        insertRcsOperateLog(list,rcsOperateLog,OperateLogOneEnum.Play_Set_Sorting.getName(),map.getDisplaySort()+"");
        insertRcsOperateLog(list,rcsOperateLog,OperateLogOneEnum.Play_Set_Name.getName(),map.getName());
        bean.getCategoryList().forEach(m->{
                rcsOperateLog.setParameterName(OperateLogOneEnum.Game_play_Content_Deletion.getName());
                setLogDateList(rcsOperateLog,list,getPlayNameZsEn(m.getMarketCategoryId(),sportId),OperateLogOneEnum.NONE.getName());

        });
        setAddLanguageList(rcsOperateLog,list, JSON.toJSONString(map.getLanguage()));
        rcsOperateLogMapper.bathInserts(list);
        return null;
    }

    private  List<RcsOperateLog> insertRcsOperateLog(List<RcsOperateLog> list,RcsOperateLog rcsOperateLog,String name,String after){
        RcsOperateLog newLog=setData(rcsOperateLog);
        newLog.setParameterName(name);
        newLog.setAfterVal(after);
        list.add(newLog);
        return list;
    }



    /**
     *  编辑玩法集
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog updateCategorySetAndCategory(RcsOperateLog rcsOperateLog, LogAllBean bean){
        RcsMarketCategorySet map=bean.getMarketCategorySet();
        //Map<String, String> map=BaseUtils.jsonStringMap(bean.getMarketCategorySet());
        rcsOperateLog.setOperatePageCode(getBehavior(map.getSportId()+""));
        rcsOperateLog.setObjectNameByObj(JSON.toJSONString(map.getLanguage()));
        rcsOperateLog.setBehavior(OperateLogOneEnum.Edit_Game_Set_Management.getName());
        insertOperateLog(rcsOperateLog, bean);
        return null;
    }

    private void insertOperateLog(RcsOperateLog rcsOperateLog, LogAllBean bean){
        List<RcsOperateLog> list=new ArrayList<>();
        LogAllBean beforeBean=BaseUtils.mapObject(bean.getBeforeParams(),LogAllBean.class);
        assert beforeBean != null;
        RcsMarketCategorySet map= beforeBean.getMarketCategorySet();
        RcsMarketCategorySet mapBean=bean.getMarketCategorySet();
       Integer sportId=  mapBean.getSportId().intValue();
       if(!mapBean.equals(map)){
           setLogDateList(rcsOperateLog,list,mapBean,map);
       }if(!mapBean.getLanguage().equals(map.getLanguage())){
           setLanguageList(rcsOperateLog, list,JSON.toJSONString(map.getLanguage()),JSON.toJSONString(mapBean.getLanguage()));
       }if(!bean.getCategoryList().equals(beforeBean.getCategoryList())){
            Map<Long,CategoryVo>  mapList= new HashMap<>();
            bean.getCategoryList().forEach(m->{
                mapList.put(m.getMarketCategoryId(),m);
            });
            setCategoryVo(rcsOperateLog, list, mapList, beforeBean.getCategoryList(),sportId);
            Map<Long,CategoryVo>   mapBeforeList= new HashMap<>();
            beforeBean.getCategoryList().forEach(m->{
                mapBeforeList.put(m.getMarketCategoryId(),m);
            });
            setBeforeCategoryVo(rcsOperateLog, list, mapBeforeList, bean.getCategoryList(),sportId);
        }
       rcsOperateLogMapper.bathInserts(list);
    }


    public List<RcsOperateLog> setBeforeCategoryVo(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,Map<Long,CategoryVo> map,List<CategoryVo> voList,Integer sportId){
        voList.forEach(m->{
            if(null==m.getId()){
                rcsOperateLog.setParameterName(OperateLogOneEnum.New_game_play_content.getName());
                setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),getPlayNameZsEn(m.getMarketCategoryId(),sportId));
            }else{
                if(!m.getOrderNo().equals(map.get(m.getMarketCategoryId()).getOrderNo())){
                    rcsOperateLog.setParameterName(OperateLogOneEnum.Sort_game_play_content.getName());
                    setLogDateList(rcsOperateLog,list,m.getOrderNo().toString(),map.get(m.getMarketCategoryId()).getOrderNo().toString());
                }
            }

        });

        return list;
    }

    public List<RcsOperateLog> setCategoryVo(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,Map<Long,CategoryVo> map,List<CategoryVo> voList,Integer sportId){
        voList.forEach(m->{
            if(null==map.get(m.getMarketCategoryId())){
                rcsOperateLog.setParameterName(OperateLogOneEnum.Game_play_Content_Deletion.getName());
                setLogDateList(rcsOperateLog,list,getPlayNameZsEn(m.getMarketCategoryId(),sportId),OperateLogOneEnum.NONE.getName());
            }
        });

     return list;
    }





    /**
     *  修改语言记录日志
     * @param rcsOperateLog
     * @param list
     * @param before
     * @param after
     * @return
     */
    private List<RcsOperateLog> setLanguageList(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,String before,String after){
        I18nBean beforeI18nBean =JSONObject.parseObject(before,I18nBean.class);
        I18nBean afterI18nBean=JSONObject.parseObject(after,I18nBean.class);
        if(!beforeI18nBean.getAd().equals(afterI18nBean.getAd())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_AD.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getAd(),afterI18nBean.getAd());
        }if(!beforeI18nBean.getEn().equals(afterI18nBean.getEn())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_EN.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getEn(),afterI18nBean.getEn());
        }if(!beforeI18nBean.getZh().equals(afterI18nBean.getZh())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_ZH.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getZh(),afterI18nBean.getZh());
        }if(!beforeI18nBean.getKo().equals(afterI18nBean.getKo())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_KO.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getKo(),afterI18nBean.getKo());
        }if(!beforeI18nBean.getEs().equals(afterI18nBean.getEs())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_ES.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getEs(),afterI18nBean.getEs());
        }if(!beforeI18nBean.getIt_IT().equals(afterI18nBean.getIt_IT())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_it_IT.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getIt_IT(),afterI18nBean.getIt_IT());
        }if(!beforeI18nBean.getDe_DE().equals(afterI18nBean.getDe_DE())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_de_DE.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getDe_DE(),afterI18nBean.getDe_DE());
        }if(!beforeI18nBean.getFr_FR().equals(afterI18nBean.getFr_FR())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_fr_FR.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getFr_FR(),afterI18nBean.getFr_FR());
        }if(!beforeI18nBean.getPt().equals(afterI18nBean.getPt())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_PT.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getPt(),afterI18nBean.getPt());
        }if(!beforeI18nBean.getMs().equals(afterI18nBean.getMs())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_MS.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getMs(),afterI18nBean.getMs());
        }if(!beforeI18nBean.getMy().equals(afterI18nBean.getMy())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_MY.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getMy(),afterI18nBean.getMy());
        }if(!beforeI18nBean.getVi().equals(afterI18nBean.getVi())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_VI.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getVi(),afterI18nBean.getVi());
        }if(!beforeI18nBean.getTh().equals(afterI18nBean.getTh())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_TH.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getTh(),afterI18nBean.getTh());
        }if(!beforeI18nBean.getJa().equals(afterI18nBean.getJa())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_JA.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getJa(),afterI18nBean.getJa());
        }if(!beforeI18nBean.getRu().equals(afterI18nBean.getRu())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_RU.getName());
            setLogDateList(rcsOperateLog,list,beforeI18nBean.getRu(),afterI18nBean.getRu());
        }

      return list;
    }
    /**
     *  修改语言记录日志
     * @param rcsOperateLog
     * @param list
     * @param after
     * @return
     */
    private List<RcsOperateLog> setAddLanguageList(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,String after){
        I18nBean afterI18nBean=JSONObject.parseObject(after,I18nBean.class);
        if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getAd())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_AD.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getAd());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getEn())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_EN.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getEn());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getZh())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_ZH.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getZh());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getKo())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_KO.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getKo());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getEs())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_ES.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getEs());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getIt_IT())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_it_IT.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getIt_IT());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getDe_DE())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_de_DE.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getDe_DE());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getFr_FR())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_fr_FR.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getFr_FR());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getPt())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_PT.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getPt());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getMs())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_MS.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getMs());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getMy())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_MY.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getMy());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getVi())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_VI.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getVi());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getTh())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_TH.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getTh());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getJa())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_JA.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getJa());
        }if(!OperateLogOneEnum.NONE.getName().equals(afterI18nBean.getRu())){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Multilingual_Editing_RU.getName());
            setLogDateList(rcsOperateLog,list,OperateLogOneEnum.NONE.getName(),afterI18nBean.getRu());
        }

        return list;
    }





    private List<RcsOperateLog> setLogDateList(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,String before,String after){
        RcsOperateLog newLog=setData(rcsOperateLog);
        setBeforeAfterData(newLog,before,after);
        list.add(newLog);
        return list;
    }

    private List<RcsOperateLog> setLogDateList(RcsOperateLog rcsOperateLog,List<RcsOperateLog> list,RcsMarketCategorySet beforeMap,RcsMarketCategorySet afterMap){
        if(!afterMap.getType().equals(beforeMap.getType())){
            RcsOperateLog newLog=setData(rcsOperateLog);
            newLog.setParameterName(OperateLogOneEnum.Play_Set_Type.getName());
            newLog.setBeforeVal(setTypeString(beforeMap.getType()+"",1));
            newLog.setAfterVal(setTypeString(afterMap.getType()+"",1));
            list.add(newLog);
        }if(!afterMap.getStatus().equals(beforeMap.getStatus())){
            RcsOperateLog newLog=setData(rcsOperateLog);
            newLog.setParameterName(OperateLogOneEnum.Play_Set_Status.getName());
            newLog.setBeforeVal(setTypeString(beforeMap.getStatus()+"",2));
            newLog.setAfterVal(setTypeString(afterMap.getStatus()+"",2));
            list.add(newLog);
        }if(!afterMap.getDisplaySort().equals(beforeMap.getDisplaySort())){
            RcsOperateLog newLog=setData(rcsOperateLog);
            newLog.setParameterName(OperateLogOneEnum.Play_Set_Sorting.getName());
            newLog.setBeforeVal(beforeMap.getDisplaySort()+"");
            newLog.setAfterVal(afterMap.getDisplaySort()+"");
            list.add(newLog);
        }
        if(!afterMap.getName().equals(beforeMap.getName())){
            RcsOperateLog newLog=setData(rcsOperateLog);
            newLog.setParameterName(OperateLogOneEnum.Play_Set_Name.getName());
            newLog.setBeforeVal(beforeMap.getName());
            newLog.setAfterVal(afterMap.getName());
            list.add(newLog);
        }
        return list;
   }



   private RcsOperateLog setData(RcsOperateLog rcsOperateLog){
       RcsOperateLog newLog=new RcsOperateLog();
       BeanUtils.copyProperties(rcsOperateLog,newLog);
        return newLog;
   }
  private RcsOperateLog setBeforeAfterData(RcsOperateLog rcsOperateLog,String before,String after){
      rcsOperateLog.setBeforeVal(before);
      rcsOperateLog.setAfterVal(after);
        return rcsOperateLog;
  }



    /**
     *  玩法集 对于的code值
     * @param sportId
     * @return
     */
    public Integer getBehavior(String sportId){
        switch (sportId){
            case "1":
                return MatchTypeEnum.OPERATE_PAGE_WFJGL_ZQ.getId();
            case "2":
                return MatchTypeEnum.OPERATE_PAGE_WFJGL_LQ.getId();

        }

        return MatchTypeEnum.NONE.getId();
    }


    /**
     *  玩法开售
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog matchConfirmMarketCategorySell(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog=switchConfig(rcsOperateLog,bean,bean.getMatchInfoId());
        rcsOperateLog.setBehavior(OperateLogOneEnum.Selling_Game_Play.getName());
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP.getId():MatchTypeEnum.OPERATE_PAGE_GQCP.getId());
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setAfterVal(bean.getCategorySetName()==null?"-":bean.getCategorySetName());
        return rcsOperateLog;
    }

    /**
     *  操盘指派
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog changePersonLiable(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog=switchConfig(rcsOperateLog,bean,bean.getMatchId());
        rcsOperateLog.setBehavior(OperateLogOneEnum.Trading_Assignment.getName());
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP.getId():MatchTypeEnum.OPERATE_PAGE_GQCP.getId());
        rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setAfterVal(bean.getTraderCode());
        return rcsOperateLog;
    }
 //updateCategorySet
 public void updateCategorySet(LogParameters joinPoint){
  List<CategoryVo> list= JSONArray.parseArray(JSONObject.toJSONString(joinPoint.getArgs()[0]), CategoryVo.class);
     RcsOperateLog rcsOperateLog=joinPoint.getLog();
     rcsOperateLog.setSportId(1);
     rcsOperateLog.setOperatePageCode(MatchTypeEnum.OPERATE_PAGE_WFJGL_ZQ.getId());
     rcsOperateLog.setParameterName(OperateLogOneEnum.NONE.getName());
     rcsOperateLog.setExtObjectName(OperateLogOneEnum.NONE.getName());
     rcsOperateLog.setOperateTime(new Date());
     rcsOperateLog.setExtObjectId(OperateLogOneEnum.NONE.getName());
     rcsOperateLog.setBehavior(OperateLogOneEnum.Play_Set_Sorting.getName());
     rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
     rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
     List<RcsOperateLog> rcsOperateLogList=new ArrayList<>();
     list.forEach(m->{
         RcsOperateLog rcsLog = new RcsOperateLog();
         BeanUtils.copyProperties(rcsOperateLog, rcsLog);
         rcsLog.setParameterName(m.getId()+"");
         rcsLog.setBeforeVal(m.getOrderNo()+"");
         rcsLog.setAfterVal(m.getOrderNo()+"");
         rcsOperateLogList.add(rcsLog);


     });
     if(!rcsOperateLogList.isEmpty()){
         rcsOperateLogMapper.bathInserts(rcsOperateLogList);
     }
 }


    /**
     * 批量指派操盘手日志
     * @param joinPoint
     */
    public void setWeights(LogParameters joinPoint){
        List<TradingAssignmentVo> list = JSONArray.parseArray(JSONObject.toJSONString(joinPoint.getArgs()[0]), TradingAssignmentVo.class);
         log.info("批量指派操盘手日志:{}",list);
        List<RcsOperateLog> rcsOperateLogList=new ArrayList<>();
        RcsOperateLog rcsOperateLog=joinPoint.getLog();
        //初始化基本数据
        if(null!=list&& !list.isEmpty()){
            switchConfig(rcsOperateLog,null,list.get(0).getMatchId());
            rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                    ?MatchTypeEnum.OPERATE_PAGE_ZPCP.getId():MatchTypeEnum.OPERATE_PAGE_GQCP.getId());
            rcsOperateLog.setBeforeVal(OperateLogOneEnum.NONE.getName());
            rcsOperateLog.setOperateTime(new Date());
            rcsOperateLog.setBehavior(OperateLogOneEnum.Batch_Assignment_Of_Traders.getName());
            rcsOperateLog.setExtObjectIdByObj(list.get(0).getTypeId());
            rcsOperateLog.setExtObjectNameByObj(getPlayNameZsEn(list.get(0).getTypeId(),rcsOperateLog.getSportId()));
        }

        list.forEach(m->{
            List<TradingAssignmentSubPlayVo> sysTraderWeightList=m.getSysTraderWeightList();
            log.info("操盘手数据:{}",sysTraderWeightList);
            sysTraderWeightList.forEach(c->{
                List<RcsCategorySetTraderWeight> sysTraderList=c.getSysTraderWeightList();
                sysTraderList.forEach(f->{
                    //if(null!=f.getShowEdit()&&f.getShowEdit().equals(0)) {
                        RcsOperateLog rcsLog = new RcsOperateLog();
                        BeanUtils.copyProperties(rcsOperateLog, rcsLog);
                        rcsLog.setExtObjectIdByObj(c.getTypeId());
                        rcsLog.setExtObjectNameByObj(c.getSetNames());
                        rcsLog.setAfterVal(f.getTraderCode()==null?"-":f.getTraderCode());
                        rcsOperateLogList.add(rcsLog);
                    //}
                });

            });

        });
     if(rcsOperateLogList.size()>0){
         rcsOperateLogMapper.bathInserts(rcsOperateLogList);
     }

    }

    public void updateStandardMatchSortValue(LogParameters joinPoint){
        List<StandardMatchInfoSortValueUpateBO> list = JSONArray.parseArray(JSONObject.toJSONString(joinPoint.getArgs()[0]), StandardMatchInfoSortValueUpateBO.class);
        List<RcsOperateLog> rcsOperateLogList=new ArrayList<>();
        RcsOperateLog rcsOperateLog=joinPoint.getLog();
        //初始化基本数据
        if(null!=list&&list.size()>0){
            rcsOperateLog.setOperateTime(new Date());
            rcsOperateLog.setBehavior(OperateLogOneEnum.SORT.getName());
            rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
            rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
            list.forEach(m->{
            switchConfig(rcsOperateLog,null,list.get(0).getStandardMatchId());
            rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                    ?MatchTypeEnum.OPERATE_PAGE_ZPCP.getId():MatchTypeEnum.OPERATE_PAGE_GQCP.getId());
                RcsOperateLog rcsLog = new RcsOperateLog();
                BeanUtils.copyProperties(rcsOperateLog, rcsLog);
                rcsLog.setBeforeVal(m.getOldValue()+"");
                rcsLog.setAfterVal(m.getSortValue()+"");
                rcsOperateLogList.add(rcsLog);
            });

        }
        if(rcsOperateLogList.size()>0){
            rcsOperateLogMapper.bathInserts(rcsOperateLogList);
        }

    }



    /**
     *  调整参数  累计差额
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog balanceToZero(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog=switchConfig(rcsOperateLog,bean,bean.getMatchId());
        rcsOperateLog.setBehavior(OperateLogOneEnum.BET_BOOKING_SWITCH.getName());
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        rcsOperateLog.setBeforeVal(bean.getBeforeParams().get("").toString());
        rcsOperateLog.setAfterVal(BigDecimal.ZERO.toString());
        return rcsOperateLog;
    }


    /**
     *  预约投注开关日志
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog modifyPendingOderStauts(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog=switchConfig(rcsOperateLog,bean,bean.getMatchId());
        rcsOperateLog.setParameterName(OperateLogOneEnum.BET_BOOKING_SWITCH.getName());
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        rcsOperateLog.setBeforeVal(getBeforeValStatusName(bean.getPendingOrderStatus().toString()));
        rcsOperateLog.setAfterVal(getStatusName(bean.getPendingOrderStatus().toString()));
        return rcsOperateLog;
    }

    /**
     *  商户用户单场 限额
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog modifyMatchPendingOrderParam(RcsOperateLog rcsOperateLog, LogAllBean bean){
        RcsTournamentTemplate template=rcsTournamentTemplateMapper.selectById(bean.getId());
       Long matchId=bean.getMatchId();
        if(null!=template){
            matchId=template.getTypeVal();
        }
        rcsOperateLog=switchConfig(rcsOperateLog,bean,matchId);
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.OPERATE_PAGE_ZPCP.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        if(null!=bean.getBusinesPendingOrderPayVal()&&!bean.getBusinesPendingOrderPayVal().equals(bean.getBeforeParams().get("businesPendingOrderPayVal"))){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Merchant_Single_Appointment.getName());
            rcsOperateLog.setBeforeVal(bean.getBeforeParams().get("businesPendingOrderPayVal").toString());
            rcsOperateLog.setAfterVal(bean.getBusinesPendingOrderPayVal());
            rcsOperateLogMapper.insert(rcsOperateLog);
        } if(null!=bean.getUserPendingOrderPayVal()&&!bean.getUserPendingOrderPayVal().equals(bean.getBeforeParams().get("userPendingOrderPayVal"))){
            rcsOperateLog.setParameterName(OperateLogOneEnum.User_Single_Appointment.getName());
            rcsOperateLog.setBeforeVal(bean.getBeforeParams().get("userPendingOrderPayVal").toString());
            rcsOperateLog.setAfterVal(bean.getUserPendingOrderPayVal());
            rcsOperateLogMapper.insert(rcsOperateLog);
        }
        if(null!=bean.getUserPendingOrderCount()&&!bean.getUserPendingOrderCount().equals(bean.getBeforeParams().get("userPendingOrderCount"))){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Number_Of_Appointments.getName());
            rcsOperateLog.setBeforeVal(bean.getBeforeParams().get("userPendingOrderCount").toString());
            rcsOperateLog.setAfterVal(bean.getUserPendingOrderCount());
            rcsOperateLogMapper.insert(rcsOperateLog);
        }
        if(null!=bean.getPendingOrderRate()&&!bean.getPendingOrderRate().equals(bean.getBeforeParams().get("pendingOrderRate"))){
            rcsOperateLog.setParameterName(OperateLogOneEnum.Booking_Betting_Rate.getName());
            rcsOperateLog.setBeforeVal(bean.getBeforeParams().get("pendingOrderRate").toString());
            rcsOperateLog.setAfterVal(bean.getPendingOrderRate());
            rcsOperateLogMapper.insert(rcsOperateLog);
        }
        return null;
    }

    /**
     * MTS-1拒接开关
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog modifyMtsSwitchConfig(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog= switchConfig(rcsOperateLog,bean,bean.getMatchId());

        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        Map<String,String> map= BaseUtils.jsonStringMap(bean.getMtsConfigValue());
        if(map!=null&&null!= map.get("mtsSwitch")){
            rcsOperateLog.setParameterName(OperateLogOneEnum.MTS_Reject_Switch.getName());
            rcsOperateLog.setBeforeVal(getBeforeValStatusName(map.get("mtsSwitch")));
            rcsOperateLog.setAfterVal(getStatusName(map.get("mtsSwitch")));
            rcsOperateLogMapper.insert(rcsOperateLog);
        }
        Map<String,String> beforeMap= BaseUtils.jsonStringMap(bean.getBeforeParams().get("mtsConfigValue").toString());
        if(null!=beforeMap) {
            if (null != map.get("contactPercentage") && !map.get("contactPercentage").equals(beforeMap.get("contactPercentage"))) {
                rcsOperateLog.setParameterName(OperateLogOneEnum.Percentage_Difference.getName());
                rcsOperateLog.setBeforeVal(beforeMap.get("contactPercentage"));
                rcsOperateLog.setAfterVal(map.get("contactPercentage"));
                rcsOperateLogMapper.insert(rcsOperateLog);
            }
            if (null != map.get("waitTime") && !map.get("waitTime").equals(beforeMap.get("waitTime"))) {
                rcsOperateLog.setOperatePageName(OperateLogOneEnum.Waiting_Time_Rejecting.getName());
                rcsOperateLog.setBeforeVal(beforeMap.get("waitTime"));
                rcsOperateLog.setAfterVal(map.get("waitTime"));
                rcsOperateLogMapper.insert(rcsOperateLog);
            }
        }
       return null;
    }

    /**
     *  接拒2.0开关日志
     * @param rcsOperateLog
     * @param bean
     * @return
     */
    public RcsOperateLog modifyDistanceSwitch(RcsOperateLog rcsOperateLog, LogAllBean bean){
        rcsOperateLog= switchConfig(rcsOperateLog,bean,bean.getTypeVal());
        rcsOperateLog.setParameterName(OperateLogOneEnum.Seal_Off_Disk_Reject.getName());
        rcsOperateLog.setExtObjectIdByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setOperatePageCode(rcsOperateLog.getOperatePageCode().equals(MatchTypeEnum.EARLY.getId())
                ?MatchTypeEnum.OPERATE_PAGE_ZPCP_SZ.getId():MatchTypeEnum.OPERATE_PAGE_GQCP_SZ.getId());
        rcsOperateLog.setBeforeVal(getBeforeValStatusName(bean.getDistanceSwitch()));
        rcsOperateLog.setAfterVal(getStatusName(bean.getDistanceSwitch()));
        return rcsOperateLog;
    }
    /**
     * 轉換狀態碼
     *
     * @param status
     * @return
     */
    private String getStatusName(String status) {
        switch (status) {
            case "0":
                return "关";
            case "1":
                return "开";
            default:
                return "";
        }
    }
    /**
     * 轉換狀態碼
     *
     * @param status
     * @return
     */
    private String getBeforeValStatusName(String status) {
        switch (status) {
            case "1":
                return "关";
            case "0":
                return "开";
            default:
                return "";
        }
    }


    /**
     *  日志模板 1 公共参数
     * @param rcsOperateLog
     * @param bean
     * @param matchId
     */
    public RcsOperateLog switchConfig(RcsOperateLog rcsOperateLog, LogAllBean bean,Long matchId){
       StandardMatchInfo info= getStandardMatchInfo(matchId);
       if(null!=info) {
           if (null == rcsOperateLog.getSportId()) {
               rcsOperateLog.setSportId(info.getSportId().intValue());
           }
           if (MatchTypeEnum.EARLY.getId().equals(info.getMatchStatus())) {
               rcsOperateLog.setOperatePageCode(MatchTypeEnum.EARLY.getId());
           } else {
               rcsOperateLog.setOperatePageCode(MatchTypeEnum.LIVE.getId());
           }
           String teamString = montageEnAndZsIs(bean == null ? null : bean.getTeamList(), matchId);
           rcsOperateLog.setParameterName(OperateLogOneEnum.NONE.getName());

           rcsOperateLog.setOperateTime(new Date());
           rcsOperateLog.setMatchId(matchId);
           rcsOperateLog.setExtObjectId(info.getMatchManageId());
           rcsOperateLog.setExtObjectName(teamString);
           rcsOperateLog.setObjectId(info.getMatchManageId());
           rcsOperateLog.setObjectName(teamString);

       }else{
           rcsOperateLog.setSportId(1);
           rcsOperateLog.setOperatePageCode(MatchTypeEnum.LIVE.getId());
           rcsOperateLog.setParameterName(OperateLogOneEnum.NONE.getName());
           rcsOperateLog.setExtObjectName(OperateLogOneEnum.NONE.getName());
           rcsOperateLog.setObjectId(OperateLogOneEnum.NONE.getName());
           rcsOperateLog.setObjectName(OperateLogOneEnum.NONE.getName());
           rcsOperateLog.setOperateTime(new Date());
           rcsOperateLog.setExtObjectId(OperateLogOneEnum.NONE.getName());

       }
      return rcsOperateLog;
    }


    /**
     *  日志模板 2 公共参数
     * @param rcsOperateLog
     * @param bean
     * @param matchId
     */
    public RcsOperateLog switchTelConfig(RcsOperateLog rcsOperateLog, LogAllBean bean,Long matchId){
        String teamString=montageEnAndZsIs(bean.getTeamList(),matchId);
        rcsOperateLog.setExtObjectName(teamString);
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setParameterName(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setExtObjectId(bean.getMatchManageId());
        rcsOperateLog.setExtObjectIdByObj(bean.getMatchManageId());
        rcsOperateLog.setExtObjectNameByObj(teamString);
        return rcsOperateLog;
    }

    /**
     * 透過隊伍列表組出 賽事名稱 中文
     *
     * @param teamList
     * @return
     */
    private String getMatchName(List<MatchTeamInfo> teamList, Long matchId) {
        //防止前端未传值处理
        teamList= (null==teamList||teamList.size()==0)?
                standardSportTeamMapper.queryTeamListByMatchIdZs(matchId):teamList;
        //取隊伍名稱
        String home = "", away = "";
        for (MatchTeamInfo teamVo : teamList) {
            log.info("teamVo队伍值:{}",teamVo);
            String name = Optional.ofNullable(teamVo.getText()).orElse("");
            if (StringUtils.isEmpty(name)&&null!=teamVo.getNames()) {
                name = teamVo.getNames().get("zs");
            }else{
                name= teamVo.getText();
            }
            if ("home".equals(teamVo.getMatchPosition())) {
                home = name;
            } else if ("away".equals(teamVo.getMatchPosition())) {
                away = name;
            }
        }
        return home + " (主)VS " + away;
    }

    /**
     * 透過隊伍列表組出 賽事名稱 英文
     *
     * @param teamList
     * @return
     */
    private String getMatchNameEn(List<MatchTeamInfo> teamList,Long matchId) {
        //防止前端未传值处理
        teamList= (null==teamList||teamList.size()==0)?
                standardSportTeamMapper.queryTeamListByMatchIdEn(matchId):teamList;
        log.info("::业务操作誌消費開始-消费数据记录++++->{}",teamList);
        //取隊伍名稱
        String home = "", away = "";
        for (MatchTeamInfo teamVo : teamList) {
            String name = Optional.ofNullable(teamVo.getText()).orElse("");
            if (StringUtils.isEmpty(name)&&null!=teamVo.getNames()) {
                name=teamVo.getNames().get("en");
            }else{
                name= teamVo.getText();
            }
            if ("home".equals(teamVo.getMatchPosition())) {
                home = name;
            } else if ("away".equals(teamVo.getMatchPosition())) {
                away = name;
            }
        }
        return home + " (host)VS " + away;
    }

    /**
     *  中英文队伍处理
     * @param teamList
     * @param matchId
     * @return
     */
    private String montageEnAndZsIs(List<MatchTeamInfo> teamList,Long matchId ){
        Map<String,String> map=new HashMap<String, String>();
        map.put("zs",getMatchName(teamList,matchId));
        map.put("en",getMatchNameEn(teamList,matchId));
        return JSONObject.toJSONString(map);
    }

    /**
     *  中英文玩法处理
     * @param playId
     * @param sportId
     * @return
     */
   private String getPlayNameZsEn(Long playId, Integer sportId) {
       LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportIdZcEn(playId, sportId);
        return Objects.nonNull(playName) ? playName.getText() : "";
    }

    /**
     *  中文玩法处理
     * @param playId
     * @param sportId
     * @return
     */
    private String getPlayNameZs(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

    /**
     * 英文玩法处理
     * @param playId
     * @param sportId
     * @return
     */
    private String getPlayNameEn(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportIdEn(playId, sportId);
        return Objects.nonNull(playName) ? playName.getText() : "";
    }

    private StandardMatchInfo getStandardMatchInfo(Long id){
        return standardMatchInfoMapper.selectById(id);
    }

    private String setTypeString(String typeVal,Integer type){

        if(type==1){
            switch (typeVal){
                case "1":
                return "风控型";
                case "2":
                    return "绩效型";
                case "0":
                    return "展示型";
            }
        } if(type==2){
            switch (typeVal){
                case "2":
                    return "开启";
                case "3":
                    return "关闭";

            }

        }

       return typeVal;

    }


}
