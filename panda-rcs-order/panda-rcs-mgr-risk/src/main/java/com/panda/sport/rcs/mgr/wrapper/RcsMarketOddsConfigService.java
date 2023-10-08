package com.panda.sport.rcs.mgr.wrapper;

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
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2019-11-01 16:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface RcsMarketOddsConfigService extends IService<RcsMarketOddsConfig> {

    RcsMarketOddsConfig getMarketOdds(RcsMarketOddsConfig rcsMarketOddsConfig);

    List<OrderDetailStatReportVo> queryMarketStatByMarketId(Long marketId);

    /**
     * @Description   添加记录或者更新记录
     * @Param [rcsMarketOddsConfig]
     * @Author  toney
     * @Date  19:57 2020/2/18
     * @return com.panda.sport.rcs.pojo.RcsMarketOddsConfig
     **/
    int insertOrUpdate(RcsMarketOddsConfig rcsMarketOddsConfig);

}
