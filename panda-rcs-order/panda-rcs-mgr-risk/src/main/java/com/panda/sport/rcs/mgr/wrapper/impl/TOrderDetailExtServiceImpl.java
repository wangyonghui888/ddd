package com.panda.sport.rcs.mgr.wrapper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.TOrderDetailExtMapper;
import com.panda.sport.rcs.mgr.wrapper.TOrderDetailExtService;
import com.panda.sport.rcs.pojo.TOrderDetailExt;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2020-01-31
 */
@Service
@Slf4j
public class TOrderDetailExtServiceImpl extends ServiceImpl<TOrderDetailExtMapper, TOrderDetailExt> implements TOrderDetailExtService {

    @Autowired
    private TOrderDetailExtMapper detailExtMapper;
    @Override
    public void insertOrUpdateTOrderDetailExt(List<TOrderDetailExt> exts) {

    }
}
