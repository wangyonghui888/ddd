package com.panda.sport.rcs.console.controller.system;

import com.mongodb.MongoClient;
import com.mongodb.client.result.UpdateResult;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchMarketLiveBean;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.MatchEventInfoFlowingService;
import com.panda.sport.rcs.console.service.MatchStatisticsInfoFlowingService;
import com.panda.sport.rcs.console.service.MatchStatusFlowingService;
import com.panda.sport.rcs.console.service.RcsLogFomatService;
import com.panda.sport.rcs.console.service.RcsStandardSportMarketSellFlowingService;
import com.panda.sport.rcs.console.service.StandardSportMarketFlowingService;
import com.panda.sport.rcs.console.service.StandardSportMarketOddsFlowingService;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
@RequestMapping("matchFlowing")
@Slf4j
public class MatchFlowingPageController {

    @Autowired
    private RedisClient redisClient;

    private String CACHE_REDIS_KEY_TASK = RedisKeys.RCS_TASK_CACHE_KEY;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StandardSportMarketFlowingService standardSportMarketFlowingService;


    @Autowired
    private StandardSportMarketOddsFlowingService standardSportMarketOddsFlowingService;


    @Autowired
    private MatchEventInfoFlowingService matchEventInfoFlowingService;


    @Autowired
    private MatchStatusFlowingService matchStatusFlowingService;


    @Autowired
    private MatchStatisticsInfoFlowingService matchStatisticsInfoFlowingService;

    @Autowired
    private RcsStandardSportMarketSellFlowingService rcsStandardSportMarketSellFlowingService;


    @Autowired
    RcsLogFomatService rcsLogFomatService;

    @Autowired
    @Qualifier("jedisClusterTrade")
    JedisCluster jedisCluster;
    @RequestMapping("market")
    public String market() {
        log.info("进入记录查询market");
        return "matchFlowing/market";
    }

    @RequestMapping("marketA")
    public String marketA() {
        log.info("进入记录查询marketA");
        return "matchFlowing/marketA";
    }

