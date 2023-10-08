package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsHideRangeConfig;
import com.panda.sport.rcs.pojo.dto.RcsHideRangeConfigDTO;

import java.util.List;

/**
 * <p>
 * 藏单区间配置表 服务类
 * </p>
 *
 * @author pumelo
 * @since 2023-04-23
 */
public interface IRcsHideRangeConfigService extends IService<RcsHideRangeConfig> {

    /**
     * 保存藏单投注货量-金额区间配置
     * @param configs
     */
    void saveHideList(List<RcsHideRangeConfigDTO> configs);

    /**
     * 获取藏单投注货量-金额区间配置
     * @return
     */
    List<RcsHideRangeConfigDTO> getHideList();

}
