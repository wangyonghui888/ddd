package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.common.Constants;
import com.panda.sport.rcs.console.dao.RcsQuotaBusinessRateMapper;
import com.panda.sport.rcs.console.dto.RcsQuotaBusinessRateDTO;
import com.panda.sport.rcs.console.pojo.RcsQuotaBusinessRateExcelVO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsBusinessRateService;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eamon
 */
@Service
@Slf4j
public class RcsBusinessRateServiceImpl implements RcsBusinessRateService {
    @Autowired
    RcsQuotaBusinessRateMapper rcsQuotaBusinessRateMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public PageDataResult listPage(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTO dto) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.listPage(dto);
        if (list != null && list.size() > 0) {
            PageInfo<RcsQuotaBusinessRateDTO> pageInfo = new PageInfo<>(list);
            pageDataResult.setList(list);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

    @Override
    public void updateBusinessRate(RcsQuotaBusinessRateDTO dto) {
        rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
        String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, dto.getBusinessId());
        String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE, dto.getBusinessId());
        String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE, dto.getBusinessId());
        String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE, dto.getBusinessId());
        String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE, dto.getBusinessId());
        String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, dto.getBusinessId());
        if (dto.getMtsRate() == null) {
            redisClient.delete(mtsRateKey);
        } else {
            redisClient.set(mtsRateKey, dto.getMtsRate());
        }
        if (dto.getCtsRate() == null) {
            redisClient.delete(ctsRateKey);
        } else {
            redisClient.set(ctsRateKey, dto.getCtsRate());
        }
        if (dto.getGtsRate() == null) {
            redisClient.delete(gtsRateKey);
        } else {
            redisClient.set(gtsRateKey, dto.getGtsRate());
        }
        if (dto.getOtsRate() == null) {
            redisClient.delete(otsRateKey);
        } else {
            redisClient.set(otsRateKey, dto.getOtsRate());
        }
        if (dto.getRtsRate() == null) {
            redisClient.delete(rtsRateKey);
        } else {
            redisClient.set(rtsRateKey, dto.getRtsRate());
        }
        if (dto.getVirtualRate() == null) {
            redisClient.delete(virtualRateKey);
        } else {
            redisClient.set(virtualRateKey, dto.getVirtualRate());
        }
    }

    @Override
    public Map<String, String> getAllRate() {
        Map<String, String> rateMap = new HashMap<>(8);
        String mtsRateAll = redisClient.get(Constants.MTS_AMOUNT_RATE_ALL);
        String ctsRateAll = redisClient.get(Constants.CTS_AMOUNT_RATE_ALL);
        String gtsRateAll = redisClient.get(Constants.GTS_AMOUNT_RATE_ALL);
        String otsRateAll = redisClient.get(Constants.OTS_AMOUNT_RATE_ALL);
        String rtsRateAll = redisClient.get(Constants.RTS_AMOUNT_RATE_ALL);
        String virtualRateAll = redisClient.get(Constants.VIRTUAL_AMOUNT_RATE_ALL);
        //mts通用折扣利率
        rateMap.put("mtsRateAll", "null".equals(mtsRateAll) ? "" : mtsRateAll);
        rateMap.put("ctsRateAll", "null".equals(ctsRateAll) ? "" : ctsRateAll);
        rateMap.put("gtsRateAll", "null".equals(gtsRateAll) ? "" : gtsRateAll);
        rateMap.put("otsRateAll", "null".equals(otsRateAll) ? "" : otsRateAll);
        rateMap.put("rtsRateAll", "null".equals(rtsRateAll) ? "" : rtsRateAll);
        //虚拟通用折扣利率
        rateMap.put("virtualRateAll", "null".equals(virtualRateAll) ? "" : virtualRateAll);
        return rateMap;
    }

    @Override
    public void saveAllRate(RcsQuotaBusinessRateDTO dto) {
        if (dto.getMtsRateAll() != null) {
            redisClient.set(Constants.MTS_AMOUNT_RATE_ALL, dto.getMtsRateAll());
        }
        if (dto.getCtsRateAll() != null) {
            redisClient.set(Constants.CTS_AMOUNT_RATE_ALL, dto.getCtsRateAll());
        }
        if (dto.getGtsRateAll() != null) {
            redisClient.set(Constants.GTS_AMOUNT_RATE_ALL, dto.getGtsRateAll());
        }
        if (dto.getOtsRateAll() != null) {
            redisClient.set(Constants.OTS_AMOUNT_RATE_ALL, dto.getOtsRateAll());
        }
        if (dto.getRtsRateAll() != null) {
            redisClient.set(Constants.RTS_AMOUNT_RATE_ALL, dto.getRtsRateAll());
        }
        if (dto.getVirtualRateAll() != null) {
            redisClient.set(Constants.VIRTUAL_AMOUNT_RATE_ALL, dto.getVirtualRateAll());
        }
    }

    @Override
    public void batchUpdateBusinessRate(RcsQuotaBusinessRateDTO dto) {
        if (StringUtils.isNotBlank(dto.getBusIds())) {
            String[] busIds = dto.getBusIds().split(",");
            dto.setBusinessIds(Arrays.asList(busIds));
        }
        int result = rcsQuotaBusinessRateMapper.batchUpdateBusinessRate(dto);
        if (result > 0) {
            if (StringUtils.isBlank(dto.getBusIds())) {
                dto.setBusinessIds(rcsQuotaBusinessRateMapper.getIdList());
            }
            List<List<String>> idsList = ListUtils.partition(dto.getBusinessIds(), 1000);
            try {
                for (List<String> idList : idsList) {
                    //更新缓存
                    for (String id : idList) {
                        if (dto.getMtsRateAll() != null) {
                            String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, id);
                            redisClient.set(mtsRateKey, dto.getMtsRateAll());
                        }
                        if (dto.getCtsRateAll() != null) {
                            String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE, id);
                            redisClient.set(ctsRateKey, dto.getCtsRateAll());
                        }
                        if (dto.getGtsRateAll() != null) {
                            String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE, id);
                            redisClient.set(gtsRateKey, dto.getGtsRateAll());
                        }
                        if (dto.getOtsRateAll() != null) {
                            String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE, id);
                            redisClient.set(otsRateKey, dto.getOtsRateAll());
                        }
                        if (dto.getRtsRateAll() != null) {
                            String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE, id);
                            redisClient.set(rtsRateKey, dto.getRtsRateAll());
                        }
                        if (dto.getVirtualRateAll() != null) {
                            String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, id);
                            redisClient.set(virtualRateKey, dto.getVirtualRateAll());
                        }
                    }
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void batchUpdateVirtualRate(RcsQuotaBusinessRateDTO dto) {
        if (StringUtils.isNotBlank(dto.getBusIds())) {
            String[] busIds = dto.getBusIds().split(",");
            dto.setBusinessIds(Arrays.asList(busIds));
        }
        List<RcsQuotaBusinessRateDTO> noSetMerchantsList = rcsQuotaBusinessRateMapper.queryNoSetRate();
        if (null != noSetMerchantsList && noSetMerchantsList.size() > 0) {
            noSetMerchantsList.forEach(item -> {
                item.setMtsRate(new BigDecimal(1));
                item.setVirtualRate(BigDecimal.ZERO);
                rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(item);
            });
        }
        int result = rcsQuotaBusinessRateMapper.batchUpdateVirtualRate(dto);
        if (result > 0) {
            List<RcsQuotaBusinessRateDTO> merchantsList = rcsQuotaBusinessRateMapper.selectMerchantsList(dto);
            //更新缓存
            merchantsList.forEach(item -> {
                String id = String.valueOf(item.getBusinessId());
                if (dto.getMtsRateAll() != null) {
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, id);
                    redisClient.set(mtsRateKey, dto.getMtsRateAll());
                }
                if (dto.getCtsRateAll() != null) {
                    String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE, id);
                    redisClient.set(ctsRateKey, dto.getCtsRateAll());
                }
                if (dto.getGtsRateAll() != null) {
                    String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE, id);
                    redisClient.set(gtsRateKey, dto.getGtsRateAll());
                }
                if (dto.getOtsRateAll() != null) {
                    String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE, id);
                    redisClient.set(otsRateKey, dto.getOtsRateAll());
                }
                if (dto.getRtsRateAll() != null) {
                    String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE, id);
                    redisClient.set(rtsRateKey, dto.getRtsRateAll());
                }
                if (dto.getVirtualRateAll() != null) {
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, id);
                    redisClient.set(virtualRateKey, dto.getVirtualRateAll());
                }
            });
            log.info(String.format("batchUpdateVirtualRate merchantsList count:%s", merchantsList.size()));
        }

    }

    @Override
    public void initBusinessRate() {
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCount();
        log.info("商户折扣利率数据初始化，总数数据totalSize：{}", totalSize);
        int titlePage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
        for (int i = 0; i < titlePage; i++) {
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPage(offset, pageSize);
            if (list != null && list.size() > 0) {
                for (RcsQuotaBusinessRateDTO dto : list) {
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, dto.getBusinessId());
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, dto.getBusinessId());
                    String mtsRate = redisClient.get(mtsRateKey);
                    String virtualRate = redisClient.get(virtualRateKey);
                    if (StringUtils.isNotBlank(mtsRate)) {
                        dto.setMtsRate(new BigDecimal(mtsRate));
                    } else {
                        dto.setMtsRate(new BigDecimal(1));
                    }
                    if (StringUtils.isNotBlank(virtualRate)) {
                        dto.setVirtualRate(new BigDecimal(virtualRate));
                    } else {
                        dto.setVirtualRate(new BigDecimal("0.28"));
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
                }
            }
            pageNo++;
            log.info("商户折扣利率数据初始化，offset：{}", offset);
        }
        log.info("商户折扣利率数据初始化完成");
    }

    @Override
    public void initRedisBusinessRate() {
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCount();
        log.info("initRedisBusinessRate商户折扣利率数据初始化，总数数据totalSize：{}", totalSize);
        int titlePage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
        for (int i = 0; i < titlePage; i++) {
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPage(offset, pageSize);
            if (list != null && list.size() > 0) {
                for (RcsQuotaBusinessRateDTO dto : list) {
                    String mtsRateKey = String.format(Constants.MTS_AMOUNT_RATE, dto.getBusinessId());
                    String ctsRateKey = String.format(Constants.CTS_AMOUNT_RATE, dto.getBusinessId());
                    String gtsRateKey = String.format(Constants.GTS_AMOUNT_RATE, dto.getBusinessId());
                    String otsRateKey = String.format(Constants.OTS_AMOUNT_RATE, dto.getBusinessId());
                    String rtsRateKey = String.format(Constants.RTS_AMOUNT_RATE, dto.getBusinessId());
                    String virtualRateKey = String.format(Constants.VIRTUAL_AMOUNT_RATE, dto.getBusinessId());
                    if (dto.getMtsRate() != null) {
                        redisClient.set(mtsRateKey, dto.getMtsRate().toString());
                    } else {
                        redisClient.set(mtsRateKey, "1");
                        dto.setMtsRate(new BigDecimal("1"));
                    }
                    if (dto.getCtsRate() != null) {
                        redisClient.set(ctsRateKey, dto.getCtsRate().toString());
                    } else {
                        redisClient.set(ctsRateKey, "1");
                        dto.setCtsRate(new BigDecimal("1"));
                    }
                    if (dto.getGtsRate() != null) {
                        redisClient.set(gtsRateKey, dto.getGtsRate().toString());
                    } else {
                        redisClient.set(gtsRateKey, "1");
                        dto.setGtsRate(new BigDecimal("1"));
                    }
                    if (dto.getOtsRate() != null) {
                        redisClient.set(otsRateKey, dto.getOtsRate().toString());
                    } else {
                        redisClient.set(otsRateKey, "1");
                        dto.setOtsRate(new BigDecimal("1"));
                    }
                    if (dto.getRtsRate() != null) {
                        redisClient.set(rtsRateKey, dto.getRtsRate().toString());
                    } else {
                        redisClient.set(rtsRateKey, "1");
                        dto.setRtsRate(new BigDecimal("1"));
                    }
                    if (dto.getVirtualRate() != null) {
                        redisClient.set(virtualRateKey, dto.getVirtualRate().toString());
                    } else {
                        redisClient.set(virtualRateKey, "0.28");
                        dto.setVirtualRate(new BigDecimal("0.28"));
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRate(dto);
                }
            }
            pageNo++;
            log.info("initRedisBusinessRate商户折扣利率数据初始化，offset：{}", offset);
        }
        log.info("initRedisBusinessRate商户折扣利率数据初始化完成");
    }


    @Override
    public PageDataResult listPageDj(Integer pageNum, Integer pageSize, RcsQuotaBusinessRateDTO dto) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(pageNum, pageSize);
        List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.listPageDj(dto);
        if (list != null && list.size() > 0) {
            PageInfo<RcsQuotaBusinessRateDTO> pageInfo = new PageInfo<>(list);
            pageDataResult.setList(list);
            pageDataResult.setTotals((int) pageInfo.getTotal());
        }
        return pageDataResult;
    }

    @Override
    public int updateBusinessRateDj(RcsQuotaBusinessRateDTO dto) {
        int result = rcsQuotaBusinessRateMapper.updateBusinessRateDj(dto);
        String otsRateKey = String.format(Constants.DJ_OTS_AMOUNT_RATE, dto.getBusinessId());
        if (dto.getOtsRate() == null) {
            redisClient.delete(otsRateKey);
        } else {
            redisClient.set(otsRateKey, dto.getOtsRate());
        }
        return result;
    }

    @Override
    public Map<String, String> getAllRateDj() {
        Map<String, String> rateMap = new HashMap<>(1);
        String otsRateAll = redisClient.get(Constants.DJ_OTS_AMOUNT_RATE_ALL);
        //mts通用折扣利率
        rateMap.put("otsRateAll", otsRateAll);
        return rateMap;
    }

    @Override
    public void saveAllRateDj(RcsQuotaBusinessRateDTO dto) {
        if (dto.getOtsRateAll() == null) {
            redisClient.delete(Constants.DJ_OTS_AMOUNT_RATE_ALL);
        } else {
            redisClient.set(Constants.DJ_OTS_AMOUNT_RATE_ALL, dto.getOtsRateAll());
        }
    }

    @Override
    public void batchUpdateBusinessRateDj(RcsQuotaBusinessRateDTO dto) {
        if (StringUtils.isNotBlank(dto.getBusIds())) {
            String[] busIds = dto.getBusIds().split(",");
            dto.setBusinessIds(Arrays.asList(busIds));
            int result = rcsQuotaBusinessRateMapper.batchUpdateBusinessRateDj(dto);
            if (result > 0) {
                //更新缓存
                for (String ids : dto.getBusinessIds()) {
                    String otsRateKey = String.format(Constants.DJ_OTS_AMOUNT_RATE, ids);
                    if (dto.getOtsRateAll() != null) {
                        redisClient.set(otsRateKey, dto.getOtsRateAll());
                    }
                }
            }
        }
    }

    @Override
    public void batchAddBusinessRateDj(RcsQuotaBusinessRateDTO dto) {
        if (Objects.nonNull(dto.getBusinessId())) {
            rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRateDj(dto);
            String otsRateKey = String.format(Constants.DJ_OTS_AMOUNT_RATE, dto.getBusinessId());
            if (dto.getOtsRate() != null) {
                redisClient.set(otsRateKey, dto.getOtsRate());
            }
        }
    }

    @Override
    @Async("asyncServiceExecutor")
    public void batchAddOrUpdateBusinessRateDj(List<RcsQuotaBusinessRateExcelVO> collect, CountDownLatch countDownLatch) {
        try {
            if (!CollectionUtils.isEmpty(collect)) {
                rcsQuotaBusinessRateMapper.batchAddOrUpdateBusinessRateDj(collect);
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    @Override
    public void batchUpdateVirtualRateDj(RcsQuotaBusinessRateDTO dto) {
        if (StringUtils.isNotBlank(dto.getBusIds())) {
            String[] busIds = dto.getBusIds().split(",");
            dto.setBusinessIds(Arrays.asList(busIds));
        }
        int result = rcsQuotaBusinessRateMapper.batchUpdateVirtualRateDj(dto);
        if (result > 0) {
            List<RcsQuotaBusinessRateDTO> merchantsList = rcsQuotaBusinessRateMapper.selectMerchantsListDj(dto);
            //更新缓存
            merchantsList.forEach(item -> {
                String virtualRate = String.format(Constants.DJ_OTS_AMOUNT_RATE, item.getBusinessId());
                if (item.getOtsRate() != null) {
                    redisClient.set(virtualRate, item.getOtsRate());
                }
            });
            log.info("电竞例外批量设置OtsRate:{}, count:{}", dto.getOtsRate(), merchantsList.size());
        }
    }

    @Override
    public void initBusinessRateDj() {
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCountDj();
        log.info("电竞商户折扣利率数据初始化，总数数据totalSize：{}", totalSize);
        int titlePage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
        for (int i = 0; i < titlePage; i++) {
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPageDj(offset, pageSize);
            if (list != null && list.size() > 0) {
                for (RcsQuotaBusinessRateDTO dto : list) {
                    String otsRateKey = String.format(Constants.DJ_OTS_AMOUNT_RATE, dto.getBusinessId());
                    String otsRate = redisClient.get(otsRateKey);
                    if (StringUtils.isNotBlank(otsRate)) {
                        dto.setOtsRate(new BigDecimal(otsRate));
                    } else {
                        dto.setOtsRate(new BigDecimal(1));
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRateDj(dto);
                }
            }
            pageNo++;
            log.info("电竞商户折扣利率数据初始化，offset：{}", offset);
        }
        log.info("电竞商户折扣利率数据初始化完成");
    }

    @Override
    public void initRedisBusinessRateDj() {
        int pageNo = 0;
        int pageSize = 200;
        int totalSize = rcsQuotaBusinessRateMapper.selectMerchantsCountDj();
        log.info("initRedisBusinessRate电竞商户折扣利率数据初始化，总数数据totalSize：{}", totalSize);
        int titlePage = totalSize % pageSize == 0 ? totalSize / pageSize : totalSize / pageSize + 1;
        for (int i = 0; i < titlePage; i++) {
            int offset = pageNo * pageSize;
            List<RcsQuotaBusinessRateDTO> list = rcsQuotaBusinessRateMapper.selectMerchantsByPageDj(offset, pageSize);
            if (list != null && list.size() > 0) {
                for (RcsQuotaBusinessRateDTO dto : list) {
                    String otsRateKey = String.format(Constants.DJ_OTS_AMOUNT_RATE, dto.getBusinessId());
                    if (dto.getOtsRate() != null) {
                        redisClient.set(otsRateKey, dto.getOtsRate().toString());
                    } else {
                        redisClient.set(otsRateKey, "1");
                        dto.setOtsRate(new BigDecimal("1"));
                    }
                    rcsQuotaBusinessRateMapper.insertOrUpdateBusinessRateDj(dto);
                }
            }
            pageNo++;
            log.info("initRedisBusinessRate电竞商户折扣利率数据初始化，offset：{}", offset);
        }
        log.info("initRedisBusinessRate电竞商户折扣利率数据初始化完成");
    }
}
