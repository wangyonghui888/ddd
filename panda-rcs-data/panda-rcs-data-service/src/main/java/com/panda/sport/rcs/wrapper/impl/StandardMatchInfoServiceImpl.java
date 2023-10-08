package com.panda.sport.rcs.wrapper.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.StandardMatchInfoMapper;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.wrapper.IStandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author V
 * @since 2019-09-26
 */
@Slf4j
@Service
public class StandardMatchInfoServiceImpl extends ServiceImpl<StandardMatchInfoMapper, StandardMatchInfo> implements IStandardMatchInfoService {

}
