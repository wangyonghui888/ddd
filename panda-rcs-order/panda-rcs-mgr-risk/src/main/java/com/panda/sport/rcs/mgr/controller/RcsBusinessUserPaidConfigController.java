package com.panda.sport.rcs.mgr.controller;

import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsBusinessConfigMapper;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessUserPaidConfigService;
import com.panda.sport.rcs.mgr.wrapper.RcsCodeService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.UserPaidVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.constants.RedisKeys.PAID_CONFIG_REDIS_CACHE;
import static com.panda.sport.rcs.mgr.mq.impl.BasicConfigProvider.msgConfTag;
import static com.panda.sport.rcs.mgr.wrapper.impl.RcsPaidConfigServiceImp.BUS_USER_CONFIG_KEY;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  用户单日额度管理
 * @Date: 2019-10-07 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping(value = "businessUserPaid")
public class RcsBusinessUserPaidConfigController {

    @Autowired
    private RcsBusinessUserPaidConfigService rcsBusinessUserPaidConfigService;
    @Autowired
    private RcsBusinessConfigMapper rcsBusinessConfigMapper;

    @Autowired
    private RcsCodeService rcsCodeService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    ProducerSendMessageUtils sendMessage;

    @RequestMapping(value = "getList")
    public HttpResponse<List<UserPaidVo>> getList(@RequestParam("businessId") long businessId) {
        List<UserPaidVo> userPaidListView = rcsBusinessUserPaidConfigService.getUserPaidListView(businessId);
        Long userPaid = rcsCodeService.getRcsCodeList("amountSet", "userPaid");
        Long userDayPaid = rcsCodeService.getRcsCodeList("amountSet", "userDayPaid");
        if (userPaidListView.size() == 0) {
            RcsBusinessUserPaidConfig rcsBusinessUserPaidConfig = new RcsBusinessUserPaidConfig();
            rcsBusinessUserPaidConfig.setBusinessId(businessId);
            rcsBusinessUserPaidConfig.setUserId(-1L);
            rcsBusinessUserPaidConfig.setUserMatchPayRate(100);
            rcsBusinessUserPaidConfig.setUserDayPayRate(100);
            rcsBusinessUserPaidConfig.setUserMatchPayVal(NumberUtils.getBigDecimal(userPaid));
            rcsBusinessUserPaidConfig.setUserDayPayVal(NumberUtils.getBigDecimal(userDayPaid));
            rcsBusinessUserPaidConfig.setStatus(1);
            rcsBusinessUserPaidConfigService.save(rcsBusinessUserPaidConfig);
        }
        return HttpResponse.success(rcsBusinessUserPaidConfigService.getUserPaidListView(businessId));
    }

    @RequestMapping(value = "getUserPaid")
    public HttpResponse<RcsBusinessUserPaidConfig> getUserPaid(@RequestParam("id") long id) {
        return HttpResponse.success(rcsBusinessUserPaidConfigService.getById(id));
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public HttpResponse update(@RequestBody RcsBusinessUserPaidConfig rcsBusinessUserPaidConfig) {
        try {
            Long userPaid = rcsCodeService.getRcsCodeList("amountSet", "userPaid");
            Long userDayPaid = rcsCodeService.getRcsCodeList("amountSet", "userDayPaid");
            if (rcsBusinessUserPaidConfig.getUserDayPayRate() > 0) {
                rcsBusinessUserPaidConfig.setUserDayPayVal(new BigDecimal(rcsBusinessUserPaidConfig.getUserDayPayRate()).divide(new BigDecimal(10000)).multiply(NumberUtils.getBigDecimal(userDayPaid)).setScale(2));
            }
            if (rcsBusinessUserPaidConfig.getUserMatchPayRate() > 0) {
                rcsBusinessUserPaidConfig.setUserMatchPayVal(new BigDecimal(rcsBusinessUserPaidConfig.getUserMatchPayRate()).divide(new BigDecimal(10000)).multiply(NumberUtils.getBigDecimal(userPaid)).setScale(2));
            }
            rcsBusinessUserPaidConfigService.updateRcsBusinessUserPaidConfig(rcsBusinessUserPaidConfig);

            List<RcsBusinessUserPaidConfig> userList = rcsBusinessConfigMapper.queryBusUserConifgList();
            Map<Long, List<RcsBusinessUserPaidConfig>> result = userList.stream().filter(m -> m.getBusinessId().equals(rcsBusinessUserPaidConfig.getBusinessId())).collect(Collectors.groupingBy(RcsBusinessUserPaidConfig::getBusinessId));
            if (result != null && result.size() > 0) {
                for (Long busId : result.keySet()) {
                    sendMessage.sendMessage(msgConfTag, BUS_USER_CONFIG_KEY, String.valueOf(busId), result.get(busId));
                }
            }
        } catch (Exception e) {
            log.error("::businessUserPaid{}:: 修改失败{}", rcsBusinessUserPaidConfig.getId(),e.getMessage(),e);
            return HttpResponse.fail("修改失败");
        }
        if (redisClient.exist(PAID_CONFIG_REDIS_CACHE + "BusUser")) {
            redisClient.delete(PAID_CONFIG_REDIS_CACHE + "BusUser");
        }
        return HttpResponse.success("保存成功");
    }
}
