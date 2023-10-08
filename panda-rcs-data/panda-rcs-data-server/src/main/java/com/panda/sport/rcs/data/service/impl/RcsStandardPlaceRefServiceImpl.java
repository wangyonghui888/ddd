package com.panda.sport.rcs.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.merge.bo.I18nItemBO;
import com.panda.sport.rcs.data.mapper.RcsStandardPlaceRefMapper;
import com.panda.sport.rcs.data.service.RcsStandardPlaceRefService;
import com.panda.sport.rcs.pojo.RcsStandardPlaceRef;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RcsStandardPlaceRefServiceImpl extends ServiceImpl<RcsStandardPlaceRefMapper, RcsStandardPlaceRef> implements RcsStandardPlaceRefService {

    @Resource
    private RcsStandardPlaceRefMapper rcsStandardPlaceRefMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return rcsStandardPlaceRefMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertOrUpdate(RcsStandardPlaceRef record) {
        return rcsStandardPlaceRefMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsStandardPlaceRef record) {
        return rcsStandardPlaceRefMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int insertSelective(RcsStandardPlaceRef record) {
        return rcsStandardPlaceRefMapper.insertSelective(record);
    }

    @Override
    public RcsStandardPlaceRef selectByPrimaryKey(Long id) {
        return rcsStandardPlaceRefMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(RcsStandardPlaceRef record) {
        return rcsStandardPlaceRefMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(RcsStandardPlaceRef record) {
        return rcsStandardPlaceRefMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateBatch(List<RcsStandardPlaceRef> list) {
        return rcsStandardPlaceRefMapper.updateBatch(list);
    }

    @Override
    public int updateBatchSelective(List<RcsStandardPlaceRef> list) {
        return rcsStandardPlaceRefMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<RcsStandardPlaceRef> list) {
        return rcsStandardPlaceRefMapper.batchInsert(list);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsStandardPlaceRef> list) {
        if (CollectionUtils.isEmpty(list)){return 0;}
        rcsStandardPlaceRefMapper.batchInsertOrUpdate(list);
        return 1;
    }

	@Override
	public List<Map<String, Object>> queryOddsByPlaceNumAndPlayId(Map<String, Object> params) {
		return rcsStandardPlaceRefMapper.queryOddsByPlaceNumAndPlayId(params);
	}

}

