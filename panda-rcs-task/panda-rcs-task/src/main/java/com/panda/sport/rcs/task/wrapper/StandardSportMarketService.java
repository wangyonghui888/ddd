package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportMarket;

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
    /**
     * 获取赛事盘口的基本信息
     *
     * @param map
     * @return
     */
    Map<String, Object> queryMatchMarketInfo(Map<String, Object> map);

    /**
     * 通过模板id查询nameCode
     *
     * @param oddsFieldsTemplateId
     * @return
     */
    String queryOddTemplateInfo(Long oddsFieldsTemplateId);

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     * @Description //TODO
     * @Param [match]
     * @Author kimi
     * @Date 2020/3/2
     **/
    List<StandardSportMarket> selectStandardSportMarketByMatchIdAndPlayIdAndPlayId(Long matchId);

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     * @Description //TODO
     * @Param [match]
     * @Author kimi
     * @Date 2020/3/2
     **/
    StandardSportMarket selectStandardSportMarketByMarketId(Long marketId);

    /**
     * 查询模板
     * @param templateCode
     * @return
     */
    String queryOddTemplateInfo(String templateCode);
}
