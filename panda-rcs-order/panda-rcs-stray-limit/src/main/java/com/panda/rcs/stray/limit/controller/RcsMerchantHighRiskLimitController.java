package com.panda.rcs.stray.limit.controller;


import com.panda.rcs.stray.limit.entity.vo.RcsMerchantHighRiskRespVo;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantInterval;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantLowLimit;
import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSingleLimit;
import com.panda.rcs.stray.limit.service.IRcsMerchantHighRiskLimitWebService;
import com.panda.rcs.stray.limit.service.RcsMerchantIntervalService;
import com.panda.rcs.stray.limit.utils.IPUtil;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 串关高风险页面查询修改接口 前端控制器
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@RestController
@RequestMapping("/limit2/high/risk")
@Slf4j
@Api(tags = "串关高风险页面查询修改接口 前端控制器")
public class RcsMerchantHighRiskLimitController {


    @Autowired
    private IRcsMerchantHighRiskLimitWebService rcsMerchantHighRiskLimitWebService;

    @Autowired
    private RcsMerchantIntervalService rcsMerchantIntervalService;

    /**
     * 串关高风险页面 查询接口
     */
    @ApiOperation(value = "串关高风险页面 查询接口")
    @GetMapping(value = "/queryData/{sportId}")
    public HttpResponse<RcsMerchantHighRiskRespVo> queryData(@PathVariable Integer sportId) {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(rcsMerchantHighRiskLimitWebService.queryData(sportId), linkId);
        } catch (Exception e) {
            log.error(String.format("串关高风险页面 查询接口异常  linkId: %s 错误详情: %s",linkId,e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @ApiOperation(value = "串关高风险页面 查询单日额度用完最低可投注金额配置")
    @GetMapping(value = "/query/lowData")
    public HttpResponse queryByLowData() {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(rcsMerchantHighRiskLimitWebService.queryByLowData(), linkId);
        } catch (Exception e) {
            log.error(String.format("查询单日额度用完最低可投注金额配置异常  linkId: %s 错误详情: %s",linkId,e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询单日额度用完最低可投注金额配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @ApiOperation(value = "串关高风险页面 修改单日额度用完最低可投注金额配置")
    @PostMapping(value = "/update/lowData")
    public HttpResponse updateByLowData(@RequestBody List<RcsMerchantLowLimit> rcsMerchantLowLimitList, HttpServletRequest request) {
        String linkId = CommonUtils.mdcPut();
        try {
            if(!CollectionUtils.isEmpty(rcsMerchantLowLimitList)){
                for(RcsMerchantLowLimit ip: rcsMerchantLowLimitList){
                    ip.setIp(IPUtil.getRequestIp(request));
                }
            }
            rcsMerchantHighRiskLimitWebService.updateByLowData(rcsMerchantLowLimitList);
            return HttpResponse.success(linkId);
        } catch (Exception e) {
            log.error(String.format("修改单日额度用完最低可投注金额配置异常  linkId: %s 错误详情: %s", linkId, e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询单日额度用完最低可投注金额配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @ApiOperation(value = "串关高风险页面 查询高风险单注赛种投注限制区间配置")
    @GetMapping(value = "/query/singleLimitData/{sportId}")
    public HttpResponse queryBySingleLimitData(@PathVariable Integer sportId) {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(rcsMerchantHighRiskLimitWebService.queryBySingleLimitData(sportId), linkId);
        } catch (Exception e) {
            log.error(String.format("查询高风险单注赛种投注限制区间配置异常  linkId: %s 错误详情: %s", linkId, e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询高风险单注赛种投注限制配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @ApiOperation(value = "串关高风险页面 修改高风险单注赛种投注区间限制")
    @PostMapping(value = "/update/singleLimitData")
    public HttpResponse updateBySingleLimitData(@RequestBody List<RcsMerchantSingleLimit> rcsMerchantSingleLimits, HttpServletRequest request) {
        String linkId = CommonUtils.mdcPut();
        try {
            if(!CollectionUtils.isEmpty(rcsMerchantSingleLimits)){
                for(RcsMerchantSingleLimit ip: rcsMerchantSingleLimits){
                    ip.setIp(IPUtil.getRequestIp(request));
                }
            }
            rcsMerchantHighRiskLimitWebService.updateBySingleLimitData(rcsMerchantSingleLimits);
            return HttpResponse.success(linkId);
        } catch (Exception e) {
            log.error(String.format("修改高风险单注赛种投注限制区间配置异常  linkId: %s 错误详情: %s", linkId, e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询单日额度用完最低可投注金额区间配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }


    @ApiOperation(value = "串关高风险页面 查询高风险单注赛种区间最大金额")
    @GetMapping(value = "/query/intervalMaxValue/{sportId}")
    public HttpResponse queryIntervalMaxValue(@PathVariable Integer sportId) {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(rcsMerchantIntervalService.queryAll(sportId), linkId);
        } catch (Exception e) {
            log.error(String.format("查询高风险单注赛种区间最大金额异常  linkId: %s 错误详情: %s", linkId, e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询高风险单注赛种区间最大金额配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @ApiOperation(value = "串关高风险页面 修改高风险单注赛种区间最大金额")
    @PostMapping(value = "/update/intervalMaxValue")
    public HttpResponse updateByIntervalMaxValue(@RequestBody List<RcsMerchantInterval> rcsMerchantIntervalList, HttpServletRequest request) {
        String linkId = CommonUtils.mdcPut();
        try {
            //操作添加IP
            String ip = IPUtil.getRequestIp(request);
            for(RcsMerchantInterval log: rcsMerchantIntervalList){
                log.setIp(ip);
            }
            rcsMerchantIntervalService.updateData(rcsMerchantIntervalList);
            return HttpResponse.success(linkId);
        } catch (Exception e) {
            log.error(String.format("修改高风险单注赛种投注限制配置异常  linkId: %s 错误详情: %s", linkId, e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 查询单日额度用完最低可投注金额配置接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    /**
     * 串关高风险页面 修改接口
     */
    @PostMapping(value = "/updateData")
    @ApiOperation(value = "串关高风险页面 修改接口")
    public HttpResponse queryData(@RequestBody RcsMerchantHighRiskRespVo rcsMerchantHighRiskRespVo, HttpServletRequest request) {
        String linkId = CommonUtils.mdcPut();
        try {
            rcsMerchantHighRiskRespVo.setIp(IPUtil.getRequestIp(request));
            rcsMerchantHighRiskLimitWebService.updateData(rcsMerchantHighRiskRespVo);
            return HttpResponse.success(linkId);
        } catch (Exception e) {
            log.error(String.format("串关高风险页面 修改异常 linkId: %s 错误详情: %s",linkId,e.getMessage()));
            return HttpResponse.failToMsg("串关高风险页面 修改接口异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }


}
