package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsBusinessPlayPaidConfig;
import com.panda.sport.rcs.pojo.StandardSportMarketCategory;
import com.panda.sport.rcs.pojo.SystemItemDict;

import java.util.List;
import java.util.Map;

/**
 * @author :  kimi
 * @Description :  玩法维度接口
 * @Date: 2019-10-03 21:13
 */
public interface RcsBusinessPlayPaidConfigService extends IService<RcsBusinessPlayPaidConfig> {

    /**
     * @Description 根据表的字段进行查询
     * @Param [columnMap]  数据库字段 值
     * @Author kimi
     * @Date 2019/10/4
     **/

    List<RcsBusinessPlayPaidConfig> getRcsBusinessPlayPaidConfigList(Map<String, Object> columnMap);

    /**
     * @return void
     * @Description 更新数据库字段
     * @Param [playRestriction]
     * @Author kimi
     * @Date 2019/10/4
     **/
    void updateRcsBusinessPlayPaidConfig(RcsBusinessPlayPaidConfig rcsBusinessPlayPaidConfig);

    /**
     * @return void
     * @Description //批量插入
     * @Param [rcsBusinessPlayPaidConfigList]
     * @Author kimi
     * @Date 2019/10/12
     **/

    boolean insertRcsBusinessPlayPaidConfigList(List<RcsBusinessPlayPaidConfig> rcsBusinessPlayPaidConfigList);
}
