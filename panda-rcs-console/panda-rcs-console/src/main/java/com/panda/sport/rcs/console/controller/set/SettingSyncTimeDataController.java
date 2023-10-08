package com.panda.sport.rcs.console.controller.set;

import com.panda.sport.rcs.console.dao.StandardMatchInfoDTOMapper;
import com.panda.sport.rcs.console.dto.SyncTimeSettingDTO;
import com.panda.sport.rcs.console.pojo.LanguageInternation;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoDetailFlowing;
import com.panda.sport.rcs.console.service.CommonService;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping("setting")
@Slf4j
public class SettingSyncTimeDataController {


    @Autowired
    @Qualifier("jedisClusterTrade")
    JedisCluster redisClient;
    @Autowired
    private StandardMatchInfoDTOMapper standardMatchInfoDTOMapper;

    @Autowired
    private CommonService commonService;

    private String CACHE_REDIS_KEY_TASK = RedisKeys.RCS_TASK_CACHE_KEY;
    private static final String SPORT_TYPE_DATA = "SPORT_TYPE_DATA";
    private static final String SPORT_REGION_DATA = "SPORT_REGION_DATA";
    private static final String SPORT_TOURNAMENT_DATA = "SPORT_TOURNAMENT_DATA";
    private static final String SPORT_MATCH_TEAM_DATA = "SPORT_MATCH_TEAM_DATA";
    private static final String SPORT_MARKET_CATEGORY_DATA = "SPORT_MARKET_CATEGORY_DATA";
    private static final String VIRTUAL_MARKET_CATEGORY = "VIRTUAL_MARKET_CATEGORY";
    private static final String SPORT_OUTRIGHT_MATCH_DATA = "SPORT_OUTRIGHT_MATCH_DATA";
    private static final String STANDARD_SPORT_PLAYER = "STANDARD_SPORT_PLAYER";
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @RequestMapping("timeSettingPage")
    public String market() {
        log.info("进入记录查询market");
        return "setting/timeSetting";
    }

