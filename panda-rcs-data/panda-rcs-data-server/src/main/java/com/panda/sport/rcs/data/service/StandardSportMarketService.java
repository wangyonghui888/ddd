package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMarketMessageDTO;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.pojo.dto.StandardMarketPlaceDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 足球赛事盘口表. 使用盘口关联的功能存在以下假设：同一个盘口的显示值不可变更，如果变更需要删除2个盘口之间的关联关系。。 服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
public interface StandardSportMarketService extends IService<StandardSportMarket> {
    int insertOrUpdate(StandardSportMarket standardSportMarket);

    int batchInsertOrUpdate(List<StandardMarketMessageDTO> list);

    /**
     * 获取标准投注项模板ID
     *
     * @param playId
     * @return
     */
    Map<Integer, Long> getOddsFieldsTemplateId(Long playId);

    /**
     * 获取主盘口位置信息
     *
     * @param matchId
     * @param playId
     * @return
     * @author Paca
     */
    StandardMarketPlaceDto getMainMarketPlaceInfo(Long matchId, Long playId);
}
