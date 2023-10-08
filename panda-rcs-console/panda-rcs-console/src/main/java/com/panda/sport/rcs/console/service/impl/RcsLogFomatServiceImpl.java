package com.panda.sport.rcs.console.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsLogFomatMapper;
import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.pojo.MatchStatusFlowing;
import com.panda.sport.rcs.console.pojo.RcsLogFomat;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsLogFomatService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsLogFomatServiceImpl implements RcsLogFomatService {

    @Resource
    private RcsLogFomatMapper rcsLogFomatMapper;

    @Override
    public int updateBatch(List<RcsLogFomat> list) {
        return rcsLogFomatMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<RcsLogFomat> list) {
        return rcsLogFomatMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsLogFomat record) {
        return rcsLogFomatMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsLogFomat record) {
        return rcsLogFomatMapper.insertOrUpdateSelective(record);
    }

    @Override
    public PageDataResult getRcsLogFomats(MatchFlowingDTO matchFlowingDTO, Integer pageNum, Integer pageSize) {

        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List rcsLogFomats = rcsLogFomatMapper.getRcsLogFomats(matchFlowingDTO);
        if(rcsLogFomats.size() != 0){
            PageInfo<MatchStatusFlowing> pageInfo = new PageInfo<>(rcsLogFomats);
            pageDataResult.setList(rcsLogFomats);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

}
