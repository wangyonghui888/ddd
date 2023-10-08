package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.MatchEventInfoFlowingMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchEventInfoFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.MatchEventInfoFlowingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class MatchEventInfoFlowingServiceImpl implements MatchEventInfoFlowingService {

    @Resource
    private MatchEventInfoFlowingMapper matchEventInfoFlowingMapper;

    @Override
    public int updateBatch(List<MatchEventInfoFlowing> list) {
        return matchEventInfoFlowingMapper.updateBatch(list);
    }


    @Override
    public int batchInsert(List<MatchEventInfoFlowing> list) {
        return matchEventInfoFlowingMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(MatchEventInfoFlowing record) {
        return matchEventInfoFlowingMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(MatchEventInfoFlowing record) {
        return matchEventInfoFlowingMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getEventList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(MatchEventInfoFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if (StringUtils.isBlank(matchFlowingDTO.getStandardMatchId()) && StringUtils.isBlank(matchFlowingDTO.getLinkId()) && StringUtils.isBlank(matchFlowingDTO.getEventCode()) &&
            StringUtils.isBlank(matchFlowingDTO.getStartTime()) && StringUtils.isBlank(matchFlowingDTO.getEndTime()) && StringUtils.isBlank(matchFlowingDTO.getDataSourceCode())) {
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY, -12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }

        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(matchFlowingDTO.getStandardMatchId())) criteria.andEqualTo("standardMatchId", matchFlowingDTO.getStandardMatchId());
        if (StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId", matchFlowingDTO.getLinkId());
        if (StringUtils.isNotBlank(matchFlowingDTO.getEventCode())) criteria.andEqualTo("eventCode", matchFlowingDTO.getEventCode());
        if (StringUtils.isNotBlank(matchFlowingDTO.getDataSourceCode())) criteria.andEqualTo("dataSourceCode", matchFlowingDTO.getDataSourceCode());
        if (StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThan("insertTime", matchFlowingDTO.getStartTime());
        if (StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThan("insertTime", matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<MatchEventInfoFlowing> matchEventInfoFlowings = matchEventInfoFlowingMapper.selectByExample(example);
        if (matchEventInfoFlowings.size() != 0) {
            PageInfo<MatchEventInfoFlowing> pageInfo = new PageInfo<>(matchEventInfoFlowings);
            pageDataResult.setList(matchEventInfoFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}

