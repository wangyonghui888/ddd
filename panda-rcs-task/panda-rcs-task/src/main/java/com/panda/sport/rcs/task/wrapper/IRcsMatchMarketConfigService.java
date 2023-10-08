package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfigSub;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;

import java.util.List;

/**
 * <p>
 * 赛事设置表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-24
 */
public interface IRcsMatchMarketConfigService extends IService<RcsMatchMarketConfig> {
    /**
     * 同步
     */
   void insertFromTemplate(RcsTournamentTemplateComposeModel model);

   void insertFromTemplate(RcsTournamentTemplateComposeModel model,String messageSource);

    RcsMatchMarketConfigSub queryMatchMarketConfigSub(RcsMatchMarketConfig config);
}
