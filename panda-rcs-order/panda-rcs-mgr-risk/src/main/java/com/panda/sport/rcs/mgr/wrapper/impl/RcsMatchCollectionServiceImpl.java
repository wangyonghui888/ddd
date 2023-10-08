package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsMatchCollectionMapper;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.RcsMatchCollection;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.mgr.wrapper.MongoService;
import com.panda.sport.rcs.mgr.wrapper.RcsMatchCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private RcsMatchCollectionMapper rcsMatchCollectionMapper;

    @Autowired
    MongoService mongoService;

    @Override
    public List<RcsMatchCollection> selectByMap(Map<String, Object> columnMap) {
        return rcsMatchCollectionMapper.selectByMap(columnMap);
    }
}
