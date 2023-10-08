package com.panda.sport.rcs.mgr.service.orderhide.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.enums.OrderHideCategoryEnum;
import com.panda.sport.rcs.pojo.enums.SpecialEnum;
import com.panda.sport.rcs.mgr.mq.bean.HideOrderDTO;
import com.panda.sport.rcs.mgr.service.orderhide.ITOrderHideService;
import com.panda.sport.rcs.mgr.utils.BaseUtil;
import com.panda.sport.rcs.mgr.utils.ThreadUtil;
import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.TOrderHide;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 藏单服务实现类
 * </p>
 *
 * @author skyKong
 */
@Service
@Component
@Slf4j
public class RcsOrderHideServiceImpl  {

  @Autowired
  ITOrderHideService itOrderHideService;

  @Autowired
  RedisClient redisClient;
  /**
   * @param hideOrderDTO 藏单实体
   * 处理藏单异步接口
   * */
  public void doHideOrderHand(HideOrderDTO hideOrderDTO) {
    if(hideOrderDTO==null){
      return;
    }
    ThreadUtil.submit(() -> {
      Thread.currentThread().setName("doHideOrderHand" + hideOrderDTO.getOrderNo());
      log.info("doHideOrderHand-待处理长度：{}", ThreadUtil.size());
       try{
         doWork(hideOrderDTO);
       }catch (Exception ex){
         log.error(" ::doHideOrderHand{}::  error {}", hideOrderDTO.getOrderNo(),ex.getMessage());
       }
    });
  }
  private void doWork(HideOrderDTO hideOrderDTO){
    long stat =System.currentTimeMillis();
    log.info("::{}:: doWork hideOrderDTO ：{}", hideOrderDTO.getOrderNo(), JSONObject.toJSONString(hideOrderDTO));
    String hideOrderKey =String.format("rcs:order:hide:%s",hideOrderDTO.getOrderNo());
    String hideOrderKeyRedis = redisClient.get(hideOrderKey);
    if(null !=hideOrderKeyRedis){
      log.info("::{}:: doWork 缓存返回 ：{}",hideOrderDTO.getOrderNo(),hideOrderKeyRedis);
      return;
    }
    if(SpecialEnum.Special.getId().equals(hideOrderDTO.getAmountTypeVo().getSpecial())){
        saveSpecial(hideOrderDTO.getOrderNo(),hideOrderDTO.getAmountTypeVo(),hideOrderDTO.getVipLeve());
    } else {
        saveOrdinary(hideOrderDTO.getOrderNo(), hideOrderDTO.getAmountTypeVo(),hideOrderDTO.getDeviceType(), hideOrderDTO.getVolumePercentage());
    }
    redisClient.setExpiry(hideOrderKey, hideOrderDTO.getOrderNo(), 10 * 60L);
    log.info("::{}:: doWork hideOrderDTO {} times {}", hideOrderDTO.getOrderNo(), JSONObject.toJSONString(hideOrderDTO),System.currentTimeMillis()-stat);
  }
  /**
   * 特殊设置货量保存
   * */
  private void saveSpecial(String orderNo, AmountTypeVo amountTypeVo, Integer vipLeve){
    int rows;
    TOrderHide orderHide;
    if(amountTypeVo.getVolumePercentage().compareTo(new BigDecimal(1))==0){
      log.warn(" ::saveSpecial {}:: volumePercentage  {} 不需要保存",orderNo,amountTypeVo.getVolumePercentage());
      return;
    }
    if(1==vipLeve){
      orderHide = setTOrderHide(orderNo, amountTypeVo);
    }else{
      orderHide = setTOrderHideSpecial(orderNo, amountTypeVo);
    }
    rows=itOrderHideService.insertOrUpdate(orderHide);
    log.info(" ::saveSpecial {}::  save {}",orderNo,rows);
  }
  private TOrderHide setTOrderHide(String orderNo,AmountTypeVo amountTypeVo) {
    TOrderHide orderHide=new TOrderHide();
    orderHide.setOrderNo(orderNo);
    orderHide.setVolumePercentage(BigDecimal.ZERO);
    orderHide.setCategory(amountTypeVo.getCategory());
    return orderHide;
  }
  private TOrderHide setTOrderHideSpecial(String orderNo,AmountTypeVo amountTypeVo) {
    TOrderHide orderHide=new TOrderHide();
    orderHide.setOrderNo(orderNo);
    orderHide.setVolumePercentage(amountTypeVo.getVolumePercentage());
    orderHide.setCategory(amountTypeVo.getCategory());
    return orderHide;
  }
  /**
   * 普通货量保存
   * */
  private void saveOrdinary(String orderNo,AmountTypeVo amountTypeVo,Integer deviceType,BigDecimal volumePercentage){
    if(volumePercentage.compareTo(new BigDecimal(1)) == 0) {
      amountTypeVo.setCategory(OrderHideCategoryEnum.EQUIPMENT.getId());
    }
    volumePercentage= volumePercentage.multiply(CommonUtils.getVolumeByDeviceType(deviceType));
    TOrderHide orderHide=setTOrderHide(orderNo,amountTypeVo,volumePercentage);
    int rows = itOrderHideService.insertOrUpdate(orderHide);
    log.info("::saveOrdinary {}::  result {}",orderNo,rows);
  }
  /**
   * 特殊设置
   * */
  private TOrderHide setTOrderHide(String orderNo,AmountTypeVo amountTypeVo,BigDecimal volumePercentage) {
    TOrderHide orderHide=new TOrderHide();
    orderHide.setOrderNo(orderNo);
    orderHide.setVolumePercentage(volumePercentage);
    orderHide.setCategory(amountTypeVo.getCategory());
    return orderHide;
  }
}
