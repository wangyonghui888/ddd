package com.panda.sport.rcs.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.StandardSportMarketOddsFlowingMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.GetMarketOddsByParamVO;
import com.panda.sport.rcs.console.pojo.StandardSportMarketOddsFlowing;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.StandardSportMarketOddsFlowingService;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class StandardSportMarketOddsFlowingServiceImpl implements StandardSportMarketOddsFlowingService {

    @Resource
    private StandardSportMarketOddsFlowingMapper standardSportMarketOddsFlowingMapper;

    @Override
    public int updateBatch(List<StandardSportMarketOddsFlowing> list) {
        return standardSportMarketOddsFlowingMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<StandardSportMarketOddsFlowing> list) {
        return standardSportMarketOddsFlowingMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(StandardSportMarketOddsFlowing record) {
        return standardSportMarketOddsFlowingMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(StandardSportMarketOddsFlowing record) {
        return standardSportMarketOddsFlowingMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getMarketOddsList(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(StandardSportMarketOddsFlowing.class);

        //如果查询条件都为空 只查询72小时以内的数据 为了加速
        if(StringUtils.isBlank(matchFlowingDTO.getMarketId())&&StringUtils.isBlank(matchFlowingDTO.getLinkId())&&StringUtils.isBlank(matchFlowingDTO.getOId())
                &&StringUtils.isBlank(matchFlowingDTO.getStartTime())&&StringUtils.isBlank(matchFlowingDTO.getEndTime())&&StringUtils.isBlank(matchFlowingDTO.getPlaceNumId())){
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.HOUR_OF_DAY,-12);
            String sTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(instance.getTime());
            matchFlowingDTO.setStartTime(sTime);
        }

        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(matchFlowingDTO.getMarketId())) criteria.andEqualTo("marketId",matchFlowingDTO.getMarketId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getLinkId())) criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getOId())) criteria.andEqualTo("oId",matchFlowingDTO.getOId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getPlaceNumId())) criteria.andEqualTo("placeNumId",matchFlowingDTO.getPlaceNumId());
        if(StringUtils.isNotBlank(matchFlowingDTO.getDataType())) criteria.andEqualTo("dataType",matchFlowingDTO.getDataType());
        if(StringUtils.isNotBlank(matchFlowingDTO.getStartTime())) criteria.andGreaterThan("insertTime",matchFlowingDTO.getStartTime());
        if(StringUtils.isNotBlank(matchFlowingDTO.getEndTime())) criteria.andLessThan("insertTime",matchFlowingDTO.getEndTime());
        example.setOrderByClause("insert_time desc");

        List<StandardSportMarketOddsFlowing> standardSportMarketOddsFlowings = standardSportMarketOddsFlowingMapper.selectByExample(example);
        if(standardSportMarketOddsFlowings.size() != 0){
            PageInfo<StandardSportMarketOddsFlowing> pageInfo = new PageInfo<>(standardSportMarketOddsFlowings);
            pageDataResult.setList(standardSportMarketOddsFlowings);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

    @Override
    public List getMarketOddsByParam(MatchFlowingDTO matchFlowingDTO) {
        Example example = new Example(StandardSportMarketOddsFlowing.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("linkId",matchFlowingDTO.getLinkId());
        criteria.andEqualTo("marketId",matchFlowingDTO.getMarketId());
        List<StandardSportMarketOddsFlowing> standardSportMarketOddsFlowings = standardSportMarketOddsFlowingMapper.selectByExample(example);
        List<GetMarketOddsByParamVO> getMarketOddsByParamVOS = JsonFormatUtils.fromJsonArray(JSONObject.toJSONString(standardSportMarketOddsFlowings), GetMarketOddsByParamVO.class);
        return getMarketOddsByParamVOS;
    }

}
