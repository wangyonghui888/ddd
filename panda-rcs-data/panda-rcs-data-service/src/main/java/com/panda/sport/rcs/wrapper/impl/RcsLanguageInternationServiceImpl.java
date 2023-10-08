package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsLanguageInternationMapper;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.utils.TwoLevelCacheUtil;
import com.panda.sport.rcs.wrapper.RcsLanguageInternationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RcsLanguageInternationServiceImpl extends ServiceImpl<RcsLanguageInternationMapper, RcsLanguageInternation> implements RcsLanguageInternationService {

	private static final String PLAYER_LANGUAGE = "playerLanguage:";

    @Autowired
    TwoLevelCacheUtil twoLevelCacheUtil;

    @Override
    public String getPlayerLanguage(String nameCode) {
        String key=PLAYER_LANGUAGE+nameCode;
        String value = twoLevelCacheUtil.get(key, key1 -> {
            String name = this.baseMapper.getPlayLanguageByNamecode(nameCode);
            if(CommonUtils.isNotBlankAndNull(name)){
                return name;
            }else {
                log.info("Language cache not exists, load from database, nameCode::{}::", nameCode);
                return "";
            }
        });
        return value;
    }
    
    
}
