package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.mgr.mq.bean.RcsMerchantsHideRangeConfigDto;
import com.panda.sport.rcs.pojo.RcsMerchantsHideRangeConfig;
import org.springframework.stereotype.Service;

/**
 * 藏单配置接口
 * @author bobi
 */
@Service
public interface RcsMerchantsHideRangeConfigService extends IService<RcsMerchantsHideRangeConfig> {
    void editHideStatusList(RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig);

    void editHideMoneyList(RcsMerchantsHideRangeConfigDto rcsMerchantsHideRangeConfig);
}
