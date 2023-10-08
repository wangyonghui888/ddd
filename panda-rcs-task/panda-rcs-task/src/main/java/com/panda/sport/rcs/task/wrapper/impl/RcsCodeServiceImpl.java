package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsCodeMapper;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.panda.sport.rcs.constants.RedisKey.EXPRIY_TIME_5_HOURS;
import static com.panda.sport.rcs.constants.RedisKeys.RCS_CODE_CACHE;

/**
 * 数据字典接口
 *
 * @author admin
 * @since 2020-01-13
 */
@Service
public class RcsCodeServiceImpl extends ServiceImpl<RcsCodeMapper, RcsCode> implements RcsCodeService {
    @Autowired
    private RcsCodeMapper rcsCodeMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public String getValue(String fatherKey, String childKey) {
        String result = "";
        String key = String.format(RCS_CODE_CACHE, fatherKey, childKey);
        boolean exist = redisClient.exist(String.format(RCS_CODE_CACHE, fatherKey, childKey));
        if (exist) {
            result = redisClient.get(key);
        } else {
            QueryWrapper<RcsCode> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(RcsCode::getFatherKey, fatherKey);
            wrapper.lambda().eq(RcsCode::getChildKey, childKey);
            RcsCode rcsCode = rcsCodeMapper.selectOne(wrapper);
            result = rcsCode.getValue();
            redisClient.setExpiry(key, rcsCode.getValue(),  EXPRIY_TIME_5_HOURS);
        }
        return result;
    }

    @Override
    public List<RcsCode> selectRcsCods(String fatherKey) {
        QueryWrapper<RcsCode> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(RcsCode::getFatherKey, fatherKey);
        List<RcsCode> rcsCodes = rcsCodeMapper.selectList(wrapper);
        return rcsCodes;
    }

}
