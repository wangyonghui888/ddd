package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsLanguageInternation;
import com.panda.sport.rcs.vo.I18nItemVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RcsLanguageInternationService extends IService<RcsLanguageInternation> {
    

    /**
     * 根据nameCode从缓存获取所有语言集
     *
     * @param nameCode
     * @return
     */
    List<I18nItemVo> getCachedNamesByCode(Long nameCode);

    /**
     * 根据nameCode从缓存获取所有语言集
     * 获取顺序：本地缓存->redis->database
     * 返回格式为MAP
     *
     * @param nameCode
     * @return
     */
    Map<String, String> getCachedNamesMapByCode(Long nameCode);

    Map<Long, Map<String, String>> selectLanguageInternationByPlayId(Set<Long> playIds);
}
