package com.panda.sport.rcs.oddin.task;

import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.oddin.client.GrpcPullSingleClient;
import com.panda.sport.rcs.oddin.client.GrpcTicketClient;
import com.panda.sport.rcs.oddin.config.NacosParameter;
import com.panda.sport.rcs.oddin.entity.ots.TicketOuterClass;
import com.panda.sport.rcs.oddin.entity.ots.TicketResultOuterClass;
import com.panda.sport.rcs.oddin.pool.GrpcPullSingleClientPool;
import com.panda.sport.rcs.oddin.pool.GrpcTicketClientPool;
import com.panda.sport.rcs.oddin.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import static com.panda.sport.rcs.oddin.common.Constants.VALIDATE_TICKET_GRPC_CONNECTION_TIME;
import static com.panda.sport.rcs.oddin.common.RedisKeyConstants.VALIDATE_PULLSINGLE_GRPC_CONNECTION;
import static com.panda.sport.rcs.oddin.common.RedisKeyConstants.VALIDATE_TICKET_GRPC_CONNECTION;

/**
 * @author conway
 */
@Service
@Slf4j
public class ValidateGrpcConnectionTask {
    @Resource
    private RedisClient redisClient;
    @Resource
    private NacosParameter nacosParameter;

    /**
     * 校验注单链接是否断开
     */
    @Scheduled(fixedDelay =16000)
    public void validateTicketGrpcConnection() {
        String validateTime = redisClient.get(VALIDATE_TICKET_GRPC_CONNECTION);
        if (StringUtils.isBlank(validateTime)) {
            log.info("检验注单链接,从缓存中获取到的校验时间为空，不需要发进行链接超时校验，获取到的时间:{}", validateTime);
            return;
        }
        Long time = System.currentTimeMillis() - Long.valueOf(validateTime);
        if (time > nacosParameter.getValidateGrpcConnectionTimeout()) {
            log.info("=====校验注单grpc链接，链接断开，销毁已建立的链接=====");
            redisClient.delete(VALIDATE_TICKET_GRPC_CONNECTION);
            GrpcTicketClientPool.destroyPool();
        }
    }

    /**
     * 每隔十秒钟发送一个校验grpc注单链接的心跳
     */
    @Scheduled(fixedDelay=10000)
    public void validateTicketGrpcConnectionKeepAlive() {
        String validateTime = redisClient.get(VALIDATE_TICKET_GRPC_CONNECTION);
        if (StringUtils.isNotBlank(validateTime)) {
            log.info("检验注单链接,从缓存中获取到的校验时间不为空，不需要发送校验链接心跳，获取到的时间:{}", validateTime);
            return;
        }
        GrpcTicketClient client = GrpcTicketClientPool.borrowObject();
        client.orderObserver.onNext(TicketOuterClass.TicketRequest.newBuilder().setKeepalive(TicketOuterClass.TicketRequest.newBuilder().getKeepaliveBuilder()).build());
        log.info("检验注单链接10s心跳--->" + DateUtil.format_sss(new Date()));
        redisClient.setExpiry(VALIDATE_TICKET_GRPC_CONNECTION,System.currentTimeMillis(), VALIDATE_TICKET_GRPC_CONNECTION_TIME);
        GrpcTicketClientPool.returnObject(client);
    }

    /**
     * 校验拉单链接是否断开
     */
    @Scheduled(fixedDelay =16000)
    public void validatePullSingleGrpcConnection() {
        String validateTime = redisClient.get(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
        if (StringUtils.isBlank(validateTime)) {
            log.info("检验拉单链接,从缓存中获取到的校验时间为空，不需要发进行链接超时校验，获取到的时间:{}", validateTime);
            return;
        }
        Long time = System.currentTimeMillis() - Long.valueOf(validateTime);
        if (time > nacosParameter.getValidateGrpcConnectionTimeout()) {
            log.info("=====校验拉单grpc链接，链接断开，销毁已建立的链接=====");
            redisClient.delete(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
            GrpcPullSingleClientPool.destroyPool();
        }
    }

    /**
     * 每隔十秒钟发送一个校验grpc拉单链接的心跳
     */
    @Scheduled(fixedDelay=10000)
    public void validatePullsingleGrpcConnectionKeepAlive() {
        String validateTime = redisClient.get(VALIDATE_PULLSINGLE_GRPC_CONNECTION);
        if (StringUtils.isNotBlank(validateTime)) {
            log.info("检验拉单链接,从缓存中获取到的校验时间不为空，不需要发送校验链接心跳，获取到的时间:{}", validateTime);
            return;
        }
        GrpcPullSingleClient client = GrpcPullSingleClientPool.borrowObject();
        client.requestStreamObserver.onNext(TicketResultOuterClass.TicketResultRequest.newBuilder().setKeepalive(TicketResultOuterClass.TicketResultRequest.newBuilder().getKeepaliveBuilder()).build());
        log.info("检验拉单链接10s心跳--->" + DateUtil.format_sss(new Date()));
        redisClient.setExpiry(VALIDATE_PULLSINGLE_GRPC_CONNECTION,System.currentTimeMillis(), VALIDATE_TICKET_GRPC_CONNECTION_TIME);
        GrpcPullSingleClientPool.returnObject(client);
    }
}
