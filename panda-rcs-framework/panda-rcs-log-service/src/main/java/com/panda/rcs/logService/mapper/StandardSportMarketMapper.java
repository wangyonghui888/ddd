package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.RcsMatchMarketConfig;
import com.panda.rcs.logService.vo.StandardSportMarket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 足球赛事盘口表. 使用盘口关联的功能存在以下假设：同一个盘口的显示值不可变更，如果变更需要删除2个盘口之间的关联关系。。 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Repository
@Mapper
public interface StandardSportMarketMapper extends BaseMapper<StandardSportMarket> {

    /**
     * @return java.util.List<com.panda.sport.rcs.pojo.StandardSportMarket>
     * @Description //根据赛事id和玩法id查询所有有效的盘口
     * @Param [config]
     * @Author Sean
     * @Date 11:04 2020/10/6
     **/
    List<StandardSportMarket> selectMarketOddsByMarketIds(@Param("config") RcsMatchMarketConfig config);






}


