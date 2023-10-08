package com.panda.sport.rcs.service;

import com.panda.sport.rcs.pojo.RcsOmitConfig;
import com.panda.sport.rcs.pojo.vo.RcsOmitConfigBatchUpdateVo;
import com.panda.sport.rcs.pojo.vo.RcsPageQueryVo;
import com.panda.sport.rcs.vo.HttpResponse;

/**
 * @author wiker
 * @date 2023/8/20 16:36
 **/
public interface RcsOmitConfigService {
    /**
     * @Description   //分页查询
     * @Param [current, size]
     * @Author  tim
     * @Date   2023/8/2
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.panda.sport.rcs.pojo.RcsOmitConfig>
     **/
    HttpResponse<RcsPageQueryVo> listPage(Integer current, Integer size, Long merchantsId, String merchantsCode );

    HttpResponse<RcsOmitConfig> getDefaultConfig();

    HttpResponse<RcsOmitConfig> batchUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo);

    HttpResponse<RcsOmitConfig> exceptUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo);

    HttpResponse<RcsOmitConfig> defaultUpdateConfig(RcsOmitConfigBatchUpdateVo reqVo);


}
