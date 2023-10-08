package com.panda.sport.rcs.trade.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsLabelLimitConfig;
import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.RcsLabelLimitConfigVo;

import java.util.List;

public interface RcsLabelLimitConfigService extends IService<RcsLabelLimitConfig> {
    /**
     *
     * @param rcsLabelLimitConfigVoList
     * @param tradeId
     * @return
     */
    HttpResponse updateRcsLabelLimitConfigVo(List<RcsLabelLimitConfigVo> rcsLabelLimitConfigVoList,Integer tradeId,String ip);

    /**
     *
     * @param matchLength 入参
     * @param config 配置文件获取，如 57:5000,66:10000,55:20000
     * @return
     */
    Integer getMachValue(Integer matchLength, String config);
}
