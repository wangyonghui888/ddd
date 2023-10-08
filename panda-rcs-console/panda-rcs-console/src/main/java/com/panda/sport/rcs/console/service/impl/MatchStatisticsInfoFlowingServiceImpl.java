package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoDetailFlowingMapper;
import com.panda.sport.rcs.console.dao.MatchStatisticsInfoFlowingMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoDetailFlowing;
import com.panda.sport.rcs.console.pojo.MatchStatisticsInfoFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.MatchStatisticsInfoFlowingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class MatchStatisticsInfoFlowingServiceImpl implements MatchStatisticsInfoFlowingService {

    @Resource
    private MatchStatisticsInfoFlowingMapper matchStatisticsInfoFlowingMapper;
    @Resource
    private MatchStatisticsInfoDetailFlowingMapper matchStatisticsInfoDetailFlowingMapper;

    @Override
    public int insertOrUpdate(MatchStatisticsInfoFlowing record) {
        return matchStatisticsInfoFlowingMapper.insertOrUpdate(record);
    }



    @Override
    public PageDataResult getStatisticsList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(MatchStatisticsInfoFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if(StringUtils.isBlank(matchFlowingDTO.getStandardMatchId())&&StringUtils.isBlank(matchFlowingDTO.getLinkId())&&StringUtils.isBlank(matchFlowingDTO.getOId())&&StringUtils.isBlank(matchFlowingDTO.getStartTime())&&StringUtils.isBlank(matchFlowingDTO.getEndTime())){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY,-12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }

        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(matchFlowingDTO.getStandardMatchId())) criteria.andEqualTo("standardMatchId",matchFlowingDTO.getStandardMatchId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getOId())) criteria.andEqualTo("oId",matchFlowingDTO.getOId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThan("insertTime",matchFlowingDTO.getStartTime());
        if(StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThan("insertTime",matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<MatchStatisticsInfoFlowing> matchStatisticsInfoFlowings = matchStatisticsInfoFlowingMapper.selectByExample(example);
        if(matchStatisticsInfoFlowings.size() != 0){
            PageInfo<MatchStatisticsInfoFlowing> pageInfo = new PageInfo<>(matchStatisticsInfoFlowings);
            pageDataResult.setList(matchStatisticsInfoFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

    @Override
    public PageDataResult getStatisticsDetailList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(MatchStatisticsInfoDetailFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if(StringUtils.isBlank(matchFlowingDTO.getStandardMatchId())&&StringUtils.isBlank(matchFlowingDTO.getLinkId())&&
                StringUtils.isBlank(matchFlowingDTO.getOId())&&StringUtils.isBlank(matchFlowingDTO.getStartTime())&&StringUtils.isBlank(matchFlowingDTO.getEndTime())
                &&StringUtils.isBlank(matchFlowingDTO.getCode())
        ){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY,-12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(matchFlowingDTO.getStandardMatchId())) criteria.andEqualTo("standardMatchId",matchFlowingDTO.getStandardMatchId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getOId())) criteria.andEqualTo("oId",matchFlowingDTO.getOId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getCode())) criteria.andEqualTo("code",matchFlowingDTO.getCode());
        if(StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThan("insertTime",matchFlowingDTO.getStartTime());
        if(StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThan("insertTime",matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<MatchStatisticsInfoDetailFlowing> matchStatisticsInfoDetailFlowings = matchStatisticsInfoDetailFlowingMapper.selectByExample(example);
        if(matchStatisticsInfoDetailFlowings.size() != 0){
            PageInfo<MatchStatisticsInfoDetailFlowing> pageInfo = new PageInfo<>(matchStatisticsInfoDetailFlowings);
            pageDataResult.setList(matchStatisticsInfoDetailFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}

