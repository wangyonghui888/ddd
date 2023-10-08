package com.panda.rcs.stray.limit.controller;


import com.panda.rcs.stray.limit.entity.vo.RcsMerchantSeriesRespVo;
import com.panda.rcs.stray.limit.service.RcsMerchantSeriesService;
import com.panda.rcs.stray.limit.utils.IPUtil;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 串关配置页面接口 前端控制器
 * </p>
 *
 * @author joey
 * @since 2022-03-27
 */
@RestController
@RequestMapping("/limit2/series")
@Slf4j
@Api(tags="串关配置页面接口 前端控制器")
public class RcsMerchantSeriesController {


    @Autowired
    private RcsMerchantSeriesService rcsMerchantSeriesService;

    /**
     * 串关配置页面接口 赛种总配置 以及 串关总配置
     */
    @GetMapping(value = "/queryData")
    @ApiOperation(value = "串关配置页面接口 赛种总配置 以及 串关总配置")
    public HttpResponse<RcsMerchantSeriesRespVo> queryData() {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(rcsMerchantSeriesService.queryData(), linkId);
        }  catch (Exception e) {
            log.error(String.format("串关配置页面接口查询异常 linkId: %s 错误详情: %s",linkId,e.getMessage()));
            return HttpResponse.failToMsg("串关配置页面接口 赛种总配置 以及 串关总配置 查询异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }



    @PostMapping(value = "/updateData")
    @ApiOperation(value = "串关配置页面接口 赛种总配置 以及 串关总配置 更新")
    public HttpResponse updateData(@RequestBody RcsMerchantSeriesRespVo rcsMerchantSeriesRespVo, HttpServletRequest request) {
        String linkId = CommonUtils.mdcPut();
        try {
            rcsMerchantSeriesRespVo.setIp(IPUtil.getRequestIp(request));
            rcsMerchantSeriesService.updateData(rcsMerchantSeriesRespVo);
            return HttpResponse.success(linkId);
        }  catch (Exception e) {
            log.error(String.format("串关配置页面接口修改异常 linkId: %s 错误详情: %s",linkId,e.getMessage()));
            return HttpResponse.failToMsg("串关配置页面接口 赛种总配置 以及 串关总配置 更新异常！", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }


}
