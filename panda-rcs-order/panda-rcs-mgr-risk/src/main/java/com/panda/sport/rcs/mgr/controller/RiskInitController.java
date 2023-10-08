package com.panda.sport.rcs.mgr.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.panda.sport.rcs.enums.MatchTypeEnum;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.RcsQuotaCrossBorderLimitEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaLimitOtherTypeEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaMerchantSingleFieldLimitEnum;
import com.panda.sport.rcs.mgr.enums.RcsQuotaUserSingleSiteQuotaEnum;
import com.panda.sport.rcs.mgr.utils.PlayTypeConstants;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaBusinessLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaCrossBorderLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaLimitOtherDataService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaMerchantSingleFieldLimitService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserDailyQuotaService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleNoteService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaUserSingleSiteQuotaService;
import com.panda.sport.rcs.pojo.RcsQuotaCrossBorderLimit;
import com.panda.sport.rcs.pojo.RcsQuotaLimitOtherData;
import com.panda.sport.rcs.pojo.RcsQuotaMerchantSingleFieldLimit;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleNote;
import com.panda.sport.rcs.pojo.RcsQuotaUserSingleSiteQuota;
import com.panda.sport.rcs.vo.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/risk")
@Slf4j
public class RiskInitController {
	
    @Autowired
    @Qualifier("mgrQuotaBusinessLimitServiceImpl")
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;
    @Autowired
    private RcsQuotaMerchantSingleFieldLimitService rcsQuotaMerchantSingleFieldLimitService;
    @Autowired
    private RcsQuotaUserDailyQuotaService rcsQuotaUserDailyQuotaService;
    @Autowired
    private RcsQuotaUserSingleSiteQuotaService rcsQuotaUserSingleSiteQuotaService;
    @Autowired
    private RcsQuotaUserSingleNoteService rcsQuotaUserSingleNoteService;
    @Autowired
    private RcsQuotaCrossBorderLimitService rcsQuotaCrossBorderLimitService;
    @Autowired
    private RcsQuotaLimitOtherDataService rcsQuotaLimitOtherDataService;
	
    /**
    * @Description: 初始化所有限额参数
    * @Param: []
    * @return: com.panda.sport.rcs.vo.HttpResponse
    * @Author: KIMI
    * @Date: 2020/11/25
    */
    @RequestMapping(value = "/quotaDataInit",method = RequestMethod.GET)
    public HttpResponse  init(){
        try {
        	//初始化商户限额数据
            initRcsQuotaBusinessLimit();
		} catch (Exception e) {
			log.error("初始化商户限额数据失败",e);
		}
        try {
        	//初始化商户单场限额
            initRcsQuotaMerchantSingleFieldLimit();
		} catch (Exception e) {
			log.error("初始化商户单场限额失败",e);
		}
        try {
        	//初始化用户单日限额
            initRcsQuotaUserDailyQuota();
		} catch (Exception e) {
			log.error("初始化用户单日限额失败",e);
		}
        try {
        	//初始化用户单场限额
            initRcsQuotaUserSingleSiteQuota();
        }catch (Exception e) {
        	log.error("初始化用户单场限额失败",e);
		}
        try {
        	//初始化单注单关限额
            initRcsQuotaUserSingleNote();
		} catch (Exception e) {
			log.error("初始化单注单关限额失败",e);
		}
        try {
        	//初始化单注串关数据
            iniTrcsQuotaCrossBorderLimit();
		} catch (Exception e) {
			log.error("初始化单注串关数据失败",e);
		}
        try {
        	//初始化单注串关其他数据
            initRcsQuotaLimitOtherData();
		} catch (Exception e) {
			log.error("初始化单注串关其他数据失败",e);
		}
        return HttpResponse.success();
    }

