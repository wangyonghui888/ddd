package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import com.panda.sport.rcs.console.pojo.StandardSportMarketOddsFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.MatchStatusFlowingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.panda.sport.rcs.console.dao.MatchStatusFlowingMapper;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class MatchStatusFlowingServiceImpl implements MatchStatusFlowingService {

    @Resource
    private MatchStatusFlowingMapper matchStatusFlowingMapper;

    @Override
    public int updateBatch(List<MatchStatusFlowing> list) {
        return matchStatusFlowingMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<MatchStatusFlowing> list) {
        return matchStatusFlowingMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(MatchStatusFlowing record) {
        return matchStatusFlowingMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(MatchStatusFlowing record) {
        return matchStatusFlowingMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getStatusList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(MatchStatusFlowing.class);

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

        List<MatchStatusFlowing> matchStatusFlowings = matchStatusFlowingMapper.selectByExample(example);
        if(matchStatusFlowings.size() != 0){
            PageInfo<MatchStatusFlowing> pageInfo = new PageInfo<>(matchStatusFlowings);
            pageDataResult.setList(matchStatusFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}

