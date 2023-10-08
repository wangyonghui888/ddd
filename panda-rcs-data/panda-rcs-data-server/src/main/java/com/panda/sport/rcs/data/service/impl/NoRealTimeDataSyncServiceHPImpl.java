package com.panda.sport.rcs.data.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.common.util.UuidUtils;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.panda.merge.api.IOutrightMatchDataQueryApi;
import com.panda.merge.api.IStandardDataQueryApi;
import com.panda.merge.api.IStandardSportPlayerQueryApi;
import com.panda.merge.bo.I18nItemBO;
import com.panda.merge.bo.OutrightMatchInfoBO;
import com.panda.merge.bo.StandardMarketCategoryBO;
import com.panda.merge.bo.StandardMarketCategoryFieldBO;
import com.panda.merge.bo.StandardMatchInfoBO;
import com.panda.merge.bo.StandardSportMarketCategoryBO;
import com.panda.merge.bo.StandardSportOddsFieldsTempletBO;
import com.panda.merge.bo.StandardSportPlayerBO;
import com.panda.merge.bo.StandardSportRegionBO;
import com.panda.merge.bo.StandardSportTeamBO;
import com.panda.merge.bo.StandardSportTournamentBO;
import com.panda.merge.bo.StandardSportTypeBO;
import com.panda.merge.dto.OutrightMatchInfoDTO;
import com.panda.merge.dto.PageModel;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchInfoDTO;
import com.panda.merge.dto.StandardSportMarketCategoryDTO;
import com.panda.merge.dto.StandardSportPlayerDTO;
import com.panda.merge.dto.StandardSportRegionDTO;
import com.panda.merge.dto.StandardSportTournamentDTO;
import com.panda.merge.dto.StandardSportTypeDTO;
import com.panda.sport.rcs.common.MqConstants;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.data.service.IStandardMatchInfoService;
import com.panda.sport.rcs.data.service.IStandardMatchTeamRelationService;
import com.panda.sport.rcs.data.service.IStandardPlayerMatchRelationService;
import com.panda.sport.rcs.data.service.IStandardSportMarketCategoryService;
import com.panda.sport.rcs.data.service.IStandardSportOddsFieldsTempletService;
import com.panda.sport.rcs.data.service.IStandardSportPlayerService;
import com.panda.sport.rcs.data.service.IStandardSportRegionService;
import com.panda.sport.rcs.data.service.IStandardSportTeamService;
import com.panda.sport.rcs.data.service.IStandardSportTournamentService;
import com.panda.sport.rcs.data.service.IStandardSportTypeService;
import com.panda.sport.rcs.data.service.IStandardTeamTournamentRelationService;
import com.panda.sport.rcs.data.service.RcsLanguageInternationService;
import com.panda.sport.rcs.data.service.RcsStandardOutrightMatchInfoService;
import com.panda.sport.rcs.data.service.RcsStandardSportPlayerService;
import com.panda.sport.rcs.data.service.RcsSysUserService;
import com.panda.sport.rcs.data.sync.INoRealTimeDataSyncService;
import com.panda.sport.rcs.data.utils.CommonUtil;
import com.panda.sport.rcs.data.utils.DataCache;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import com.panda.sport.rcs.data.utils.SysUserFlagEnum;
import com.panda.sport.rcs.data.utils.WordsTools;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.pojo.RcsStandardOutrightMatchInfo;
import com.panda.sport.rcs.pojo.RcsStandardSportPlayer;
import com.panda.sport.rcs.pojo.RcsSysUser;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardMatchTeamRelation;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.StandardSportOddsFieldsTemplet;
import com.panda.sport.rcs.pojo.StandardSportRegion;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import com.panda.sport.rcs.pojo.StandardSportTournament;
import com.panda.sport.rcs.pojo.StandardSportType;
import com.panda.sport.rcs.pojo.ThirdMatchBO;
import com.panda.sport.rcs.utils.WordToPinYinUtil;
import com.panda.sports.api.ISystemUserOrgAuthApi;
import com.panda.sports.api.vo.ShortSysUserVO;
import com.panda.sports.api.vo.SysTraderVO;
import com.panda.virtual.api.IVirtualMarketCategoryQueryApi;
import com.panda.virtual.bo.VirtualMarketCategoryBO;
import com.panda.virtual.bo.VirtualSportMarketCategoryBO;
import com.panda.virtual.dto.VirtualMarketCategoryPageDTO;
import com.panda.virtual.dto.VirtualRequest;
import com.panda.virtual.dto.VirtualResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceCrossThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName DataSyncServiceImpl
 * @Description: 同步静态数据实现类
 * @Author Vector
 * @Date 2019/9/28
 **/
@Service
@Slf4j
@TraceCrossThread
public class NoRealTimeDataSyncServiceHPImpl implements INoRealTimeDataSyncService {

    @Autowired
    RcsLanguageInternationService rcsLanguageInternationService;

    @Autowired
    IStandardMatchInfoService iStandardMatchInfoService;

    @Autowired
    IStandardMatchTeamRelationService iStandardMatchTeamRelationService;

    @Autowired
    IStandardPlayerMatchRelationService iStandardPlayerMatchRelationService;

    @Autowired
    IStandardSportMarketCategoryService iStandardSportMarketCategoryService;

    @Autowired
    IStandardSportOddsFieldsTempletService iStandardSportOddsFieldsTempletService;

    @Autowired
    IStandardSportPlayerService iStandardSportPlayerService;

    @Autowired
    IStandardSportTeamService iStandardSportTeamService;

    @Autowired
    IStandardSportRegionService iStandardSportRegionService;

    @Autowired
    IStandardSportTournamentService iStandardSportTournamentService;

    @Autowired
    IStandardTeamTournamentRelationService iStandardTeamTournamentRelationService;

    @Autowired
    IStandardSportTypeService iStandardSportTypeService;

    @Autowired
    RcsStandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    RcsStandardSportPlayerService rcsStandardSportPlayerService;

    @Autowired
    RcsSysUserService rcsSysUserService;

    @Reference(interfaceClass = IStandardDataQueryApi.class, lazy = true, check = false, timeout = 12000)
    IStandardDataQueryApi standardDataQueryApi;

    @Reference(interfaceClass = IOutrightMatchDataQueryApi.class, lazy = true, check = false, timeout = 12000)
    IOutrightMatchDataQueryApi iOutrightMatchDataQueryApi;

    @Reference(interfaceClass = IVirtualMarketCategoryQueryApi.class, lazy = true, check = false, timeout = 12000)
    IVirtualMarketCategoryQueryApi iVirtualMarketCategoryQueryApi;

    @Reference(interfaceClass = ISystemUserOrgAuthApi.class, lazy = true, check = false, timeout = 12000)
    ISystemUserOrgAuthApi iSystemUserOrgAuthApi;

