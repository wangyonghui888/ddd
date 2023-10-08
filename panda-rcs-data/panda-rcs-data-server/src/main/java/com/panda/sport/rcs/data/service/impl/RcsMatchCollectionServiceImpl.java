package com.panda.sport.rcs.data.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsMatchCollectionMapper;
import com.panda.sport.rcs.data.service.RcsMatchCollectionService;
import com.panda.sport.rcs.pojo.RcsMatchCollection;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-10-25 14:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsMatchCollectionServiceImpl extends ServiceImpl<RcsMatchCollectionMapper, RcsMatchCollection> implements RcsMatchCollectionService {

    @Override
    public int deleteMatch(Long standardMatchId) {
        UpdateWrapper<RcsMatchCollection> rcsMatchCollectionWrapper = new UpdateWrapper<>();
        rcsMatchCollectionWrapper.eq(standardMatchId != null, "match_id", standardMatchId);
        RcsMatchCollection rcsMatchCollection = new RcsMatchCollection();
        rcsMatchCollection.setUpdateTime(new Date());
        rcsMatchCollection.setStatus(0);
        return baseMapper.update(rcsMatchCollection,rcsMatchCollectionWrapper);
    }

}
