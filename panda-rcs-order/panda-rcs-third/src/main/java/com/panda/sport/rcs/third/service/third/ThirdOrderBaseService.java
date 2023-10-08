package com.panda.sport.rcs.third.service.third;

import com.panda.sport.rcs.mapper.StandardSportMarketOddsMapper;
import com.panda.sport.rcs.pojo.StandardSportMarketOdds;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Beulah
 * @date 2023/3/31 19:49
 * @description 通用方法基类
 */
@Service
@Slf4j
public class ThirdOrderBaseService {


    @Resource
    StandardSportMarketOddsMapper standardSportMarketOddsMapper;


    /**
     * 获取
     *
     * @param selectionId 投注项id
     * @return 投注项信息
     */
    public StandardSportMarketOdds getStandardSportMarketOdds(String selectionId) {
        return standardSportMarketOddsMapper.selectById(selectionId);
    }

}
