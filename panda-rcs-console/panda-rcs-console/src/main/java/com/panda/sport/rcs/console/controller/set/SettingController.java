package com.panda.sport.rcs.console.controller.set;

import com.panda.sport.rcs.console.dto.SettingDTO;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisCluster;

import java.util.*;

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
public class SettingController {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    @Qualifier("jedisClusterTrade")
    JedisCluster jedisCluster;

    @RequestMapping("redisSetting")
    public String redisSetting() {
        log.info("进入记录查询redisSetting");
        return "setting/setRedis";
    }


    @RequestMapping("redisSettingTrade")
    public String redisSettingTrade() {
        log.info("进入记录查询redisSettingTrade");
        return "setting/setRedisTrade";
    }

    @RequestMapping(value = "/setRedis", method = RequestMethod.POST)
    @ResponseBody
    public String getAllTime(SettingDTO settingDTO) {
        log.info(JsonFormatUtils.toJson(settingDTO));
        try {
            if (settingDTO.getType() == null || settingDTO.getKey() == null) {
                return "参数不正确";
            }
            if (settingDTO.getType().equals("1")) {
                redisClient.hSet(settingDTO.getKey(), settingDTO.getHashKey(), settingDTO.getValue());
            } else if (settingDTO.getType().equals("2")) {
                redisClient.set(settingDTO.getKey(), settingDTO.getValue());
            } else if (settingDTO.getType().equals("3")) {
                redisClient.hashRemove(settingDTO.getKey(), settingDTO.getHashKey());
            } else if (settingDTO.getType().equals("4")) {
                redisClient.delete(settingDTO.getKey());
            } else if (settingDTO.getType().equals("5")) {
                List<String> idsSet = new ArrayList<>();
                idsSet.addAll(Arrays.asList(settingDTO.getKey().trim().split(",")));
                List<List<String>> setList = ListUtils.partition(idsSet, 1000);
                for (List<String> strings : setList) {
                    for (String id : strings) {
                        if (id != null) {
                            redisClient.delete(id);
                        }
                    }
                    Thread.sleep(1000);
                }
            } else {
                return "参数不正确";
            }
            return "成功";
        } catch (Exception e) {
            log.error("setRedis异常！", e);
            return "失败";
        }
    }


    @RequestMapping(value = "/setRedis-trade", method = RequestMethod.POST)
    @ResponseBody
    public String setRedisTrade(SettingDTO settingDTO) {
        log.info(JsonFormatUtils.toJson(settingDTO));
        try {
            if (settingDTO.getType() == null || settingDTO.getKey() == null) {
                return "参数不正确";
            }
            if (settingDTO.getType().equals("1")) {
                jedisCluster.hset(settingDTO.getKey(), settingDTO.getHashKey(), settingDTO.getValue());
            } else if (settingDTO.getType().equals("2")) {
                jedisCluster.set(settingDTO.getKey(), settingDTO.getValue());
            } else if (settingDTO.getType().equals("3")) {
                jedisCluster.hdel(settingDTO.getKey(), settingDTO.getHashKey());
            } else if (settingDTO.getType().equals("4")) {
                jedisCluster.del(settingDTO.getKey());
            } else if (settingDTO.getType().equals("5")) {
                jedisCluster.del(settingDTO.getKey());
            } else {
                return "参数不正确";
            }
            return "成功";
        } catch (Exception e) {
            log.error("setRedis异常！", e);
            return "失败";
        }
    }

    public static void main(String[] args) {
        String a = "RCS:bbb";
        String substring = a.substring(0, 4);
        if (substring.toLowerCase().equals("rcs:")) {
            System.out.println(1);
        }
        System.out.println(substring);
    }

}
