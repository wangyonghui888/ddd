package com.panda.sport.rcs.mgr.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.MatchEventInfoMapper;
import com.panda.sport.rcs.pojo.MatchEventInfo;
import com.panda.sport.rcs.mgr.wrapper.MatchEventInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @ClassName MatchEventInfoServiceImpl
 * @Description: TODO 
 * @Author Vector
 * @Date 2019/10/10 
**/
@Service
public class MatchEventInfoServiceImpl extends ServiceImpl<MatchEventInfoMapper,MatchEventInfo > implements MatchEventInfoService {

}
