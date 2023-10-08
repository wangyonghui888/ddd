package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsBusinessSingleBetConfig;
import com.panda.sport.rcs.vo.BusinessSingleBetAndPlayVo;

import java.util.List;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-11-22 15:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface RcsBusinessSingleBetConfigService extends IService<RcsBusinessSingleBetConfig> {

    List<RcsBusinessSingleBetConfig> selectBusinessSingleBetConfigList(RcsBusinessSingleBetConfig businessSingleBetConfig);

    BusinessSingleBetAndPlayVo selectBusinessSingleBetConfigView(RcsBusinessSingleBetConfig businessSingleBetConfig);

    void initBusinessSingleBetConfig(RcsBusinessSingleBetConfig businessSingleBetConfig);

    boolean updateBusinessSingleBetConfig(List<RcsBusinessSingleBetConfig> businessSingleBetConfigs);
}
