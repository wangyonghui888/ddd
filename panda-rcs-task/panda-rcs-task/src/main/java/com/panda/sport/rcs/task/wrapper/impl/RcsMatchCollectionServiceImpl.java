package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsMatchCollectionMapper;
import com.panda.sport.rcs.pojo.RcsMatchCollection;
import com.panda.sport.rcs.task.wrapper.RcsMatchCollectionService;
import org.springframework.stereotype.Service;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :
 * @Date: 2019-10-25 14:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsMatchCollectionServiceImpl extends ServiceImpl<RcsMatchCollectionMapper, RcsMatchCollection> implements RcsMatchCollectionService {


    @Override
    public int taskCleanCollection() {
        return baseMapper.taskCleantaskCleanCollection();
    }
}
