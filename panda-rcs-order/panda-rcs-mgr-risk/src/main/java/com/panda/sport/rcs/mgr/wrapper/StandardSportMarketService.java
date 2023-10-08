package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardMatchInfo;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import com.panda.sport.rcs.vo.SportMatchInfoVo;

import java.util.List;

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
     * 从DB加载赛事相关依赖数据
     *
     * @param matchEntity
     * @return
     */
    SportMatchInfoVo loadMatchInfoTreeVo(StandardMatchInfo matchEntity, List<Long> marketCategoryIds);

    /**
     * 从缓存中获取赛事盘口赔率树
     *
     * @param matchId
     * @return
     */
    SportMatchInfoVo getMatchInfoTreeVoFromCache(Long matchId);

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     * @Description 按matchId和playName进行查询数据库
     * @Param [matchId, playName]
     * @Author toney
     * @Date 15:09 2019/12/10
     **/
    List<StandardSportMarket> queryMakertInfoByMatchIdAndPlayName(Long matchId, String playName);

    /**
     * 查询主盘口信息
     *
     * @param matchId 赛事ID
     * @param playId  玩法ID
     * @return
     * @author Paca
     */
    StandardSportMarket selectMainMarketInfo(Long matchId, Long playId,String subPlayId);
}