    @RequestMapping(value = "/getMarketList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getMarketList(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize, @RequestParam("dataType") String dataType,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            matchFlowingDTO.setDataType(dataType);
            // 获取用户列表
            pdr = standardSportMarketFlowingService.getMarketList(matchFlowingDTO, pageNum ,pageSize);
            log.info("MarketList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("MarketList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("marketOdds")
    public String marketOdds() {
        log.info("进入记录查询marketOdds");
        return "matchFlowing/marketOdds";
    }

    @RequestMapping("marketOddsA")
    public String marketOddsA() {
        log.info("进入记录查询marketOddsA");
        return "matchFlowing/marketOddsA";
    }

    @RequestMapping(value = "/getMarketOddsList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getMarketOddsList(@RequestParam("pageNum") Integer pageNum,
                                            @RequestParam("pageSize") Integer pageSize,@RequestParam("dataType") String dataType,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            matchFlowingDTO.setDataType(dataType);
            // 获取用户列表
            pdr = standardSportMarketOddsFlowingService.getMarketOddsList(matchFlowingDTO, pageNum ,pageSize);
            log.info("MarketOddsList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("MarketOddsList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping(value = "/getMarketOddsByParam", method = RequestMethod.POST)
    @ResponseBody
    public List getMarketOddsByParam(String linkId,String marketId) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        List objects = new ArrayList<>();
        try {
            // 获取用户列表
            MatchFlowingDTO matchFlowingDTO = new MatchFlowingDTO();
            matchFlowingDTO.setLinkId(linkId);
            matchFlowingDTO.setMarketId(marketId);
            objects = standardSportMarketOddsFlowingService.getMarketOddsByParam(matchFlowingDTO);
            log.info("getMarketOddsByParam查询=pdr:" + objects);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("getMarketOddsByParam查询异常！", e);
        }
        return objects;
    }

    @RequestMapping("event")
    public String event() {
        log.info("进入记录查询event");
        return "matchFlowing/event";
    }

    @RequestMapping(value = "/getEventList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getEventList(@RequestParam("pageNum") Integer pageNum,
                                       @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
            pdr = matchEventInfoFlowingService.getEventList(matchFlowingDTO, pageNum ,pageSize);
            log.info("EventList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("EventList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("statistics")
    public String statistics() {
        log.info("进入记录查询statistics");
        return "matchFlowing/statistics";
    }

    @RequestMapping(value = "/getStatisticsList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getStatisticsList(@RequestParam("pageNum") Integer pageNum,
                                            @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
            pdr = matchStatisticsInfoFlowingService.getStatisticsList(matchFlowingDTO, pageNum ,pageSize);
            log.info("StatisticsList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("StatisticsList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("statisticsDetail")
    public String statisticsDetail() {
        log.info("进入记录查询statistics");
        return "matchFlowing/statisticsDetail";
    }

    @RequestMapping(value = "/getStatisticsDetailList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getStatisticsDetailList(@RequestParam("pageNum") Integer pageNum,
                                            @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
            pdr = matchStatisticsInfoFlowingService.getStatisticsDetailList(matchFlowingDTO, pageNum ,pageSize);
            log.info("StatisticsList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("StatisticsList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("status")
    public String status() {
        log.info("进入记录查询status");
        return "matchFlowing/status";
    }

    @RequestMapping(value = "/getStatusList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getStatusList(@RequestParam("pageNum") Integer pageNum,
                                        @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;
            }
            if(null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
            pdr = matchStatusFlowingService.getStatusList(matchFlowingDTO, pageNum ,pageSize);
            log.info("StatusList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("StatusList查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("openSell")
    public String openSell() {
        log.info("进入记录查询openSell");
        return "matchFlowing/openSell";
    }


    @RequestMapping(value = "/getOpenSellList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getOpenSellList(@RequestParam("pageNum") Integer pageNum,
                                          @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        /*logger.info("分页查询用户列表！搜索条件：userSearch：" + userSearch + ",pageNum:" + page.getPageNum()
                + ",每页记录数量pageSize:" + page.getPageSize());*/
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;            }
            if(null == pageSize) {
                pageSize = 10;
            }
            // 获取用户列表
            pdr = rcsStandardSportMarketSellFlowingService.getOpenSellList(matchFlowingDTO, pageNum ,pageSize);
            log.info("OpenSellList查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("OpenSellList查询异常！", e);
        }
        return pdr;
    }


    @RequestMapping("redis")
    public String redis() {
        log.info("进入redis查询");
        return "matchFlowing/redis";
    }

    @RequestMapping("redisTrade")
    public String redisTrade() {
        log.info("进入redis查询Trade");
        return "matchFlowing/redisTrade";
    }

   /* @RequestMapping(value = "/getRedisKeyValue", method = RequestMethod.POST)
    @ResponseBody
    public String getRedisKeyValue(MatchFlowingDTO matchFlowingDTO) {
        try {
            log.info("getRedisKeyValue查询");
            String text = null;
            Set<String> keys = redisClient.keys(matchFlowingDTO.getName());
            HashMap<String, String> objMap = new HashMap<>();
            for (String key : keys) {
                String s = null;
                try {
                    s = redisClient.get(key);
                } catch (Exception e) {
                    Object obj = redisClient.hGetAllToObj(key);
                    s= JsonFormatUtils.toJson(obj);
                }
                objMap.put(key,s);
            }
            text = JsonFormatUtils.toJson(objMap);
            return text;
        } catch (Exception e) {
            log.error("getRedisKeyValue查询异常！", e);
            return "getRedisKeyValue查询异常！";
        }
    }*/

    @RequestMapping(value = "/getSingelRedisKeyValue", method = RequestMethod.POST)
    @ResponseBody
    public String getSingelRedisKeyValue(MatchFlowingDTO matchFlowingDTO) {
        try {
            log.info("getSingelRedisKeyValue查询");
            String text = null;
            try {
                text = redisClient.get(matchFlowingDTO.getName());
            } catch (Exception e) {
                Object obj = redisClient.hGetAllToObj(matchFlowingDTO.getName());
                if(null!=obj){
                    text= JsonFormatUtils.toJson(obj);
                }
            }
            return text;
        } catch (Exception e) {
            log.error("getSingelRedisKeyValue查询异常！", e);
            return "getSingelRedisKeyValue查询异常！";
        }
    }

    @RequestMapping(value = "/getRedisHashKeyValue", method = RequestMethod.POST)
    @ResponseBody
    public String getRedisHashKeyValue(MatchFlowingDTO matchFlowingDTO) {
        try {
            log.info("getRedisHashKeyValue查询");
            String text = null;
            text = redisClient.hGet(matchFlowingDTO.getName(), matchFlowingDTO.getHashKey());
            return text;
        } catch (Exception e) {
            log.error("getRedisHashKeyValue查询异常！", e);
            return "getRedisHashKeyValue查询异常！";
        }
    }



    @RequestMapping(value = "/getSingelRedisKeyValue-trade", method = RequestMethod.POST)
    @ResponseBody
    public String getSingelRedisKeyValueTrade(MatchFlowingDTO matchFlowingDTO) {
        try {
            log.info("getSingelRedisKeyValue查询");
            String text = null;
            try {
                text = jedisCluster.get(matchFlowingDTO.getName());
            } catch (Exception e) {
                Object obj = jedisCluster.hgetAll(matchFlowingDTO.getName());
                if(null!=obj){
                    text= JsonFormatUtils.toJson(obj);
                }
            }
            return text;
        } catch (Exception e) {
            log.error("getSingelRedisKeyValue查询异常！", e);
            return "getSingelRedisKeyValue查询异常！";
        }
    }

    @RequestMapping(value = "/getRedisHashKeyValue-trade", method = RequestMethod.POST)
    @ResponseBody
    public String getRedisHashKeyValueTrade(MatchFlowingDTO matchFlowingDTO) {
        try {
            log.info("getRedisHashKeyValue查询");
            String text = null;
            text = jedisCluster.hget(matchFlowingDTO.getName(), matchFlowingDTO.getHashKey());
            return text;
        } catch (Exception e) {
            log.error("getRedisHashKeyValue查询异常！", e);
            return "getRedisHashKeyValue查询异常！";
        }
    }



    @RequestMapping("rcsLogFomat")
    public String rcsLogFomat() {
        log.info("进入rcsogFomat查询");
        return "matchFlowing/rcsLogFomat";
    }

    @RequestMapping(value = "/getRcsLogFomat", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getRcsLogFomat(@RequestParam("pageNum") Integer pageNum,
                                          @RequestParam("pageSize") Integer pageSize,/*@Valid PageRequest page,*/ MatchFlowingDTO matchFlowingDTO) {
        PageDataResult pdr = new PageDataResult();
        try {
            if(null == pageNum) {
                pageNum = 1;            }
            if(null == pageSize) {
                pageSize = 10;
            }
            pdr = rcsLogFomatService.getRcsLogFomats(matchFlowingDTO, pageNum ,pageSize);
            log.info("getRcsogFomat查询=pdr:" + pdr);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("getRcsogFomat查询异常！", e);
        }
        return pdr;
    }

    @RequestMapping("mongodb")
    public String mongodb() {
        log.info("进入mongodb查询");
        return "matchFlowing/mongodb";
    }

    @RequestMapping(value = "/getMatchStatus", method = RequestMethod.POST)
    @ResponseBody
    public String getMatchStatus(@RequestParam(value = "matchId") Long matchId) {
        try {
            log.info("getMatchStatus查询");
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            MatchMarketLiveBean marketLiveBean = mongoTemplate.findOne(query, MatchMarketLiveBean.class);
            if(null != marketLiveBean){
                return marketLiveBean.getMatchStatus()+"";
            }
        } catch (Exception e) {
            log.error("getMatchStatus查询异常！", e);
            return "getMatchStatus查询异常！";
        }
        return "";
    }

    @RequestMapping(value = "/setMatchStatus", method = RequestMethod.POST)
    @ResponseBody
    public UpdateResult setMatchStatus(@RequestParam(value = "matchId") Long matchId, @RequestParam(value = "matchStatus") Integer matchStatus) {
        try {
            log.info("getMatchStatus查询");
            Query query = new Query();
            query.addCriteria(Criteria.where("matchId").is(matchId));
            Update update = new Update();
            update.set("matchStatus", matchStatus);
            return mongoTemplate.upsert(query, update, MatchMarketLiveBean.class);
        } catch (Exception e) {
            log.error("getMatchStatus查询异常！", e);
        }
        return null;
    }
}