    @Reference(interfaceClass = IStandardSportPlayerQueryApi.class, lazy = true, check = false, timeout = 12000)
    IStandardSportPlayerQueryApi iStandardSportPlayerQueryApi;

    @Autowired
    private RedisClient redisClient;

    //入库的语言类型
    private static final List<String> saveLanguages= Arrays.asList("zs","zh","en");

    private static final String PREFIX_TEAM_NAME = "rcs:ws:team:name:%s";

    private static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;


    /**
     * 玩法模板缓存
     */
    private static final  String RCS_CATEGORY_TEMPLATE = "rcs:category:template:";
    @Autowired
    private RDSProducerSendMessageUtils sendMessage;

    private String CACHE_REDIS_KEY_TASK = RedisKeys.RCS_TASK_CACHE_KEY;
    protected static final String MATCH_TEMP_INFO = "matchTempInfo";
    private static final String SYSTEMUSER_FLAG  = "systemUserFlag";



    /**
     * @MethodName:
     * @Description: 体育种类同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     *
     * @param time*/
    @Override
    @Trace
    public int syncSportTypeData(String time) throws Exception {
        log.info("::{}::体育种类同步{}","xxl-job-syncSportTypeData",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"SPORT_TYPE_DATA"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncSportTypeData",time);
            startTime=time;
        }
        Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<StandardSportTypeDTO> standardSportTypeDTORequest = new Request<>();
        //判断是否没数据
        StandardSportTypeDTO data = new StandardSportTypeDTO();
        data.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
        data.setEndTime(endTime+(1000*60));
        standardSportTypeDTORequest.setData(data);
        //查询数据
        String linkId = UuidUtils.generateUuid();
        standardSportTypeDTORequest.setLinkId(linkId);
        log.info("::{}::体育种类同步in{}" ,"syncSportTypeData_"+linkId,JSON.toJSON(standardSportTypeDTORequest).toString());
        Response<List<StandardSportTypeBO>> listResponse = standardDataQueryApi.queryStandardSportTypePage(standardSportTypeDTORequest);
        log.info("::{}::体育种类同步out{}" ,"syncSportTypeData_"+linkId, JSON.toJSON(listResponse).toString());
        if (null == listResponse || null == listResponse.getData() || 0 == listResponse.getData().size()) {
            return 0;
        }
        //插入数据
        List<StandardSportTypeBO> standardSportTypeBOs = listResponse.getData();
        List<I18nItemBO> i18nItemBOS = new ArrayList<>();
        for (StandardSportTypeBO datum : standardSportTypeBOs) {
            if(!CollectionUtils.isEmpty(datum.getIl8nNameList())){
                i18nItemBOS.addAll(datum.getIl8nNameList());
            }
            StandardSportType standardSportType = BeanCopyUtils.copyProperties(datum, StandardSportType.class);
            standardSportType.setNameCode(getNameCode(datum.getIl8nNameList()));
            standardSportType.setCreateTime(System.currentTimeMillis());
            iStandardSportTypeService.insertOrUpdate(standardSportType);
        }
        insertLanguageInternation(i18nItemBOS,linkId);
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"SPORT_TYPE_DATA"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;
    }

    /**
     * @MethodName:
     * @Description: 标准体育区域同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     *
     * @param time*/
    @Override
    @Trace
    public int syncSportRegionData(String time) throws Exception {
        log.info("::{}::标准体育区域同步{}","xxl-job-syncSportRegionData",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"SPORT_REGION_DATA"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncSportRegionData",time);
            startTime=time;
        }
        Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<StandardSportRegionDTO>> sportRegionRequest = new Request<>();
        PageModel<StandardSportRegionDTO> data = new PageModel<StandardSportRegionDTO>();
        StandardSportRegionDTO standardSportRegionDTO = new StandardSportRegionDTO();
        standardSportRegionDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
        standardSportRegionDTO.setEndTime(endTime+(1000*60));
        int page = 1;
        data.setSize(100);
        data.setData(standardSportRegionDTO);
        while (true) {
            //查询数据
            data.setCurrent(page);
            sportRegionRequest.setData(data);
            String linkId = UuidUtils.generateUuid();
            sportRegionRequest.setLinkId(linkId);
            log.info("::{}::标准体育区域同步in{}" ,"syncSportRegionData_"+linkId, JSON.toJSON(sportRegionRequest).toString());
            Response<PageModel<List<StandardSportRegionBO>>> pageModelResponse = standardDataQueryApi.queryStandardSportRegionPage(sportRegionRequest);
            log.info("::{}::标准体育区域同步out{}" ,"syncSportRegionData_"+linkId, JSON.toJSON(pageModelResponse).toString());
            if (null == pageModelResponse || null == pageModelResponse.getData() || null == pageModelResponse.getData().getData() || 0 == pageModelResponse.getData().getData().size()) {
                break;
            }
            //组装数据
            List<StandardSportRegionBO> sportRegionDatas = pageModelResponse.getData().getData();
            List<StandardSportRegion> standardSportRegions = copyList(StandardSportRegion.class, sportRegionDatas);
            //插入数据
            iStandardSportRegionService.batchInsertOrUpdate(standardSportRegions);
            ++page;
            Thread.sleep(1000);
        }
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"SPORT_REGION_DATA"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;
    }

