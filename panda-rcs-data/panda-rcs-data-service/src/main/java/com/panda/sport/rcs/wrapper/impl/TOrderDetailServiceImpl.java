package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.cache.local.WebsocketConstants;
import com.panda.sport.rcs.mapper.TOrderDetailMapper;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import com.panda.sport.rcs.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.wrapper.StandardSportMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * <p>
 * 投注单详细信息表 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
@Service
@Slf4j
public class TOrderDetailServiceImpl extends ServiceImpl<TOrderDetailMapper, TOrderDetail> implements ITOrderDetailService {

    @Autowired
    private TOrderDetailMapper orderDetailMapper;
    @Autowired
    private StandardSportMarketCategoryService categoryService;

    @Override
    public String queryOptionValue(OrderItem bean, String languageType) {

        if(StringUtils.isNotEmpty(languageType)) {
            return orderDetailMapper.queryOptionValue(bean, languageType);
        }else{
            return  "";
        }
    }

    @Override
    public String queryPlayerOptionValue(Long playOptionsId, String languageType) {
        return orderDetailMapper.queryPlayerOptionValue(playOptionsId, languageType);
    }

    @Override
    public String queryMarketPlayer(Long marketId, String languageType) {
        String add3 = "";
        StandardSportMarket market = categoryService.queryCacheMarket(marketId);
        if (market != null) {
            add3 = market.getAddition3();
        }
        if (StringUtils.isNotBlank(add3)) {
            List<I18nItemVo> names = categoryService.getCachedNamesByCode(Long.parseLong(add3));
            add3 = getLanguageName(names, languageType);
        }
        return add3;
    }

    @Override
    public String queryOddsPlayer(Long playerId, String languageType) {

        String name = WebsocketConstants.SPECIAL_OPTION__PLAYERCACHE.get(playerId, key -> {
            return orderDetailMapper.queryOddsPlayer(playerId, languageType);
        });
        return name;
    }

    @Override
    public List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId, Integer matchType) {
        return orderDetailMapper.getMarketStatByMatchIdAndPlayIdAndMatchStatus(matchId, marketCategoryId, matchType);
    }


    String getLanguageName(List<I18nItemVo> i18nItemVos, String languageType) {
        String text = "";
        if (!CollectionUtils.isEmpty(i18nItemVos)) {
            text = i18nItemVos.stream().filter(fi -> StringUtils.isNotBlank(fi.getLanguageType()) && languageType.equals(fi.getLanguageType())).map(I18nItemVo::getText).findFirst().orElse(null);
        }
        return text;
    }
}
