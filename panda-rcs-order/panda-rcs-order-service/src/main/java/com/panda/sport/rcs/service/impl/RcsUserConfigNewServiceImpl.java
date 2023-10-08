package com.panda.sport.rcs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsUserConfigNewMapper;
import com.panda.sport.rcs.pojo.RcsUserConfig;
import com.panda.sport.rcs.pojo.RcsUserConfigNew;
import com.panda.sport.rcs.pojo.vo.RcsUserConfigNewConfig;
import com.panda.sport.rcs.service.IRcsUserConfigNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RcsUserConfigNewServiceImpl extends ServiceImpl<RcsUserConfigNewMapper, RcsUserConfigNew> implements IRcsUserConfigNewService {

    @Override
    public List<RcsUserConfig> getByUserId(long userId) {
        List<RcsUserConfig> list = new ArrayList<>();
        RcsUserConfigNew rcsUserConfigNew = baseMapper.selectOne(new LambdaQueryWrapper<RcsUserConfigNew>().eq(RcsUserConfigNew::getUserId, userId));
        if (rcsUserConfigNew != null) {
            if (StringUtils.isNotBlank(rcsUserConfigNew.getConfig())) {
                List<RcsUserConfigNewConfig> configs = JSONArray.parseArray(rcsUserConfigNew.getConfig(), RcsUserConfigNewConfig.class);
                list = configs.stream().map(e -> {
                    RcsUserConfig rcsUserConfig = new RcsUserConfig();
                    BeanUtils.copyProperties(rcsUserConfigNew, rcsUserConfig);
                    rcsUserConfig.setSportId(e.getSportId());
                    return rcsUserConfig;
                }).collect(Collectors.toList());
            } else {
                log.info("用户userConfigNew.sportIds 为空:{}", JSONObject.toJSONString(rcsUserConfigNew));
            }
        }
        return list;
    }
}

