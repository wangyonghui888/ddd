package com.panda.sport.rcs.data.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.data.mapper.StandardSportPlayerMapper;
import com.panda.sport.rcs.data.service.IStandardSportPlayerService;
import com.panda.sport.rcs.pojo.StandardSportPlayer;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标准球员信息表 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardSportPlayerServiceImpl extends ServiceImpl<StandardSportPlayerMapper, StandardSportPlayer> implements IStandardSportPlayerService {

}
