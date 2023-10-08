package com.sportradar.mts.sdk.impl.libs.clientapi;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.cache.LoadingCache;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.sportradar.mts.sdk.api.AccessToken;
import com.sportradar.mts.sdk.api.Ccf;
import com.sportradar.mts.sdk.api.Ticket;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.CcfResponseSchema;
import com.sportradar.mts.sdk.api.impl.mtsdto.clientapi.MaxStakeResponseSchema;
import com.sportradar.mts.sdk.api.interfaces.MtsClientApi;
import com.sportradar.mts.sdk.api.rest.DataProvider;
import com.sportradar.mts.sdk.api.utils.MtsDtoMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.concurrent.ExecutionException;


public class MtsClientApiImpl
        implements MtsClientApi {
    private static final Logger logger = LoggerFactory.getLogger(MtsClientApiImpl.class);

    private final LoadingCache<String, AccessToken> accessTokenCache;
    private final DataProvider<MaxStakeResponseSchema> maxStakeDataProvider;
    private final DataProvider<CcfResponseSchema> ccfDataProvider;
    private final String username;
    private final String password;

    public MtsClientApiImpl(LoadingCache<String, AccessToken> accessTokenCache, DataProvider<MaxStakeResponseSchema> maxStakeDataProvider, DataProvider<CcfResponseSchema> ccfDataProvider, String keycloakUsername, String keycloakPassword) {
        this.accessTokenCache = accessTokenCache;
        this.maxStakeDataProvider = maxStakeDataProvider;
        this.ccfDataProvider = ccfDataProvider;
        this.username = keycloakUsername;
        this.password = keycloakPassword;
    }


    public long getMaxStake(Ticket ticket) throws Exception {
        return getMaxStake(ticket, this.username, this.password);
    }


    public long getMaxStake(Ticket ticket, String username, String password) throws Exception {
        Preconditions.checkNotNull(ticket);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        try {
            logger.info("Called getMaxStake with ticketId={}.", ticket.getTicketId());
            AccessToken token = (AccessToken) this.accessTokenCache.get(getCacheKey(username, password));
            StringEntity stringEntity = new StringEntity(ticket.getJsonValue(), ContentType.APPLICATION_JSON);
            StopWatch sw = new StopWatch();
            sw.start();
            logger.info("sdk http请求开始");
            MaxStakeResponseSchema maxStakeBean = (MaxStakeResponseSchema) this.maxStakeDataProvider.postData(token, stringEntity);
            sw.stop();
            logger.info("sdk http请求返回:耗时" + sw.getTotalTimeMillis());
            if (maxStakeBean != null) {
                if ("403".equals(maxStakeBean.getCode())) {
                    logger.warn("获取最大最小值，权限失败：{}，删除缓存中的token！", ticket.getJsonValue());
                    this.accessTokenCache.invalidate(getCacheKey(username, password));
                    throw new RcsServiceException("mts auth expire  .");
                }
            }
            Long result = MtsDtoMapper.map(maxStakeBean);
            //取默认值处理
            if (result == null) {
                logger.warn("PA getting max stake for ticketId={} failed ：{}"
                        , ticket.getTicketId(), JSONObject.toJSONString(maxStakeBean));
                throw new RcsServiceException("mts getting max stake failed .");
            }
            if (result <= 0) {
//    	  throw new Exception("Failed to get max stake result."); 
                logger.warn("PA getting max stake for ticketId={} failed , result :{}", ticket.getTicketId(), result);
                return 0L;
            }
            return result.longValue();
        } catch (ExecutionException e) {
            logger.warn("Getting max stake for ticketId={} failed.", ticket.getTicketId());
            throw new Exception(e.getCause().getMessage());
        }
    }


    public Ccf getCcf(String sourceId) throws Exception {
        return getCcf(sourceId, this.username, this.password);
    }


    public Ccf getCcf(String sourceId, String username, String password) throws Exception {
        Preconditions.checkNotNull(sourceId);
        Preconditions.checkNotNull(username);
        Preconditions.checkNotNull(password);

        try {
            logger.info("Called getCcf with sourceId={}.", sourceId);
            AccessToken token = (AccessToken) this.accessTokenCache.get(getCacheKey(username, password));
            Ccf result = MtsDtoMapper.map((CcfResponseSchema) this.ccfDataProvider.getData(token, new String[]{sourceId}));
            if (result == null)
                throw new Exception("Failed to get ccf result.");
            return result;
        } catch (ExecutionException e) {
            logger.warn("Getting ccf for sourceId={} failed.", sourceId);
            throw new Exception(e.getCause().getMessage());
        }
    }


    private String getCacheKey(String username, String password) {
        return username + "\n" + password;
    }
}
