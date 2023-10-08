package com.panda.rcs.push.client;

import com.panda.rcs.push.entity.vo.ClientRequestVo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ClientCache {

    //统计客户端连接数、无实际业务
    public static Map<String, Channel> allClientGroupMap = new ConcurrentHashMap<>();

    /**
     * 统一客户端存储关联
     */
    public static ConcurrentHashMap<Channel, ClientRequestVo> clientGroupMap = new ConcurrentHashMap<>();

}
