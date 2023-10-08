package com.panda.sport.rcs.console.service.impl;

import com.alibaba.nacos.client.naming.utils.RandomUtils;
import com.panda.sport.rcs.console.common.utils.RandomNumber;
import com.panda.sport.rcs.console.dao.LanguageInternationMapper;
import com.panda.sport.rcs.console.dao.RcsLanguageInternationMapper;
import com.panda.sport.rcs.console.dto.SyncTimeSettingDTO;
import com.panda.sport.rcs.console.pojo.LanguageInternation;
import com.panda.sport.rcs.console.pojo.UpdateRcsMarketCategorySetBO;
import com.panda.sport.rcs.console.service.CommonService;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Resource
    private LanguageInternationMapper languageInternationMapper;

    @Resource
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;

    private static final List<String> saveLanguages = Arrays.asList("zs", "zh", "en");


    @Override
    public List<LanguageInternation> getLanguageInternations(int page, int pageSize) {
        int start = (page - 1) * pageSize;
        return languageInternationMapper.getLanguageInternations(start, pageSize);
    }

    @Override
    public int batchInsertOrUpdate(List<LanguageInternation> languageInternations) {
        if (CollectionUtils.isEmpty(languageInternations)) {
            return 0;
        }
        for (LanguageInternation rcsLanguageInternation : languageInternations) {
            try {
                String text = rcsLanguageInternation.getText();

                if (StringUtils.isNotBlank(text)) {
                    log.info(rcsLanguageInternation.getNameCode() + "_"+rcsLanguageInternation.getId()+"_同步国际化page");
                    Map map = JsonFormatUtils.fromJson(text, Map.class);
                    Map map1 = parseMapForFilter(map);
                    if (!CollectionUtils.isEmpty(map1)) {
                        rcsLanguageInternation.setText(JsonFormatUtils.toJson(map1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rcsLanguageInternationMapper.batchInsertOrUpdate(languageInternations);
    }

    @Override
    public void updatePlaySetNameCodeLanguageInternation(SyncTimeSettingDTO syncTimeSettingDTO) {
        List<String> strings = Arrays.asList(syncTimeSettingDTO.getLids().split(","));
        if(CollectionUtils.isEmpty(strings)){return;}
        List<LanguageInternation> languageInternations = rcsLanguageInternationMapper.selectIds(strings);
        if(CollectionUtils.isEmpty(languageInternations)){return;}
        log.info(JsonFormatUtils.toJson(languageInternations));
        ArrayList<UpdateRcsMarketCategorySetBO> updateRcsMarketCategorySetBOs = new ArrayList<>();
        for (LanguageInternation languageInternation : languageInternations) {
            String newNameCode = System.currentTimeMillis()+""+ RandomNumber.randomNum4Len1(2)+""+ RandomNumber.randomNum4Len2(2)+""+ RandomNumber.randomNum4Len1(1);
            UpdateRcsMarketCategorySetBO updateRcsMarketCategorySetBO = new UpdateRcsMarketCategorySetBO();
            updateRcsMarketCategorySetBO.setNameCode(Long.valueOf(newNameCode));
            updateRcsMarketCategorySetBO.setOldNameCode(languageInternation.getNameCode());
            updateRcsMarketCategorySetBOs.add(updateRcsMarketCategorySetBO);
            languageInternation.setNameCode(newNameCode);
        }
        if(!CollectionUtils.isEmpty(languageInternations)){
            rcsLanguageInternationMapper.updateRLIN(languageInternations);
        }
        if(!CollectionUtils.isEmpty(updateRcsMarketCategorySetBOs)) {
            rcsLanguageInternationMapper.updatesRMCSN(updateRcsMarketCategorySetBOs);
        }
    }

    public static Map<String, Object> parseMapForFilter(Map<String, Object> map) {
        if (map == null) {
            return null;
        } else {
            map = map.entrySet().stream()
                    .filter((e) -> checkValue(e.getKey()))
                    .collect(Collectors.toMap(
                            (e) -> (String) e.getKey(),
                            (e) -> e.getValue()
                    ));
        }
        return map;
    }

    private static boolean checkValue(String object) {
        if (StringUtils.isBlank(object)) {
            return false;
        }
        if (!saveLanguages.contains(object)) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
        String a ="aaaaa\"aaaa";
        objectObjectHashMap.put("111",a);
        System.out.println(JsonFormatUtils.toJson(objectObjectHashMap));
        System.out.println(1);
    }

    public static Integer randomRange(Integer max) {
        SecureRandom rm = new SecureRandom();
        return rm.nextInt(max) + 1;
    }
}
