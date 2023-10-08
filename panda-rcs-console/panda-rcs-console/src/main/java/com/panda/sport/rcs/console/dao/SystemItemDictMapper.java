package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.SystemItemDict;
import com.panda.sport.rcs.console.vo.SystemItemDictVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.dao
 * @ClassName: SystemItemDictMapper
 * @Description: TODO
 * @Date: 2023/3/14 20:34
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface SystemItemDictMapper {

    List<SystemItemDict> selectSystemItemDictList(@Param("systemItemDict") SystemItemDictVo systemItemDict);

    SystemItemDict selectSystemItemDictById(@Param("dictId") Long id);

    int insertSystemItemDict(SystemItemDict systemItemDict);

    int updateSystemItemDictById(SystemItemDict systemItemDict);

    int deleteSystemItemDictById(@Param("dictId") Long id);
}
