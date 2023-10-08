package com.panda.rcs.logService.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.dto.CategorySetDTO;
import com.panda.rcs.logService.dto.SportDTO;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.service.LogService;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.utils.OperateLogOneUtils;
import com.panda.rcs.logService.vo.RcsOperateLogVO;
import com.panda.sport.rcs.common.DateUtils;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.log.format.RcsOperateSimpleLog;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 操盤日誌
 * @author Z9-jing
 */
@Service
public class OperateLogServiceImpl implements LogService<RcsOperateLog, RcsOperateLogVO> {

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;

    private static final String SPORT_ID_NAME = "SPORT_";

    @Override
    public RcsOperateLog saveLog(RcsOperateLog logBean) {
        rcsOperateLogMapper.insert(logBean);
        return logBean;
    }

    @Override
    public List<RcsOperateLog> query(RcsOperateLogVO queryVO) {
        List<RcsOperateLog> list = rcsOperateLogMapper.selectByParam(queryVO);
        String lang = queryVO.getLang();
        if (!list.isEmpty()) {
            for (RcsOperateLog log : list) {
                log.setOperatePageName(OperateLogOneUtils.operatePageMap.get(log.getOperatePageCode()));
                log.setOperateTimeStr(DateUtils.parseDate(log.getOperateTime().getTime(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
                // 球种名称需返回
                try {
                    OperateLogOneEnum logEnum = OperateLogOneEnum.getByName(SPORT_ID_NAME + log.getSportId());
                    JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                    if (StringUtils.isNotBlank(langO.getString("zs"))) {
                        log.setSportName(langO.getString("zs"));
                    }
                }catch (Exception ignored) {}
                // 不是简体/繁体中文,根据多语言返回,之前保存的都是中文,多语言保存在枚举中,可根据中文获取多语言
                if (StringUtils.isNotBlank(lang) && !(lang.contains("zs") || lang.contains("zh"))) {
                    try {
                        // 根据中文获取枚举类,获取多语言JSON
                        OperateLogOneEnum logEnum = OperateLogOneEnum.getByName(log.getOperatePageName());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setOperatePageName(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}
                    try {
                        OperateLogOneEnum logEnum = OperateLogOneEnum.getByName(log.getBehavior());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setBehavior(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}
                    try {
                        OperateLogOneEnum logEnum =OperateLogOneEnum.getByName(log.getParameterName());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setParameterName(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}
                    try {
                        OperateLogOneEnum logEnum =OperateLogOneEnum.getByName(log.getBeforeVal());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setBeforeVal(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}
                    try {
                        OperateLogOneEnum logEnum = OperateLogOneEnum.getByName(log.getAfterVal());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setAfterVal(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}
                    try {
                        OperateLogOneEnum logEnum = OperateLogOneEnum.getByName(log.getSportId().toString());
                        JSONObject langO = JSONObject.parseObject(logEnum.getLangJson());
                        if (StringUtils.isNotBlank(langO.getString(lang))) {
                            log.setSportName(langO.getString(lang));
                        }
                    }catch (Exception ignored) {}


                }
                try {
                    String code=lang;
                    if(Objects.nonNull(log.getExtObjectName())&&log.getExtObjectName().contains("en")){
                        Map<String,String> map = JSONObject.parseObject(log.getExtObjectName(), new TypeReference<Map<String, String>>(){});
                        if(lang.equals("zh")) {
                            code="zs";
                        }
                        if (StringUtils.isNotBlank(map.get(code))) {
                            log.setExtObjectName(map.get(code));
                        }

                    }
                    if(log.getUserId().equals("-2")){
                        if(lang.contains("en")){
                            log.setUserName("Auto Control");
                        }else {
                            log.setUserName("自动联控");
                        }
                    }
                    if(Objects.nonNull(log.getObjectName())&&log.getObjectName().contains("en")){
                        Map<String,String> map = JSONObject.parseObject(log.getObjectName(), new TypeReference<Map<String, String>>(){});
                        if (StringUtils.isNotBlank(map.get(code))) {
                            log.setObjectName(map.get(code));
                        }
                    }
                }catch (Exception e) {}
            }
        }
        return list;
    }

    public Integer queryCount(RcsOperateLogVO queryVO) {
        return rcsOperateLogMapper.selectCountByParam(queryVO);
    }

    public List<RcsOperateSimpleLog> querySimpleLog(RcsOperateLogVO queryVO) {
        List<RcsOperateSimpleLog> list = rcsOperateLogMapper.selectSimpleLog(queryVO);
        if (!list.isEmpty() && list.size() > 0) {
            for (RcsOperateSimpleLog log : list) {
                log.setOperatePageName(OperateLogOneUtils.operatePageMap.get(log.getOperatePageCode()));
                log.setOperateTimeStr(DateUtils.parseDate(log.getOperateTime().getTime(), DateUtils.DATE_YYYY_MM_DD_HH_MM_SS));
            }
        }
        return list;
    }

    public Integer querySimpleLogCount(RcsOperateLogVO queryVO) {
        return rcsOperateLogMapper.selectSimpleLogCount(queryVO);
    }

    /**
     * 參數整理檢查
     *
     * @param queryVO
     * @return
     */
    public RcsOperateLogVO paramterCheck(RcsOperateLogVO queryVO) {
        /**
         * 判斷玩法集查詢
         */
        if (Objects.nonNull(queryVO.getCategorySetId())) {
            SportDTO sport = BaseUtils.sportPlayMap.get(queryVO.getSportId());
            CategorySetDTO categorySetDTO = sport.getCategorySetMap().get(queryVO.getCategorySetId());
            queryVO.setPlayIds(categorySetDTO.getPlayIds());
        }
        /**
         * 早盤/滾球查詢
         */
        if (Objects.nonNull(queryVO.getMatchType())) {
            switch (queryVO.getMatchType()) {
                case 0:
                    queryVO.setOperatePageCodes(BaseUtils.runningMarket);
                    break;
                case 1:
                    queryVO.setOperatePageCodes(BaseUtils.earlyMarket);
                    break;
            }
        }
        return queryVO;
    }
}
