package com.panda.sport.rcs.mgr.wrapper.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.RcsQuotaBusinessRateMapper;
import com.panda.sport.rcs.mgr.constant.Constants;
import com.panda.sport.rcs.mgr.utils.CommonUtil;
import com.panda.sport.rcs.mgr.wrapper.RcsBusinessRateService;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRate;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessRateDTOReqVo;
import com.panda.sport.rcs.utils.TradeUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author :  gulang
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.bootstrap.controller
 * @Description :  TODO
 * @Date: 2019-11-25 22:17
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsBusinessRateServiceImpl extends ServiceImpl<RcsQuotaBusinessRateMapper, RcsQuotaBusinessRate>
        implements RcsBusinessRateService {

    public static final String logCodeRate="10092";
    @Autowired
    RcsQuotaBusinessRateMapper rcsQuotaBusinessRateMapper;
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public IPage<RcsQuotaBusinessRateDTO> queryListPage(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTOReqVo reqVo) {
        IPage<RcsQuotaBusinessRateDTO> requestPage = new Page<>(pageNum, pageSize);
        IPage<RcsQuotaBusinessRateDTO> iPage = rcsQuotaBusinessRateMapper.queryListPage(requestPage, reqVo);
        return iPage;
    }


    @Override
    public int updateBusinessRate(RcsQuotaBusinessRateDTO dto,RcsQuotaBusinessRateDTO beforDto){
        int result = rcsQuotaBusinessRateMapper.updateBusinessRate(dto);
        if(result>0){
            String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE,dto.getBusinessId());
            String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE,dto.getBusinessId());
            String vrEnableKey = String.format(Constants.VR_ENABLE_AMOUNT_RATE,dto.getBusinessId());
            String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE,dto.getBusinessId());
            String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE,dto.getBusinessId());
            String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE,dto.getBusinessId());
            String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE,dto.getBusinessId());

            redisClient.set(mtsRateKey,dto.getMtsRate());
            redisClient.set(virtualRateKey,dto.getVirtualRate());
            redisClient.set(vrEnableKey,dto.getVrEnable());
            redisClient.set(ctsRateKey,dto.getCtsRate());
            redisClient.set(gtsRateKey,dto.getGtsRate());
            redisClient.set(otsRateKey,dto.getOtsRate());
            redisClient.set(rtsRateKey,dto.getRtsRate());

            setBusinessRateLog(beforDto,dto,Constants.VR_DSHBG,false,true,false);
        }
        return result;
    }

    @Override
    public Map getAllRate(){
        Map<String,String> rateMap = new HashMap();
        String mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        String virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        String vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        String ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        String gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        String otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        String rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        if(mtsRateAll == null){
            redisClient.set(Constants.MTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        }
        if(virtualRateAll == null){
            redisClient.set(Constants.VIRTUAL_AMOUNT_RATE_ALL,new BigDecimal(1));
            virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        }
        if(vrEnableAll == null){
            redisClient.set(Constants.VR_ENABLE_AMOUNT_RATE_ALL,1);
            vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        }

        if(ctsRateAll == null){
            redisClient.set(Constants.CTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        }
        if(gtsRateAll == null){
            redisClient.set(Constants.GTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        }
        if(otsRateAll == null){
            redisClient.set(Constants.OTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        }
        if(rtsRateAll == null){
            redisClient.set(Constants.RTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        }

        rateMap.put("mtsRateAll",mtsRateAll);//mts通用折扣利率
        rateMap.put("virtualRateAll",virtualRateAll);//虚拟通用折扣利率
        rateMap.put("vrEnableAll",vrEnableAll);//虚拟通用折扣利率

        rateMap.put("ctsRateAll",ctsRateAll);//mts通用折扣利率
        rateMap.put("gtsRateAll",gtsRateAll);//虚拟通用折扣利率
        rateMap.put("otsRateAll",otsRateAll);//虚拟通用折扣利率
        rateMap.put("rtsRateAll",rtsRateAll);//虚拟通用折扣利率
        return rateMap;
    }

    @Override
    public void saveAllRate(RcsQuotaBusinessRateDTO dto){

        //before的数据
        RcsQuotaBusinessRateDTO before = new RcsQuotaBusinessRateDTO();
        String mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        String virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        String vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        String ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        String gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        String otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        String rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        if(mtsRateAll == null){
            redisClient.set(Constants.MTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        }
        if(virtualRateAll == null){
            redisClient.set(Constants.VIRTUAL_AMOUNT_RATE_ALL,new BigDecimal(1));
            virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        }
        if(vrEnableAll == null){
            redisClient.set(Constants.VR_ENABLE_AMOUNT_RATE_ALL,1);
            vrEnableAll = redisClient.get(Constants.VR_ENABLE_AMOUNT_RATE_ALL);
        }
        if(ctsRateAll == null){
            redisClient.set(Constants.CTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        }
        if(gtsRateAll == null){
            redisClient.set(Constants.GTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        }
        if(otsRateAll == null){
            redisClient.set(Constants.OTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        }
        if(rtsRateAll == null){
            redisClient.set(Constants.RTS_AMOUNT_RATE_ALL,new BigDecimal(1));
            rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        }
        before.setMtsRate(new BigDecimal(mtsRateAll));
        before.setVirtualRate(new BigDecimal(virtualRateAll));
        before.setVrEnable(Integer.parseInt(vrEnableAll));
        before.setCtsRate(new BigDecimal(ctsRateAll));
        before.setRtsRate(new BigDecimal(rtsRateAll));
        before.setGtsRate(new BigDecimal(gtsRateAll));
        before.setOtsRate(new BigDecimal(otsRateAll));

        redisClient.set(Constants.MTS_AMOUNT_RATE_ALL,dto.getMtsRateAll());
        redisClient.set(Constants.VIRTUAL_AMOUNT_RATE_ALL,dto.getVirtualRateAll());
        redisClient.set(Constants.VR_ENABLE_AMOUNT_RATE_ALL,dto.getVrEnableAll());
        redisClient.set(Constants.CTS_AMOUNT_RATE_ALL,dto.getCtsRateAll());
        redisClient.set(Constants.GTS_AMOUNT_RATE_ALL,dto.getGtsRateAll());
        redisClient.set(Constants.OTS_AMOUNT_RATE_ALL,dto.getOtsRateAll());
        redisClient.set(Constants.RTS_AMOUNT_RATE_ALL,dto.getRtsRateAll());

        //after
        RcsQuotaBusinessRateDTO after = new RcsQuotaBusinessRateDTO();
        after.setMtsRate(dto.getMtsRateAll());
        after.setVirtualRate(dto.getVirtualRateAll());
        after.setVrEnable(dto.getVrEnableAll());
        after.setCtsRate(dto.getCtsRateAll());
        after.setGtsRate(dto.getGtsRateAll());
        after.setOtsRate(dto.getOtsRateAll());
        after.setRtsRate(dto.getRtsRateAll());

        //查询未设置的Rate的数据设置初始化数据
        List<RcsQuotaBusinessRateDTO> noSetMerchantsList =rcsQuotaBusinessRateMapper.queryNoSetRate();
        if(null != noSetMerchantsList && noSetMerchantsList.size() > 0){
            String finalMtsRateAll = mtsRateAll;
            String finalVirtualRateAll = virtualRateAll;
            String finalOtsRateAll = otsRateAll;
            String finalCtsRateAll = ctsRateAll;
            String finalGtsRateAll = gtsRateAll;
            String finalRtsRateAll = rtsRateAll;
            noSetMerchantsList.forEach(item->{
                item.setMtsRate(new BigDecimal(finalMtsRateAll));
                item.setVirtualRate(new BigDecimal(finalVirtualRateAll));
                item.setVrEnable(1);
                item.setCtsRate(new BigDecimal(finalCtsRateAll));
                item.setGtsRate(new BigDecimal(finalGtsRateAll));
                item.setOtsRate(new BigDecimal(finalOtsRateAll));
                item.setRtsRate(new BigDecimal(finalRtsRateAll));
                rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(item);
            });
        }
        setBusinessRateLog(before,after,Constants.VR_TYSZ,false,false,false);

    }

    @Override
    public void batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto){
        if (StringUtils.isNotBlank(dto.getBusIds())){
            String[] busIds = dto.getBusIds().split(",");
            dto.setBusinessIds(Arrays.asList(busIds));

            RcsQuotaBusinessRateDTO beforData =  new RcsQuotaBusinessRateDTO();
            beforData.setBusinessIds(Arrays.asList(busIds));
            List<RcsQuotaBusinessRateDTO> beforeList = rcsQuotaBusinessRateMapper.queryByBusinessIds(beforData);

            int result = rcsQuotaBusinessRateMapper.batchUpdateBusinessRate(dto);
            if(result>0){
                //更新缓存
                for (String ids : dto.getBusinessIds()){
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE,ids);
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE,ids);
                    String vrEnableKey = String.format(Constants.VR_ENABLE_AMOUNT_RATE,ids);
                    String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE,ids);
                    String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE,ids);
                    String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE,ids);
                    String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE,ids);

                    if (dto.getMtsRateAll() != null && !dto.getMtsRateAll().equals("")){
                        redisClient.set(mtsRateKey,dto.getMtsRateAll());
                        dto.setMtsRate(dto.getMtsRateAll());
                    }
                    if (dto.getVirtualRateAll() != null  && !dto.getVirtualRateAll().equals("") ){
                        redisClient.set(virtualRateKey,dto.getVirtualRate());
                        dto.setVirtualRate(dto.getVirtualRateAll());
                    }
                    if (dto.getVrEnableAll() != null  && !dto.getVrEnableAll().equals("")){
                        redisClient.set(vrEnableKey,dto.getVrEnableAll());
                        dto.setVrEnable(dto.getVrEnableAll());
                    }
                    if (dto.getCtsRateAll() != null  && !dto.getCtsRateAll().equals("")){
                        redisClient.set(ctsRateKey,dto.getCtsRateAll());
                        dto.setCtsRate(dto.getCtsRateAll());
                    }
                    if (dto.getGtsRateAll() != null  && !dto.getGtsRateAll().equals("")){
                        redisClient.set(gtsRateKey,dto.getGtsRateAll());
                        dto.setGtsRate(dto.getGtsRateAll());
                    }
                    if (dto.getOtsRateAll() != null  && !dto.getOtsRateAll().equals("")){
                        redisClient.set(otsRateKey,dto.getOtsRateAll());
                        dto.setOtsRate(dto.getOtsRateAll());
                    }
                    if (dto.getRtsRateAll() != null  && !dto.getRtsRateAll().equals("")){
                        redisClient.set(rtsRateKey,dto.getRtsRateAll());
                        dto.setRtsRate(dto.getRtsRateAll());
                    }
                }
                RcsQuotaBusinessRateDTO beforeLog =  new RcsQuotaBusinessRateDTO();
                beforeLog.setBusinessCode(null);
                if (dto.getMtsRateAll() != null && !dto.getMtsRateAll().equals("")){
                    beforeLog.setMtsRateStr(beforeList.stream().map(
                            a->{
                                if(a.getMtsRate() != null){
                                    return a.getMtsRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getVirtualRateAll() != null  && !dto.getVirtualRateAll().equals("") ){
                    beforeLog.setVrEnableStr(beforeList.stream().map(
                            a->{
                                if(a.getVirtualRate() != null){
                                    return a.getVirtualRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getVrEnableAll() != null  && !dto.getVrEnableAll().equals("")){
                    beforeLog.setVrEnableStr(beforeList.stream().map(a->{
                                if(a.getVrEnable() == 1){
                                    return "关";
                                }else{
                                    return "开";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getCtsRateAll() != null  && !dto.getCtsRateAll().equals("")){
                    beforeLog.setCtsRateStr(beforeList.stream().map(
                            a->{
                                if(a.getCtsRate() != null){
                                    return a.getCtsRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getGtsRateAll() != null  && !dto.getGtsRateAll().equals("")){
                    beforeLog.setGtsRateStr(beforeList.stream().map(
                            a->{
                                if(a.getGtsRate() != null){
                                    return a.getGtsRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getOtsRateAll() != null  && !dto.getOtsRateAll().equals("")){
                    beforeLog.setOtsRateStr(beforeList.stream().map(
                            a->{
                                if(a.getOtsRate() != null){
                                    return a.getOtsRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                if (dto.getRtsRateAll() != null  && !dto.getRtsRateAll().equals("")){
                    beforeLog.setRtsRateStr(beforeList.stream().map(
                            a->{
                                if(a.getRtsRate() != null){
                                    return a.getRtsRate().stripTrailingZeros().toString();
                                }else{
                                    return "null";
                                }
                            }
                    ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                }
                dto.setBusinessCode(beforeList.stream().map(RcsQuotaBusinessRateDTO::getBusinessCode).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
                setBusinessRateLog(beforeLog,dto,Constants.VR_PLSZ,true,false,false);
            }
        }
    }

    @Override
    public void batchUpdateVirtualRate(RcsQuotaBusinessRateDTO dto) {
        List<String> idList = new ArrayList<>();
        String[] busIds = dto.getBusIds().split(",");
        idList = Arrays.asList(busIds);
        dto.setBusinessIds(idList);
        RcsQuotaBusinessRateDTO batchLogId = new RcsQuotaBusinessRateDTO();
        batchLogId.setBusinessIds(idList);
        List<RcsQuotaBusinessRateDTO> beforeChangeList  = rcsQuotaBusinessRateMapper.queryByBusinessIds(batchLogId);
        RcsQuotaBusinessRateDTO beforeLog = new RcsQuotaBusinessRateDTO();
        beforeLog.setBusinessCode(null);

        //给数据赋值
        if(dto.getMtsRateAll() != null && !dto.getMtsRateAll().equals("")){
            dto.setMtsRate(dto.getMtsRateAll());
            beforeLog.setMtsRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getMtsRate() != null){
                            return a.getMtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getVirtualRateAll() != null && !dto.getVirtualRateAll().equals("")){
            dto.setVirtualRate(dto.getVirtualRateAll());
            beforeLog.setVirtualRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getVirtualRate() != null){
                            return a.getMtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getVrEnableAll() != null && !dto.getVrEnableAll().equals("")){
            dto.setVrEnable(dto.getVrEnableAll());
            beforeLog.setVrEnableStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getVrEnable() ==1){
                            return "关";
                        }else {
                            return "开";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getCtsRateAll() != null && !dto.getCtsRateAll().equals("")){
            dto.setCtsRate(dto.getCtsRateAll());
            beforeLog.setCtsRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getCtsRate() != null){
                            return a.getCtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getGtsRateAll() != null && !dto.getGtsRateAll().equals("")){
            dto.setGtsRate(dto.getGtsRateAll());
            beforeLog.setGtsRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getGtsRate() != null){
                            return a.getGtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getOtsRateAll() != null && !dto.getOtsRateAll().equals("")){
            dto.setOtsRate(dto.getOtsRateAll());
            beforeLog.setOtsRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getOtsRate() != null){
                            return a.getOtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        if(dto.getRtsRateAll() != null && !dto.getRtsRateAll().equals("")){
            dto.setRtsRate(dto.getRtsRateAll());
            beforeLog.setRtsRateStr(beforeChangeList.stream().map(
                    a->{
                        if(a.getRtsRate() != null){
                            return a.getRtsRate().stripTrailingZeros().toString();
                        }else{
                            return "null";
                        }
                    }
            ).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
        }
        //批量更新例外设置
        int result = rcsQuotaBusinessRateMapper.batchUpdateVirtualRate(dto);
        if (result > 0){
            dto.setBusinessCode(beforeChangeList.stream().map(RcsQuotaBusinessRateDTO::getBusinessCode).collect(Collectors.toList()).stream().collect(Collectors.joining(",")));
            setBusinessRateLog(beforeLog,dto,Constants.VR_LWPLSZ,true,false,true);
        }

//        //查询未设置的Rate的数据设置初始化数据
//        List<RcsQuotaBusinessRateDTO> noSetMerchantsList =rcsQuotaBusinessRateMapper.queryNoSetRate();
//        if(null != noSetMerchantsList && noSetMerchantsList.size() > 0){
//            noSetMerchantsList.forEach(item->{
////                RcsQuotaBusinessRateDTO oneBefore = new RcsQuotaBusinessRateDTO();
////                oneBefore = item;
//                item.setMtsRate(new BigDecimal(1));
//                item.setVirtualRate(new BigDecimal(1));
//                item.setVrEnable(1);
//                item.setCtsRate(new BigDecimal(1));
//                item.setGtsRate(new BigDecimal(1));
//                item.setOtsRate(new BigDecimal(1));
//                item.setRtsRate(new BigDecimal(1));
//                rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(item);
//
////                RcsQuotaBusinessRateDTO oneAfter = new RcsQuotaBusinessRateDTO();
////                oneAfter = item;
////                setBusinessRateLog(oneBefore,oneAfter,Constants.VR_LWPLSZ,true);
//            });
//        }


//        int result = rcsQuotaBusinessRateMapper.batchUpdateVirtualRate(dto);
//            if (result > 0){
//                RcsQuotaBusinessRateDTO beforeLog = new RcsQuotaBusinessRateDTO();
//
//                setBusinessRateLog(changeBefore,dto,Constants.VR_LWPLSZ,true,false);

//                List<RcsQuotaBusinessRateDTO>  merchantsList=rcsQuotaBusinessRateMapper.selectMerchantsList(dto);
//                //更新缓存
//                merchantsList.forEach(item->{
//                    String ctsRate = String.format(Constants.CTS_AMOUNT_RATE,item.getBusinessId());
//                    String gtsRate = String.format(Constants.GTS_AMOUNT_RATE,item.getBusinessId());
//                    String mtsRate = String.format(Constants.MTS_AMOUNT_RATE,item.getBusinessId());
//                    String otsRate = String.format(Constants.OTS_AMOUNT_RATE,item.getBusinessId());
//                    String rtsRate = String.format(Constants.RTS_AMOUNT_RATE,item.getBusinessId());
//                    String virtualRate = String.format(Constants.VIRTUAL_AMOUNT_RATE,item.getBusinessId());
//                    String vrEnable = String.format(Constants.VR_ENABLE_AMOUNT_RATE,item.getBusinessId());
//                    if (dto.getCtsRateAll() != null){
//                        redisClient.set(ctsRate,dto.getCtsRateAll());
//                    }
//                    if (dto.getGtsRateAll() != null){
//                        redisClient.set(gtsRate,dto.getGtsRateAll());
//                    }
//                    if (dto.getMtsRateAll() != null){
//                        redisClient.set(mtsRate,dto.getMtsRateAll());
//                    }
//                    if (dto.getOtsRateAll() != null){
//                        redisClient.set(otsRate,dto.getOtsRateAll());
//                    }
//                    if (dto.getRtsRateAll() != null){
//                        redisClient.set(rtsRate,dto.getRtsRateAll());
//                    }
//                    if (dto.getVirtualRateAll() != null){
//                        redisClient.set(virtualRate,dto.getVirtualRateAll());
//                    }
//                    if (dto.getVrEnableAll() != null){
//                        redisClient.set(vrEnable,dto.getVrEnableAll());
//                    }
//                });
//                for(RcsQuotaBusinessRateDTO changeBefore: beforeChangeList){
//                    setBusinessRateLog(changeBefore,dto,Constants.VR_LWPLSZ,true,false);
//                }
//                log.info(String.format("batchUpdateVirtualRate merchantsList count:%s",merchantsList.size()));

//            }
    }

    @Override
    public List<RcsQuotaBusinessRateDTO> queryByBusinessId(RcsQuotaBusinessRateDTO dto) {
        return  rcsQuotaBusinessRateMapper.queryByBusinessId(dto);
    }

    @Override
    public List<RcsQuotaBusinessRateDTO> queryByBusinessIds(RcsQuotaBusinessRateDTO dto) {
        return  rcsQuotaBusinessRateMapper.queryByBusinessIds(dto);
    }

    @Override
    public void initBusinessRate(){
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCount();
        log.info("商户折扣利率数据初始化，总数数据totalSize：{}",totalSize);
        int totlePage = totalSize%pageSize == 0 ? totalSize/pageSize : totalSize/pageSize + 1;
        for (int i = 0; i<totlePage; i++){
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPage(offset,pageSize);
            if (list != null && list.size() > 0){
                for (RcsQuotaBusinessRateDTO dto : list){
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE,dto.getBusinessId());
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE,dto.getBusinessId());
                    String vrEnableKey = String.format(Constants.VR_ENABLE_AMOUNT_RATE,dto.getBusinessId());
                    String mtsRate = redisClient.get(mtsRateKey);
                    String virtualRate = redisClient.get(virtualRateKey);
                    String vrEnable = redisClient.get(vrEnableKey);
                    if (StringUtils.isNotBlank(mtsRate)){
                        dto.setMtsRate(new BigDecimal(mtsRate));
                    }else {
                        dto.setMtsRate(new BigDecimal(1));
                    }
                    if (StringUtils.isNotBlank(virtualRate)){
                        dto.setVirtualRate(new BigDecimal(virtualRate));
                    }else {
                        dto.setVirtualRate(new BigDecimal(1));
                    }
                    if (StringUtils.isNotBlank(vrEnable)){
                        dto.setVrEnable(Integer.parseInt(vrEnable));
                    }else {
                        dto.setVrEnable(1);
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
                }
            }
            pageNo++;
            log.info("商户折扣利率数据初始化，offset：{}",offset);
        }
        log.info("商户折扣利率数据初始化完成");
    }

    @Override
    public void initRedisBusinessRate() {
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCount();
        log.info("initRedisBusinessRate商户折扣利率数据初始化，总数数据totalSize：{}", totalSize);
        int totlePage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
        for (int i = 0; i < totlePage; i++) {
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPage(offset, pageSize);
            if (list != null && list.size() > 0) {
                for (RcsQuotaBusinessRateDTO dto : list) {
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, dto.getBusinessId());
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, dto.getBusinessId());
                    String vrEnableKey = String.format(Constants.VR_ENABLE_AMOUNT_RATE, dto.getBusinessId());
                    if (dto.getMtsRate() != null) {
                        redisClient.set(mtsRateKey, dto.getMtsRate().toString());
                    }else {
                        redisClient.set(mtsRateKey, "1");
                        dto.setMtsRate(new BigDecimal("1"));
                    }
                    if (dto.getVirtualRate() != null) {
                        redisClient.set(virtualRateKey, dto.getVirtualRate().toString());
                    }else {
                        redisClient.set(virtualRateKey, "0.28");
                        dto.setVirtualRate(new BigDecimal("0.28"));
                    }
                    if (dto.getVrEnable() != null) {
                        redisClient.set(vrEnableKey, dto.getVirtualRate().toString());
                    }else {
                        redisClient.set(vrEnableKey, String.valueOf(1));
                        dto.setVrEnable(1);
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
                }
            }
            pageNo++;
            log.info("initRedisBusinessRate商户折扣利率数据初始化，offset：{}", offset);
        }
        log.info("initRedisBusinessRate商户折扣利率数据初始化完成");
    }

    /**
     *
     * @param beforeDto 操作之前对象
     * @param afterDto  操作之后对象
     * @param operateType 操作类型（默认设置 批量设置 例外批量设置 单商户变更）
     */
    private void setBusinessRateLog(RcsQuotaBusinessRateDTO beforeDto,RcsQuotaBusinessRateDTO afterDto,String operateType,Boolean bruBatch,Boolean bruSigle,Boolean other) {
        String userId = null;
        try{
            userId= TradeUserUtils.getUserId().toString();
        }catch (Exception e){
            log.error("::{}::查询操作用户错误{}",CommonUtil.getRequestId(), e.getMessage(), e);
        }
        List<RcsQuotaBusinessLimitLog> logList = new ArrayList<>();
        if(afterDto.getBusinessCode() != null && bruBatch==true){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName(other == true ? "例外商户Code" : "商户Code");
            limitLoglog.setBeforeVal(null);
            limitLoglog.setAfterVal(afterDto.getBusinessCode());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        if((afterDto.getMtsRate() != null && beforeDto.getMtsRate() != null && afterDto.getMtsRate().compareTo(beforeDto.getMtsRate())!=0) || (afterDto.getMtsRate() != null && bruBatch == true)){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("MTS折扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getMtsRateStr());
            }else{
                limitLoglog.setBeforeVal((beforeDto.getMtsRate() ==null ? null : beforeDto.getMtsRate().stripTrailingZeros().toString()));
            }
            limitLoglog.setAfterVal(afterDto.getMtsRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }

        if((afterDto.getVirtualRate() != null && beforeDto.getVirtualRate() != null && afterDto.getVirtualRate().compareTo(beforeDto.getVirtualRate()) != 0) || (afterDto.getVirtualRate() != null && bruBatch == true)){
//        if(afterDto.getVirtualRate() != null && !beforeDto.getVirtualRate().equals(afterDto.getVirtualRate())){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("虚拟折扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getVirtualRateStr());
            }else{
                limitLoglog.setBeforeVal((beforeDto.getVirtualRate() ==null ? null : beforeDto.getVirtualRate().stripTrailingZeros().toString()));
            }
            limitLoglog.setAfterVal(afterDto.getVirtualRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }

        if((afterDto.getVrEnable() != null && beforeDto.getVrEnable() != null && !afterDto.getVrEnable().equals(beforeDto.getVrEnable())) || (afterDto.getVrEnable() != null && bruBatch == true)){
//        if(afterDto.getVrEnable() != null && !beforeDto.getVrEnable().equals(afterDto.getVrEnable())){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("虚拟藏单开关");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getVrEnableStr());
            }else{
                if(beforeDto.getVrEnable().toString().equals("1")){
                    limitLoglog.setBeforeVal("关");
                }
                if(beforeDto.getVrEnable().toString().equals("2")){
                    limitLoglog.setBeforeVal("开");
                }
            }
            if(afterDto.getVrEnable().toString().equals("1")){
                limitLoglog.setAfterVal("关");
            }
            if(afterDto.getVrEnable().toString().equals("2")){
                limitLoglog.setAfterVal("开");
            }
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        //以下四个
        if((afterDto.getCtsRate() != null && beforeDto.getCtsRate() != null && afterDto.getCtsRate().compareTo(beforeDto.getCtsRate())!=0) || (afterDto.getCtsRate() != null && bruBatch == true)){
//        if((afterDto.getCtsRate() != null && beforeDto.getCtsRate() != null && !beforeDto.getCtsRate().equals(afterDto.getCtsRate())) || (afterDto.getCtsRate() != null && beforeDto.getCtsRate()==null) ){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("CST扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getCtsRateStr());
            }else{
                limitLoglog.setBeforeVal(beforeDto.getCtsRate() ==null ? null : beforeDto.getCtsRate().stripTrailingZeros().toString());
            }
            limitLoglog.setAfterVal(afterDto.getCtsRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        if((afterDto.getGtsRate() != null && beforeDto.getGtsRate() != null && afterDto.getGtsRate().compareTo(beforeDto.getGtsRate())!=0) || (afterDto.getGtsRate() != null && bruBatch == true)){
//        if((afterDto.getGtsRate() != null && beforeDto.getGtsRate() != null && !beforeDto.getGtsRate().equals(afterDto.getGtsRate())) || (afterDto.getCtsRate() != null && beforeDto.getCtsRate()==null)  ){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("GST扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getGtsRateStr());
            }else{
                limitLoglog.setBeforeVal(beforeDto.getGtsRate() ==null ? null : beforeDto.getGtsRate().stripTrailingZeros().toString());
            }
            limitLoglog.setAfterVal(afterDto.getGtsRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        if((afterDto.getOtsRate() != null && beforeDto.getOtsRate() != null && afterDto.getOtsRate().compareTo(beforeDto.getOtsRate())!=0) || (afterDto.getOtsRate() != null && bruBatch == true)){
//        if((afterDto.getOtsRate() != null && beforeDto.getOtsRate() != null  && !beforeDto.getOtsRate().equals(afterDto.getOtsRate())) || (afterDto.getOtsRate() != null && beforeDto.getOtsRate()==null)  ){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("OTS扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getOtsRateStr());
            }else{
                limitLoglog.setBeforeVal((beforeDto.getOtsRate() ==null ? null : beforeDto.getOtsRate().stripTrailingZeros().toString()));
            }
            limitLoglog.setAfterVal(afterDto.getOtsRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        if((afterDto.getRtsRate() != null && beforeDto.getRtsRate() != null && afterDto.getRtsRate().compareTo(beforeDto.getRtsRate())!=0) || (afterDto.getRtsRate() != null && bruBatch == true)){
//        if((afterDto.getRtsRate() != null && beforeDto.getRtsRate() != null  && !beforeDto.getRtsRate().equals(afterDto.getRtsRate())) || (afterDto.getRtsRate() != null && beforeDto.getRtsRate()==null)  ){
            RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
            limitLoglog.setOperateCategory("系统设置");
            limitLoglog.setObjectId(bruSigle==true ? beforeDto.getBusinessCode() : null);
            limitLoglog.setObjectName(null);
            limitLoglog.setExtObjectId("-");
            limitLoglog.setExtObjectName("-");
            limitLoglog.setOperateType(operateType);
            limitLoglog.setParamName("RTS扣利率");
            if(bruBatch == true){
                limitLoglog.setBeforeVal(beforeDto.getRtsRateStr());
            }else{
                limitLoglog.setBeforeVal((beforeDto.getRtsRate() ==null ? null : beforeDto.getRtsRate().stripTrailingZeros().toString()));
            }
            limitLoglog.setAfterVal(afterDto.getRtsRate().toString());
            limitLoglog.setUserId(userId);
            logList.add(limitLoglog);
        }
        if(logList != null && logList.size()>0){
            producerSendMessageUtils.sendMessage(CommonUtil.RCS_BUSINESS_LOG_SAVE, null, logCodeRate, JSONObject.toJSONString(logList));
        }
    }


}
