package com.panda.sport.rcs.task.job.danger.entity;

import org.springframework.stereotype.Component;

@Component
public class QueryCommParams {

    public static final int page = 1;

    public static final int pageSize = 10000;

    public static final Integer SUCCESS_CODE = 200;

    public static final Integer ORDER_BY = 1;

    public static final String ORDER = "desc";

    public static final int REDIS_EXPIRE_TIME = 30 * 24 * 60 * 60;

    /**
     * 危险玩家组缓存key
     */
    public final static String DANGER_PLAY_GROUP_KEY = "rcs:danger:player:group:";

    /**
     * 危险联赛缓存key
     */
    public final static String DANGER_TOURNAMENT_KEY = "rcs:danger:tournament:";

    /**
     * 危险球队缓存key
     */
    public final static String DANGER_TEAM_KEY = "rcs:danger:team:";

    /**
     * 危险指纹池缓存key
     */
    public final static String DANGER_FP_KEY = "rcs:danger:fp:";

    /**
     * 危险Ip缓存key
     */
    public final static String DANGER_IP_KEY = "rcs:danger:ip:";

    /**
     * 危险ip请求
     */
    public static final String DANGER_IP_REQUEST_URL = "/riskIpPool/getUserIpRiskList";

    /**
     * 危险指纹请求
     */
    public static final String DANGER_FP_REQUEST_URL = "/riskFp/riskFpList";

    /**
     * 危险联赛请求
     */
    public static final String DANGER_TOURNAMENT_REQUEST_URL = "/riskManagement/getTyRiskTournamentList";

    /**
     * 危险球队请求
     */
    public static final String DANGER_TEAM_REQUEST_URL = "/riskManagement/getRiskTeamList";

    /**
     * 危险玩家组请求
     */
    public static final String DANGER_USER_GROUP_REQUEST_URL = "/tyUserGroup/queryUserGroup";

    /**
     * 危险用户玩家组请求
     */
    public static final String DANGER_USER_LINK_GROUP_REQUEST_URL = "/tyUserGroup/getUserListByGroupId";

}
