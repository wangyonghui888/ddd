package com.panda.sport.rcs.console.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsStandardSportMarketSellFlowingMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.RcsStandardSportMarketSellFlowing;
import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsStandardSportMarketSellFlowingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class RcsStandardSportMarketSellFlowingServiceImpl implements RcsStandardSportMarketSellFlowingService {

    @Resource
    private RcsStandardSportMarketSellFlowingMapper rcsStandardSportMarketSellFlowingMapper;

    @Override
    public int updateBatch(List<RcsStandardSportMarketSellFlowing> list) {
        return rcsStandardSportMarketSellFlowingMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsStandardSportMarketSellFlowing> list) {
        return rcsStandardSportMarketSellFlowingMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsStandardSportMarketSellFlowing record) {
        return rcsStandardSportMarketSellFlowingMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsStandardSportMarketSellFlowing record) {
        return rcsStandardSportMarketSellFlowingMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getOpenSellList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(RcsStandardSportMarketSellFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if(StringUtils.isBlank(matchFlowingDTO.getStandardMatchId())&&StringUtils.isBlank(matchFlowingDTO.getLinkId())&&StringUtils.isBlank(matchFlowingDTO.getOId())&&StringUtils.isBlank(matchFlowingDTO.getStartTime())&&StringUtils.isBlank(matchFlowingDTO.getEndTime())){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY,-12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }

        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(matchFlowingDTO.getStandardMatchId())) criteria.andEqualTo("matchInfoId",matchFlowingDTO.getStandardMatchId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getOId())) criteria.andEqualTo("oId",matchFlowingDTO.getOId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThanOrEqualTo("insertTime",matchFlowingDTO.getStartTime());
        if(StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThanOrEqualTo("insertTime",matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<RcsStandardSportMarketSellFlowing> standardSportMarketFlowings = rcsStandardSportMarketSellFlowingMapper.selectByExample(example);
        if(standardSportMarketFlowings.size() != 0){
            PageInfo<RcsStandardSportMarketSellFlowing> pageInfo = new PageInfo<>(standardSportMarketFlowings);
            pageDataResult.setList(standardSportMarketFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}
