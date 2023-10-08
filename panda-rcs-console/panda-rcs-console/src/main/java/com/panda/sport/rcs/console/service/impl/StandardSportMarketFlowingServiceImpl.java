package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.StandardSportMarketFlowingMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.StandardSportMarketFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.StandardSportMarketFlowingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class StandardSportMarketFlowingServiceImpl implements StandardSportMarketFlowingService {

    @Resource
    private StandardSportMarketFlowingMapper standardSportMarketFlowingMapper;

    @Override
    public int updateBatch(List<StandardSportMarketFlowing> list) {
        return standardSportMarketFlowingMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<StandardSportMarketFlowing> list) {
        return standardSportMarketFlowingMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(StandardSportMarketFlowing record) {
        return standardSportMarketFlowingMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(StandardSportMarketFlowing record) {
        return standardSportMarketFlowingMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getMarketList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(StandardSportMarketFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if(StringUtils.isBlank(matchFlowingDTO.getStandardMatchId())&&StringUtils.isBlank(matchFlowingDTO.getLinkId())&&
                StringUtils.isBlank(matchFlowingDTO.getOId())&&StringUtils.isBlank(matchFlowingDTO.getStartTime())&&
                StringUtils.isBlank(matchFlowingDTO.getEndTime())&&StringUtils.isBlank(matchFlowingDTO.getVersionId())
                &&StringUtils.isBlank(matchFlowingDTO.getPlaceNumId())){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY,-12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }

        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(matchFlowingDTO.getStandardMatchId())) criteria.andEqualTo("standardMatchInfoId",matchFlowingDTO.getStandardMatchId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getPlaceNumId())) criteria.andEqualTo("placeNumId",matchFlowingDTO.getPlaceNumId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getOId())) criteria.andEqualTo("oId",matchFlowingDTO.getOId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getVersionId())) criteria.andEqualTo("versionId",matchFlowingDTO.getVersionId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getDataType())) criteria.andEqualTo("dataType",matchFlowingDTO.getDataType());
        if(StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThan("insertTime",matchFlowingDTO.getStartTime());
        if(StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThan("insertTime",matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<StandardSportMarketFlowing> standardSportMarketFlowings = standardSportMarketFlowingMapper.selectByExample(example);
        if(standardSportMarketFlowings.size() != 0){
            PageInfo<StandardSportMarketFlowing> pageInfo = new PageInfo<>(standardSportMarketFlowings);
            pageDataResult.setList(standardSportMarketFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}