    /**
     * @MethodName:
     * @Description: 标准联赛表同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     *
     * @param time*/
    @Override
    @Trace
    public int syncSportTournamentData(String time) throws Exception {
        log.info("::{}::标准联赛表同步{}","xxl-job-syncSportTournamentData",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"SPORT_TOURNAMENT_DATA"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncSportTournamentData",time);
            startTime=time;
        }
    	Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<StandardSportTournamentDTO>> sportTournamentRequest = new Request<>();
        PageModel<StandardSportTournamentDTO> data = new PageModel<StandardSportTournamentDTO>();
        StandardSportTournamentDTO standardSportTournamentDTO = new StandardSportTournamentDTO();
        standardSportTournamentDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
        standardSportTournamentDTO.setEndTime(endTime+(1000*60));
        int page = 1;
        data.setSize(100);
        data.setData(standardSportTournamentDTO);
        while (true) {
            //查询数据
            data.setCurrent(page);
            sportTournamentRequest.setData(data);
            String linkId = UuidUtils.generateUuid();
            sportTournamentRequest.setLinkId(linkId);
            log.info("::{}::标准联赛表同步in{}" ,"syncSportTournamentData_"+linkId, JSON.toJSON(sportTournamentRequest).toString());
            Response<PageModel<List<StandardSportTournamentBO>>> pageModelResponse = standardDataQueryApi.querySportTournamentPage(sportTournamentRequest);
            log.info("::{}::标准联赛表同步out{}"  ,"syncSportTournamentData_"+linkId, JSON.toJSON(pageModelResponse).toString());
            if (null == pageModelResponse || null == pageModelResponse.getData() || null == pageModelResponse.getData().getData() || 0 == pageModelResponse.getData().getData().size()) {
                break;
            }
            //组装数据
            List<StandardSportTournamentBO> sportTournamentDatas = pageModelResponse.getData().getData();
            List<StandardSportTournament> standardSportTournaments = copyList(StandardSportTournament.class, sportTournamentDatas);
            List<I18nItemBO> i18nItemBOS = new ArrayList<>();
            Map<String, String> leaguageMap = new HashMap<String, String>();
            for (StandardSportTournamentBO sportTournamentDatum : sportTournamentDatas) {
                if (!CollectionUtils.isEmpty(sportTournamentDatum.getIl8nNameList())) {
                	Map<String, I18nItemBO> languageMap = new HashMap<String, I18nItemBO>();
                	sportTournamentDatum.getIl8nNameList().stream().forEach(nameBean -> {
                		if(Arrays.asList("en","zs","zh").contains(nameBean.getLanguageType())) {
                			languageMap.put(nameBean.getLanguageType(), nameBean);
                		}
                	});
                	i18nItemBOS.addAll(languageMap.values());
//                    i18nItemBOS.addAll(sportTournamentDatum.getIl8nNameList());
                    try {
                        leaguageMap.put(String.valueOf(sportTournamentDatum.getId()), languageMap.containsKey("zs") ? languageMap.get("zs").getText() : languageMap.get("en").getText());
                    }catch (Exception e){
                        log.error(e.getMessage(),e);
                    }
                }
            }
            standardSportTournaments.forEach(bean -> {
            	if(leaguageMap.containsKey(String.valueOf(bean.getId()))) {
            		String nameConcate = WordToPinYinUtil.getFirshChar(leaguageMap.get(String.valueOf(bean.getId())));
            		bean.setNameConcat(nameConcate);
            	}
            });

            //操作数据
            iStandardSportTournamentService.batchInsertOrUpdate(standardSportTournaments);
            insertLanguageInternation(i18nItemBOS, linkId);
            ++page;
            Thread.sleep(1000);
        }
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"SPORT_TOURNAMENT_DATA"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;
    }

