package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMatchCollection;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-10-25 14:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsMatchCollectionService extends IService<RcsMatchCollection> {
    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.RcsMatchCollection>
     * @Description //查询操作
     * @Param [columnMap]
     * @Author kimi
     * @Date 2019/10/25
     **/
    List<RcsMatchCollection> selectByMap(Map<String, Object> columnMap);

}
