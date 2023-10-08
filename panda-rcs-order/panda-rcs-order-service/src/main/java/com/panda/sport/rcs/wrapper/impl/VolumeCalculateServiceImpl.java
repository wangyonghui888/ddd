//package com.panda.sport.rcs.wrapper.impl;
//
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.panda.sport.rcs.constants.RcsConstant;
//import com.panda.sport.rcs.core.cache.client.RedisClient;
//import com.panda.sport.rcs.mapper.RcsLabelSportVolumePercentageMapper;
//import com.panda.sport.rcs.pojo.AmountTypeVo;
//import com.panda.sport.rcs.pojo.RcsLabelSportVolumePercentage;
//import com.panda.sport.rcs.pojo.dto.VolumeDTO;
//import com.panda.sport.rcs.pojo.enums.OrderHideCategoryEnum;
//import com.panda.sport.rcs.pojo.enums.SpecialEnum;
//import com.panda.sport.rcs.wrapper.VolumeCalculateService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.Objects;
//
///**
// * 计算货量服务
// *
// * @author skyKong
// */
//@Slf4j
//@Service
//public class VolumeCalculateServiceImpl implements VolumeCalculateService {
//    @Autowired
//    RedisClient redisClient;
//
//    @Value("${device.volume.percentage.pc:1}")
//    private  String device_volume_pc;
//
//    @Value("${device.volume.percentage.h5:0.6}")
//    private  String device_volume_h5;
//
//    @Value("${device.volume.percentage.app:0.3}")
//    private  String device_volume_app;
//
//    private final String RISK_DYNAMIC_CONFIG_SWITCH = "rcs:trade:bet:volume:config";
//
//    private final String  CONFIG_STATUS = "status";
//
//    private final String  CONFIG_SPORTIDS = "sportIds";
//    @Autowired
//    private RcsLabelSportVolumePercentageMapper labelSportVolumePercentageMapper;
//    /**
//     * 计算货量
//     * */
//   public AmountTypeVo getVolumePercentage(VolumeDTO volumeDTO){
//       if(Objects.isNull(volumeDTO)){
//           log.warn("volumeDTO 实体为空值不需要处理");
//           return null;
//       }
//       log.info("::{}::获取货量开始{}", volumeDTO.getOrderNo(),JSONObject.toJSONString(volumeDTO));
//       String volumePercentageKey =String.format(RcsConstant.RCS_ORDER_VOLUME_PERCENTAGE,volumeDTO.getOrderNo());
//       AmountTypeVo amountTypeVo = new AmountTypeVo();
//       String volumePercentageRedis = redisClient.get(volumePercentageKey);
//       if (!StringUtils.isBlank(volumePercentageRedis)) {
//           log.warn("::{}::直接从缓存返回货量:{}", volumeDTO.getOrderNo(), volumePercentageRedis);
//           amountTypeVo = JSONObject.parseObject(volumePercentageRedis, AmountTypeVo.class);
//           return amountTypeVo;
//       }
//       //为了避免外面逻辑报错，则设置一个默认值用于返回
//       BigDecimal volumePercentage = new BigDecimal("1");
//       //优先判断特殊赛种货量百分比，有则返回，没有则往下走判断商户限额模式
//      // orderBean.setSportId(orderBean.getItems().get(0).getSportId());
//       String userPercentageBetKey = "risk:trade:rcs_user_sport_type_bet_limit_config:" + volumeDTO.getUid();
//       String itemKey = String.valueOf(volumeDTO.getSportId());
//       String betAmountPercentage = redisClient.hGet(userPercentageBetKey, itemKey);
//       log.info("::{}::用户货量key:{} 用户货量value:{}  sportId:{} VIP类型:{}",
//               volumeDTO.getOrderNo(), userPercentageBetKey,betAmountPercentage, itemKey,volumeDTO.getVipLevel());
//       //判断当前赛种是否有设置当前货量百分比
//       if (StringUtils.isNotBlank(betAmountPercentage)) {
//           volumePercentage = new BigDecimal(betAmountPercentage).divide(RcsConstant.BASE, 2, BigDecimal.ROUND_DOWN);
//           log.info("::{}::getVolumePercentage::获取货量返回赛种:,{} betAmountPercentage {}", volumeDTO.getOrderNo(), volumePercentage,betAmountPercentage);
//           amountTypeVo.setVolumePercentage(volumePercentage);
//           amountTypeVo.setSpecial(SpecialEnum.Special.getId());
//           amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
//           redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 10 * 60L);
//           return amountTypeVo;
//       } else {
//           //判断是否有设置全局货量百分比（全部赛种 sportId = 0）
//           itemKey = "0";
//           betAmountPercentage = redisClient.hGet(userPercentageBetKey, itemKey);
//           if (StringUtils.isNotBlank(betAmountPercentage)) {
//               volumePercentage = new BigDecimal(betAmountPercentage).divide(RcsConstant.BASE, 2, BigDecimal.ROUND_DOWN);
//               log.info("::{}::获取货量返回赛种全局 {}", volumeDTO.getOrderNo(), volumePercentage);
//               amountTypeVo.setVolumePercentage(volumePercentage);
//               amountTypeVo.setSpecial(SpecialEnum.Special.getId());
//               amountTypeVo.setCategory(OrderHideCategoryEnum.USER.getId());
//               redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 15 * 60L);
//               return amountTypeVo;
//           }
//       }
//       //用户标签货量百分比获取
//       RcsLabelSportVolumePercentage labelLimitConfig=setLabelLimitConfig(volumeDTO.getUserTagLevel(),volumeDTO.getSportId());
//       log.info("::{}::用户标签查询 userTagLevel:{} sportId:{} 返回 labelLimitConfig:{}",
//               volumeDTO.getOrderNo(), volumeDTO.getUserTagLevel(),volumeDTO.getSportId(),JSONObject.toJSONString(labelLimitConfig));
//       //货量百分比 仅用于  特殊限额管控中未设置“特殊VIP限额”的用户。
//       if (volumeDTO.getVipLevel() != 1 && Objects.nonNull(labelLimitConfig) && null !=labelLimitConfig.getVolumePercentage()) {
//           volumePercentage = labelLimitConfig.getVolumePercentage().divide(RcsConstant.BASE);
//           log.info("::{}::获取货量返回标签:{}", volumeDTO.getOrderNo(), volumePercentage);
//           amountTypeVo.setVolumePercentage(volumePercentage);
//           amountTypeVo.setSpecial(SpecialEnum.Special.getId());
//           amountTypeVo.setCategory(OrderHideCategoryEnum.LABEL.getId());
//           redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 15 * 60L);
//           return amountTypeVo;
//       }
//       // 优先取商户特殊货量百分比配置
//       String businessBetPercentKey = RcsConstant.RCS_TRADE_BUSINESS_BET_PERCENT;
//       String businessBetPercentValue = redisClient.hGet(businessBetPercentKey, volumeDTO.getTenantId().toString());
//       String dynamicConfigSwitchValue = redisClient.hGet(RISK_DYNAMIC_CONFIG_SWITCH,CONFIG_STATUS);
//       String sportIdsSwitchValue = redisClient.hGet(RISK_DYNAMIC_CONFIG_SWITCH,CONFIG_SPORTIDS);
//       log.info("::{}::动态全局开关返回:{} 动态全局赛种返回:{} 商户货量Key:{} 商户货量返回值:{}"
//               ,volumeDTO.getOrderNo(), dynamicConfigSwitchValue,sportIdsSwitchValue,businessBetPercentKey,businessBetPercentValue);
//       if (StringUtils.isNotBlank(businessBetPercentValue)) {
//           volumePercentage = new BigDecimal(businessBetPercentValue);
//           AmountTypeVo amountType=setDynamicVolume(dynamicConfigSwitchValue,sportIdsSwitchValue,volumePercentageKey,volumePercentage,volumeDTO);
//           if(Objects.nonNull(amountType))  {
//               log.info("::{}::有商户货量动态有开关返回百分比:{}", volumeDTO.getOrderNo(),amountType.getVolumePercentage());
//               return amountType;
//           }
//           amountTypeVo.setVolumePercentage(volumePercentage);
//           amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
//           amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
//           redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 15 * 60L);
//           log.info("::{}::有商户货量返回百分比:{}", volumeDTO.getOrderNo(), volumePercentage);
//           return amountTypeVo;
//       }
//       AmountTypeVo amountType=setDynamicVolume(dynamicConfigSwitchValue,sportIdsSwitchValue,volumePercentageKey,volumePercentage,volumeDTO);
//       if(Objects.nonNull(amountType))  {
//           log.info("::{}::无商户货量动态开关百分比:{}", volumeDTO.getOrderNo(), volumePercentage);
//           return amountType;
//       }
//       amountTypeVo.setVolumePercentage(volumePercentage);
//       amountTypeVo.setSpecial(SpecialEnum.ordinary.getId());
//       amountTypeVo.setCategory(OrderHideCategoryEnum.MERCHANT.getId());
//       log.info("::{}::无商户货量返回默认:{}", volumeDTO.getOrderNo(), volumePercentage);
//       redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 15 * 60L);
//       return amountTypeVo;
//   }
//    private  AmountTypeVo setDynamicVolume(String userConfigSwitch, String sportIdsSwitch, String volumePercentageKey, BigDecimal volumePercentage, VolumeDTO volumeDTO){
//        if(StringUtils.isBlank(userConfigSwitch) || StringUtils.isBlank(sportIdsSwitch)){
//            log.warn("::{}::风控用户开关没有设置 userConfigSwitch:{}  sportIdsSwitch:{}",volumeDTO.getOrderNo(),userConfigSwitch,sportIdsSwitch);
//            return  null;
//        }
//        if(Objects.isNull(volumeDTO.getTenantId())){
//            log.warn("::{}::商户ID为空",volumeDTO.getOrderNo());
//            return  null;
//        }
//        String merchantSwitchKey=RcsConstant.RCS_RISK_BET_VOLUME_SWITCH + volumeDTO.getTenantId();
//        String merchantSwitchValue = redisClient.get(merchantSwitchKey);
//        log.info("::{}:: dynamicVolume :: merchantSwitchKey:{},merchantSwitchValue:{}",volumeDTO.getOrderNo(),merchantSwitchKey,merchantSwitchValue);
//        if(!StringUtils.isBlank(merchantSwitchValue) && "0".equals(merchantSwitchValue)){
//            log.warn("::{}:: dynamicVolume 商户:{} 没有对应的商户开关或是开关已关闭",volumeDTO.getOrderNo(),merchantSwitchKey);
//            return  null;
//        }
//        if("1".equals(userConfigSwitch) && sportIdsSwitch.contains(volumeDTO.getSportId().toString())){
//            log.info("::{}:: dynamicVolume 藏单开关:{}", volumeDTO.getOrderNo(), userConfigSwitch);
//            String stringHideVolume=String.format(RcsConstant.RCS_DYNAMIC_HIDE_ORDER_RATE,volumeDTO.getUid(),volumeDTO.getSportId());
//            String hideVolume = redisClient.get(stringHideVolume);
//            log.info("::{}:: dynamicVolume hideVolumeKey{} hideVolume value:{}",volumeDTO.getOrderNo(),stringHideVolume, hideVolume);
//            if(StringUtils.isBlank(hideVolume)){
//                hideVolume="0";
//            }
//            BigDecimal deviceMinVolumePercentage=getMinVolumeByDeviceType(volumeDTO.getDeviceType());
//            BigDecimal hideVolumePercentage = new BigDecimal(hideVolume);
//            BigDecimal bigHideVolume;
//            BigDecimal subHideVolume=new BigDecimal("1").subtract(hideVolumePercentage);
//            int flag = subHideVolume.compareTo(deviceMinVolumePercentage);
//            if(flag==-1){
//                bigHideVolume = deviceMinVolumePercentage;
//            }else {
//                bigHideVolume=subHideVolume;
//            }
//            log.info("::{}:: dynamicVolume 商户货量值{} 相减后藏单值:{}",volumeDTO.getOrderNo(),volumePercentage,bigHideVolume);
//            volumePercentage=volumePercentage.multiply(bigHideVolume).setScale(2,BigDecimal.ROUND_DOWN);
//            AmountTypeVo amountTypeVo=new AmountTypeVo();
//            amountTypeVo.setVolumePercentage(volumePercentage);
//            amountTypeVo.setSpecial(SpecialEnum.Special.getId());
//            amountTypeVo.setCategory(OrderHideCategoryEnum.DYNAMIC.getId());
//            log.info("::{}:: dynamicVolume 动态开关返回:{}", volumeDTO.getOrderNo(), JSONObject.toJSONString(amountTypeVo));
//            redisClient.setExpiry(volumePercentageKey, JSONObject.toJSONString(amountTypeVo), 15 * 60L);
//            return amountTypeVo;
//        }
//        return  null;
//    }
//    private    RcsLabelSportVolumePercentage setLabelLimitConfig(Integer userTagLevel,Integer sportId){
//        String userTagLevelKey =String.format("risk:user:tag:level:%s",userTagLevel.toString()+sportId.toString());
//        String userTagLevelValue=redisClient.get(userTagLevelKey);
//        if(!StringUtils.isBlank(userTagLevelValue)){
//            return JSONObject.parseObject(userTagLevelValue,RcsLabelSportVolumePercentage.class);
//        }
//        RcsLabelSportVolumePercentage rcsLabelSportVolumePercentage =getLabelLimitConfig(userTagLevel,sportId);
//        if (Objects.isNull(rcsLabelSportVolumePercentage)) {
//            rcsLabelSportVolumePercentage=getLabelLimitConfig(userTagLevel,0);
//        }
//        if(Objects.nonNull(rcsLabelSportVolumePercentage)){
//            redisClient.setExpiry(userTagLevelKey, JSONObject.toJSONString(rcsLabelSportVolumePercentage), 15 * 60L);
//        }
//        return  rcsLabelSportVolumePercentage;
//    }
//    private  RcsLabelSportVolumePercentage getLabelLimitConfig(Integer userTagLevel,Integer sportId){
//        LambdaQueryWrapper<RcsLabelSportVolumePercentage> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(RcsLabelSportVolumePercentage::getTagId, userTagLevel);
//        wrapper.eq(RcsLabelSportVolumePercentage::getSportId, sportId);
//        return labelSportVolumePercentageMapper.selectOne(wrapper);
//    }
//    public BigDecimal getMinVolumeByDeviceType(Integer deviceType) {
//        BigDecimal volumePercentage;
//        switch (deviceType){
//            case 1:  // 1h5
//                volumePercentage=new BigDecimal(device_volume_h5);
//                break;
//            case 2: // 2pc
//                volumePercentage=new BigDecimal(device_volume_pc);
//                break;
//            default: // 3app
//                volumePercentage=new BigDecimal(device_volume_app);
//                break;
//        }
//        return  volumePercentage;
//    }
//}
