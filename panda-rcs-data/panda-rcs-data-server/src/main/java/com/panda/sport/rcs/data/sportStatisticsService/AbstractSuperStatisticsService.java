package com.panda.sport.rcs.data.sportStatisticsService;

import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.data.config.RedissonManager;
import com.panda.sport.rcs.data.utils.RDSProducerSendMessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Administrator
 */
@Slf4j
public abstract class AbstractSuperStatisticsService<T> {

    protected static final String RCS_DATA_KEY_CACHE_KEY = RedisKeys.RCS_DATA_KEY_CACHE_KEY;
    protected Long sportId = 0L;
    protected static final String MARK_EVENT = "markEvent";
    public static final String MATCH_TEMP_INFO = "matchTempInfo";
    protected List<String> unFilterScoreData= new ArrayList<>();

    @Autowired
    protected RDSProducerSendMessageUtils sendMessage;

    @Autowired
    protected RedisClient redisClient;

    @Autowired
    protected RedissonManager redissonManager;

    /**
     * 初始化
     */
    @PostConstruct
    protected abstract void initial();

    /**
     * 当前处理事件处理的运动种类id
     *
     * @return java.lang.Long
     * @description
     * @author dorich
     * @date 2020/08/19
     **/
    public Long getSportId() {
        return sportId;
    }
}
