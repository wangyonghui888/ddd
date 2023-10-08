package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.RcsMatchConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-11-08 20:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface RcsMatchConfigService extends IService<RcsMatchConfig> {

    RcsMatchConfig selectMatchConfig(Long matchId);
}
