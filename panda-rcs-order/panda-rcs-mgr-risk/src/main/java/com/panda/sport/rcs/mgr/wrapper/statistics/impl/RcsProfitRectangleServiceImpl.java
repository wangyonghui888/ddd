package com.panda.sport.rcs.mgr.wrapper.statistics.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.statistics.RcsProfitRectangleMapper;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.mgr.wrapper.statistics.RcsProfitRectangleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper.statistics.impl
 * @Description :  TODO
 * @Date: 2019-12-11 16:40
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@Slf4j
public class RcsProfitRectangleServiceImpl extends ServiceImpl<RcsProfitRectangleMapper, RcsProfitRectangle> implements RcsProfitRectangleService {
    @Autowired
    private RcsProfitRectangleMapper mapper;
    /**
     * @Description  删除记录
     * @Param [matchId, playId]
     * @Author  toney
     * @Date  11:07 2019/12/21
     * @return java.lang.Integer
     **/
    @Override
    public Integer deleteByMatchIdAndPlayId(Long matchId,Integer playId){
        return mapper.deleteByMatchIdAndPlayId(matchId,playId);
    }
    /**
     * 批量插入
     * @param map
     */
    @Override
    public Integer batchInsert(List<RcsProfitRectangle> map){
        return mapper.batchInsert(map);
    }


}