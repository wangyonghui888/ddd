package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitReqVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mapper
 * @Description :  TODO
 * @Date: 2020-09-04 15:05
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsQuotaBusinessLimitMapper extends BaseMapper<RcsQuotaBusinessLimit> {
    /**
     * @Description 分页查询
     * @Param [page]
     * @Author  kimi
     * @Date   2020/9/10
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit>
     **/
    IPage<RcsQuotaBusinessLimit> listPage(IPage<RcsQuotaBusinessLimit> page);

    /**
     * 限额配置列表分页查询
     *
     * @param page
     * @param reqVo
     * @return
     */
    IPage<RcsQuotaBusinessLimit> limitConfigList(IPage<RcsQuotaBusinessLimit> page, @Param("reqVo") RcsQuotaBusinessLimitReqVo reqVo);

    IPage<RcsQuotaBusinessLimit> getSubList(IPage<RcsQuotaBusinessLimit> page, @Param("merchantId") Long merchantId, @Param("agentIds")String[] agentIds, @Param("agentNames") String[] agentNames);

    List<RcsQuotaBusinessLimit> listByBusinessIds(@Param("businessIds") List<String> businessIds);

    List<String> queryParentName();

    List<Long> getSportIdsList(@Param("sportId") Integer sportId);

}
