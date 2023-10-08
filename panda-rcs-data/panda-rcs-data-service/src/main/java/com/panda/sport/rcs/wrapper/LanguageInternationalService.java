package com.panda.sport.rcs.wrapper;

import com.panda.sport.rcs.vo.I18nItemVo;

import java.util.List;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 多语言国际化
 * @Author : Paca
 * @Date : 2020-11-22 10:46
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface LanguageInternationalService  {

    /**
     * 根据nameCode从缓存获取所有语言集
     *
     * @param nameCode
     * @return
     */
    List<I18nItemVo> getCachedNamesByCode(Long nameCode);
}
