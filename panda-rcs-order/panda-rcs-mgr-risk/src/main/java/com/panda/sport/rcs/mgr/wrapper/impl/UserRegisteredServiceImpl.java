package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TUserMapper;
import com.panda.sport.rcs.mapper.UserRegisteredMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.TUser;
import com.panda.sport.rcs.mgr.wrapper.UserRegisteredService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  用户注册服务实现类
 * @Date: 2019-10-21 16:22
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class UserRegisteredServiceImpl extends ServiceImpl<UserRegisteredMapper, TUser> implements UserRegisteredService {
    @Autowired
    TUserMapper userMapper;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public Integer saveUserRegistered (TUser user){
        log.info("user:{}", JSONObject.toJSONString(user));

        //Integer count =userMapper.insertOrUpdate(user);

        TUser userBean =  userMapper.selectByUserId(user.getUid());
        Integer count =0;
        if(userBean!=null){
            userBean.setUserLevel(user.getUserLevel());
            userBean.setModifyTime(System.currentTimeMillis());
            count=userMapper.updateById(userBean);

            Map<String, Object> map = new HashMap<>();
            map.put("userId", user.getUid());
            map.put("tagId", user.getUserLevel());
            producerSendMessageUtils.sendMessage("rcs_user_tag_last_time_syn", map);
        }else
        {
            count= userMapper.insert(user);
        }

        return count;
    }
}
