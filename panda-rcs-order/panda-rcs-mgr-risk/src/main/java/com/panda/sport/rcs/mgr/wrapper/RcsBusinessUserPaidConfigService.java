package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.RcsBusinessUserPaidConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.vo.UserPaidVo;

import java.util.List;

/**
 * <p>
 * 用户最大赔付设置 服务类
 * </p>
 *
 * @author admin
 * @since 2019-10-04
 */
public interface RcsBusinessUserPaidConfigService extends IService<RcsBusinessUserPaidConfig> {

    List<UserPaidVo> getUserPaidListView(Long businessId);

    void updateRcsBusinessUserPaidConfig(RcsBusinessUserPaidConfig rcsBusinessUserPaidConfig);

}