    private void initRcsQuotaLimitOtherData() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("status", 1);
        rcsQuotaLimitOtherDataService.removeByMap(columnMap);
        List<RcsQuotaLimitOtherData> rcsQuotaLimitOtherDataList=new ArrayList<>();
        for (RcsQuotaLimitOtherTypeEnum rcsQuotaLimitOtherTypeEnum : RcsQuotaLimitOtherTypeEnum.values()) {
                RcsQuotaLimitOtherData rcsQuotaLimitOtherData = new RcsQuotaLimitOtherData();
                rcsQuotaLimitOtherData.setType(rcsQuotaLimitOtherTypeEnum.getType());
                rcsQuotaLimitOtherData.setBaseValue(new BigDecimal(rcsQuotaLimitOtherTypeEnum.getBase()));
                rcsQuotaLimitOtherData.setStatus(1);
                rcsQuotaLimitOtherData.setSportId(rcsQuotaLimitOtherTypeEnum.getSportId());
                rcsQuotaLimitOtherDataList.add(rcsQuotaLimitOtherData);
        }
        rcsQuotaLimitOtherDataService.saveBatch(rcsQuotaLimitOtherDataList);
    }

    private  void iniTrcsQuotaCrossBorderLimit(){
        Map<String, Object> columnMap=new HashMap<>();
        columnMap.put("status",1);
        rcsQuotaCrossBorderLimitService.removeByMap(columnMap);
        List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList=new ArrayList<>();
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(5));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(8));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(10));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(7));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(9));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(4));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(3));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(6));
        rcsQuotaCrossBorderLimitList.addAll(initRcsQuotaCrossBorderLimit(-1));
        rcsQuotaCrossBorderLimitService.saveBatch(rcsQuotaCrossBorderLimitList);
    }
    public List<RcsQuotaCrossBorderLimit> initRcsQuotaCrossBorderLimit(Integer sportId){
        List<RcsQuotaCrossBorderLimit> rcsQuotaCrossBorderLimitList=new ArrayList<>();
        for (RcsQuotaCrossBorderLimitEnum rcsQuotaCrossBorderLimitEnum:RcsQuotaCrossBorderLimitEnum.values()) {
            int sportId1=sportId;
            if (sportId!=1 && sportId!=2){
                sportId1=5;
            }
            if (sportId1==rcsQuotaCrossBorderLimitEnum.getSportId().intValue()) {
                RcsQuotaCrossBorderLimit rcsQuotaCrossBorderLimit = new RcsQuotaCrossBorderLimit();
                rcsQuotaCrossBorderLimit.setSportId(sportId);
                rcsQuotaCrossBorderLimit.setTournamentLevel(rcsQuotaCrossBorderLimitEnum.getTournamentLevel());
                rcsQuotaCrossBorderLimit.setSeriesConnectionType(rcsQuotaCrossBorderLimitEnum.getSeriesConnectionType());
                rcsQuotaCrossBorderLimit.setQuotaBase(Constants.QUOTA_BASE);
                rcsQuotaCrossBorderLimit.setQuotaProportion(new BigDecimal(rcsQuotaCrossBorderLimitEnum.getQuotaProportion()));
                rcsQuotaCrossBorderLimit.setQuota(rcsQuotaCrossBorderLimit.getQuotaProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
                rcsQuotaCrossBorderLimit.setStatus(1);
                rcsQuotaCrossBorderLimitList.add(rcsQuotaCrossBorderLimit);
            }
        }
        return rcsQuotaCrossBorderLimitList;
    }


    private void initRcsQuotaUserSingleNote(){
        Map<String, Object> columnMap=new HashMap<>();
        columnMap.put("status",1);
        rcsQuotaUserSingleNoteService.removeByMap(columnMap);
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList=new ArrayList<>();
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(5));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(8));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(10));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(7));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(9));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(4));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(3));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(6));
        rcsQuotaUserSingleNoteList.addAll(initRcsQuotaUserSingleNote(-1));
        rcsQuotaUserSingleNoteService.saveBatch(rcsQuotaUserSingleNoteList);
    }

    private List<RcsQuotaUserSingleNote> initRcsQuotaUserSingleNote(Integer sportId){
        //通同一个体育种类数据基础值必须一样
        Long quotaBase= Constants.QUOTA_BASE;
        List<RcsQuotaUserSingleNote> rcsQuotaUserSingleNoteList=new ArrayList<>();
        int[] ints = PlayTypeConstants.get(sportId.longValue());
        for (MatchTypeEnum matchTypeEnum : MatchTypeEnum.values()) {
            for (int x = 0; x < ints.length; x++) {
                RcsQuotaUserSingleNote rcsQuotaUserSingleNote1 = rcsQuotaUserSingleNoteService.createRcsQuotaUserSingleNote(sportId, matchTypeEnum.getId(), ints[x], quotaBase);
                rcsQuotaUserSingleNoteList.add(rcsQuotaUserSingleNote1);
            }
        }
        return rcsQuotaUserSingleNoteList;
    }


    private void initRcsQuotaUserSingleSiteQuota( ){
        Map<String, Object> columnMap=new HashMap<>();
        columnMap.put("status",1);
        rcsQuotaUserSingleSiteQuotaService.removeByMap(columnMap);
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList=new ArrayList<>();
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(5));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(8));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(10));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(7));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(9));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(4));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(3));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(6));
        rcsQuotaUserSingleSiteQuotaList.addAll(initRcsQuotaUserSingleSiteQuota(-1));
        rcsQuotaUserSingleSiteQuotaService.saveBatch(rcsQuotaUserSingleSiteQuotaList);
    }

    private List<RcsQuotaUserSingleSiteQuota> initRcsQuotaUserSingleSiteQuota(Integer sportId){
        //先初始化用户单场限额数据
        List<RcsQuotaUserSingleSiteQuota> rcsQuotaUserSingleSiteQuotaList=new ArrayList<>();
        for (RcsQuotaUserSingleSiteQuotaEnum rcsQuotaUserSingleSiteQuotaEnum: RcsQuotaUserSingleSiteQuotaEnum.values()){
            RcsQuotaUserSingleSiteQuota rcsQuotaUserSingleSiteQuota=new RcsQuotaUserSingleSiteQuota();
            rcsQuotaUserSingleSiteQuota.setSportId(sportId);
            rcsQuotaUserSingleSiteQuota.setTemplateLevel(rcsQuotaUserSingleSiteQuotaEnum.getLevel());
            rcsQuotaUserSingleSiteQuota.setUserSingleSiteQuotaBase(Constants.QUOTA_BASE);
            rcsQuotaUserSingleSiteQuota.setEarlyUserSingleSiteQuotaProportion(new BigDecimal(rcsQuotaUserSingleSiteQuotaEnum.getEarlyTradingRatio()));
            rcsQuotaUserSingleSiteQuota.setEarlyUserSingleSiteQuota(rcsQuotaUserSingleSiteQuota.getEarlyUserSingleSiteQuotaProportion().multiply(new BigDecimal(Constants.QUOTA_BASE)));
            rcsQuotaUserSingleSiteQuota.setLiveUserSingleSiteQuotaProportion(new BigDecimal(rcsQuotaUserSingleSiteQuotaEnum.getRollingRatio()));
            rcsQuotaUserSingleSiteQuota.setLiveUserSingleSiteQuota(new BigDecimal(Constants.QUOTA_BASE).multiply(rcsQuotaUserSingleSiteQuota.getLiveUserSingleSiteQuotaProportion()));
            rcsQuotaUserSingleSiteQuota.setStatus(1);
            rcsQuotaUserSingleSiteQuotaList.add(rcsQuotaUserSingleSiteQuota);
        }
        return rcsQuotaUserSingleSiteQuotaList;
    }

  private void  initRcsQuotaUserDailyQuota(){
      Map<String, Object> columnMap=new HashMap<>();
      columnMap.put("status",1);
      rcsQuotaUserDailyQuotaService.removeByMap(columnMap);
      rcsQuotaUserDailyQuotaService.initRcsQuotaUserDailyQuota();
  }

  private  void initRcsQuotaBusinessLimit() {
      Map<String, Object> columnMap=new HashMap<>();
      columnMap.put("status",1);
      rcsQuotaBusinessLimitService.removeByMap(columnMap);
      rcsQuotaBusinessLimitService.initRcsQuotaBusinessLimit();
  }


    public void initRcsQuotaMerchantSingleFieldLimit(){
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList=new ArrayList<>();
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(5));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(8));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(10));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(7));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(9));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(4));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(3));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(6));
        rcsQuotaMerchantSingleFieldLimitList.addAll(initRcsQuotaMerchantSingleFieldLimit(-1));
        Map<String, Object> columnMap=new HashMap<>();
        columnMap.put("status",1);
        rcsQuotaMerchantSingleFieldLimitService.removeByMap(columnMap);
        rcsQuotaMerchantSingleFieldLimitService.saveBatch(rcsQuotaMerchantSingleFieldLimitList);
    }

    public List<RcsQuotaMerchantSingleFieldLimit> initRcsQuotaMerchantSingleFieldLimit(Integer sportId){
        List<RcsQuotaMerchantSingleFieldLimit> rcsQuotaMerchantSingleFieldLimitList=new ArrayList<>();
        for (RcsQuotaMerchantSingleFieldLimitEnum rcsQuotaMerchantSingleFieldLimitEnum:RcsQuotaMerchantSingleFieldLimitEnum.values()){
            RcsQuotaMerchantSingleFieldLimit rcsQuotaMerchantSingleFieldLimit=new RcsQuotaMerchantSingleFieldLimit();
            rcsQuotaMerchantSingleFieldLimit.setSportId(sportId);
            rcsQuotaMerchantSingleFieldLimit.setTemplateLevel(rcsQuotaMerchantSingleFieldLimitEnum.getLevel());
            rcsQuotaMerchantSingleFieldLimit.setCompensationLimitBase(Constants.QUOTA_BASE);
            rcsQuotaMerchantSingleFieldLimit.setEarlyMorningPaymentLimitRatio(new BigDecimal(rcsQuotaMerchantSingleFieldLimitEnum.getEarlyTradingRatio()));
            rcsQuotaMerchantSingleFieldLimit.setEarlyMorningPaymentLimit((long)(rcsQuotaMerchantSingleFieldLimit.getEarlyMorningPaymentLimitRatio().doubleValue() * Constants.QUOTA_BASE));
            rcsQuotaMerchantSingleFieldLimit.setLiveBallPayoutLimitRatio(new BigDecimal(rcsQuotaMerchantSingleFieldLimitEnum.getRollingRatio()));
            rcsQuotaMerchantSingleFieldLimit.setLiveBallPayoutLimit((long)(rcsQuotaMerchantSingleFieldLimit.getLiveBallPayoutLimitRatio().doubleValue() * Constants.QUOTA_BASE));
            rcsQuotaMerchantSingleFieldLimit.setStatus(1);
            rcsQuotaMerchantSingleFieldLimitList.add(rcsQuotaMerchantSingleFieldLimit);
        }
        return rcsQuotaMerchantSingleFieldLimitList;
    }
}
