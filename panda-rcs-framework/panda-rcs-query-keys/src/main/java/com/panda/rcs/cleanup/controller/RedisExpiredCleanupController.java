package com.panda.rcs.cleanup.controller;

import com.panda.rcs.cleanup.entity.QueryParameterVo;
import com.panda.rcs.cleanup.entity.STempRedisKey;
import com.panda.rcs.cleanup.mapper.TempRedisKesMapper;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceClusterConnection;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


@Slf4j
@Api(value = "Redis获取数据清理")
@RequestMapping(value = "clean")
@RestController
public class RedisExpiredCleanupController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private TempRedisKesMapper tempRedisKesMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation(value = "根据模块字符串查询具体存储Key-Keys")
    @DeleteMapping("/queryKeys")
    public void queryKeys(@RequestBody QueryParameterVo parameter){
        try {
            Long time = System.currentTimeMillis();
            log.info("::请求参数={}", parameter);
            String redisKeys = parameter.getKeysPrefix() + ":";
            for (String keys : parameter.getDateLists()){
                String allKeys = redisKeys + keys + "*";
                Set<String> kLists = redisTemplate.keys(allKeys);
                if(kLists != null && kLists.size() > 0){
                    for (String s : kLists){
                        STempRedisKey sTempRedisKey = new STempRedisKey();
                        sTempRedisKey.setRedisKey(s);
                        sTempRedisKey.setSaveDate(keys);
                        sTempRedisKey.setCreateTime(Long.toString(System.currentTimeMillis()));
                        tempRedisKesMapper.saveInfo(sTempRedisKey);
                        Thread.sleep(200);
                        log.info("Keys = {}", s);
                    }
                    log.info("总条数 ===== " + kLists.size());
                }
                Thread.sleep(3000);
            }
            log.info("总耗时 == {}", System.currentTimeMillis() - time);
        } catch (Exception e){
            log.error("异常信息=", e);
        }
    }


    @ApiOperation(value = "根据模块字符串查询具体存储Key-Scan")
    @DeleteMapping("/queryScan")
    public void queryScan(@RequestBody QueryParameterVo parameter){
        try {
            Long time = System.currentTimeMillis();
            log.info("::请求参数={}", parameter);
            String redisKeys = parameter.getKeysPrefix() + ":";
            for (String keys : parameter.getDateLists()){
                String allKeys = redisKeys + keys + "*";
                Set<String> kLists = queryScanKeys(allKeys);

                if(kLists != null && kLists.size() > 0){
                    for (String s : kLists){
                        STempRedisKey sTempRedisKey = new STempRedisKey();
                        sTempRedisKey.setRedisKey(s);
                        sTempRedisKey.setSaveDate(keys);
                        sTempRedisKey.setCreateTime(Long.toString(System.currentTimeMillis()));
                        tempRedisKesMapper.saveInfo(sTempRedisKey);
                        Thread.sleep(200);
                        log.info("Keys = {}", s);
                    }
                    log.info("总条数 ===== " + kLists.size());
                }
                Thread.sleep(3000);
            }
            log.info("总耗时 == {}", System.currentTimeMillis() - time);
        } catch (Exception e){
            log.error("异常信息=", e);
        }
    }

    @ApiOperation(value = "根据模块字符串查询具体存储Key-queryScanTo")
    @DeleteMapping("/queryScanTo")
    public void queryScanTo(@RequestBody QueryParameterVo parameter){
        try {
            Long time = System.currentTimeMillis();
            log.info("::请求参数={}", parameter);
            String redisKeys = parameter.getKeysPrefix() + ":";
            for (String keys : parameter.getDateLists()){
                String allKeys = redisKeys + keys + "*";
                Set<String> kLists = keysAndDel(allKeys);

                if(kLists != null && kLists.size() > 0){
                    for (String s : kLists){
                        STempRedisKey sTempRedisKey = new STempRedisKey();
                        sTempRedisKey.setRedisKey(s);
                        sTempRedisKey.setSaveDate(keys);
                        sTempRedisKey.setCreateTime(Long.toString(System.currentTimeMillis()));
                        tempRedisKesMapper.saveInfo(sTempRedisKey);
                        Thread.sleep(200);
                        log.info("Keys = {}", s);
                    }
                    log.info("总条数 ===== " + kLists.size());
                }
                Thread.sleep(3000);
            }
            log.info("总耗时 == {}", System.currentTimeMillis() - time);
        } catch (Exception e){
            log.error("异常信息=", e);
        }

    }

    private Set<String> queryScanKeys(String matchKey){
        Set<String> keys = new HashSet();
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        RedisConnection redisConnection = connectionFactory.getConnection();
        Cursor<byte[]> scan = null;
        if(redisConnection instanceof JedisClusterConnection){
            RedisClusterConnection clusterConnection = connectionFactory.getClusterConnection();
            Iterable<RedisClusterNode> redisClusterNodes = clusterConnection.clusterGetNodes();
            Iterator<RedisClusterNode> iterator = redisClusterNodes.iterator();
            while (iterator.hasNext()) {
                RedisClusterNode next = iterator.next();
                scan = clusterConnection.scan(next, ScanOptions.scanOptions().match(matchKey).count(10000).build());
                while (scan.hasNext()) {
                    keys.add(new String(scan.next()));
                }
                try {
                    if(scan != null){
                        scan.close();
                    }
                } catch (IOException e) {
                    log.error("scan遍历key关闭游标异常",e);
                }
            }
            return keys;
        }
        if(redisConnection instanceof JedisConnection){
            scan = redisConnection.scan(ScanOptions.scanOptions().match(matchKey).count(10000).build());
            while (scan.hasNext()){
                //找到一次就添加一次
                keys.add(new String(scan.next()));
            }
            try {
                if(scan != null){
                    scan.close();
                }
            } catch (IOException e) {
                log.error("scan遍历key关闭游标异常",e);
            }
            return keys;
        }
        return keys;
    }

    public Set<String> keysAndDel(String queryKeys){
        ArrayList<byte []> keyList = new ArrayList<>();
        LettuceClusterConnection con = (LettuceClusterConnection) redisTemplate.getConnectionFactory().getClusterConnection();
        RedisAdvancedClusterAsyncCommands<byte[], byte[]> nativeConnection = (RedisAdvancedClusterAsyncCommands<byte[], byte[]>)con.getNativeConnection();
        RedisAdvancedClusterCommands<byte[], byte[]> sync = nativeConnection.getStatefulConnection().sync();

        KeyScanCursor<byte []> scanCursor = null;

        ScanArgs scanArgs = new ScanArgs();
        scanArgs.match(queryKeys);
        scanArgs.limit(1000);
        do{
            if(scanCursor == null){
                scanCursor = sync.scan(scanArgs);
            } else {
                scanCursor = sync.scan(scanCursor, scanArgs);
            }
        } while (!scanCursor.isFinished());

        Set<String> keys = new HashSet();

        for (byte [] bytes : keyList){
            String key = null;
            try {
                key = new String(bytes, "utf-8");
                keys.add(key);
            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }
        return keys;
    }

}
