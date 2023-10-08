package com.panda.sport.rcs.mgr.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitLogReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitReqVo;
import com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitVo;
import com.panda.sport.rcs.vo.HttpResponse;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  TODO
 * @Date: 2020-09-04 15:03
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsQuotaBusinessLimitService extends IService<RcsQuotaBusinessLimit> {
    /**
     * @Description   //分页查询
     * @Param [current, size]
     * @Author  kimi
     * @Date   2020/9/10
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.panda.sport.rcs.pojo.RcsQuotaBusinessLimit>
     **/
    IPage<RcsQuotaBusinessLimit> listPage(Integer current,Integer size );

    /**
     * @Description   //TODO
     * @Param [current, size]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitVo>
     **/
    @Deprecated
    HttpResponse<RcsQuotaBusinessLimitVo> getList(Integer current, Integer size);

    /**
     * 限额配置列表
     *
     * @param reqVo
     * @return
     */
    HttpResponse<RcsQuotaBusinessLimitVo> limitConfigList(RcsQuotaBusinessLimitReqVo reqVo);

    /**
     * @Description   //TODO
     * @Param [current, size]
     * @Author  kimi
     * @Date   2020/9/26
     * @return com.panda.sport.rcs.vo.HttpResponse<com.panda.sport.rcs.pojo.vo.RcsQuotaBusinessLimitVo>
     **/
    HttpResponse<RcsQuotaBusinessLimitVo> getSubList(Integer current, Integer size, Long merchantId, String[] agentIds, String[] agentNames);

    /**
     * 初始化数据
     * @return
     */
    List<RcsQuotaBusinessLimit> initRcsQuotaBusinessLimit();



    HttpResponse<RcsQuotaBusinessLimit> getgetBusinessLRiskStatusList(Long userId);

    HttpResponse<RcsQuotaBusinessLimitVo> limitConfigLogList(RcsQuotaBusinessLimitLogReqVo reqVo);

    void addRcsQuotaBusinessLimitLog(RcsQuotaBusinessLimit rcsQuotaBusinessLimit,RcsQuotaBusinessLimit rcsQuotaBusinessLimitNew);

    List<String> queryParentName();

}
