package com.panda.sport.rcs.dj.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.JsonObject;
import com.mysql.cj.xdevapi.JsonArray;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.dto.dj.*;
import com.panda.sport.rcs.constants.RcsConstant;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.dj.dto.*;
import com.panda.sport.rcs.dj.util.BaseUtils;
import com.panda.sport.rcs.dj.util.CharacterUtils;
import com.panda.sport.rcs.dj.util.HttpUtil;
import com.panda.sport.rcs.dj.util.MD5Util;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.mapper.TUserMapper;

import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.pojo.dj.RcsDjBet;
import com.panda.sport.rcs.pojo.dj.RcsDjOrder;
import com.panda.sport.rcs.pojo.dj.RcsDjUser;

import com.panda.sport.rcs.service.IRcsDjBetService;
import com.panda.sport.rcs.service.IRcsDjOrderService;
import com.panda.sport.rcs.service.IRcsDjUserService;

import com.panda.sport.rcs.utils.SeriesTypeUtils;
import com.panda.sport.rcs.wrapper.limit.RcsQuotaBusinessLimitService;
import io.swagger.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @ClassName DjServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2021/9/14 18:50
 * @Version 1.0
 **/
@Service
@Slf4j
public class DjServiceImpl {

    @Autowired
    private IRcsDjUserService iRcsDjUserService;

    @Autowired
    private IRcsDjOrderService iRcsDjOrderService;

    @Autowired
    private IRcsDjBetService iRcsDjBetService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsQuotaBusinessLimitService rcsQuotaBusinessLimitService;

    @Autowired
    private TUserMapper tUserMapper;

    @Value("${url}")
    private String url;

    @Value("${register}")
    private String register;

    @Value("${limit}")
    private String limit;

    @Value("${bet}")
    private String bet;

    @Value("${cancelOrder}")
    private String cancelOrder;

    @Value("${merchant}")
    private String merchant;

    @Value("${key}")
    private String key;

    private String SEPARATOR = "_";

    static List<String> listSeries = new ArrayList<String>() {{
        add("3004");
        add("40011");
        add("50026");
        add("60057");
        add("700120");
        add("800247");
        add("900502");
        add("10001013");
    }};
//        public static void main(String[] args) {
//        String str = "[\n" +
//                "\t{\n" +
//                "\t\t\"match_id\": \"63455595421407334_65162983730157359\",\n" +
//                "\t\t\"min_bet\": 1,\n" +
//                "\t\t\"max_bet\": 59734\n" +
//                "\t},\n" +
//                "\t{\n" +
//                "\t\t\"match_id\": \"63455595421407334_66348438045760659\",\n" +
//                "\t\t\"min_bet\": 3,\n" +
//                "\t\t\"max_bet\": 987\n" +
//                "\t},\n" +
//                "\t{\n" +
//                "\t\t\"match_id\": \"65162983730157359_66348438045760659\",\n" +
//                "\t\t\"min_bet\": 3,\n" +
//                "\t\t\"max_bet\": 1106\n" +
//                "\t},\n" +
//                "\t{\n" +
//                "\t\t\"match_id\": \"63455595421407334_65162983730157359_66348438045760659\",\n" +
//                "\t\t\"min_bet\": 5,\n" +
//                "\t\t\"max_bet\": 424\n" +
//                "\t},\n" +
//                "\t{\n" +
//                "\t\t\"match_id\": \"all\",\n" +
//                "\t\t\"min_bet\": 3,\n" +
//                "\t\t\"max_bet\": 6183\n" +
//                "\t}\n" +
//                "]";
//
//        List<DJAmountLimitResVo> resList = new ArrayList<>();
//        List<DjResponseV2ListDto> list = JSONArray.parseArray(str, DjResponseV2ListDto.class);
//        Map<Integer, Integer> emptyArray = BaseUtils.getEmptyArray(3);
//
//        DjResponseV2ListDto all = null;
//        for (Map.Entry<Integer, Integer> map : emptyArray.entrySet()) {
//            //System.out.println("key----->"+map.getKey() +"    value------>"+map.getValue());
//            DjResponseV2ListDto minSeries = null;
//            Integer keyResp = map.getKey();
//            for (int i = 0; i < list.size(); i++) {
//                DjResponseV2ListDto djResponseV2ListDto = list.get(i);
//                if (djResponseV2ListDto.getMatch_id().equals("all")) {
//                    all = djResponseV2ListDto;
//                    //System.out.println(map.getKey()+"---------all----------"+all);
//                    continue;
//                }
//                String[] s = djResponseV2ListDto.getMatch_id().split("_");
//                if ((keyResp - 1) / 1000 == s.length) {
//                    if (minSeries == null) {
//                        minSeries = djResponseV2ListDto;
//                    }
//                    if (djResponseV2ListDto.getMax_bet() < minSeries.getMax_bet()) {
//                        minSeries = djResponseV2ListDto;
//                    }
//                }
//                //System.out.println(map.getKey()+"-----minSeries---------"+minSeries);
//            }
//            System.out.println(map.getKey()+"-----最小值------"+minSeries);
//            System.out.println(map.getKey()+"-----all---------"+all);
//
//            if (listSeries.contains(String.valueOf(keyResp))) {
//                System.out.println("contains-----------------------"+keyResp);
//                System.out.println("contains------------all-----------"+all);
//                //temp去all
//                minSeries = all;
//            }
//
//            DJAmountLimitResVo vo = new DJAmountLimitResVo();
//            vo.setMaxStake(Long.parseLong(minSeries.getMax_bet().toString()));
//            vo.setMinStake(Long.parseLong(minSeries.getMin_bet().toString()));
//            vo.setSeriesType(keyResp);
//            resList.add(vo);
//            System.out.println(map.getKey()+"--------------resList-------------"+resList);
//        }
//
//        System.out.println(resList);
//
//    }