    /**
     * @MethodName:
     * @Description: 赛事表/标准球队信息/赛事球队关系表同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     *
     * @param time*/
    @Override
    @Trace
    public int syncMathTeamData(String time) throws Exception {
        log.info("::{}::赛事表/标准球队信息/赛事球队关系表同步{}","xxl-job-syncMathTeamData",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"SPORT_MATCH_TEAM_DATA"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncMathTeamData",time);
            startTime=time;
        }
    	Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<StandardMatchInfoDTO>> pageModelRequest = new Request<>();
        PageModel<StandardMatchInfoDTO> data = new PageModel<StandardMatchInfoDTO>();
        StandardMatchInfoDTO standardMatchInfoDTO = new StandardMatchInfoDTO();
        standardMatchInfoDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
//        standardMatchInfoDTO.setEndTime(endTime+(1000*60));
        int page = 1;
        data.setSize(100);
        data.setData(standardMatchInfoDTO);
        pageModelRequest.setData(data);
        while (true) {
            //查询数据
            data.setCurrent(page);
            String linkId = UuidUtils.generateUuid();
            pageModelRequest.setLinkId(linkId);
            log.info("::{}::赛事表/标准球队信息/赛事球队关系表同步in{}"  ,"syncMathTeamData_"+linkId, JSON.toJSON(pageModelRequest).toString());
            Response<PageModel<List<StandardMatchInfoBO>>> pageModelResponse = standardDataQueryApi.querySportMathTeamPage(pageModelRequest);
            log.info("::{}::赛事表/标准球队信息/赛事球队关系表同步out{}"  ,"syncMathTeamData_"+linkId, JSON.toJSON(pageModelResponse).toString());
            if (pageModelResponse == null || pageModelResponse.getData() == null || pageModelResponse.getData().getData() == null || pageModelResponse.getData().getData().size() == 0) {
                break;
            }
            //组装数据
            List<StandardMatchInfoBO> matchInfoDatas = pageModelResponse.getData().getData();
            List<StandardMatchInfo> standardMatchInfos = copyList(StandardMatchInfo.class, matchInfoDatas);
            List<I18nItemBO> i18nItemBOS = new ArrayList<>();
            List<StandardSportTeam> standardSportTeams = new ArrayList<>();
            ArrayList<StandardMatchTeamRelation> standardMatchTeamRelations = new ArrayList<>();
            Map<String, Map<String, String>> teamMap = new HashMap<String, Map<String,String>>();
            for (StandardMatchInfoBO matchInfoDatum : matchInfoDatas) {
                if(!CollectionUtils.isEmpty(matchInfoDatum.getIl8nMatchPositionList())){
                    i18nItemBOS.addAll(matchInfoDatum.getIl8nMatchPositionList());
                }
                List<StandardSportTeamBO> sportTeamList = matchInfoDatum.getSportTeamList();
                standardSportTeams.addAll(copyList(StandardSportTeam.class, sportTeamList));

                teamMap.put(String.valueOf(matchInfoDatum.getId()), new HashMap<String, String>());
                for (StandardSportTeamBO standardSportTeamBO : sportTeamList) {
                    if (!CollectionUtils.isEmpty(standardSportTeamBO.getIl8nNameList())) {
                    	Map<String, I18nItemBO> languageMap = new HashMap<String, I18nItemBO>();
                    	standardSportTeamBO.getIl8nNameList().stream().forEach(nameBean -> {
                    		if(Arrays.asList("en","zs","zh").contains(nameBean.getLanguageType())) {
                    			languageMap.put(nameBean.getLanguageType(), nameBean);
                    		}
                    	});
//                        i18nItemBOS.addAll(standardSportTeamBO.getIl8nNameList());
                        i18nItemBOS.addAll(languageMap.values());
                         try{
                             teamMap.get(String.valueOf(matchInfoDatum.getId()))
                        	 .put(standardSportTeamBO.getMatchTeamRelation().getMatchPosition(), languageMap.containsKey("zs") ? languageMap.get("zs").getText() : languageMap.get("en").getText());
                         }catch (Exception e){
                             log.error(e.getMessage(),e);
                         }
                    }
                    StandardMatchTeamRelation matchTeamRelation = new StandardMatchTeamRelation();
                    matchTeamRelation.setStandardMatchId(matchInfoDatum.getId());
                    matchTeamRelation.setStandardTeamId(standardSportTeamBO.getId());
                    matchTeamRelation.setMatchPosition(standardSportTeamBO.getMatchTeamRelation().getMatchPosition());
                    matchTeamRelation.setRemark(standardSportTeamBO.getMatchTeamRelation().getRemark());
                    long relationTime = System.currentTimeMillis();
                    matchTeamRelation.setCreateTime(relationTime);
                    matchTeamRelation.setModifyTime(relationTime);
                    standardMatchTeamRelations.add(matchTeamRelation);
                }
            }
            dataProcessing(standardMatchInfos,teamMap);
            //操作数据
            iStandardMatchInfoService.batchInsertOrUpdate(standardMatchInfos);
            iStandardSportTeamService.batchInsertOrUpdate(standardSportTeams);
            //插入关系表
            iStandardMatchTeamRelationService.batchInsertOrUpdate(standardMatchTeamRelations);
            insertLanguageInternation(i18nItemBOS, linkId);
            //清除ws项目rcs:ws:team key
            //cleanWsKey(standardMatchInfos);
            ++page;
            Thread.sleep(1000);
        }
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"SPORT_MATCH_TEAM_DATA"), String.valueOf(endTime - 1000 * 60));

        return 0;
    }

    /**
     * @MethodName:
     * @Description: 玩法表/标准玩法投注项表同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     *
     * @param time*/
    @Override
    @Trace
    public int syncSportMarketCategoryData(String time) throws Exception {
        log.info("::{}::玩法表/标准玩法投注项表同步{}","xxl-job-syncSportMarketCategoryData",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"SPORT_MARKET_CATEGORY_DATA"));
    	if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncSportMarketCategoryData",time);
            startTime=time;
        }
        long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<StandardSportMarketCategoryDTO>> pageModelRequest = new Request<>();
        PageModel<StandardSportMarketCategoryDTO> data = new PageModel<>();
        StandardSportMarketCategoryDTO standardSportMarketCategoryDTO = new StandardSportMarketCategoryDTO();
        standardSportMarketCategoryDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0L : Long.parseLong(startTime));
        standardSportMarketCategoryDTO.setEndTime(endTime+(1000*60));
        int page = 1;
        data.setSize(100);
        data.setData(standardSportMarketCategoryDTO);
        pageModelRequest.setData(data);
        boolean isUpdate = false;
        while (true) {
            //查询数据
            data.setCurrent(page);
            String linkId = UuidUtils.generateUuid();
            pageModelRequest.setLinkId(linkId);
            log.info("::{}::玩法表/标准玩法投注项表同步in{}", "syncSportMarketCategoryData_" + linkId, JSON.toJSON(pageModelRequest).toString());
            Response<PageModel<List<StandardMarketCategoryBO>>> pageModelResponse = standardDataQueryApi.queryStandardSportMarketCategoryPage(pageModelRequest);
            log.info("::{}::玩法表/标准玩法投注项表同步out{}", "syncSportMarketCategoryData_" + linkId, JSON.toJSON(pageModelResponse).toString());

            if (pageModelResponse == null || pageModelResponse.getData() == null || pageModelResponse.getData().getData() == null || pageModelResponse.getData().getData().size() == 0) {
                break;
            }

            //重组数据
            List<I18nItemBO> i18nItemBOS = new ArrayList<>();
            List<StandardMarketCategoryBO> categoryList = pageModelResponse.getData().getData();
            List<StandardSportMarketCategory> standardSportMarketCategories = new ArrayList<>();
            ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets = new ArrayList<>();
            ArrayList<Map<String, Object>> categoryRefList = new ArrayList<>();
            for(StandardMarketCategoryBO category : categoryList) {
                if(category.getSportMarketCategories() != null && category.getSportMarketCategories().size() > 0 ) {
                    for( StandardSportMarketCategoryBO sport : category.getSportMarketCategories()) {
                        StandardSportMarketCategory categoryBean = new StandardSportMarketCategory();
                        categoryBean.setMultiMarket(category.getMultiMarket());
                        categoryBean.setOrderNo(category.getOrderNo());
                        categoryBean.setOddsSwitch(category.getSupportOdds());
                        categoryBean.setFieldsNum(category.getFieldsNum());
                        categoryBean.setStatus(category.getStatus());
                        categoryBean.setId(category.getId());
                        categoryBean.setSportId(sport.getSportId());
                        categoryBean.setTemplateH5(sport.getTemplateH5Client());
                        categoryBean.setTemplatePc(sport.getTemplatePcClient());
                        if(!CollectionUtils.isEmpty(category.getNameI18n()) ) {
                            try {
                                categoryBean.setNameCode(category.getNameI18n().get(0).getNameCode());
                            }catch (Exception e) {
                                log.error("标准玩法同步异常:", e);
                            }
                        }
                        categoryBean.setSportId(sport.getSportId());
                        standardSportMarketCategories.add(categoryBean);
                        Map<String, Object> info = new HashMap<>();
                        info.put("category_id", category.getId());
                        info.put("sport_id", sport.getSportId());
                        info.put("scope_id", sport.getScopeId());
                        info.put("order_no", sport.getOrderNo());
                        try {
                            info.put("name_code", sport.getNameI18n().get(0).getNameCode());
                        }catch (Exception e) {
                            log.warn(e.getMessage(),e);
                        }
                        info.put("status", sport.getStatus());

                        categoryRefList.add(info);
                        if (!CollectionUtils.isEmpty(sport.getNameI18n())) {
                            i18nItemBOS.addAll(sport.getNameI18n());
                        }
                    }
                } else {
                    StandardSportMarketCategory categoryBean = new StandardSportMarketCategory();
                    categoryBean.setId(category.getId());
                    categoryBean.setMultiMarket(category.getMultiMarket());
                    categoryBean.setOrderNo(category.getOrderNo());
                    categoryBean.setOddsSwitch(category.getSupportOdds());
                    categoryBean.setFieldsNum(category.getFieldsNum());
                    categoryBean.setStatus(category.getStatus());
                    if (!CollectionUtils.isEmpty(category.getNameI18n())) {
                        categoryBean.setNameCode(category.getNameI18n().get(0).getNameCode());
                    }
                    standardSportMarketCategories.add(categoryBean);
                }

                if (!CollectionUtils.isEmpty(category.getNameI18n())) {
                    i18nItemBOS.addAll(category.getNameI18n());
                }
                if (category.getMarketCategoryFields() != null && category.getMarketCategoryFields().size() > 0) {
                    for (StandardMarketCategoryFieldBO field : category.getMarketCategoryFields()) {
                        StandardSportOddsFieldsTemplet templet = new StandardSportOddsFieldsTemplet();
                        templet.setId(field.getId());
                        templet.setOrderNo(field.getOrderNo());
                        templet.setModifyTime(System.currentTimeMillis());
                        templet.setMarketCategoryId(category.getId());
                        try {
                            templet.setNameCode(field.getNameI18n().get(0).getNameCode());
                        } catch (Exception e) {
                            log.warn(e.getMessage(), e);
                        }

                        standardSportOddsFieldsTemplets.add(templet);

                        if (!CollectionUtils.isEmpty(field.getNameI18n())) {
                            i18nItemBOS.addAll(field.getNameI18n());
                        }
                    }
                }

            }

            //操作数据
            log.info("::{}::本次玩法模板id更新:{}", linkId, JsonFormatUtils.toJson(standardSportMarketCategories));
            iStandardSportMarketCategoryService.batchInsertOrUpdate(standardSportMarketCategories);
            iStandardSportMarketCategoryService.batchInsertOrUpdateCategoryRef(categoryRefList);
            iStandardSportOddsFieldsTempletService.batchInsertOrUpdate(standardSportOddsFieldsTemplets);
            insertLanguageInternation(i18nItemBOS, linkId);
            ++page;
            isUpdate = true;
        }

        if(isUpdate) {
        	Map<String, Object> map = new HashMap<>();
        	map.put("type", "AllPlay");
        	sendMessage.sendMessage(MqConstants.RCS_ORDER_SDK_CACHE, "AllPlay", "", map);
        }

        redisClient.set(String.format(CACHE_REDIS_KEY_TASK, "SPORT_MARKET_CATEGORY_DATA"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;
    }

    /**
     * @return 虚拟赛事玩法同步
     * @throws Exception
     * @param time
     */
    @Override
    @Trace
    public int queryVirtualMarketCategoryPage(String time) throws Exception {
        log.info("::{}::虚拟赛事玩法同步{}","xxl-job-queryVirtualMarketCategoryPage",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, "VIRTUAL_MARKET_CATEGORY"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","queryVirtualMarketCategoryPage",time);
            startTime=time;
        }
        Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        VirtualRequest<VirtualMarketCategoryPageDTO> request = new VirtualRequest<>();
        VirtualMarketCategoryPageDTO virtualMarketCategoryPageDTO = new VirtualMarketCategoryPageDTO();
        virtualMarketCategoryPageDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
        virtualMarketCategoryPageDTO.setEndTime(endTime + (1000 * 60));
        request.setData(virtualMarketCategoryPageDTO);
        //查询数据
        String linkId = UuidUtils.generateUuid();
        request.setLinkId(linkId);
        log.info("::{}::玩法表虚拟玩法in{}" ,"queryVirtualMarketCategoryPage_"+linkId, JSON.toJSON(request).toString());
        VirtualResponse<List<VirtualMarketCategoryBO>> listVirtualResponse = iVirtualMarketCategoryQueryApi.queryVirtualMarketCategoryPage(request);
        log.info("::{}::玩法表虚拟玩法out{}" ,"queryVirtualMarketCategoryPage_"+linkId, JSON.toJSON(listVirtualResponse).toString());
        if (null == listVirtualResponse || null == listVirtualResponse.getData() || 0 == listVirtualResponse.getData().size()) {
            return 0;
        }
        //重组数据
        List<com.panda.virtual.bo.I18nItemBO> i18nItemBOS = new ArrayList<>();
        List<StandardSportMarketCategory> standardSportMarketCategories = new ArrayList<StandardSportMarketCategory>();
        ArrayList<Map<String, Object>> categoryRefList = new ArrayList<>();
        //ArrayList<StandardSportOddsFieldsTemplet> standardSportOddsFieldsTemplets = new ArrayList<>();
        List<VirtualMarketCategoryBO> data = listVirtualResponse.getData();
        for (VirtualMarketCategoryBO datum : data) {
            StandardSportMarketCategory categoryBean = new StandardSportMarketCategory();
            categoryBean.setId(datum.getId());
            categoryBean.setSportId(0L);
            categoryBean.setFieldsNum(datum.getFieldsNum());
            categoryBean.setMultiMarket(datum.getMultiMarket());
            categoryBean.setTemplatePc(datum.getTemplatePc());
            categoryBean.setTemplateH5(datum.getTemplateH5());
            categoryBean.setModifyTime(datum.getModifyTime());
            categoryBean.setCreateTime(endTime);
            try {
                categoryBean.setNameCode(datum.getNameI18n().get(0).getNameCode());
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            standardSportMarketCategories.add(categoryBean);
            i18nItemBOS.addAll(datum.getNameI18n());
            List<VirtualSportMarketCategoryBO> sportMarketCategories = datum.getSportMarketCategories();
            for (VirtualSportMarketCategoryBO sportMarketCategory : sportMarketCategories) {
                Map<String, Object> info = new HashMap<String, Object>();
                info.put("category_id", categoryBean.getId());
                info.put("sport_id", sportMarketCategory.getSportId());
                info.put("scope_id", sportMarketCategory.getScopeId());
                info.put("order_no", sportMarketCategory.getOrderNo());
                info.put("status", sportMarketCategory.getStatus());
                try {
                    if (!CollectionUtils.isEmpty(sportMarketCategory.getNameI18n())) {
                        info.put("name_code", sportMarketCategory.getNameI18n().get(0).getNameCode());
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
                categoryRefList.add(info);
                if (!CollectionUtils.isEmpty(sportMarketCategory.getNameI18n())) {
                    i18nItemBOS.addAll(sportMarketCategory.getNameI18n());
                }
            }
        }
        //操作数据
        iStandardSportMarketCategoryService.batchInsertOrUpdate(standardSportMarketCategories);
        iStandardSportMarketCategoryService.batchInsertOrUpdateCategoryRef(categoryRefList);
        //iStandardSportOddsFieldsTempletService.batchInsertOrUpdate(standardSportOddsFieldsTemplets);
        List<I18nItemBO> i18nItemBOS1 = transferI18nItemBO(i18nItemBOS);
        insertLanguageInternation(i18nItemBOS1, linkId);
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK, "VIRTUAL_MARKET_CATEGORY"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;
    }


    /**
     * @MethodName:
     * @Description: 冠军赛事同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/10/21
     *
     * @param time*/
    @Override
    public int syncOutrightMatch(String time) throws Exception {
        log.info("::{}::冠军赛事同步{}","xxl-job-syncOutrightMatch",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, "SPORT_OUTRIGHT_MATCH_DATA"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","syncOutrightMatch",time);
            startTime=time;
        }
        Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<OutrightMatchInfoDTO>> pageModelRequest = new Request<>();
        PageModel<OutrightMatchInfoDTO> data = new PageModel<OutrightMatchInfoDTO>();
        OutrightMatchInfoDTO outrightMatchInfoDTO = new OutrightMatchInfoDTO();
        outrightMatchInfoDTO.setBeginTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime));
        outrightMatchInfoDTO.setEndTime(endTime + (1000 * 60));
        int page = 1;
        data.setSize(100);
        data.setData(outrightMatchInfoDTO);
        pageModelRequest.setData(data);
        while (true) {
            //查询数据
            data.setCurrent(page);
            String linkId = UuidUtils.generateUuid();
            pageModelRequest.setLinkId(linkId);
            log.info("::{}::冠军赛事同步in{}" ,"syncOutrightMatch_"+linkId, JSON.toJSON(pageModelRequest).toString());
            Response<PageModel<List<OutrightMatchInfoBO>>> pageModelResponse = iOutrightMatchDataQueryApi.queryOutrihtMatch(pageModelRequest);
            log.info("::{}::冠军赛事同步out{}" ,"syncOutrightMatch_"+linkId, JSON.toJSON(pageModelResponse).toString());
            if (pageModelResponse == null || pageModelResponse.getData() == null || pageModelResponse.getData().getData() == null || pageModelResponse.getData().getData().size() == 0) {
                break;
            }
            List<I18nItemBO> i18nItemBOS = new ArrayList<>();
            List<OutrightMatchInfoBO> outrightMatchInfoBOs = pageModelResponse.getData().getData();
            List<RcsStandardOutrightMatchInfo> standardMatchInfos = copyList(RcsStandardOutrightMatchInfo.class, outrightMatchInfoBOs);
            for (RcsStandardOutrightMatchInfo standardMatchInfo : standardMatchInfos) {
                standardMatchInfo.setNameCode(standardMatchInfo.getId());
                Map<String, String> map = standardMatchInfo.getMap();
                String en = map.get("en");
                String zs = map.get("zs");
                I18nItemBO enI18nItemBO = new I18nItemBO();
                I18nItemBO zsI18nItemBO = new I18nItemBO();
                enI18nItemBO.setLanguageType("en");
                enI18nItemBO.setText(en);
                enI18nItemBO.setNameCode(standardMatchInfo.getId());
                zsI18nItemBO.setLanguageType("zs");
                zsI18nItemBO.setText(zs);
                zsI18nItemBO.setNameCode(standardMatchInfo.getId());
                i18nItemBOS.add(enI18nItemBO);
                i18nItemBOS.add(zsI18nItemBO);
            }
            standardOutrightMatchInfoService.batchInsertOrUpdate(standardMatchInfos);
            insertLanguageInternation(i18nItemBOS, linkId);
            ++page;
            Thread.sleep(1000);
        }
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"SPORT_OUTRIGHT_MATCH_DATA"), String.valueOf(endTime - 1000 * 60));
        return 0;
    }


    /**
     * @MethodName:
     * @Description: 系统用户同步
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2021/2/9
     *
     * @param time*/
    @Override
    public int snycShortSysUserList(String time) throws Exception {
        log.info("::{}::xxl-job time:{}","snycShortSysUserList",time);
        List<ShortSysUserVO> list=iSystemUserOrgAuthApi.getShortSysUserList();
        List<SysTraderVO> traders=iSystemUserOrgAuthApi.getShortSysUserList(1);
        Map<String, SysTraderVO> tradersMap = traders.stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), e -> e));
        ArrayList<Integer> traderIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(traders)){
            for (SysTraderVO sysTraderVO : traders) {
                traderIds.add(sysTraderVO.getId());
            }
        }
        if(CollectionUtils.isEmpty(list)){return 0;}
        ArrayList<RcsSysUser> rcsSysUsers = new ArrayList<>();
        for (ShortSysUserVO shortSysUserVO : list) {
            RcsSysUser rcsSysUser = new RcsSysUser();
            rcsSysUser.setId(shortSysUserVO.getId().longValue());
            rcsSysUser.setUserCode(shortSysUserVO.getUserCode());
            rcsSysUser.setWorkCode(shortSysUserVO.getWorkCode());
            rcsSysUser.setOrgId(shortSysUserVO.getOrgId());
            rcsSysUser.setPositionId(shortSysUserVO.getPositionId());
            rcsSysUser.setEnabled(shortSysUserVO.getEnabled()?1:0);
            rcsSysUser.setLogicDelete(shortSysUserVO.getLogicDelete()?1:0);
            if(traderIds.contains(shortSysUserVO.getId())){
                rcsSysUser.setUserFlag(SysUserFlagEnum.TRADER.getCode());
                rcsSysUser.setRoles(tradersMap.get(String.valueOf(shortSysUserVO.getId())).getRoles());
            }else{
                rcsSysUser.setUserFlag(SysUserFlagEnum.OTHER.getCode());
            }
            rcsSysUsers.add(rcsSysUser);
        }
        rcsSysUserService.batchInsertOrUpdate(rcsSysUsers);
        return 0;
    }


    /**
     * 球员信息数据接入
     * @return
     * @throws Exception
     * @param time
     */
    @Override
    public int queryStandardSportPlayer(String time) throws Exception {
        log.info("::{}::球员信息数据接入{}","xxl-job-queryStandardSportPlayer",time);
        String startTime = redisClient.get(String.format(CACHE_REDIS_KEY_TASK,"STANDARD_SPORT_PLAYER"));
        if(StringUtils.isNotBlank(time)&& NumberUtils.isNumber(time)){
            log.info("::{}::xxl-job time:{}","queryStandardSportPlayer",time);
            startTime=time;
        }
        Long endTime = System.currentTimeMillis();
        //redis拆分兜底
        if(CommonUtil.isBlankOrNull(startTime)){startTime=String.valueOf(endTime-24*60*60*1000);}
        Request<PageModel<StandardSportPlayerDTO>> sportRegionRequest = new Request<>();
        PageModel<StandardSportPlayerDTO> data = new PageModel<StandardSportPlayerDTO>();
        StandardSportPlayerDTO standardSportPlayerDTO = new StandardSportPlayerDTO();
        standardSportPlayerDTO.setModifyTime(StringUtils.isBlank(startTime) ? 0l : Long.parseLong(startTime)-6*1000);
        int page = 1;
        data.setSize(100);
        data.setData(standardSportPlayerDTO);
        while (true) {
            //查询数据
            data.setCurrent(page);
            sportRegionRequest.setData(data);
            String linkId = UuidUtils.generateUuid();
            sportRegionRequest.setLinkId(linkId);
            log.info("::{}::球员信息数据同步in{}" ,"queryStandardSportPlayer_"+linkId, JSON.toJSON(sportRegionRequest).toString());
            Response<PageModel<List<StandardSportPlayerBO>>> pageModelResponse = iStandardSportPlayerQueryApi.queryStandardSportPlayerByUpdateTime(sportRegionRequest);
            log.info("::{}::球员信息数据同步out{}" ,"queryStandardSportPlayer_"+linkId, JSON.toJSON(pageModelResponse).toString());
            if (null == pageModelResponse || null == pageModelResponse.getData() || null == pageModelResponse.getData().getData() || 0 == pageModelResponse.getData().getData().size()) {
                break;
            }
            //组装数据
            List<StandardSportPlayerBO> standardSportPlayerBOs = pageModelResponse.getData().getData();
            List<RcsStandardSportPlayer> rcsStandardSportPlayers = copyList(RcsStandardSportPlayer.class, standardSportPlayerBOs);
            List<I18nItemBO> i18nItemBOS = new ArrayList<>();
            for (StandardSportPlayerBO standardSportPlayerBO : standardSportPlayerBOs) {
                if(!CollectionUtils.isEmpty(standardSportPlayerBO.getIl8nNameList())){
                    i18nItemBOS.addAll(standardSportPlayerBO.getIl8nNameList());
                }
            }
            rcsStandardSportPlayerService.batchInsertOrUpdate(rcsStandardSportPlayers);
            insertLanguageInternation(i18nItemBOS, linkId);
            //插入数据
            ++page;
            Thread.sleep(1000);
        }
        redisClient.set(String.format(CACHE_REDIS_KEY_TASK,"STANDARD_SPORT_PLAYER"), String.valueOf(endTime - 1000 * 60 * 5));
        return 0;



    }

    /**
     * @MethodName:
     * @Description: 插入国际化数据
     * @Param:
     * @Return:
     * @Author: V
     * @Date: 2019/9/28
     **/
    private void insertLanguageInternation(List<I18nItemBO> il8nMatchPositionList, String linkId)  {
        try {
            Map map = insertLanguageInternationCheck(il8nMatchPositionList,linkId);
            Object newVersion = map.get("newVersion");
            if (null != newVersion) {
                ArrayList<RcsLanguageInternation> rcsLanguageInternations = (ArrayList<RcsLanguageInternation>) newVersion;
                rcsLanguageInternationService.batchInsertOrUpdate(rcsLanguageInternations);
            }
        } catch (Exception e) {
            log.error(e.getMessage()+"HP"+JsonFormatUtils.toJson(il8nMatchPositionList), e);
        }
    }

    /**
     * 转换 I18nItemBO
     * @param i18nItemBOS
     * @return
     */
    private List<I18nItemBO> transferI18nItemBO(List<com.panda.virtual.bo.I18nItemBO> i18nItemBOS) {
        List<I18nItemBO> i18nItemBOSs = new ArrayList<I18nItemBO>();
        for (com.panda.virtual.bo.I18nItemBO i18nItemBO : i18nItemBOS) {
            I18nItemBO i18nItemBO1 = new I18nItemBO();
            i18nItemBO1.setLanguageType(i18nItemBO.getLanguageType());
            i18nItemBO1.setModifyTime(i18nItemBO.getModifyTime());
            i18nItemBO1.setNameCode(i18nItemBO.getNameCode());
            i18nItemBO1.setRemark(i18nItemBO.getRemark());
            i18nItemBO1.setText(i18nItemBO.getText());
            i18nItemBOSs.add(i18nItemBO1);
        }
        return i18nItemBOSs;
    }

    /**
     * 国际化检测
     * @param list
     * @param linkId
     * @return
     */
    private static Map insertLanguageInternationCheck(List<I18nItemBO> list, String linkId) {
        log.info("::{}::国际化过滤进来size" + list.size(),linkId);
        ArrayList<RcsLanguageInternation> rcsLanguageInternations = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<Long, List<I18nItemBO>> collect = list.stream().filter(a -> saveLanguages.contains(a.getLanguageType())).collect(Collectors.groupingBy(I18nItemBO::getNameCode));
        Iterator<Map.Entry<Long, List<I18nItemBO>>> iterator = collect.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<I18nItemBO>> next = iterator.next();
            Long key = next.getKey();
            try {
                StringBuilder stb = new StringBuilder();
                List<I18nItemBO> valueList = next.getValue();
                for (I18nItemBO i18nItemBO : valueList) {
                    stb.append(i18nItemBO.getNameCode())
                            .append(i18nItemBO.getLanguageType())
                            .append(i18nItemBO.getText());
                }
                LinkedHashMap map = transferMapJson(valueList);
                String oMd52 = DataCache.insertCheckCache.getIfPresent(String.valueOf(key));
                String l2 = JsonFormatUtils.toJson(map);
                String nMd52 = DigestUtils.md5DigestAsHex(stb.toString().getBytes());
                if (StringUtils.isBlank(oMd52) || (!oMd52.equals(nMd52))) {
                    RcsLanguageInternation rcsLanguageInternation = new RcsLanguageInternation();
                    rcsLanguageInternation.setNameCode(WordsTools.stringValueOf(key));
                    rcsLanguageInternation.setText(l2);
                    rcsLanguageInternations.add(rcsLanguageInternation);
                }
                DataCache.insertCheckCache.put(String.valueOf(key), nMd52);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("::{}::国际化过滤出去size{}" ,linkId, rcsLanguageInternations.size());
        HashMap<Object, Object> map = new HashMap<>();
        map.put("newVersion", rcsLanguageInternations);
        return map;
    }

    /**
     * 标准赛事检测
     * @param list
     * @return
     */
    private static List<StandardMatchInfoBO> insertMatchCheck(List<StandardMatchInfoBO> list){
        try {
            log.info("2我的进来size"+list.size());
            ArrayList<StandardMatchInfoBO> standardMatchInfoBOs = new ArrayList<>();
            if (CollectionUtils.isEmpty(list)){return null;}
            for (StandardMatchInfoBO bean : list) {
                StringBuilder stb = new StringBuilder()
                        .append(bean.getId())
                        .append(bean.getSportId())
                        .append(bean.getStandardTournamentId())
                        .append(bean.getOperateMatchStatus())
                        .append(bean.getBeginTime())
                        .append(bean.getCanParlay())
                        .append(bean.getNeutralGround())
                        .append(bean.getVisible())
                        .append(bean.getMatchManageId())
                        .append(bean.getRiskManagerCode())
                        .append(bean.getDataSourceCode())
                        .append(bean.getRemark())
                        .append(bean.getThirdMatchSourceId())
                        //.append(bean.getModifyTime())
                        .append(bean.getPreMatchBusiness())
                        .append(bean.getLiveOddBusiness())
                        .append(bean.getScore())
                        .append(bean.getMatchPeriodId())
                        .append(bean.getLotteryNumber())
                        .append(bean.getMatchOver())
                        .append(bean.getSecondsMatchStart())
                        .append(bean.getMatchStatus())
                        .append(bean.getRoundType())
                        .append(bean.getTournamentLevel())
                        .append(bean.getMatchDataProviderCode())
                        .append(bean.getPreMatchTime())
                        .append(bean.getLiveOddTime())
                        .append(bean.getMatchPositionNameCode())
                        .append(bean.getPreRiskManagerCode())
                        .append(bean.getPreMatchDataProviderCode())
                        .append(bean.getLiveMatchDataProviderCode())
                        .append(bean.getLiveRiskManagerCode())
                        .append(bean.getPreMatchSellStatus())
                        .append(bean.getLiveMatchSellStatus())
                        .append(bean.getMatchLength())
                        .append(bean.getSeasonId())
                        .append(bean.getDisplayMarketCount())
                        .append(bean.getLiveMarketCount());
                String nMd5 = DigestUtils.md5DigestAsHex(stb.toString().getBytes());
                String oMd5 = DataCache.insertCheckCache.getIfPresent(String.valueOf(bean.getId()));
                if(StringUtils.isBlank(oMd5)||(!oMd5.equals(nMd5))){
                    standardMatchInfoBOs.add(bean);
                }else{
                    log.warn("重复赛事数据,不存赛事表:"+bean.getId());
                }
                DataCache.insertCheckCache.put(String.valueOf(bean.getId()),nMd5);
            }
            log.info("2我的出去size"+standardMatchInfoBOs.size());
            return standardMatchInfoBOs;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * @MethodName:
     * @Description: list拷贝
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    public static <T, E> List<E> copyList(Class<E> classOfE, List<T> list) throws IllegalAccessException, InstantiationException {
        List<E> eLis = new ArrayList<E>();
        for (T t : list) {
            E e = classOfE.newInstance();
            //高速反射值进createTime
            e = BeanCopyUtils.copyProperties(t, classOfE);
            MethodAccess access = MethodAccess.get(classOfE);
            int index = access.getIndex("setCreateTime", Long.class);
            access.invoke(e,index,System.currentTimeMillis());

            try {
            	 if(t instanceof StandardSportTournamentBO) {
                 	Method method = t.getClass().getMethod("getIl8nNameList");
                 	List<I18nItemBO> i18List = (List<I18nItemBO>) method.invoke(t);

                 	int code = access.getIndex("setNameCode", Long.class);
                    access.invoke(e,code,getNameCode(i18List));
                    //区域id
                    Method method2 = t.getClass().getMethod("getStandardSportRegionId");
                    Long regionId = (Long) method2.invoke(t);
                    int code2 = access.getIndex("setRegionId", Long.class);
                    access.invoke(e,code2,regionId);
                 }else if(t instanceof StandardSportTeamBO) {
                	 Method method = t.getClass().getMethod("getIl8nNameList");
                  	List<I18nItemBO> i18List = (List<I18nItemBO>) method.invoke(t);

                  	int code = access.getIndex("setNameCode", Long.class);
                     access.invoke(e,code,getNameCode(i18List));
                 }else if(t instanceof StandardSportOddsFieldsTempletBO) {
                	 Method method = t.getClass().getMethod("getI18nNameList");
                   	List<I18nItemBO> i18List = (List<I18nItemBO>) method.invoke(t);

                   	int code = access.getIndex("setNameCode", Long.class);
                      access.invoke(e,code,getNameCode(i18List));
                  }else if(t instanceof StandardSportMarketCategoryBO) {
                 	 	Method method = t.getClass().getMethod("getI18nNameList");
                    	List<I18nItemBO> i18List = (List<I18nItemBO>) method.invoke(t);

                    	int code = access.getIndex("setNameCode", Long.class);
                       access.invoke(e,code,getNameCode(i18List));
                   }
            }catch (Exception e1) {
            	log.warn(e1.getMessage(),e1);
            }
            eLis.add(e);
        }
        return eLis;
    }

    private static Long getNameCode(List<I18nItemBO> il8nNameList) {
        if(il8nNameList != null && il8nNameList.size() > 0 ) {
            return il8nNameList.get(0).getNameCode();
        }
        return null;
    }

    private void dataProcessing(List<StandardMatchInfo> standardMatchInfos, Map<String, Map<String, String>> teamMap) {
        try {
            if(CollectionUtils.isEmpty(standardMatchInfos)) {return;}
            for (StandardMatchInfo standardMatchInfo : standardMatchInfos) {
                if ("Sold".equals(standardMatchInfo.getLiveMatchSellStatus())) {
                    standardMatchInfo.setLiveOddBusiness(1);
                } else {
                    standardMatchInfo.setLiveOddBusiness(0);
                }
                if ("Sold".equals(standardMatchInfo.getPreMatchSellStatus())) {
                    standardMatchInfo.setPreMatchBusiness(1);
                } else {
                    standardMatchInfo.setPreMatchBusiness(0);
                }
                //matchOver 为1的时候 liveOddBusiness  preMatchBusiness这两个状态变为0
                if(standardMatchInfo!=null&&standardMatchInfo.getMatchOver()==1){
                    standardMatchInfo.setLiveOddBusiness(0);
                    standardMatchInfo.setPreMatchBusiness(0);
                    standardMatchInfo.setMatchStatus(3);
                }
                if(teamMap.containsKey(String.valueOf(standardMatchInfo.getId()))) {
                	Map<String, String> lenguageMap = teamMap.get(String.valueOf(standardMatchInfo.getId()));
                	String concate = lenguageMap.remove("home");
                	if(StringUtils.isBlank(concate)) {concate = "";}
                	concate = WordToPinYinUtil.getFirshChar(concate);
                	for(String type : lenguageMap.keySet()) {
                		concate = concate + WordToPinYinUtil.getFirshChar(lenguageMap.get(type));
                	}
                	standardMatchInfo.setNameConcat(concate);
                }
                //设置第三方赛事信息
                try {
                    if (!CollectionUtils.isEmpty(standardMatchInfo.getThirdMatchInfoList())){
                        List<ThirdMatchBO> thirdMatchBOS = JSONObject.parseObject(JsonFormatUtils.toJson(standardMatchInfo.getThirdMatchInfoList()), new TypeReference<List<ThirdMatchBO>>() {});
                        if (!CollectionUtils.isEmpty(thirdMatchBOS)){standardMatchInfo.setThirdMatchListStr(JsonFormatUtils.toJson(thirdMatchBOS));}
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                //设定赛事信息缓存
                try {
                    String key1 = String.format(RCS_DATA_KEY_CACHE_KEY, MATCH_TEMP_INFO, standardMatchInfo.getId());
                    redisClient.hSet(key1,"period",String.valueOf(standardMatchInfo.getMatchPeriodId()));
                    if(standardMatchInfo.getMatchType()==2||standardMatchInfo.getMatchType()==3){
                        //电竞
                        redisClient.hSet(key1,"isESport",String.valueOf(standardMatchInfo.getId()));
                    }
                    redisClient.expireKey(key1,2 * 24 * 60 * 60);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                //滚球标识
                String oddsLive_status = redisClient.get(String.format("rcs:matchInfo:oddsLive:status:%s",standardMatchInfo.getId()));
                if(StringUtils.isNotBlank(oddsLive_status)){
                    standardMatchInfo.setOddsLive(Integer.valueOf(oddsLive_status));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
		System.out.println(WordToPinYinUtil.getFirshChar("a"));
	}

    private static LinkedHashMap transferMapJson(List<I18nItemBO> valueList) {
        LinkedHashMap<Object, Object> objectObjectHashMap = new LinkedHashMap<>();
        for (I18nItemBO i18nItemBO : valueList) {
            objectObjectHashMap.put(i18nItemBO.getLanguageType(),i18nItemBO.getText());
        }
        return objectObjectHashMap;
    }
}