    @RequestMapping(value = "/getAllTime", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getAllTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            String sportType = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_TYPE_DATA));
            String sportRegion = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_REGION_DATA));
            String sportTournament = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_TOURNAMENT_DATA));
            String mathTeam = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_MATCH_TEAM_DATA));
            String marketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_MARKET_CATEGORY_DATA));
            String virtualMarketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, VIRTUAL_MARKET_CATEGORY));
            String sportOutrightMatchData = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_OUTRIGHT_MATCH_DATA));
            String standardSportPlayer = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, STANDARD_SPORT_PLAYER));
            HashMap<String, Object> map = new HashMap<>();
            map.put("sportType", sportType);
            map.put("sportRegion", sportRegion);
            map.put("sportTournament", sportTournament);
            map.put("mathTeam", mathTeam);
            map.put("marketCategory", marketCategory);
            map.put("virtualMarketCategory", virtualMarketCategory);
            map.put("sportOutrightMatchData", sportOutrightMatchData);
            map.put("standardSportPlayer", standardSportPlayer);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("getAllTime查询异常！", e);
        }
        return null;
    }


    @RequestMapping(value = "/updateSyncSportTypeDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String updateSyncSportTypeDataTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_TYPE_DATA), syncTimeSettingDTO.getBeginTime());
            String sportType = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_TYPE_DATA));
            return sportType;
        } catch (Exception e) {
            log.error("updateSyncSportTypeDataTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateSyncSportRegionDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String updateSyncSportRegionDataTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_REGION_DATA), syncTimeSettingDTO.getBeginTime());
            String sportRegion = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_REGION_DATA));
            return sportRegion;
        } catch (Exception e) {
            log.error("updateSyncSportRegionDataTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateSyncSportTournamentDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String updateSyncSportTournamentDataTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_TOURNAMENT_DATA), syncTimeSettingDTO.getBeginTime());
            String sportTournament = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_TOURNAMENT_DATA));
            return sportTournament;
        } catch (Exception e) {
            log.error("updateSyncSportTournamentDataTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateSyncMathTeamDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String updateSyncMathTeamDataTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_MATCH_TEAM_DATA), syncTimeSettingDTO.getBeginTime());
            String mathTeam = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_MATCH_TEAM_DATA));
            return mathTeam;
        } catch (Exception e) {
            log.error("updateSyncMathTeamDataTime异常！", e);
        }
        return null;
    }


    @RequestMapping(value = "/updateSyncSportMarketCategoryDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String syncSportMarketCategoryData(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_MARKET_CATEGORY_DATA), syncTimeSettingDTO.getBeginTime());
            String marketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_MARKET_CATEGORY_DATA));
            return marketCategory;
        } catch (Exception e) {
            log.error("updateSyncSportMarketCategoryDataTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateSyncVirtualMarketCategoryDataTime", method = RequestMethod.POST)
    @ResponseBody
    public String syncVirtualMarketCategoryData(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, VIRTUAL_MARKET_CATEGORY), syncTimeSettingDTO.getBeginTime());
            String marketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, VIRTUAL_MARKET_CATEGORY));
            return marketCategory;
        } catch (Exception e) {
            log.error("updateSyncVirtualMarketCategoryDataTime异常！", e);
        }
        return null;
    }


    @RequestMapping(value = "/updateSTANDARD_SPORT_PLAYERTime", method = RequestMethod.POST)
    @ResponseBody
    public String syncSportOutrightMatchData(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, SPORT_OUTRIGHT_MATCH_DATA), syncTimeSettingDTO.getBeginTime());
            String marketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, SPORT_OUTRIGHT_MATCH_DATA));
            return marketCategory;
        } catch (Exception e) {
            log.error("updateSyncportOutrightMatchDataTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateStandardSportPlayerTime", method = RequestMethod.POST)
    @ResponseBody
    public String updateStandardSportPlayerTime(SyncTimeSettingDTO syncTimeSettingDTO) {
        log.info(JsonFormatUtils.toJson(syncTimeSettingDTO));
        try {
            redisClient.set(String.format(CACHE_REDIS_KEY_TASK, STANDARD_SPORT_PLAYER), syncTimeSettingDTO.getBeginTime());
            String marketCategory = redisClient.get(String.format(CACHE_REDIS_KEY_TASK, STANDARD_SPORT_PLAYER));
            return marketCategory;
        } catch (Exception e) {
            log.error("updateStandardSportPlayerTime异常！", e);
        }
        return null;
    }

    @RequestMapping(value = "/updateBussIdAndName", method = RequestMethod.POST)
    @ResponseBody
    public String updateBussIdAndName(SyncTimeSettingDTO syncTimeSettingDTO) {
        try {
            producerSendMessageUtils.sendMessage("rcs_order_sdk_cache", "{}");
            Map<String, Object> params = new HashMap<>();
            params.put("in_businessName", syncTimeSettingDTO.getBussName());
            params.put("in_businessId", Integer.parseInt(syncTimeSettingDTO.getBussId()));
            params.put("out_ret", 1);
            params.put("out_desc", 1);
            standardMatchInfoDTOMapper.setAddNewBusinessData(params);
            return "1";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/startMoveLanguageInternation", method = RequestMethod.POST)
    @ResponseBody
    public String startMoveLanguageInternation(SyncTimeSettingDTO syncTimeSettingDTO) {
        try {
            int page = 1;
            while (true) {
                log.info("同步国际化page-for begin:" + page);
                List<LanguageInternation> list = commonService.getLanguageInternations(page, 1000);
                if (CollectionUtils.isEmpty(list)){
                    log.info("同步国际化page-for endend");
                    return "1";
                }
                commonService.batchInsertOrUpdate(list);
                log.info("同步国际化page-for end" + page);
                ++page;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        System.out.println(1);
        return "1";
    }



    @RequestMapping(value = "/updatePlaySetNameCodeLanguageInternation", method = RequestMethod.POST)
    @ResponseBody
    public String updatePlaySetNameCodeLanguageInternation(SyncTimeSettingDTO syncTimeSettingDTO) {
        try {
            log.info("updatePlaySetNameCodeLanguageInternation");
            commonService.updatePlaySetNameCodeLanguageInternation(syncTimeSettingDTO);
            log.info("updatePlaySetNameCodeLanguageInternation end");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        System.out.println(1);
        return "1";
    }
}
