package com.panda.sport.rcs.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.merge.dto.StandardMarketOddsDTO;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.dto.StandardMarketPlaceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StandardSportMarketMapper
 * @Description: TODO
 * @Author Vector
 * @Date 2019/10/7
 **/
@Mapper
public interface StandardSportMarketMapper extends BaseMapper<StandardSportMarket> {

    int updateBatch(List<StandardSportMarket> list);

    int batchInsert(@Param("list") List<StandardSportMarket> list);

    int insertOrUpdate(StandardSportMarket record);

    int insertOrUpdateSelective(StandardSportMarket record);

    int batchInsertOrUpdate(@Param("list") List<StandardMarketMessageDTO> list);

	void updateAdditionOne(Map<String, Object> map);

    /**
     * @Description   //根据玩法获取模板id和投注项排序
     * @Param [marketCategoryId]
     * @Author  sean
     * @Date   2020/12/27
     * @return java.util.List<com.panda.sport.rcs.pojo.dto.odds.RcsStandardMarketDTO>
     **/
    List<StandardMarketOddsDTO> selectOddsFieldsTempletId(@Param("marketCategoryId") Long marketCategoryId);

    /**
     * 查询盘口位置信息
     *
     * @param matchId  赛事ID
     * @param playIds  玩法ID集合，可为空，为空查所有
     * @param placeNum 盘口位置，可为空，为空查所有
     * @return
     * @author Paca
     */
    @Deprecated
    List<StandardMarketPlaceDto> selectMarketPlaceInfo(@Param("matchId") Long matchId, @Param("playIds") Collection<Long> playIds, @Param("placeNum") Integer placeNum);
}