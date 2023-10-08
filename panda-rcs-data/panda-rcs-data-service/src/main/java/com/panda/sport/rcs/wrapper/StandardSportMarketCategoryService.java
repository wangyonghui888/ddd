package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import com.panda.sport.rcs.vo.I18nItemVo;

import java.util.List;

public interface StandardSportMarketCategoryService extends IService<StandardSportMarketCategory> {

    String getPlayName(Integer sportId, Integer categoryId, String languageType);

    /**
     * 根据nameCode从缓存获取所有语言集
     *
     * @param nameCode
     * @return
     */
    List<I18nItemVo> getCachedNamesByCode(Long nameCode);

    StandardSportMarketCategory queryCachedCategory(String sportId, Long id);


    List<I18nItemVo> getTournmentName(Long tourId);

    List<I18nItemVo> championMatchNameAllLanguage(Long matchId);

    String queryChampionOptionValue(Long playOptionsId, String languageType);

    StandardSportMarket queryCacheMarket(Long marketId);

    List<I18nItemVo>  queryChampionPlayName(Long marketId);

    StandardSportMarketOdds queryCacheMarketOdds(Long playOptionsId);

    String specialOddsName(Integer playId, String languageType, StandardSportMarketOdds odds);


}
