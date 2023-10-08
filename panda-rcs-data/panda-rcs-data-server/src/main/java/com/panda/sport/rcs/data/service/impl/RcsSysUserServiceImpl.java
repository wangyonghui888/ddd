package com.panda.sport.rcs.data.service.impl;



import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.RcsSysUserMapper;
import com.panda.sport.rcs.data.service.RcsSysUserService;
import com.panda.sport.rcs.pojo.RcsSysUser;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RcsSysUserServiceImpl extends ServiceImpl<RcsSysUserMapper, RcsSysUser> implements RcsSysUserService {

    @Resource
    private RcsSysUserMapper rcsSysUserMapper;

    @Override
    public int batchInsert(List<RcsSysUser> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsSysUserMapper.batchInsert(list);
    }

    @Override
    public int insertOrUpdate(RcsSysUser record) {
        return rcsSysUserMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(RcsSysUser record) {
        return rcsSysUserMapper.insertOrUpdateSelective(record);
    }

    @Override
    public int batchInsertOrUpdate(List<RcsSysUser> list) {
        if(CollectionUtils.isEmpty(list)){return 0;}
        return rcsSysUserMapper.batchInsertOrUpdate(list);
    }

}


