package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;

public interface RcsLanguageInternationService extends IService<RcsLanguageInternation> {

	String getPlayerLanguage(String nameCode);
    
}