    public List<DJAmountLimitResVo> getBetAmountLimit(DJLimitAmoutRequest reqVo) throws Exception {
        String linkId = "getBetAmountLimit";
        if (reqVo != null) {
            linkId = reqVo.getMerchant() + "" + reqVo.getUserId();
        }
        log.info("::{}::查询限额开始:{}", linkId, JSONObject.toJSONString(reqVo));
        //查库看该用户是不是新用户
        LambdaQueryWrapper<RcsDjUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsDjUser::getPandaId, reqVo.getUserId().toString());
        RcsDjUser rcsDjUser = iRcsDjUserService.getOne(wrapper);
        //数据库中没有说明是新用户，需要去三方注册
        if (ObjectUtil.isNull(rcsDjUser)) {
            register(reqVo);
            rcsDjUser = iRcsDjUserService.getOne(wrapper);
        }
        log.info("限额获取getSeriesType值:{}", reqVo.getSeriesType());

        //定义方法返回值
        List<DJAmountLimitResVo> resList = new ArrayList<>();
        //串关
        if (reqVo.getSeriesType() == 1) {
            DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
            //下单类型：1，3 ，4 只有一条数据
            //4-复合玩法
            DjResponseV2DataDto djLimitDto = null;
            try{
                djLimitDto = getDjAmountLimitV2ResVos(reqVo, rcsDjUser, RcsConstant.ORDER_DJ_ONE);
            }catch (Exception ex){
                DJAmountLimitResVo oneDto =  new DJAmountLimitResVo();
                oneDto.setMaxStake(Long.parseLong("0"));
                oneDto.setMinStake(Long.parseLong("0"));
                oneDto.setSeriesType(reqVo.getSeriesType());
                resList.add(oneDto);
                return resList;
            }

            List<DjResponseV2ListDto> list = djLimitDto.getList();
            Integer maxLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMax_bet).collect(Collectors.toList()));
            Integer minLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMin_bet).collect(Collectors.toList()));
            djAmountLimitResVo.setMaxStake(Long.parseLong(maxLimit.toString()));
            djAmountLimitResVo.setMinStake(Long.parseLong(minLimit.toString()));
            djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
            resList.add(djAmountLimitResVo);
            return resList;
        } else {
            //获取串关类型组合，比如2001 3001 3004...
            Map<Integer, Integer> emptyArray = BaseUtils.getEmptyArray(reqVo.getSelectionList().size());
            //2-普通串关
            DjResponseV2DataDto djLimitDto = null;
            try{
                djLimitDto = getDjAmountLimitV2ResVos(reqVo, rcsDjUser, RcsConstant.ORDER_DJ_TWO);
            }catch (Exception ex){
                DJAmountLimitResVo manyDto =  new DJAmountLimitResVo();
                manyDto.setMaxStake(Long.parseLong("0"));
                manyDto.setMinStake(Long.parseLong("0"));
                manyDto.setSeriesType(reqVo.getSeriesType());
                resList.add(manyDto);
                return resList;
            }

            List<DjResponseV2ListDto> list = djLimitDto.getList();
            //定义最大关
            DjResponseV2ListDto maxSeries = null;

            for (Map.Entry<Integer, Integer> map : emptyArray.entrySet()) {
                DjResponseV2ListDto tempMin = null;
                Integer keyResp = map.getKey();
                try {
                    for (int i = 0; i < list.size(); i++) {
                        DjResponseV2ListDto djResponseV2ListDto = list.get(i);
                        //获取最大Series
                        if (djResponseV2ListDto.getMatch_id().equals("all")) {
                            maxSeries = djResponseV2ListDto;
                            continue;
                        }
                        //获取DJ接口数据的分割数组
                        String[] s = djResponseV2ListDto.getMatch_id().split("_");
                        int perfix = (keyResp - 1) / 1000;
                        if (keyResp > 10001) {
                            perfix = (keyResp - 1) / 10000;
                        }
                        if (perfix == s.length) {
                            //最小值取值
                            if (tempMin == null) {
                                tempMin = djResponseV2ListDto;
                            }
                            if (djResponseV2ListDto.getMax_bet() < tempMin.getMax_bet()) {
                                tempMin = djResponseV2ListDto;
                            }
                        }
                    }
                    if (listSeries.contains(String.valueOf(keyResp))) {
                        tempMin = maxSeries;
                    }
                    DJAmountLimitResVo vo = new DJAmountLimitResVo();
                    if (tempMin == null) {
                        vo.setMaxStake(0L);
                        vo.setMinStake(0L);
                    } else {
                        vo.setMaxStake(Long.parseLong(tempMin.getMax_bet().toString()));
                        vo.setMinStake(Long.parseLong(tempMin.getMin_bet().toString()));
                    }
                    vo.setSeriesType(keyResp);
                    resList.add(vo);
                } catch (Exception e) {
                    log.error("getBetAmountLimit请求异常:{}", e.getMessage());
                    DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
                    djAmountLimitResVo.setSeriesType(keyResp);
                    djAmountLimitResVo.setMinStake(0L);
                    djAmountLimitResVo.setMaxStake(0L);
                    resList.add(djAmountLimitResVo);
                }
            }
        }
        return resList;
    }

    public static Integer getSeriesTypeV2(Integer seriesType) {
        if (seriesType == null) {
            throw new RcsServiceException("seriesType参数错误");
        }
        if (seriesType < 1000 && seriesType.equals(1)) {
            return 1;
        } else {
            if (seriesType < 1000) {
                throw new RcsServiceException("seriesType参数错误");
            } else {
                int type = 0;
                Integer prifix = Integer.parseInt(String.valueOf(seriesType).substring(0, 4));
                if (prifix % 10 == 0) {
                    type = prifix / 100;
                } else {
                    type = prifix / 1000;
                }
                if (type > 4) {
                    type = 4;
                }
                return type;
            }
        }
    }

    /**
     * 获取单个投注项限额
     *
     * @param reqVo
     * @param rcsDjUser
     * @return
     */
    private DjLimitDto getDjAmountLimitResVos(DJLimitAmoutRequest reqVo, RcsDjUser rcsDjUser) throws Exception {
        Long time = System.currentTimeMillis() / 1000;
        Map<String, String> paramMap = new HashMap<>();
        List<Map<String, String>> mapList = new ArrayList<>();
        for (Selection selection : reqVo.getSelectionList()) {
            Map<String, String> map = new HashMap<>();
            map.put("match_id", selection.getMatchId().toString());
            map.put("market_id", selection.getMarketId().toString());
            map.put("odd_id", selection.getOddsId().toString());
            mapList.add(map);
        }
        paramMap.put("quota", JSON.toJSONString(mapList));
        paramMap.put("time", time.toString());
        paramMap.put("merchant", merchant);
        paramMap.put("member_id", rcsDjUser.getDjId());
        paramMap.put("account", reqVo.getUsername());
        paramMap.put("key", key);

        String sign = getSign(paramMap);
        paramMap.put("sign", sign);
        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key, paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String string = HttpUtil.doGet(url, limit, nameValuePairs);
        DjResponseDto djResponseDto = JSONObject.parseObject(string, DjResponseDto.class);
        log.info("电竞查询余额返回数据:{}", JSON.toJSONString(djResponseDto));
        if ("true".equals(djResponseDto.getStatus())) {
            DjLimitDto djLimitDto = JSONObject.parseObject(djResponseDto.getData(), DjLimitDto.class);
            log.info("电竞查询限额:{}", JSON.toJSONString(djLimitDto));

            return djLimitDto;
        } else {
            throw new QueryLimitAmoutException(djResponseDto.getData());
        }
    }

    /**
     * 电竞限额新接口
     *
     * @param reqVo
     * @param rcsDjUser
     * @return
     * @throws Exception
     * @type type的类型 1-普通注单 2-普通串关 3-局内串关 4-复合玩法
     */
    public DjResponseV2DataDto getDjAmountLimitV2ResVos(DJLimitAmoutRequest reqVo, RcsDjUser rcsDjUser, int type) throws Exception {
        Long time = System.currentTimeMillis() / 1000;
        JSONObject paramMap = new JSONObject();
        paramMap.put("order_type", type);
        paramMap.put("merchant", Long.parseLong(merchant));
        paramMap.put("member_id", Long.parseLong(rcsDjUser.getDjId()));
        //paramMap.put("member_id", 435242545653453L);
        paramMap.put("account", reqVo.getUsername());

        JSONArray mapList = new JSONArray();
        for (Selection selection : reqVo.getSelectionList()) {
            JSONObject map = new JSONObject();
            map.put("match_id", selection.getMatchId().toString());
            map.put("market_id", selection.getMarketId().toString());
            map.put("odd_id", selection.getOddsId().toString());
            map.put("odds", selection.getOdds());
            mapList.add(map);
        }
        paramMap.put("data", mapList);
        paramMap.put("time", time.toString());
        paramMap.put("key", key);
        //sign = MD5(LowerCase(order_type+merchant+member_id+account+time+Key)）,全部当作字符串拼接转小写再MD5
        StringBuilder signBuilder = new StringBuilder();
        signBuilder.append(paramMap.get("order_type"))
                .append(paramMap.get("merchant"))
                .append(paramMap.get("member_id"))
                .append(paramMap.get("account"))
                .append(paramMap.get("time"))
                .append(paramMap.get("key"));
        String sign = MD5Util.stringMd5(signBuilder.toString().toLowerCase());
        paramMap.put("sign", sign);
        log.info("v2电竞查询余额Params:{}", paramMap.toJSONString());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("merchant", merchant);
        String string = HttpUtil.doPostJsonHeader("https://" + url + limit, headerMap, paramMap.toJSONString());
        log.info("v2电竞查询余额返回数据String:{}", string);
        DjResponseV2Dto djResponseDto = JSONObject.parseObject(string, DjResponseV2Dto.class);
        //log.info("v2电竞查询余额返回数据解析:{}", JSON.toJSONString(djResponseDto));
        if ("true".equals(djResponseDto.getStatus())) {
            DjResponseV2DataDto djLimitDto = JSONObject.parseObject(djResponseDto.getData(), DjResponseV2DataDto.class);
            //log.info("v2电竞查询限额:{}", JSON.toJSONString(djLimitDto));
            return djLimitDto;
        } else {
            throw new QueryLimitAmoutException(djResponseDto.getData());
        }
    }

    public DJAmountLimitResVo getBetAmountLimitOld(DJLimitAmoutRequest reqVo) throws Exception {
        String linkId = "getBetAmountLimit";
        if (reqVo != null) {
            linkId = reqVo.getMerchant() + "" + reqVo.getUserId();
        }
        log.info("::{}::查询限额开始:{}", linkId, JSONObject.toJSONString(reqVo));
        //查库看该用户是不是新用户
        LambdaQueryWrapper<RcsDjUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsDjUser::getPandaId, reqVo.getUserId().toString());
        RcsDjUser rcsDjUser = iRcsDjUserService.getOne(wrapper);
        //数据库中没有说明是新用户，需要去三方注册
        if (ObjectUtil.isNull(rcsDjUser)) {
            register(reqVo);
            rcsDjUser = iRcsDjUserService.getOne(wrapper);
        }
        log.info("限额获取getSeriesType值:{}", reqVo.getSeriesType());
        //下单类型：1-普通注单 2-普通串关 3-局内串关 4-复合玩法
        //下单类型：1，3 ，4 只有一条数据
        //2-普通串关有多条数据
        //普通注单(1) 普通串关(2001,3001,4001,5001) 局内串关(?) 复合玩法(3004,40011,50026)
        //
        DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
        if (reqVo.getSeriesType().equals(RcsConstant.Two_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.THREE_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.FOUR_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.FIVE_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.SIX_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.SENVEN_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.ENGIT_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.NINE_LEVEL)
                || reqVo.getSeriesType().equals(RcsConstant.TEN_LEVEL)) {
            //下单类型：1，3 ，4 只有一条数据
            //2-普通串关
            DjResponseV2DataDto djLimitDto = getDjAmountLimitV2ResVos(reqVo, rcsDjUser, RcsConstant.ORDER_DJ_TWO);
            List<DjResponseV2ListDto> list = djLimitDto.getList();
            List<Long> matchIds = reqVo.getSelectionList().stream().sorted(Comparator.comparing(Selection::getMatchId)).collect(Collectors.toList()).stream().map(Selection::getMatchId).collect(Collectors.toList());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < matchIds.size(); i++) {
                sb.append(matchIds.get(i)).append("_");
            }
            String result = sb.toString().substring(0, sb.toString().length() - 1);
            log.info("普通串关赛事id组成的key=" + result);
            DjResponseV2ListDto resDto = list.stream().filter(a -> a.getMatch_id().equals(result)).findFirst().orElse(null);
            if (resDto != null) {
                djAmountLimitResVo.setMaxStake(Long.parseLong(resDto.getMax_bet().toString()));
                djAmountLimitResVo.setMinStake(Long.parseLong(resDto.getMin_bet().toString()));
                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
            }
        } else if (reqVo.getSeriesType().equals(RcsConstant.Single_LEVEL)) {
            //下单类型：1，3 ，4 只有一条数据
            //4-复合玩法
            DjResponseV2DataDto djLimitDto = getDjAmountLimitV2ResVos(reqVo, rcsDjUser, RcsConstant.ORDER_DJ_ONE);
            List<DjResponseV2ListDto> list = djLimitDto.getList();
            Integer maxLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMax_bet).collect(Collectors.toList()));
            Integer minLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMin_bet).collect(Collectors.toList()));
            djAmountLimitResVo.setMaxStake(Long.parseLong(maxLimit.toString()));
            djAmountLimitResVo.setMinStake(Long.parseLong(minLimit.toString()));
            djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
            return djAmountLimitResVo;
        } else if (reqVo.getSeriesType().equals(RcsConstant.THREE_LEVEL_4)
                || reqVo.getSeriesType().equals(RcsConstant.FOUR_LEVEL_11)
                || reqVo.getSeriesType().equals(RcsConstant.FIVE_LEVEL_26)) {
            DjResponseV2DataDto djLimitDto = getDjAmountLimitV2ResVos(reqVo, rcsDjUser, RcsConstant.ORDER_DJ_TWO);
            List<DjResponseV2ListDto> list = djLimitDto.getList();
            DjResponseV2ListDto resDto = list.stream().filter(a -> a.getMatch_id().equals("all")).findFirst().orElse(null);
            if (resDto != null) {
                djAmountLimitResVo.setMaxStake(Long.parseLong(resDto.getMax_bet().toString()));
                djAmountLimitResVo.setMinStake(Long.parseLong(resDto.getMin_bet().toString()));
                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
            }
        } else {
            throw new QueryLimitAmoutException("串关投注项个数不对或者串关类型不对,SeriesType=" + reqVo.getSeriesType());
        }
        return djAmountLimitResVo;

