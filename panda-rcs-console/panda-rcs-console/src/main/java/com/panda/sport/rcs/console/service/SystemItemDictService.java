package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.vo.SystemItemDictVo;

import java.util.Map;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.service
 * @ClassName: SystemItemDictService
 * @Description: TODO
 * @Date: 2023/3/14 20:29
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface SystemItemDictService {

    PageDataResult getDictPage(SystemItemDictVo systemItemDictVo);

    Map<String, Object> delDict(Long id);

    Map<String, Object> editDict(SystemItemDictVo systemItemDictVo);

    Map<String, Object> addDict(SystemItemDictVo systemItemDictVo);
}
