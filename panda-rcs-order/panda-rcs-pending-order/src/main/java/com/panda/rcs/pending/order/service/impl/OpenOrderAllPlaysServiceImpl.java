package com.panda.rcs.pending.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.panda.merge.dto.ConfigCashOutTradeItemDTO;
import com.panda.merge.dto.TradeMarketUiConfigDTO;
import com.panda.rcs.pending.order.constants.NumberConstant;
import com.panda.rcs.pending.order.enums.ManagerCodeEnum;
import com.panda.rcs.pending.order.param.TournamentTemplateParam;
import com.panda.rcs.pending.order.pojo.*;
import com.panda.rcs.pending.order.service.IOpenOrderAllPlaysService;
import com.panda.rcs.pending.order.service.IRcsTournamentTemplateService;
import com.panda.rcs.pending.order.tourTemplate.DataSourceCodeVo;
import com.panda.rcs.pending.order.utils.CommonUtil;
import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.rcs.core.utils.BeanCopyUtils;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplateEventMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateEvent;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.service.IRcsTournamentTemplatePlayMargainService;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 预约订单处理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpenOrderAllPlaysServiceImpl implements IOpenOrderAllPlaysService {

    @Autowired
    private ProducerSendMessageUtils sendMessage;

    //标准赛事
    @Autowired
    private final StandardMatchInfoMapper standardMatchInfoMapper;

    //联赛模板
    @Autowired
    private final IRcsTournamentTemplateService tournamentTemplateService;
    //玩法
    @Autowired
    private final IRcsTournamentTemplatePlayMargainService rcsTournamentTemplatePlayMargainService;

    @Override
    public HttpResponse<?> openOrderPreAllPlays() {
        //开启预约投注早盘所有玩法
        //1.查询标准赛事里面是早盘的赛事，2，找到模板，3，根据模板ID修改玩法里面状态，4封装数据同步给业务。、
        //1、查询早盘赛事，不包括滚球
        try {
            ///2.找到模板
            List<RcsTournamentTemplate> rcsTournamentTemplateList = tournamentTemplateService.getTournamentTemplateList();
            if (Objects.isNull(rcsTournamentTemplateList) || rcsTournamentTemplateList.size() == 0) {
                log.info("没有模板信息:{}", JSON.toJSONString(rcsTournamentTemplateList));
                return HttpResponse.error(202, "没有模板信息");
            }
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            //3.更新模板状态
            for (RcsTournamentTemplate rcsTournamentTemplate : rcsTournamentTemplateList) {
                executorService.execute(() -> {
                    rcsTournamentTemplate.setPendingOrderStatus(NumberConstant.NUM_ONE);//更新预约状态
                    tournamentTemplateService.updateById(rcsTournamentTemplate);

                    //log.info("更新赛事模板同步联赛设置数据: Message:{}",JSONObject.toJSON(rcsTournamentTemplate));
                    TradeMarketUiConfigDTO dto1 = getCommonClass(rcsTournamentTemplate);
                    sendMessage.sendMessage("RCS_TOUR_TEMPLATE_CASHOUT_TOPIC", "matchs", dto1.getStandardMatchInfoId() + "_" + rcsTournamentTemplate.getMatchType(), dto1.getConfigCashOutTradeItemDTO());

                    Long templateId = rcsTournamentTemplate.getId();
                    //4.根据模板ID查询玩法
                    List<RcsTournamentTemplatePlayMargain> rcsTournamentTemplatePlayMargainList = rcsTournamentTemplatePlayMargainService.getTemplatePlayMargainList(templateId.intValue());
                    //System.out.println(rcsTournamentTemplatePlayMargainList.size()+"||=======================");
                    if (Objects.isNull(rcsTournamentTemplatePlayMargainList) || rcsTournamentTemplatePlayMargainList.size() == 0) {
                        //没有玩法
                        log.info("没有玩法信息，模板ID:{}", templateId);
                    } else {
                        for (RcsTournamentTemplatePlayMargain rcsTournamentTemplatePlayMargain : rcsTournamentTemplatePlayMargainList) {
                            rcsTournamentTemplatePlayMargain.setPendingOrderStatus(NumberConstant.NUM_ONE);
                            //更新玩法状态
                            rcsTournamentTemplatePlayMargainService.updateById(rcsTournamentTemplatePlayMargain);
                            //发送mq
                            TournamentTemplateUpdateParam tournamentTemplateUpdateParam = new TournamentTemplateUpdateParam();
                            TournamentTemplatePlayMargainParam margin = new TournamentTemplatePlayMargainParam();
                            margin.setPlayId(rcsTournamentTemplatePlayMargain.getPlayId());
                            margin.setPendingOrderStatus(rcsTournamentTemplatePlayMargain.getPendingOrderStatus());

                            tournamentTemplateUpdateParam.setPlayMargainList(Arrays.asList(margin));
                            tournamentTemplateUpdateParam.setTypeVal(rcsTournamentTemplate.getTypeVal());
                            tournamentTemplateUpdateParam.setMatchType(rcsTournamentTemplate.getMatchType());
                            tournamentTemplateUpdateParam.setDataSourceCode(rcsTournamentTemplate.getDataSourceCode());
                            TournamentTemplatePlayVo playVo = getTournamentTemplatePlayVo(tournamentTemplateUpdateParam);

                            List<TournamentTemplateCategoryVo> categoryList = BeanCopyUtils.copyPropertiesList(tournamentTemplateUpdateParam.getPlayMargainList(), TournamentTemplateCategoryVo.class);
                            playVo.setCategoryList(categoryList);
                            String linkId = CommonUtils.getLinkId("play_template_update");
                            playVo.setCategoryList(categoryList);
                            Request request = new Request();
                            request.setData(playVo);
                            request.setGlobalId(linkId);
                            log.info("发送mq推送联赛模板玩法数据:linkId:{} ************ Message:{}", linkId, JSONObject.toJSON(request));
                            sendMessage.sendMessage("Tournament_Template_Play", linkId, String.valueOf(playVo.getStandardMatchId()), JSONObject.toJSON(request));
                        }
                    }
                });
            }
            executorService.shutdown();
            return HttpResponse.success();

        } catch (IllegalArgumentException ex) {
            return HttpResponse.error(201, ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return HttpResponse.error(HttpResponse.FAIL, ex.getMessage());
        }
    }

    private TournamentTemplatePlayVo getTournamentTemplatePlayVo(TournamentTemplateUpdateParam param) {
        TournamentTemplatePlayVo playVo = new TournamentTemplatePlayVo();
        DataSourceCodeVo weight = JSONObject.parseObject(param.getDataSourceCode(), DataSourceCodeVo.class);
        playVo.setStandardMatchId(param.getTypeVal());
        playVo.setMatchType(param.getMatchType());
        playVo.setBcWeight(weight.getBc());
        playVo.setBgWeight(weight.getBg());
        playVo.setSrWeight(weight.getSr());
        playVo.setTxWeight(weight.getTx());
        playVo.setRbWeight(weight.getRb());
        playVo.setPdWeight(weight.getPd());
        playVo.setAoWeight(weight.getAo());
        playVo.setPiWeight(weight.getPi());
        playVo.setLsWeight(weight.getLs());
        playVo.setBeWeight(weight.getBe());
        playVo.setKoWeight(weight.getKo());
        playVo.setBtWeight(weight.getBt());
        if (!StringUtils.isEmpty(param.getTemplateName())) {
            if (param.getTemplateName().contains(ManagerCodeEnum.MTS.getId())) {
                playVo.setRiskManagerCode(ManagerCodeEnum.MTS.getId());
            } else {
                playVo.setRiskManagerCode(ManagerCodeEnum.PA.getId());
            }
        }

        //传给融合的操盘模式  PA or MTS
        if (!StringUtils.isEmpty(param.getRiskManagerCode())) {
            playVo.setRiskManagerCode(param.getRiskManagerCode());
        }
        return playVo;
    }

    /**
     * 联赛模板
     *
     * @param param
     * @return
     */
    private TournamentTemplateMatchVo getTournamentTemplateMatchVo(RcsTournamentTemplate param) {
        TournamentTemplateMatchVo matchVo = new TournamentTemplateMatchVo();
        DataSourceCodeVo weight = JSONObject.parseObject(param.getDataSourceCode(), DataSourceCodeVo.class);
        matchVo.setStandardMatchId(param.getTypeVal());
        matchVo.setMatchType(param.getMatchType());
        matchVo.setBcWeight(weight.getBc());
        matchVo.setBgWeight(weight.getBg());
        matchVo.setSrWeight(weight.getSr());
        matchVo.setTxWeight(weight.getTx());
        matchVo.setRbWeight(weight.getRb());
        return matchVo;
    }

    public TradeMarketUiConfigDTO getCommonClass(RcsTournamentTemplate temp) {
        ConfigCashOutTradeItemDTO cashOutTradeItemDTO = new ConfigCashOutTradeItemDTO();
        cashOutTradeItemDTO.setMatchId(temp.getTypeVal());
        cashOutTradeItemDTO.setMatchPreStatus(temp.getMatchPreStatus());
        cashOutTradeItemDTO.setPendingOrderStatus(temp.getPendingOrderStatus());
        cashOutTradeItemDTO.setMarketType(temp.getMatchType());
        cashOutTradeItemDTO.setDataSourceCode(CommonUtil.getDataSourceCode(temp.getEarlySettStr()));
        TradeMarketUiConfigDTO dto = new TradeMarketUiConfigDTO();
        dto.setConfigCashOutTradeItemDTO(cashOutTradeItemDTO);
        dto.setStandardMatchInfoId(temp.getTypeVal());
        return dto;
    }
}
