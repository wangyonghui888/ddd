package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsMarketChampionExt;
import com.panda.sport.rcs.pojo.dao.RcsMarketChampionExtVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RcsMarketChampionExtMapper extends BaseMapper<RcsMarketChampionExt> {

    /**
     * @Description: 获取到达封盘时间的冠军赛事盘口数据
     * @Author carver
     * @Date 2021/7/17 15:34
     * @return: com.panda.sport.rcs.pojo.RcsMarketChampionExt
     **/
    List<RcsMarketChampionExt> queryChampionMatchBySeal();

    /**
     * @Description: 获取到达封盘时间的冠军赛事盘口数据new
     * @Author carver
     * @Date 2021/7/17 15:34
     * @return: com.panda.sport.rcs.pojo.dao.RcsMarketChampionExtVO
     **/
    List<RcsMarketChampionExtVO> queryChampionMatchBySealNew();
}