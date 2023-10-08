package com.panda.sport.rcs.task.job.match;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.panda.merge.dto.Request;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.RcsMarketChampionExtMapper;
import com.panda.sport.rcs.mapper.RcsTradeConfigMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsTradeConfig;
import com.panda.sport.rcs.pojo.dao.RcsMarketChampionExtVO;
import com.panda.sport.rcs.utils.i18n.CommonUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 扫描冠军赛事，处理到了封盘时间的盘口
 *
 * @author carver
 * @ClassName: ChampionMatchSealJobHandler
 * @Description: TODO
 * @date 2021年7月17日 下午2:06:44
 */
@JobHandler(value = "championMatchSealJobHandler")
@Component
@Slf4j
public class ChampionMatchSealJobHandler extends IJobHandler {
    @Autowired
    private RcsMarketChampionExtMapper rcsMarketChampionExtMapper;
    @Autowired
    private RcsTradeConfigMapper rcsTradeConfigMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        String linkId = CommonUtils.getLinkId("championMatchSeal");
        try {
            log.info("::{}::扫描冠军赛事获取到达封盘时间的盘口-开始时间:{}", linkId, DateUtils.getCurrentTime());
            List<RcsMarketChampionExtVO> list = rcsMarketChampionExtMapper.queryChampionMatchBySealNew().stream().peek(e -> {
                RcsTradeConfig config = Optional.ofNullable(rcsTradeConfigMapper.selectOne(Wrappers.lambdaQuery(RcsTradeConfig.class)
                        .eq(RcsTradeConfig::getMatchId, String.valueOf(e.getStandardMatchInfoId()))
                        .eq(RcsTradeConfig::getTargerData, e.getMarketId())
                        .orderByDesc(RcsTradeConfig::getId).last("LIMIT 1"))).orElse(new RcsTradeConfig());

                e.setDataSource(config.getDataSource());
                e.setStatus(config.getStatus());
            }).filter(f -> (f.getDataSource() != null && f.getDataSource().equals(1))
                    && (f.getStatus() == null || f.getStatus().equals(0))).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(list)) {
                Request<List<RcsMarketChampionExtVO>> request = new Request<>();
                request.setData(list);
                request.setLinkId(linkId);
                request.setDataSourceTime(System.currentTimeMillis());
                log.info("::{}::扫描冠军赛事获取到达封盘时间的盘口:{}", linkId, JsonFormatUtils.toJson(list));
                producerSendMessageUtils.sendMessage("RCS_TRADE_CHAMPION_MATCH_SEAL", null, request.getLinkId(), request);
            }
            log.info("::{}::扫描冠军赛事获取到达封盘时间的盘口-结束时间:{}", linkId, DateUtils.getCurrentTime());
        } catch (Exception e) {
            log.error("::{}::扫描冠军赛事获取到达封盘时间的盘口异常:", linkId, e);
        }
        return ReturnT.SUCCESS;
    }

}
