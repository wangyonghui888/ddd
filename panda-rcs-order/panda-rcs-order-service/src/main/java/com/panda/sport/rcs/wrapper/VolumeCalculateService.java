package com.panda.sport.rcs.wrapper;

import com.panda.sport.rcs.pojo.AmountTypeVo;
import com.panda.sport.rcs.pojo.dto.VolumeDTO;

/**
 * 计算货量服务
 *
 * @author skyKong
 */
public interface VolumeCalculateService {
    /**
     * 计算货量
     * */
    AmountTypeVo getVolumePercentage(VolumeDTO volumeDTO);
}
