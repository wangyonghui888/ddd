package com.panda.sport.rcs.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.vo.OrderDetailStatReportVo;

import java.util.List;

/**
 * <p>
 * 投注单详细信息表 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-10-03
 */
public interface ITOrderDetailService extends IService<TOrderDetail> {

    /**
     * 查询投注项值
     * @return java.lang.String
     * @Description 查询OptionValue
     * @Param [bean]
     * @Author Sean
     * @Date 17:54 2019/12/7
     **/
    String queryOptionValue(OrderItem bean,String languageType);


    String queryPlayerOptionValue(Long playOptionsId,String languageType);

    String queryMarketPlayer(Long marketId,String languageType);

    String queryOddsPlayer(Long playerId,String languageType);

    /**
     * 查询平衡值明细
     * @param matchId
     * @param marketCategoryId
     * @return
     */
    List<OrderDetailStatReportVo> getMarketStatByMatchIdAndPlayId(Long matchId, Long marketCategoryId, Integer matchType);

}