//        if (RcsConstant.Single_LEVEL .equals( reqVo.getSeriesType()) && !CollectionUtils.isEmpty(reqVo.getSelectionList()) &&1 == reqVo.getSelectionList().size()) {
//            //DjLimitDto djLimitDto = getDjAmountLimitResVos(reqVo, rcsDjUser);
//            DjResponseV2DataDto djLimitDto = getDjAmountLimitV2ResVos(reqVo,rcsDjUser);
//            List<DjResponseV2ListDto> list = djLimitDto.getList();
//            Integer maxLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMax_bet).collect(Collectors.toList()));
//            Integer minLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMin_bet).collect(Collectors.toList()));
//            Long maxAmount = new BigDecimal(djLimitDto.getMax_prize()).divide(new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).subtract(new BigDecimal("1")), 0, BigDecimal.ROUND_DOWN).longValue();
//            //Long minAmount = Long.valueOf(djLimitDto.getMin_bet());
//            Long minAmount = Long.valueOf(minLimit);
//            maxAmount = maxAmount > 0L ? maxAmount : 0L;
//            minAmount = maxAmount == 0L ? 0L : minAmount;
//            DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
//            //单注投注额限额和单注赔付的较小
//            //Long limitAmount = Math.min(maxAmount, Long.valueOf(djLimitDto.getMax_bet()));
//            Long limitAmount = Math.min(maxAmount, Long.valueOf(maxLimit));
//            djAmountLimitResVo.setMaxStake(limitAmount);
//            djAmountLimitResVo.setMinStake(minAmount);
//            djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
//            return djAmountLimitResVo;
//        } else if (RcsConstant.Single_LEVEL .equals( reqVo.getSeriesType()) && !CollectionUtils.isEmpty(reqVo.getSelectionList()) && reqVo.getSelectionList().size() == type) {
//            //DjLimitDto djLimitDto = getDjAmountLimitResVos(reqVo, rcsDjUser);
//            DjResponseV2DataDto djLimitDto = getDjAmountLimitV2ResVos(reqVo,rcsDjUser);
//            List<DjResponseV2ListDto> list = djLimitDto.getList();
//            Integer maxLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMax_bet).collect(Collectors.toList()));
//            Integer minLimit = Collections.min(list.stream().map(DjResponseV2ListDto::getMin_bet).collect(Collectors.toList()));
//            if (RcsConstant.Two_LEVEL.equals(reqVo.getSeriesType()) || RcsConstant.THREE_LEVEL.equals(reqVo.getSeriesType()) || RcsConstant.FOUR_LEVEL.equals(reqVo.getSeriesType()) || RcsConstant.FIVE_LEVEL.equals(reqVo.getSeriesType())) {
//                BigDecimal seriesOdds = new BigDecimal(1);
//                for (Selection selection : reqVo.getSelectionList()) {
//                    seriesOdds = seriesOdds.multiply(new BigDecimal(selection.getOdds()));
//                }
//                Long seriesLimitAmount = new BigDecimal(djLimitDto.getMax_prize()).divide(seriesOdds.subtract(new BigDecimal("1")), 0, BigDecimal.ROUND_DOWN).longValue();
//                //Long maxbet = Math.min(Long.valueOf(djLimitDto.getMax_bet()), seriesLimitAmount);
//                Long maxbet = Math.min(Long.valueOf(maxLimit), seriesLimitAmount);
//                DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
//                djAmountLimitResVo.setMaxStake(maxbet);
//                //djAmountLimitResVo.setMinStake(Long.valueOf(djLimitDto.getMin_bet()));
//                djAmountLimitResVo.setMinStake(Long.valueOf(minLimit));
//                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
//                return djAmountLimitResVo;
//            } else if (RcsConstant.THREE_LEVEL_4.equals(reqVo.getSeriesType())) {
//                BigDecimal seriesTwo1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds()));
//                BigDecimal seriesTwo2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesTwo3 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesThree = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal decimal = seriesTwo1.subtract(new BigDecimal(1)).add(seriesTwo2.subtract(new BigDecimal(1))).add(seriesTwo3.subtract(new BigDecimal(1))).add(seriesThree.subtract(new BigDecimal(1)));
//                Long seriesLimit = new BigDecimal(djLimitDto.getMax_prize()).divide(decimal, 0, BigDecimal.ROUND_DOWN).longValue();
//                //Long maxbet = Math.min(Long.valueOf(djLimitDto.getMax_bet()), seriesLimit);
//                Long maxbet = Math.min(Long.valueOf(maxLimit), seriesLimit);
//                DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
//                djAmountLimitResVo.setMaxStake(maxbet);
//                //djAmountLimitResVo.setMinStake(Long.valueOf(djLimitDto.getMin_bet()));
//                djAmountLimitResVo.setMinStake(Long.valueOf(minLimit));
//                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
//                return djAmountLimitResVo;
//            } else if (RcsConstant.FOUR_LEVEL_11.equals(reqVo.getSeriesType())) {
//                BigDecimal seriesTwo1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds()));
//                BigDecimal seriesTwo2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesTwo3 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesTwo4 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesTwo5 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesTwo6 = new BigDecimal(reqVo.getSelectionList().get(2).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesThree2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree3 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree4 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesFour = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal decimal = seriesTwo1.subtract(new BigDecimal(1)).add(seriesTwo2.subtract(new BigDecimal(1))).add(seriesTwo3.subtract(new BigDecimal(1)))
//                        .add(seriesTwo4.subtract(new BigDecimal(1))).add(seriesTwo5.subtract(new BigDecimal(1))).add(seriesTwo6.subtract(new BigDecimal(1)))
//                        .add(seriesThree1.subtract(new BigDecimal(1))).add(seriesThree2.subtract(new BigDecimal(1))).add(seriesThree3.subtract(new BigDecimal(1)))
//                        .add(seriesThree4.subtract(new BigDecimal(1))).add(seriesFour.subtract(new BigDecimal(1)));
//                Long seriesLimit = new BigDecimal(djLimitDto.getMax_prize()).divide(decimal, 0, BigDecimal.ROUND_DOWN).longValue();
//                double bigOdds = 0.0;
//                for (Selection selection : reqVo.getSelectionList()) {
//                    if (selection.getOdds() > bigOdds)
//                        bigOdds = selection.getOdds();
//                }
//                Long limit = new BigDecimal(djLimitDto.getMax_prize()).divide((new BigDecimal(bigOdds).subtract(new BigDecimal(1))).multiply(new BigDecimal(7)), 0, BigDecimal.ROUND_DOWN).longValue();
//                //Long maxBet = Math.min(Long.valueOf(djLimitDto.getMax_bet()), seriesLimit);
//                Long maxBet = Math.min(Long.valueOf(maxLimit), seriesLimit);
//                Long maxBetFinal = Math.min(limit, maxBet);
//                DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
//                djAmountLimitResVo.setMaxStake(maxBetFinal);
//                //djAmountLimitResVo.setMinStake(Long.valueOf(djLimitDto.getMin_bet()));
//                djAmountLimitResVo.setMinStake(Long.valueOf(minLimit));
//                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
//                return djAmountLimitResVo;
//            } else if (RcsConstant.FIVE_LEVEL_26.equals(reqVo.getSeriesType())) {
//
//                BigDecimal seriesTwo1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds()));
//                BigDecimal seriesTwo2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesTwo3 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesTwo4 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesTwo5 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesTwo6 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesTwo7 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesTwo8 = new BigDecimal(reqVo.getSelectionList().get(2).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesTwo9 = new BigDecimal(reqVo.getSelectionList().get(2).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesTwo10 = new BigDecimal(reqVo.getSelectionList().get(3).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds()));
//                BigDecimal seriesThree2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree3 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree4 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesThree5 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree6 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree7 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree8 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree9 = new BigDecimal(reqVo.getSelectionList().get(2).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesThree10 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesFour1 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds()));
//                BigDecimal seriesFour2 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesFour3 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesFour4 = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesFour5 = new BigDecimal(reqVo.getSelectionList().get(1).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//                BigDecimal seriesFive = new BigDecimal(reqVo.getSelectionList().get(0).getOdds()).multiply(new BigDecimal(reqVo.getSelectionList().get(1).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(2).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(3).getOdds())).multiply(new BigDecimal(reqVo.getSelectionList().get(4).getOdds()));
//
//                BigDecimal decimal = seriesTwo1.subtract(new BigDecimal(1)).add(seriesTwo2.subtract(new BigDecimal(1))).add(seriesTwo3.subtract(new BigDecimal(1)))
//                        .add(seriesTwo4.subtract(new BigDecimal(1))).add(seriesTwo5.subtract(new BigDecimal(1))).add(seriesTwo6.subtract(new BigDecimal(1)))
//                        .add(seriesTwo7.subtract(new BigDecimal(1))).add(seriesTwo8.subtract(new BigDecimal(1)))
//                        .add(seriesTwo9.subtract(new BigDecimal(1))).add(seriesTwo10.subtract(new BigDecimal(1)))
//                        .add(seriesThree1.subtract(new BigDecimal(1))).add(seriesThree2.subtract(new BigDecimal(1)))
//                        .add(seriesThree3.subtract(new BigDecimal(1))).add(seriesThree4.subtract(new BigDecimal(1)))
//                        .add(seriesThree5.subtract(new BigDecimal(1))).add(seriesThree6.subtract(new BigDecimal(1)))
//                        .add(seriesThree7.subtract(new BigDecimal(1))).add(seriesThree8.subtract(new BigDecimal(1)))
//                        .add(seriesThree9.subtract(new BigDecimal(1))).add(seriesThree10.subtract(new BigDecimal(1)))
//                        .add(seriesFour1.subtract(new BigDecimal(1))).add(seriesFour2.subtract(new BigDecimal(1)))
//                        .add(seriesFour3.subtract(new BigDecimal(1))).add(seriesFour4.subtract(new BigDecimal(1)))
//                        .add(seriesFour5.subtract(new BigDecimal(1))).add(seriesFive.subtract(new BigDecimal(1)));
//                Long seriesLimit = new BigDecimal(djLimitDto.getMax_prize()).divide(decimal, 0, BigDecimal.ROUND_DOWN).longValue();
//                //Long maxbet = Math.min(Long.valueOf(djLimitDto.getMax_bet()), seriesLimit);
//                Long maxbet = Math.min(Long.valueOf(maxLimit), seriesLimit);
//                DJAmountLimitResVo djAmountLimitResVo = new DJAmountLimitResVo();
//                djAmountLimitResVo.setMaxStake(maxbet);
//                //djAmountLimitResVo.setMinStake(Long.valueOf(djLimitDto.getMin_bet()));
//                djAmountLimitResVo.setMinStake(Long.valueOf(minLimit));
//                djAmountLimitResVo.setSeriesType(reqVo.getSeriesType());
//                return djAmountLimitResVo;
//            } else {
//                throw new QueryLimitAmoutException("串关投注项个数不对或者串关类型不对,SeriesType=" + reqVo.getSeriesType());
//            }
//        } else {
//            throw new QueryLimitAmoutException("串关投注项个数不对或者串关类型不对,SeriesType=" + reqVo.getSeriesType());
//        }
    }

    /**
     * 获取sign
     *
     * @param paramMap
     * @return
     */
    private String getSign(Map<String, String> paramMap) {
        List<String> keyList = new ArrayList<>(paramMap.keySet());
        Collections.sort(keyList);
        StringBuilder signBuilder = new StringBuilder();
        for (String key : keyList) {
            signBuilder.append(key).append("=").append(paramMap.get(key)).append("&");
        }
        String param = signBuilder.substring(0, signBuilder.toString().length() - 1);

        String encryt = MD5Util.stringMd5(param);

        String str1 = encryt.substring(0, 9);
        String str2 = encryt.substring(9, 17);
        String str3 = encryt.substring(17);

        StringBuilder builder2 = new StringBuilder(CharacterUtils.getRandomString(2));

        return builder2.append(str1).append(CharacterUtils.getRandomString(2))
                .append(str2).append(CharacterUtils.getRandomString(2))
                .append(str3).append(CharacterUtils.getRandomString(2)).toString();
    }

    private void register(DJLimitAmoutRequest reqVo) {
        Long time = System.currentTimeMillis() / 1000;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("username", reqVo.getUsername());
        paramMap.put("tester", reqVo.getTester());
        paramMap.put("time", time.toString());
        paramMap.put("merchant", merchant);
        paramMap.put("key", key);

        String sign = getSign(paramMap);

        paramMap.put("sign", sign);
        Map<String, String> head = new HashMap<>();
        head.put("sign", sign);
        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key, paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String string = HttpUtil.doPost(url, register, nameValuePairs, head);
        DjResponseDto dto = JSONObject.parseObject(string, DjResponseDto.class);
        if ("false".equals(dto.getStatus())) {
            throw new RegisterException("三方注册报错:" + dto.getData());
        }
        RcsDjUser rcsDjUser = new RcsDjUser().setDjId(dto.getData()).setPandaId(reqVo.getUserId().toString()).setCreateTime(System.currentTimeMillis());
        iRcsDjUserService.save(rcsDjUser);
    }

    private String getUserByUserId(Long userId) {
        String userInfoKey = String.format("rcs:user:by:userId:%s", userId);
        if (redisClient.exist(userInfoKey)) {
            String userInfoValue = redisClient.get(userInfoKey);
            log.info("::{}:: 缓存数据 userInfoKey key{} value{}", userId, userInfoKey, userInfoValue);
            return redisClient.get(userInfoKey);
        }
        LambdaQueryWrapper<RcsDjUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RcsDjUser::getPandaId, userId.toString());
        RcsDjUser rcsDjUser = iRcsDjUserService.getOne(wrapper, false);
        log.info("::{}:: 查询用户信息{}", userId, JSONObject.toJSONString(rcsDjUser));
        if (Objects.isNull(rcsDjUser) || StringUtils.isEmpty(rcsDjUser.getDjId())) {
            log.warn("::{}::ID查询记录为空", userId);
            return null;
        }
        redisClient.setExpiry(userInfoKey, rcsDjUser.getDjId(), 15 * 60L);
        return rcsDjUser.getDjId();
    }

    private String getTenantCodeByUserId(Long userId) {
        /*String tenantCodeKey=String.format("rcs:tenant:code:user:%s",userId);
        if(redisClient.exist(tenantCodeKey)){
           return redisClient.get(tenantCodeKey);
        }
        TUser user =tUserMapper.selectByUserId(userId);
        if(Objects.nonNull(user)){
            redisClient.setExpiry(tenantCodeKey,user.getMerchantCode(),10 * 60l);
        }
        return user.getMerchantCode();*/
        try {
            Map<String, String> map = tUserMapper.selectBusinessIdByUserId(userId);
            if (map != null) {
                return String.valueOf(map.get("merchants_id"));
            }
        } catch (Exception e) {
            log.error("::查询用户所属商户id异常::{}", e.getMessage());
        }
        return null;
    }

    /**
     * 电竞体育投注接口
     *
     * @param djBetReqVo
     * @return
     */
    public Response<DJBetResVo> djBet(DJBetReqVo djBetReqVo) {
        log.info("::{}::电竞投注实体{}", djBetReqVo.getOrderNo(), JSONObject.toJSONString(djBetReqVo));
        //获取用户信息
        String djUserId = getUserByUserId(djBetReqVo.getUserId());
        if (StringUtils.isBlank(djUserId)) {
            log.warn("::{}::电竞投注对应的电竞用户ID为空", djBetReqVo.getOrderNo());
            return Response.error(500, "电竞投注对应的电竞用户ID为空", null);
        }
        saveOrderDetail(djBetReqVo);
        //调取三方进行投注
        String volumePercentage = getVolumePercentage(djBetReqVo);
        log.info("::{}::电竞投注货量{}", djBetReqVo.getOrderNo(), volumePercentage);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("c", djBetReqVo.getBetNum().toString());
        paramMap.put("device", djBetReqVo.getDevice().toString());
        paramMap.put("merchant", merchant);
        paramMap.put("uid", djUserId);
        paramMap.put("account", djBetReqVo.getAccountName());
        paramMap.put("ip", djBetReqVo.getIp());
        paramMap.put("odd_update_type", djBetReqVo.getOddUpdateType().toString());
        if(StringUtils.isNotBlank(volumePercentage)){
            paramMap.put("hide_amount_rate", volumePercentage);
        }
        paramMap.put("key", key);
        if (djBetReqVo.getSeriesType() == 1) {
            Selection selection = djBetReqVo.getOrderList().get(0).getSelections().get(0);
            String b0 = new StringBuilder("mch=").append(selection.getMatchId())
                    .append("&mkt=").append(selection.getMarketId())
                    .append("&oid=").append(selection.getOddsId())
                    .append("&odd=").append(selection.getOdds())
                    .append("&a=").append(djBetReqVo.getOrderList().get(0).getAmount())
                    .append("&bt=").append(djBetReqVo.getOrderList().get(0).getOrderType())
                    .append("&order=").append(djBetReqVo.getOrderNo())
                    .append("&num=").append(1)
                    .toString();
            paramMap.put("b[0]", b0);
        } else {
            for (int i = 0; i < djBetReqVo.getOrderList().size(); i++) {
                DjBetOrder djBetOrder = djBetReqVo.getOrderList().get(i);
                StringBuilder itemDetail = new StringBuilder("bt=").append(djBetOrder.getOrderType())
                        .append("&t=").append(djBetOrder.getNum())
                        .append("&a=").append(djBetOrder.getAmount()).append("&b=");
                for (Selection selection : djBetOrder.getSelections()) {
                    itemDetail.append(selection.getMatchId())
                            .append(",").append(selection.getMarketId()).append(",")
                            .append(selection.getOddsId()).append(",").append(selection.getOdds()).append("|");
                }
                Integer num = 0;
                if (RcsConstant.Two_LEVEL.equals(djBetReqVo.getSeriesType()) || RcsConstant.THREE_LEVEL.equals(djBetReqVo.getSeriesType())
                        || RcsConstant.FOUR_LEVEL.equals(djBetReqVo.getSeriesType()) || RcsConstant.FIVE_LEVEL.equals(djBetReqVo.getSeriesType())) {
                    num = 1;
                } else {
                    num = djBetReqVo.getOrderList().size();
                }
                paramMap.put("b[" + i + "]", itemDetail.substring(0, itemDetail.toString().length() - 1) + "&order=" + djBetReqVo.getOrderNo() + "&num=" + num);
            }
        }
        String sign = getSign(paramMap);
        paramMap.put("sign", sign);
        Map<String, String> head = new HashMap<>();
        head.put("sign", sign);
        paramMap.remove("key");
        log.info("::{}:: 请求参数:{}", djBetReqVo.getOrderNo(), JSON.toJSONString(paramMap));
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key, paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String doPostResult = HttpUtil.doPost(url, bet, nameValuePairs, head);
        log.info("::{}:: 电竞体育投注返回数据:{}", djBetReqVo.getOrderNo(), doPostResult);
        DjResponseDto djResponseDto = JSONObject.parseObject(doPostResult, DjResponseDto.class);

        updateOrderAndDetail(djBetReqVo, djResponseDto);

        if ("true".equals(djResponseDto.getStatus())) {
            DJBetResVo djBetResVo = new DJBetResVo(djBetReqVo.getOrderNo(), 1, djResponseDto.getId_mapping(), djResponseDto.getOdd_mapping());
            return Response.success(djBetResVo);
        } else {
            DJBetResVo djBetResVo = new DJBetResVo(djBetReqVo.getOrderNo(), 0, djResponseDto.getId_mapping(), djResponseDto.getOdd_mapping());
            log.warn("::{}::取消失败 拒绝代码{} 明细{}", djBetResVo.getOrderNo(), djResponseDto.getCode(), JSONObject.toJSONString(djBetResVo));
            return Response.error(Integer.valueOf(djResponseDto.getCode()), djResponseDto.getData(), djBetResVo);
        }
    }

    private String getVolumePercentage(DJBetReqVo djBetReqVo) {
        String tenantId = getTenantCodeByUserId(djBetReqVo.getUserId());
        if (StringUtils.isBlank(tenantId)) return null;
        String djVolumePercentageKey = String.format("rcs:gaming:volume:percentage:%s", tenantId);
        if (redisClient.exist(djVolumePercentageKey)) {
            String djVolumePercentageValue = redisClient.get(djVolumePercentageKey);
            log.info("::{}:: 获取电竞缓存商户货量{}", djBetReqVo.getOrderNo(), djVolumePercentageValue);
            return djVolumePercentageValue;
        }
        RcsQuotaBusinessLimit rcsQuotaBusinessLimit = rcsQuotaBusinessLimitService.getByBusinessId(tenantId);
        if (Objects.nonNull(rcsQuotaBusinessLimit) && null != rcsQuotaBusinessLimit.getGamingBetPercent()) {
            BigDecimal volumePercentage = new BigDecimal(100).subtract(rcsQuotaBusinessLimit.getGamingBetPercent());
            String value = volumePercentage.stripTrailingZeros().toPlainString();
            log.info("::{}:: 获取电竞商户货量{} 相减货量{}", djBetReqVo.getOrderNo(), rcsQuotaBusinessLimit.getGamingBetPercent(), value);
            redisClient.setExpiry(djVolumePercentageKey, value, 10 * 60L);
            return value;
        }
        log.info("::{}:: 获取配置电竞商户货量为空", djBetReqVo.getOrderNo());
        return "0";
    }

    public Response<DJCancelOrderResVo> cancelOrder(DjCancelOrderReqVo reqVo) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("order_ids", reqVo.getOrderIds());
        paramMap.put("order_type", reqVo.getOrderType() + "");
        paramMap.put("merchant", merchant);
        paramMap.put("reason", reqVo.getReasonCode() + "");
        paramMap.put("time", reqVo.getTime() + "");
        paramMap.put("key", key);
        String sign = getSign(paramMap);
        paramMap.put("sign", sign);

        paramMap.remove("key");
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : paramMap.keySet()) {
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key, paramMap.get(key));
            nameValuePairs.add(nameValuePair);
        }
        String string = HttpUtil.doPost(url, cancelOrder, nameValuePairs, null);

        DJCancelOrderResVo cancelOrderResVo = JSONObject.parseObject(string, DJCancelOrderResVo.class);

        if ("true".equals(cancelOrderResVo.getStatus())) {
            try {
                LambdaUpdateWrapper<RcsDjOrder> orderUpdateWrapper = new LambdaUpdateWrapper<>();
                orderUpdateWrapper.set(RcsDjOrder::getOrderStatus, 3);
                orderUpdateWrapper.set(RcsDjOrder::getThirdStatus, 3);
                orderUpdateWrapper.set(RcsDjOrder::getModifyTime, System.currentTimeMillis());
                orderUpdateWrapper.eq(RcsDjOrder::getOrderNo, reqVo.getOrderNo());
                iRcsDjOrderService.update(orderUpdateWrapper);
                log.info("::{}::取消注单更新rcs_order_dj成功", reqVo.getOrderNo());
            } catch (Exception e) {
                log.error("::{}::DJ取消注单更新本地库状态报错,不影响后续流程", reqVo.getOrderNo());
            }
            return Response.success(cancelOrderResVo);
        } else {
            log.warn("::{}::取消失败 拒绝代码{} 明细{}", reqVo.getOrderNo(), cancelOrderResVo.getCode(), JSONObject.toJSONString(cancelOrderResVo));
            return Response.error(Integer.valueOf(cancelOrderResVo.getCode()), cancelOrderResVo.getData(), cancelOrderResVo);
        }
    }


    /**
     * 更新订单和订单详细表
     *
     * @param djBetReqVo
     * @param djResponseDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderAndDetail(DJBetReqVo djBetReqVo, DjResponseDto djResponseDto) {
        //更新订单表和订单详细表
        LambdaUpdateWrapper<RcsDjOrder> orderUpdateWrapper = new LambdaUpdateWrapper<>();
        Integer status = "true".equals(djResponseDto.getStatus()) ? 1 : 2;
        orderUpdateWrapper.set(RcsDjOrder::getOrderStatus, status);
        orderUpdateWrapper.set(RcsDjOrder::getThirdStatus, djResponseDto.getStatus());
        orderUpdateWrapper.set(RcsDjOrder::getReason, djResponseDto.getData());
        orderUpdateWrapper.set(RcsDjOrder::getModifyTime, System.currentTimeMillis());
        orderUpdateWrapper.eq(RcsDjOrder::getOrderNo, djBetReqVo.getOrderNo());
        iRcsDjOrderService.update(orderUpdateWrapper);
        log.info("::{}::更新rcs_dj_order成功", djBetReqVo.getOrderNo());

        LambdaUpdateWrapper<RcsDjBet> detailUpdateWrapper = new LambdaUpdateWrapper<>();
        detailUpdateWrapper.set(RcsDjBet::getOrderStatus, status);
        detailUpdateWrapper.set(RcsDjBet::getModifyTime, System.currentTimeMillis());
        detailUpdateWrapper.eq(RcsDjBet::getOrderNo, djBetReqVo.getOrderNo());
        iRcsDjBetService.update(detailUpdateWrapper);
        log.info("::{}::更新rcs_dj_order_detail成功", djBetReqVo.getOrderNo());
    }

    /**
     * 保存订单和订单详细
     *
     * @param djBetReqVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderDetail(DJBetReqVo djBetReqVo) {
        //保存订单信息
        RcsDjOrder rcsDjOrder = new RcsDjOrder();
        rcsDjOrder.setBetTime(djBetReqVo.getBetTime());
        rcsDjOrder.setCreateTime(System.currentTimeMillis());
        rcsDjOrder.setDeviceType(djBetReqVo.getDevice());
        rcsDjOrder.setIp(djBetReqVo.getIp());
        rcsDjOrder.setOrderNo(djBetReqVo.getOrderNo());
        rcsDjOrder.setOrderStatus(0);
        Long orderAmoutTotal = djBetReqVo.getOrderList().stream().mapToLong(num -> num.getAmount()).sum();
        rcsDjOrder.setProductAmountTotal(orderAmoutTotal);
        rcsDjOrder.setProductCount(djBetReqVo.getBetNum());
        rcsDjOrder.setSeriesType(djBetReqVo.getSeriesType());
        rcsDjOrder.setUid(djBetReqVo.getUserId());
        rcsDjOrder.setVipLevel(djBetReqVo.getVipLevel());
        iRcsDjOrderService.save(rcsDjOrder);

        List<RcsDjBet> rcsDjBets = new ArrayList<>();
        //保存注单信息
        for (DjBetOrder djBetOrder : djBetReqVo.getOrderList()) {
            for (Selection selection : djBetOrder.getSelections()) {
                RcsDjBet rcsDjBet = new RcsDjBet();
                rcsDjBet.setBetNo(selection.getBetNo());
                rcsDjBet.setBetTime(djBetReqVo.getBetTime());
                rcsDjBet.setCreateTime(System.currentTimeMillis());
                rcsDjBet.setMarketId(selection.getMarketId().toString());
                rcsDjBet.setMatchId(selection.getMatchId());
                rcsDjBet.setMatchInfo(selection.getMatchInfo());
                rcsDjBet.setOddsValue(selection.getOdds().toString());
                rcsDjBet.setOrderStatus(0);
                rcsDjBet.setOrderNo(djBetReqVo.getOrderNo());
                rcsDjBet.setPlayOptionsId(selection.getOddsId().toString());
                rcsDjBet.setPlayOptionsName(selection.getPlayOptionsName());
                rcsDjBet.setSeriesType(djBetReqVo.getSeriesType());
                rcsDjBet.setUid(djBetReqVo.getUserId());
                rcsDjBet.setSportId(selection.getSportId().longValue());
                rcsDjBet.setSportName(selection.getSportName());
                rcsDjBets.add(rcsDjBet);
            }
        }
        rcsDjBets = rcsDjBets.stream().filter(distinctByKey1(i -> i.getBetNo())).collect(Collectors.toList());
        iRcsDjBetService.saveBatch(rcsDjBets);
    }

    static <T> Predicate<T> distinctByKey1(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
