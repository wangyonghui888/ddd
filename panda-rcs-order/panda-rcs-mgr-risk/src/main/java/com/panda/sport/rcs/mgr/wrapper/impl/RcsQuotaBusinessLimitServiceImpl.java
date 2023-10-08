package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.*;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.enums.BusinessDayPaidStatusEnum;
import com.panda.sport.rcs.mgr.enums.BusinessLimitLogTypeEnum;
import com.panda.sport.rcs.mgr.utils.StringUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mgr.wrapper.RcsQuotaBusinessLimitService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.*;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitLogReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitVo;
import com.panda.sport.rcs.utils.TradeUserUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sports.bss.spi.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-09-04 15:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service("mgrQuotaBusinessLimitServiceImpl")
@Slf4j
public class RcsQuotaBusinessLimitServiceImpl extends ServiceImpl<RcsQuotaBusinessLimitMapper, RcsQuotaBusinessLimit> implements RcsQuotaBusinessLimitService {
    @Autowired
    private RcsQuotaBusinessLimitMapper rcsQuotaBusinessLimitMapper;
    @Autowired
    private RcsCodeService rcsCodeService;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private RcsOperateMerchantsSetMapper operateMerchantsSetService;
    @Autowired
    RcsOperateMerchantsSetMapper merchantsSetMapper;
    @Autowired
    TUserMapper userMapper;
    @Autowired
    RcsQuotaBusinessLimitLogMapper rcsQuotaBusinessLimitLogMapper;
    @Autowired
    RcsSysUserMapper rcsSysUserMapper;

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    private final String logTitle = "商户风控管理";
    private final String logCode = "10010";

    @Override
    public IPage<RcsQuotaBusinessLimit> listPage(Integer current, Integer size) {
        IPage<RcsQuotaBusinessLimit> iPage = new Page(current, size);
        return rcsQuotaBusinessLimitMapper.listPage(iPage);
    }

