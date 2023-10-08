package com.panda.sport.rcs.mgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.log.LogContext;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessMatchPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessMatchPaidConfig;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.MatchPaidVo;
import com.panda.sport.rcs.vo.TournamentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKeys.PAID_CONFIG_REDIS_CACHE;
import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_MATCH_CONFIG_KEY;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-10-07 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "businessMatchPaid")
public class RcsBusinessMacthPaidConfigController {

    @Autowired
    private RcsBusinessMatchPaidConfigService rcsBusinessMatchPaidConfigService;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;

    @Autowired
    private RcsCodeService rcsCodeService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @RequestMapping(value = "getList")
    public HttpResponse<List<MatchPaidVo>> getRcsBusinessPlayPaidConfigList(RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig) {
        List<MatchPaidVo> matchPaidVos = rcsBusinessMatchPaidConfigService.getMatchPaidListView();
        matchPaidVos.stream().forEach(model -> {
            RcsCode rcsCode = rcsCodeService.getBusiness(model.getBusinessId());
            if (rcsCode != null) {
                model.setBusinessName(rcsCode.getChildKey());
            }
        });

        return HttpResponse.success(matchPaidVos);
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public HttpResponse update(@RequestBody RcsBusinessMatchPaidConfig rcsBusinessMatchPaidConfig) {
        try {
            rcsBusinessMatchPaidConfigService.updateRcsBusinessMatchPaidConfig(rcsBusinessMatchPaidConfig);
        } catch (Exception e) {
            log.error("::businessMatchPaid{}:: 修改失败 {}",rcsBusinessMatchPaidConfig.getId(),e.getMessage());
            return HttpResponse.fail("修改失败");
        }
        if (redisClient.exist(PAID_CONFIG_REDIS_CACHE + "BusMatch")) {
            redisClient.delete(PAID_CONFIG_REDIS_CACHE + "BusMatch");
        }
        sendMessage.sendMessage(msgConfTag + "," + BUS_MATCH_CONFIG_KEY + "," + rcsBusinessMatchPaidConfig.getBusinessId(), rcsBusinessMatchPaidConfig);
        return HttpResponse.success();
    }

    @RequestMapping(value = "getMatchPaids")
    public HttpResponse<RcsBusinessMatchPaidConfig> getMatchPaids(@RequestParam("businessId") long businessId, @RequestParam("sportId") long sportId) {
        Long matchPaid = rcsCodeService.getRcsCodeList("amountSet", "matchPaid" + sportId);
        if (matchPaid == null) {
            return HttpResponse.fail("赛事基数未设置基数");
        }
        TournamentVo tournamentVo = new TournamentVo();
        tournamentVo.setSportId(sportId);
        tournamentVo.setTournamentLevel("tournamentLevel");
        List<TournamentVo> tournamentVos = rcsBusinessMatchPaidConfigService.selectTournaments(tournamentVo);
        if (tournamentVos.size() > 0) {
            tournamentVos.stream().forEach(model -> {
                RcsBusinessMatchPaidConfig rcsBusinessMatchPaidConfig = rcsBusinessMatchPaidConfigService.getRusinessMatchPaidByTournamentLevelId(model.getId(), businessId);
                if (rcsBusinessMatchPaidConfig == null) {
                    rcsBusinessMatchPaidConfig = new RcsBusinessMatchPaidConfig();
                    rcsBusinessMatchPaidConfig.setBusinessId(businessId);
                    rcsBusinessMatchPaidConfig.setSportId(sportId);
                    rcsBusinessMatchPaidConfig.setTournamentLevel(Integer.parseInt(model.getValue()));
                    rcsBusinessMatchPaidConfig.setTournamentLevelCode(model.getCode());
                    rcsBusinessMatchPaidConfig.setTournamentLevelId(model.getId());
                    rcsBusinessMatchPaidConfig.setStatus(1);
                    Integer matchMaxPayRate = getPercent(Integer.parseInt(model.getValue()));
                    rcsBusinessMatchPaidConfig.setMatchMaxPayRate(NumberUtils.getBigDecimal(matchMaxPayRate));
                    rcsBusinessMatchPaidConfig.setMatchMaxPayVal(NumberUtils.getBigDecimal(matchPaid).multiply(NumberUtils.getBigDecimal(matchMaxPayRate).divide(new BigDecimal(100))));
                    rcsBusinessMatchPaidConfig.setMatchMaxConPayRate(NumberUtils.getBigDecimal(matchMaxPayRate));
                    rcsBusinessMatchPaidConfigService.save(rcsBusinessMatchPaidConfig);
                }
            });
        }
        return HttpResponse.success(rcsBusinessMatchPaidConfigService.getMatchPaids(businessId, sportId));
    }

    @RequestMapping(value = "updateMatchPaids", method = RequestMethod.POST)
    public HttpResponse updateMatchPaids(@RequestBody List<RcsBusinessMatchPaidConfig> rcsBusinessMatchPaidConfigs) {
        try {
            JSONObject jsonObj = JSONObject.parseObject(LogContext.getContext().getParamsMap());
            int type = 1;
            if (jsonObj.containsKey("type") && "2".equals(String.valueOf(jsonObj.getJSONArray("type").get(0)))) type = 2;

            final int finalType = type;

            rcsBusinessMatchPaidConfigs.stream().forEach(model -> {
                RcsBusinessMatchPaidConfig config = rcsBusinessMatchPaidConfigService.getById(model.getId());
                Long matchPaid = rcsCodeService.getRcsCodeList("amountSet", "matchPaid" + config.getSportId());
                if (config != null) {
                    if (finalType == 1) {
                        if (model.getMatchMaxPayRate().compareTo(BigDecimal.ZERO) > 0) {
                            model.setMatchMaxPayVal(NumberUtils.getBigDecimal(model.getMatchMaxPayRate()).divide(new BigDecimal(100)).multiply(NumberUtils.getBigDecimal(matchPaid)).setScale(2));
                        }
                    } else if (finalType == 2) {
                        if (model.getMatchMaxPayRate().compareTo(BigDecimal.ZERO) > 0) {
                            model.setMatchMaxConPayRate(model.getMatchMaxPayRate());
                            model.setMatchMaxConPayVal(NumberUtils.getBigDecimal(model.getMatchMaxPayRate()).divide(new BigDecimal(100)).multiply(NumberUtils.getBigDecimal(matchPaid)).setScale(2));
                            model.setMatchMaxPayRate(null);
                        }
                    }
                }
            });
            rcsBusinessMatchPaidConfigService.updateBatchById(rcsBusinessMatchPaidConfigs);

            List<RcsBusinessMatchPaidConfig> matchList = rcsBusinessConfigMapper.queryBusMatchConifgList();

            Long busId = -1L;
            for (RcsBusinessMatchPaidConfig config : matchList) {
                if (config.getId().equals(rcsBusinessMatchPaidConfigs.get(0).getId())) {
                    busId = config.getBusinessId();
                    break;
                }
            }

            Map<Long, List<RcsBusinessMatchPaidConfig>> result = matchList.stream().collect(Collectors.groupingBy(RcsBusinessMatchPaidConfig::getBusinessId));

            if (result != null && result.size() > 0) {
                for (Long busid : result.keySet()) {
                    if (busId.equals(busid)) {
                        sendMessage.sendMessage(msgConfTag, BUS_MATCH_CONFIG_KEY, String.valueOf(busId), result.get(busId));
                        break;
                    }
                }
            }
            // rcsBusinessMatchPaidConfigService.updateRcsBusinessMatchPaids(rcsBusinessMatchPaidConfigs);
        } catch (Exception e) {
            log.error("::updateMatchPaids:: 修改失败{}", e.getMessage());
            return HttpResponse.fail("修改失败");
        }
        if (redisClient.exist(PAID_CONFIG_REDIS_CACHE + "BusMatch")) {
            redisClient.delete(PAID_CONFIG_REDIS_CACHE + "BusMatch");
        }
        return HttpResponse.success();
    }

    public Integer getPercent(Integer level) {
        if (level == 1 || level.equals(1)) {
            return 100;
        }
        if (level == 2 || level.equals(2)) {
            return 80;
        }
        if (level == 3 || level.equals(3)) {
            return 50;
        }
        if (level == 4 || level.equals(4)) {
            return 40;
        }
        if (level == 5 || level.equals(5)) {
            return 30;
        }
        if (level == 6 || level.equals(6)) {
            return 20;
        }
        return 10;
    }
}
