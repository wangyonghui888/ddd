package com.panda.sport.rcs.task.wrapper;

import com.panda.sport.rcs.pojo.RcsMarketOddsConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * /**
 *
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.wrapper
 * @Description :  TODO
 * @Date: 2019-11-01 16:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface RcsMarketOddsConfigService extends IService<RcsMarketOddsConfig> {

    RcsMarketOddsConfig getMarketOdds(RcsMarketOddsConfig rcsMarketOddsConfig);

    List<OrderDetailStatReportVo> queryMarketStatByMarketId(Long marketId);
}
