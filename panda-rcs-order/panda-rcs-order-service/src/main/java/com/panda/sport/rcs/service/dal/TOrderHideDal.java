package com.panda.sport.rcs.service.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.TOrderHideMapper;
import com.panda.sport.rcs.pojo.TOrderHidePO;
import com.panda.sport.rcs.vo.TOrderHide;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * <p>
 * 藏单表服务实现
 * </p>
 *
 * @author skyKong
 * @since 2022-9-12
 */
@Service
public class TOrderHideDal extends ServiceImpl<TOrderHideMapper, TOrderHidePO>  {

  @Autowired
  TOrderHideMapper tOrderHideMapper;

  @Autowired
  RedisClient redisClient;

  public int insertOrUpdate(TOrderHide orderHide) {
    TOrderHidePO orderHidePO = new TOrderHidePO();
    BeanUtils.copyProperties(orderHide, orderHidePO);
    orderHidePO.setCreateTime(getNowTime());
    return  tOrderHideMapper.insertOrUpdate(orderHidePO);
  }
  public int insertOrUpdates(List<TOrderHidePO> orderHides) {
    return tOrderHideMapper.insertOrUpdates(orderHides);
  }
  public Long getNowTime(){
    return  LocalDateTime.now().toInstant(ZoneOffset.ofHours(+8)).toEpochMilli();// tOrderHideMapper.getNowTime().toInstant(ZoneOffset.ofHours(+8)).toEpochMilli();
  }
  public int updateStatus(TOrderHidePO orderHidePO) {
    return tOrderHideMapper.updateById(orderHidePO);
  }
  public TOrderHidePO getOne(String OrderNo,boolean forUpdate){
    LambdaQueryWrapper<TOrderHidePO> qw = Wrappers.lambdaQuery();
    qw.eq(StringUtil.isNotEmpty(OrderNo), TOrderHidePO::getOrderNo, OrderNo);
    qw.last(forUpdate, "for update");
    return this.getOne(qw);
  }
  public  List<TOrderHidePO> queryOrderHideByTime(Long startTime,Long endTime){
    LambdaQueryWrapper<TOrderHidePO> qw = Wrappers.lambdaQuery();
    qw.ge(TOrderHidePO::getCreateTime, startTime);
    qw.le(TOrderHidePO::getCreateTime, endTime);
    return this.list(qw);
  }
  public boolean deleteOrderHideByList(List<String> ids){
    return this.removeBatchByIds(ids);
  }
}