    @Transactional
    @Override
    public HttpResponse<RcsQuotaBusinessLimitVo> getList(Integer current, Integer size) {
        IPage<RcsQuotaBusinessLimit> iPage = listPage(current, size);
        List<RcsQuotaBusinessLimit> list = iPage.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            //设置缓存开关
            list.forEach(rcsQuotaBusinessLimit -> {
                //不存在才设置，更新时已经设置
                if (!redisClient.hexists(Constants.BUSINESS_TODAY_SWITCH_KEY, rcsQuotaBusinessLimit.getBusinessId())) {
                    redisClient.hSet(Constants.BUSINESS_TODAY_SWITCH_KEY, rcsQuotaBusinessLimit.getBusinessId(), String.valueOf(rcsQuotaBusinessLimit.getBusinessSingleDayLimitSwitch()));
                }
            });

            list = initRcsQuotaBusinessLimit();
            iPage = listPage(current, size);
            if (CollectionUtils.isEmpty(list)) {
                log.error("RcsQuotaBusinessLimit数据初始化失败");
                return HttpResponse.error(-1, "数据初始化失败");
            }
        }
        return handleResults(iPage);
    }

    @Override
    public HttpResponse<RcsQuotaBusinessLimitVo> limitConfigList(RcsQuotaBusinessLimitReqVo reqVo) {
        reqVo.setTagMarketLevelId(tagMarketLevelNameByOption(reqVo.getTagMarketLevelId()));
        reqVo.setTagMarketLevelIdPc(tagMarketLevelNameByOption(reqVo.getTagMarketLevelIdPc()));
        IPage<RcsQuotaBusinessLimit> requestPage = new Page<>(reqVo.getCurrent(), reqVo.getSize());
        //查询前,校验赛种列表是否存在，先将数据过滤过来
        reqVo.setCheckSelect(0);
        List<Long> businessLimitIds = new ArrayList<>();
        if (!Objects.isNull(reqVo.getSportIdList()) && reqVo.getSportIdList().size() > 0){
             for (Integer sportId:  reqVo.getSportIdList()) {
                List<Long>  bizIds=  rcsQuotaBusinessLimitMapper.getSportIdsList(sportId);
                log.info("查询出所有业务ids:"+ JSON.toJSONString(bizIds));
                businessLimitIds.addAll(bizIds);
            }
             if (Objects.isNull(businessLimitIds) || businessLimitIds.size() <= 0){
                 reqVo.setCheckSelect(0);
             }else{
                 reqVo.setCheckSelect(1);
             }
        }

        IPage<RcsQuotaBusinessLimit> iPage = rcsQuotaBusinessLimitMapper.limitConfigList(requestPage, reqVo);
        List<RcsQuotaBusinessLimit> list = iPage.getRecords();
        if (CollectionUtils.isNotEmpty(list)) {
            for (RcsQuotaBusinessLimit rcsQuotaBusinessLimit : list) {
                String businessId = rcsQuotaBusinessLimit.getBusinessId().trim();
                BigDecimal businessUsedLimit = getBusinessUsedLimit(System.currentTimeMillis(), businessId);
                rcsQuotaBusinessLimit.setStatusOfTheDay(BusinessDayPaidStatusEnum.NORMAL.getStatus());
                rcsQuotaBusinessLimit.setActualProfit(businessUsedLimit);
                BigDecimal businessSingleDayLimit = new BigDecimal(rcsQuotaBusinessLimit.getBusinessSingleDayLimit());
                for (BusinessDayPaidStatusEnum businessDayPaidStatusEnum : BusinessDayPaidStatusEnum.values()) {
                    BigDecimal bigDecimal = new BigDecimal(businessDayPaidStatusEnum.getMin());
                    if (businessUsedLimit.compareTo(businessSingleDayLimit.multiply(bigDecimal)) >= 0) {
                        rcsQuotaBusinessLimit.setStatusOfTheDay(businessDayPaidStatusEnum.getStatus());
                    }
                }
                rcsQuotaBusinessLimit.setBusinessUsedLimit(businessUsedLimit);
                if (NumberUtils.INTEGER_TWO.equals(rcsQuotaBusinessLimit.getLimitType())) {
                    rcsQuotaBusinessLimit.setHasSubFlag(true);
                }
                if (!org.springframework.util.StringUtils.isEmpty(rcsQuotaBusinessLimit.getSportIds())) {
                    List<Integer> sportIds = new ArrayList<>();
                    org.apache.commons.collections.CollectionUtils.collect(Arrays.asList(rcsQuotaBusinessLimit.getSportIds().split(",")), new Transformer() {
                        @Override
                        public Object transform(Object input) {
                            return Integer.valueOf(input.toString());
                        }
                    }, sportIds);
                    rcsQuotaBusinessLimit.setSportIdList(sportIds);
                }
            }
        }
        RcsQuotaBusinessLimitVo rcsQuotaBusinessLimitVo = new RcsQuotaBusinessLimitVo();
        rcsQuotaBusinessLimitVo.setList(list);
        rcsQuotaBusinessLimitVo.setPages(iPage.getPages());
        rcsQuotaBusinessLimitVo.setCurrent((int) iPage.getCurrent());
        rcsQuotaBusinessLimitVo.setSize((int) iPage.getSize());
        rcsQuotaBusinessLimitVo.setTotal((int) iPage.getTotal());
        return HttpResponse.success(rcsQuotaBusinessLimitVo);
    }

    @Override
    public HttpResponse<RcsQuotaBusinessLimitVo> limitConfigLogList(RcsQuotaBusinessLimitLogReqVo reqVo) {
        if (StringUtils.isNotBlank(reqVo.getOperateType())) {
            reqVo.setOperateType(BusinessLimitLogTypeEnum.getValue(Integer.valueOf(reqVo.getOperateType())));
        }
        IPage<RcsQuotaBusinessLimitLog> requestPage = new Page<>(reqVo.getCurrent(), reqVo.getSize());
        IPage<RcsQuotaBusinessLimitLog> iPage = rcsQuotaBusinessLimitLogMapper.queryByPage(requestPage, reqVo);
        List<RcsQuotaBusinessLimitLog> list = iPage.getRecords();
        list.forEach(item -> {
            item.setOperateType(setBusinessTypeName(item.getOperateType()));
        });
        int total = (int) iPage.getTotal();
        int size = (int) iPage.getSize();
        int page = (int) iPage.getCurrent();
        PageVO<RcsQuotaBusinessLimitLog> pageVO = new PageVO<>(total, size, page);
        pageVO.setRecords(list);
        return HttpResponse.success(pageVO);
    }

    private String setBusinessTypeName(String value) {
        String levelName = "";
        switch (value) {
            case "10010":
                levelName = "商户管理";
                break;
            case "10020":
                levelName = "标签管理";
                break;
            case "10030":
                levelName = "投注特征标签风控措施";
                break;
            case "10040":
                levelName = "动态风控设置";
                break;
            case "10041":
                levelName = "用户提前结算动态抽水规则";
                break;
            case "10042":
                levelName = "动态赔率分组";
                break;
            case "10043":
                levelName = "全局开关";
                break;
            case "10044":
                levelName = "标签风控措施";
                break;
            case "10045":
                levelName = "赔率分组";
                break;
            case "10050":
                levelName = "危险联赛池管理";
                break;
            case "10060":
                levelName = "危险球队池管理";
                break;
            case "10080":
                levelName = "外部备注历史记录";
                break;
            default:
                levelName = value;
                break;
        }
        return levelName;
    }

    @Override
    public HttpResponse<RcsQuotaBusinessLimitVo> getSubList(Integer current, Integer size, Long merchantId, String[] agentIds, String[] agentNames) {
        IPage<RcsQuotaBusinessLimit> iPage = new Page(current, size);
        iPage = rcsQuotaBusinessLimitMapper.getSubList(iPage, merchantId, agentIds, agentNames);
        for (RcsQuotaBusinessLimit limit : iPage.getRecords()) {
            if (StringUtils.isBlank(limit.getBusinessName())) {
                limit.setBusinessName("" + limit.getBusinessId());
            }
            if (StringUtils.isNotBlank(limit.getSportIds())) {
                List<String> list = Arrays.asList(limit.getSportIds().split(","));
                limit.setSportIdList(list.stream().map(Integer::parseInt).collect(Collectors.toList()));
            }
        }
        return handleResults(iPage);
    }

    private HttpResponse<RcsQuotaBusinessLimitVo> handleResults(IPage<RcsQuotaBusinessLimit> iPage) {
        List<RcsQuotaBusinessLimit> list = iPage.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> merchantIds = list.stream().map(e -> e.getBusinessId().trim()).collect(toList());
        //获取代理商户限额配置集
        QueryWrapper<RcsOperateMerchantsSet> merchantsSetQueryWrapper = new QueryWrapper<>();
        merchantsSetQueryWrapper.lambda().in(RcsOperateMerchantsSet::getCreditParentId, merchantIds);
        List<RcsOperateMerchantsSet> creditMerchantsSets = operateMerchantsSetService.selectList(merchantsSetQueryWrapper);
        Map<Long, RcsOperateMerchantsSet> creditMerchantsMap = creditMerchantsSets.stream().collect(Collectors.toMap(e -> Long.valueOf(e.getCreditParentId()), value -> value, (e1, e2) -> e1));
        //获取商户限额配置集
        merchantsSetQueryWrapper = new QueryWrapper<>();
        merchantsSetQueryWrapper.lambda().in(RcsOperateMerchantsSet::getMerchantsId, merchantIds);
        List<RcsOperateMerchantsSet> merchantsSets = operateMerchantsSetService.selectList(merchantsSetQueryWrapper);
        Map<String, RcsOperateMerchantsSet> merchantsMap = merchantsSets.stream().collect(Collectors.toMap(e -> e.getMerchantsId().trim(), value -> value, (e1, e2) -> e1));
        for (RcsQuotaBusinessLimit rcsQuotaBusinessLimit : list) {
            String cuurBusinessId = rcsQuotaBusinessLimit.getBusinessId().trim();
            BigDecimal businessUsedLimit = getBusinessUsedLimit(System.currentTimeMillis(), cuurBusinessId);
            rcsQuotaBusinessLimit.setStatusOfTheDay(BusinessDayPaidStatusEnum.NORMAL.getStatus());
            rcsQuotaBusinessLimit.setActualProfit(businessUsedLimit);
            BigDecimal businessSingleDayLimit = new BigDecimal(rcsQuotaBusinessLimit.getBusinessSingleDayLimit());
            for (BusinessDayPaidStatusEnum businessDayPaidStatusEnum : BusinessDayPaidStatusEnum.values()) {
                BigDecimal bigDecimal = new BigDecimal(businessDayPaidStatusEnum.getMin());
                if (businessUsedLimit.compareTo(businessSingleDayLimit.multiply(bigDecimal)) >= 0) {
                    rcsQuotaBusinessLimit.setStatusOfTheDay(businessDayPaidStatusEnum.getStatus());
                }
            }
            RcsOperateMerchantsSet tempMerchantSet = creditMerchantsMap.get(Long.valueOf(cuurBusinessId));
            if (tempMerchantSet != null) {
                rcsQuotaBusinessLimit.setHasSubFlag(true);
            }
            tempMerchantSet = merchantsMap.get(cuurBusinessId);
            if (tempMerchantSet != null) {
                rcsQuotaBusinessLimit.setLimitType(tempMerchantSet.getLimitType());
            }
            rcsQuotaBusinessLimit.setBusinessUsedLimit(businessUsedLimit);
        }
        RcsQuotaBusinessLimitVo rcsQuotaBusinessLimitVo = new RcsQuotaBusinessLimitVo();
        rcsQuotaBusinessLimitVo.setList(list);
        rcsQuotaBusinessLimitVo.setPages(iPage.getPages());
        rcsQuotaBusinessLimitVo.setCurrent((int) iPage.getCurrent());
        rcsQuotaBusinessLimitVo.setSize((int) iPage.getSize());
        rcsQuotaBusinessLimitVo.setTotal((int) iPage.getTotal());
        return HttpResponse.success(rcsQuotaBusinessLimitVo);
    }

    public BigDecimal getBusinessUsedLimit(Long time, String businessId) {
        if (time == null) {
            time = System.currentTimeMillis();
        }
        String dateExpect = DateUtils.getDateExpect(time);
        String key = String.format(RedisKeys.PAID_DATE_BUS_REDIS_CACHE, dateExpect, businessId);
        String value = redisClient.get(key);
        return StringUtil.toBigDecimal(value).divide(Constants.BASE, 2, RoundingMode.HALF_UP);
    }

    /**
     * @return java.lang.Boolean
     * @Description 初始化限额管理数据
     * @Param []
     * @Author kimi
     * @Date 2020/9/6
     **/
    public List<RcsQuotaBusinessLimit> initRcsQuotaBusinessLimit() {
        List<RcsCode> businessList = rcsCodeService.getBusinessList();
        List<RcsQuotaBusinessLimit> rcsQuotaBusinessLimitList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(businessList)) {
            for (RcsCode rcsCode : businessList) {
                RcsQuotaBusinessLimit rcsQuotaBusinessLimit = new RcsQuotaBusinessLimit();
                rcsQuotaBusinessLimit.setBusinessId(rcsCode.getValue());
                rcsQuotaBusinessLimit.setBusinessName(rcsCode.getChildKey());
                rcsQuotaBusinessLimit.setBusinessSingleDayLimitProportion(Constants.DAY_COMPENSATION_PROPORTION);
                rcsQuotaBusinessLimit.setBusinessSingleDayLimit(Constants.BUSINESS_SINGLE_DAY_LIMIT.multiply(rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion()).longValue());
                // 串关限额
                rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimitProportion(new BigDecimal("0.5"));
                rcsQuotaBusinessLimit.setBusinessSingleDaySeriesLimit(Constants.BUSINESS_SINGLE_DAY_LIMIT.multiply(rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion()).longValue());
                rcsQuotaBusinessLimit.setBusinessSingleDayGameProportion(Constants.DAY_COMPENSATION_PROPORTION);
                rcsQuotaBusinessLimit.setUserQuotaRatio(Constants.DAY_COMPENSATION_PROPORTION);
                rcsQuotaBusinessLimit.setStatus(1);
                //不确定是否要加 冠军限额比例
                //rcsQuotaBusinessLimit.setChampionBusinessProportion();
                //rcsQuotaBusinessLimit.setChampionUserProportion();
                rcsQuotaBusinessLimitList.add(rcsQuotaBusinessLimit);
            }
            saveBatch(rcsQuotaBusinessLimitList);
        }
        return rcsQuotaBusinessLimitList;
    }

    @Override
    public HttpResponse<RcsQuotaBusinessLimit> getgetBusinessLRiskStatusList(Long userId) {
        try {
            LambdaQueryWrapper<TUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(TUser::getUid, userId);
            TUser user = userMapper.selectOne(userLambdaQueryWrapper);

            LambdaQueryWrapper<RcsOperateMerchantsSet> merchantsSetLambdaQueryWrapper = new LambdaQueryWrapper<>();
            merchantsSetLambdaQueryWrapper.eq(RcsOperateMerchantsSet::getMerchantsCode, user.getMerchantCode());
            RcsOperateMerchantsSet rcsOperateMerchantsSet = merchantsSetMapper.selectOne(merchantsSetLambdaQueryWrapper);

            LambdaQueryWrapper<RcsQuotaBusinessLimit> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RcsQuotaBusinessLimit::getBusinessId, rcsOperateMerchantsSet.getMerchantsId());
            RcsQuotaBusinessLimit data = rcsQuotaBusinessLimitMapper.selectOne(wrapper);
            return HttpResponse.success(data);
        } catch (Exception e) {
            log.error("::{}::获取商户风控状态失败：{}",userId,e.getMessage(), e);
            return HttpResponse.error(-1, "服务器错误");
        }
    }

    /**
     * 增加日志，如果前端传过来的数据和数据库不一致，就记录一条日志
     *
     * @param rcsQuotaBusinessLimit 前端传过来数据
     */
    @Override
    public void addRcsQuotaBusinessLimitLog(RcsQuotaBusinessLimit rcsQuotaBusinessLimit, RcsQuotaBusinessLimit rcsQuotaBusinessLimitNew) {
        //商户单日限额开关
        List<RcsQuotaBusinessLimitLog> list = new ArrayList<>();

        if (rcsQuotaBusinessLimit.getBusinessSingleDayLimitSwitch() != rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitSwitch()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitSwitch() == null ? "关" : rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitSwitch() == 0 ? "关" : "开";
            String afterVal = rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitSwitch() == null ? "关" : rcsQuotaBusinessLimit.getBusinessSingleDayLimitSwitch() == 0 ? "关" : "开";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "商户单日亏损开关", beforeVal, afterVal));
        }
        //商户单日亏损限额
        if (rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion() != null && rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitProportion() != null && rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion().compareTo(rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitProportion()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "商户单日亏损限额",
                    rcsQuotaBusinessLimitNew.getBusinessSingleDayLimitProportion().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getBusinessSingleDayLimitProportion().multiply(Constants.BASE).intValue() + ""));
        }
        //商户单日串关亏损
        if (rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion() != null && rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimitProportion() != null && rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion().compareTo(rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimitProportion()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "商户单日串关亏损",
                    rcsQuotaBusinessLimitNew.getBusinessSingleDaySeriesLimitProportion().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getBusinessSingleDaySeriesLimitProportion().multiply(Constants.BASE).intValue() + ""));
        }
        //商户单场赔付限额
        if (rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion() != null && rcsQuotaBusinessLimitNew.getBusinessSingleDayGameProportion() != null && rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion().compareTo(rcsQuotaBusinessLimitNew.getBusinessSingleDayGameProportion()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "商户单场赔付限额",
                    rcsQuotaBusinessLimitNew.getBusinessSingleDayGameProportion().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getBusinessSingleDayGameProportion().multiply(Constants.BASE).intValue() + ""));
        }
        //用户单关累计限额
        if (rcsQuotaBusinessLimit.getUserQuotaRatio() != null && rcsQuotaBusinessLimitNew.getUserQuotaRatio() != null && rcsQuotaBusinessLimit.getUserQuotaRatio().compareTo(rcsQuotaBusinessLimitNew.getUserQuotaRatio()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "用户单关累计限额",
                    rcsQuotaBusinessLimitNew.getUserQuotaRatio().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getUserQuotaRatio().multiply(Constants.BASE).intValue() + ""));
        }
        //用户单关单注限额
        if (rcsQuotaBusinessLimit.getUserQuotaBetRatio() != null && rcsQuotaBusinessLimitNew.getUserQuotaBetRatio() != null && rcsQuotaBusinessLimit.getUserQuotaBetRatio().compareTo(rcsQuotaBusinessLimitNew.getUserQuotaBetRatio()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "用户单关单注限额",
                    rcsQuotaBusinessLimitNew.getUserQuotaBetRatio().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getUserQuotaBetRatio().multiply(Constants.BASE).intValue() + ""));
        }
        //用户串关累计限额
        if (rcsQuotaBusinessLimit.getUserStrayQuotaRatio() != null && rcsQuotaBusinessLimitNew.getUserStrayQuotaRatio() != null && rcsQuotaBusinessLimit.getUserStrayQuotaRatio().compareTo(rcsQuotaBusinessLimitNew.getUserStrayQuotaRatio()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "用户串关累计限额",
                    rcsQuotaBusinessLimitNew.getUserStrayQuotaRatio().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getUserStrayQuotaRatio().multiply(Constants.BASE).intValue() + ""));
        }
        if (Objects.nonNull(rcsQuotaBusinessLimit.getUserSingleStrayLimit()) && !rcsQuotaBusinessLimit.getUserSingleStrayLimit().equals(rcsQuotaBusinessLimitNew.getUserSingleStrayLimit())) {
            String beforeVal = rcsQuotaBusinessLimit.getUserSingleStrayLimit() == null ? "" : rcsQuotaBusinessLimit.getUserSingleStrayLimit() + "";
            String afterVal = rcsQuotaBusinessLimitNew.getUserSingleStrayLimit() == null ? "" : rcsQuotaBusinessLimitNew.getUserSingleStrayLimit() + "";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "用户串关单场限额", beforeVal, afterVal));
        }
        //冠军玩法商户限额
        if (rcsQuotaBusinessLimit.getChampionBusinessProportion() != null && rcsQuotaBusinessLimitNew.getChampionBusinessProportion() != null && rcsQuotaBusinessLimit.getChampionBusinessProportion().compareTo(rcsQuotaBusinessLimitNew.getChampionBusinessProportion()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "冠军玩法商户限额",
                    rcsQuotaBusinessLimitNew.getChampionBusinessProportion().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getChampionBusinessProportion().multiply(Constants.BASE).intValue() + ""));
        }
        //冠军玩法用户限额
        if (rcsQuotaBusinessLimit.getChampionUserProportion() != null && rcsQuotaBusinessLimitNew.getChampionUserProportion() != null && rcsQuotaBusinessLimit.getChampionUserProportion().compareTo(rcsQuotaBusinessLimitNew.getChampionUserProportion()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "冠军玩法用户限额",
                    rcsQuotaBusinessLimitNew.getChampionUserProportion().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getChampionUserProportion().multiply(Constants.BASE).intValue() + ""));
        }
        //赔率等级 pc
        if (rcsQuotaBusinessLimit.getTagMarketLevelIdPc() != null && !rcsQuotaBusinessLimit.getTagMarketLevelIdPc().equals(rcsQuotaBusinessLimitNew.getTagMarketLevelIdPc())) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "赔率等级pc",
                    tagMarketLevelNameByValue(rcsQuotaBusinessLimitNew.getTagMarketLevelIdPc()),
                    tagMarketLevelNameByValue(rcsQuotaBusinessLimit.getTagMarketLevelIdPc())));
        }
        //赔率等级 其他
        if (rcsQuotaBusinessLimit.getTagMarketLevelId() != null && !rcsQuotaBusinessLimit.getTagMarketLevelId().equals(rcsQuotaBusinessLimitNew.getTagMarketLevelId())) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "赔率等级其他",
                    tagMarketLevelNameByValue(rcsQuotaBusinessLimitNew.getTagMarketLevelId()),
                    tagMarketLevelNameByValue(rcsQuotaBusinessLimit.getTagMarketLevelId())));
        }
        //货量百分比
        if (rcsQuotaBusinessLimit.getBusinessBetPercent() != null && rcsQuotaBusinessLimitNew.getBusinessBetPercent() != null && rcsQuotaBusinessLimit.getBusinessBetPercent().compareTo(rcsQuotaBusinessLimitNew.getBusinessBetPercent()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "货量百分比",
                    rcsQuotaBusinessLimitNew.getBusinessBetPercent().multiply(Constants.BASE).intValue() + "",
                    rcsQuotaBusinessLimit.getBusinessBetPercent().multiply(Constants.BASE).intValue() + ""));
        }
        //电商货量百分比
        if (rcsQuotaBusinessLimit.getGamingBetPercent() != null && rcsQuotaBusinessLimitNew.getGamingBetPercent() != null && rcsQuotaBusinessLimit.getGamingBetPercent().compareTo(rcsQuotaBusinessLimitNew.getGamingBetPercent()) != 0) {
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "电竞货量百分比",
                    rcsQuotaBusinessLimitNew.getGamingBetPercent().intValue() + "",
                    rcsQuotaBusinessLimit.getGamingBetPercent().intValue() + ""));
        }
        //串关限额模式
        if (rcsQuotaBusinessLimit.getStraySwitchVal() != rcsQuotaBusinessLimitNew.getStraySwitchVal()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getStraySwitchVal() == null ? "旧限额模式" : rcsQuotaBusinessLimitNew.getStraySwitchVal() == 0 ? "旧限额模式" : "新限额模式";
            String afterVal = rcsQuotaBusinessLimitNew.getStraySwitchVal() == null ? "旧限额模式" : rcsQuotaBusinessLimit.getStraySwitchVal() == 0 ? "旧限额模式" : "新限额模式";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "串关限额模式", beforeVal, afterVal));
        }
        //商户自行风控开关
        if (rcsQuotaBusinessLimit.getRiskStatus() != rcsQuotaBusinessLimitNew.getRiskStatus()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getRiskStatus() == null ? "关" : rcsQuotaBusinessLimitNew.getRiskStatus() == 0 ? "关" : "开";
            String afterVal = rcsQuotaBusinessLimitNew.getRiskStatus() == null ? "关" : rcsQuotaBusinessLimit.getRiskStatus() == 0 ? "关" : "开";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "商户自行风控开关", beforeVal, afterVal));
        }
        //赔率分组动态开关
        if (rcsQuotaBusinessLimit.getTagMarketLevelStatus() != rcsQuotaBusinessLimitNew.getTagMarketLevelStatus()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getTagMarketLevelStatus() == null ? "关" : rcsQuotaBusinessLimitNew.getTagMarketLevelStatus() == 0 ? "关" : "开";
            String afterVal = rcsQuotaBusinessLimitNew.getTagMarketLevelStatus() == null ? "关" : rcsQuotaBusinessLimit.getTagMarketLevelStatus() == 0 ? "关" : "开";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "赔率分组动态开关", beforeVal, afterVal));
        }
        //备注
        if (null != rcsQuotaBusinessLimit.getRemark() && !rcsQuotaBusinessLimit.getRemark().equals(rcsQuotaBusinessLimitNew.getRemark())) {
            String beforeVal = rcsQuotaBusinessLimitNew.getRemark();
            String afterVal = rcsQuotaBusinessLimit.getRemark();
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "备注", beforeVal, afterVal));
        }
        log.info("::{}::投注延时时间-原值{},新值{}",rcsQuotaBusinessLimitNew.getBusinessId(),rcsQuotaBusinessLimit.getDelay(),rcsQuotaBusinessLimitNew.getDelay());
        //投注延时
        if ((ObjectUtils.isEmpty(rcsQuotaBusinessLimitNew.getDelay()) && !ObjectUtils.isEmpty(rcsQuotaBusinessLimit.getDelay())) || rcsQuotaBusinessLimit.getDelay() != rcsQuotaBusinessLimitNew.getDelay()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getDelay() == null ? "" : rcsQuotaBusinessLimitNew.getDelay() + "";
            String afterVal = rcsQuotaBusinessLimit.getDelay() == null ? "" : rcsQuotaBusinessLimit.getDelay() + "";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "投注延时时间", beforeVal, afterVal));
        }
        //需求优化 bug-37387 开放 信用商户限额比例设置
        if (rcsQuotaBusinessLimitNew.getCreditBetRatio() != null && rcsQuotaBusinessLimit.getCreditBetRatio() != null && rcsQuotaBusinessLimitNew.getCreditBetRatio().compareTo(rcsQuotaBusinessLimit.getCreditBetRatio()) != 0) {
            String beforeVal = rcsQuotaBusinessLimitNew.getCreditBetRatio() == null ? "" : rcsQuotaBusinessLimitNew.getCreditBetRatio() + "";
            String afterVal = rcsQuotaBusinessLimit.getCreditBetRatio() == null ? "" : rcsQuotaBusinessLimit.getCreditBetRatio() + "";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "信用商户限额比例", beforeVal, afterVal));
        }
        //赛事种类
        if (rcsQuotaBusinessLimit.getSportIds() == null) {
            rcsQuotaBusinessLimit.setSportIds("");
        }
        if (rcsQuotaBusinessLimitNew.getSportIds() == null) {
            rcsQuotaBusinessLimitNew.setSportIds("");
        }
        if (!rcsQuotaBusinessLimit.getSportIds().equals(rcsQuotaBusinessLimitNew.getSportIds())) {
            String beforeVal = "";
            if (StringUtils.isNotBlank(rcsQuotaBusinessLimitNew.getSportIds())) {
                beforeVal = rcsQuotaBusinessLimitLogMapper.getSportNameByIds(rcsQuotaBusinessLimitNew.getSportIds());
            }
            String afterVal = "";
            if (StringUtils.isNotBlank(rcsQuotaBusinessLimit.getSportIds())) {
                afterVal = rcsQuotaBusinessLimitLogMapper.getSportNameByIds(rcsQuotaBusinessLimit.getSportIds());
            }
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "投注延时赛种", beforeVal, afterVal));
        }

        //商户自行风控开关
        if (Objects.nonNull(rcsQuotaBusinessLimit.getBetVolumeStatus()) && Objects.nonNull(rcsQuotaBusinessLimitNew.getBetVolumeStatus()) && rcsQuotaBusinessLimit.getBetVolumeStatus().intValue() != rcsQuotaBusinessLimitNew.getBetVolumeStatus().intValue()) {
            String beforeVal = rcsQuotaBusinessLimitNew.getBetVolumeStatus() == null ? "关" : rcsQuotaBusinessLimitNew.getBetVolumeStatus() == 0 ? "关" : "开";
            String afterVal = rcsQuotaBusinessLimitNew.getBetVolumeStatus() == null ? "关" : rcsQuotaBusinessLimit.getBetVolumeStatus() == 0 ? "关" : "开";
            list.add(setBusinessLimitLog(rcsQuotaBusinessLimit, "投注货量动态风控", beforeVal, afterVal));
        }
        if (list.size() > 0) {
            log.info("商户管理->商户管理管理处理条数{}", list.size());
            for(RcsQuotaBusinessLimitLog ipLog: list){
                ipLog.setIp(rcsQuotaBusinessLimit.getIp());
            }
            String jsonString = JSONArray.toJSONString(list);
            producerSendMessageUtils.sendMessage(Constants.RCS_BUSINESS_LOG_SAVE, null, logCode, jsonString);
//            try {
//                int row = rcsQuotaBusinessLimitLogMapper.bathInserts(list);
//                log.warn(String.format("::%s:: 批量处理结果:%s",rcsQuotaBusinessLimit.getBusinessId(),row));
//            }catch (Exception e){
//                log.error(String.format("::%s:: 批量处理失败 error:%s",rcsQuotaBusinessLimit.getBusinessId(),e.getMessage()));
//            }
        }
    }

    private String tagMarketLevelNameByValue(String levelId) {
        String levelName = "";
        if (StringUtils.isBlank(levelId)) {
            return "";
        }
        switch (levelId) {
            case "0":
                levelName = "0";
                break;
            case "1":
                levelName = "A";
                break;
            case "2":
                levelName = "B";
                break;
            case "3":
                levelName = "C";
                break;
            case "4":
                levelName = "D";
                break;
            case "11":
                levelName = "1";
                break;
            case "12":
                levelName = "2";
                break;
            case "13":
                levelName = "3";
                break;
            case "14":
                levelName = "4";
                break;
            case "15":
                levelName = "5";
                break;
        }
        return levelName;
    }

    private String tagMarketLevelNameByOption(String levelId) {
        String levelName = "";
        if (StringUtils.isBlank(levelId)) {
            return "";
        }
        levelId = levelId.toUpperCase();
        switch (levelId) {
            case "0":
                levelName = "0";
                break;
            case "A":
                levelName = "1";
                break;
            case "B":
                levelName = "2";
                break;
            case "C":
                levelName = "3";
                break;
            case "D":
                levelName = "4";
                break;
            case "1":
                levelName = "11";
                break;
            case "2":
                levelName = "12";
                break;
            case "3":
                levelName = "13";
                break;
            case "4":
                levelName = "14";
                break;
            case "5":
                levelName = "15";
                break;
            default:
                levelName = "-100";//给个负整数，不然隔离环境容易出现查询结果不正确的现象
        }
        return levelName;
    }

    private void insertBusinessLimitLog(RcsQuotaBusinessLimit rcsQuotaBusinessLimit, String paramName,
                                        String beforeVal, String afterVal) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory("商户风控管理");
        limitLoglog.setObjectId(rcsQuotaBusinessLimit.getId().toString());
        limitLoglog.setObjectName(rcsQuotaBusinessLimit.getBusinessName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType("商户通用设置");
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        RcsSysUser user = null;
        try {
            log.warn(String.format("::%s:: 当前用户ID", TradeUserUtils.getUserId()));
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error(String.format("::%s::当前用户不存在，错误:%s", rcsQuotaBusinessLimit.getBusinessId(), e.getMessage()), e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        rcsQuotaBusinessLimitLogMapper.insert(limitLoglog);
    }

    private RcsQuotaBusinessLimitLog setBusinessLimitLog(RcsQuotaBusinessLimit rcsQuotaBusinessLimit, String paramName,
                                                         String beforeVal, String afterVal) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(logTitle);
        limitLoglog.setObjectId(rcsQuotaBusinessLimit.getId().toString());
        limitLoglog.setObjectName(rcsQuotaBusinessLimit.getBusinessName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(logCode);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        RcsSysUser user;
        try {
            user = rcsSysUserMapper.selectById(TradeUserUtils.getUserId());
        } catch (Exception e) {
            log.error(String.format("::%s::当前用户不存在，错误:%s", rcsQuotaBusinessLimit.getBusinessId(), e.getMessage()), e);
            throw new RuntimeException("当前用户不存在");
        }
        limitLoglog.setUserId(user.getId().toString());
        limitLoglog.setUserName(user.getUserCode());
        return limitLoglog;
    }

    @Override
    public List<String> queryParentName() {
        List<String> list = new ArrayList<>();
        list.add("-");//直营商户parentName，放在第一位
        list.addAll(rcsQuotaBusinessLimitMapper.queryParentName());
        return list;
    }

}
