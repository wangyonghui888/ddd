package com.panda.sport.rcs.limit.controller;

import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.service.UserLimitServiceImpl;
import com.panda.sport.rcs.limit.vo.AvailableLimitQueryReqVo;
import com.panda.sport.rcs.limit.vo.SeriesUserAvailableLimitResVo;
import com.panda.sport.rcs.limit.vo.StrayUserAvailableLimitResVo;
import com.panda.sport.rcs.utils.CommonUtils;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 串关
 * @Author : Paca
 * @Date : 2021-12-04 17:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping("/limit/series")
public class SeriesLimitController {

    @Autowired
    private UserLimitServiceImpl userLimitService;

    @PostMapping("/userAvailableLimitQuery")
    public HttpResponse<SeriesUserAvailableLimitResVo> userAvailableLimitQuery(@RequestBody AvailableLimitQueryReqVo reqVo) {
        String linkId = CommonUtils.mdcPut();
        try {
            SeriesUserAvailableLimitResVo resVo = userLimitService.getSeriesUserAvailableLimit(reqVo);
            return HttpResponse.success(resVo, linkId);
        } catch (RcsServiceException e) {
            return HttpResponse.failToMsg(e.getErrorMassage(), linkId);
        } catch (Exception e) {
            return HttpResponse.failToMsg("用户串关可用额度查询异常", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }


    @PostMapping("/userStrayLimitQuery")
    public HttpResponse<StrayUserAvailableLimitResVo> userStrayLimitQuery(@RequestBody AvailableLimitQueryReqVo reqVo) {
        String linkId = CommonUtils.mdcPut();
        try {
            return HttpResponse.success(userLimitService.userStrayLimitQuery(reqVo), linkId);
        } catch (RcsServiceException e) {
            return HttpResponse.failToMsg(e.getErrorMassage(), linkId);
        } catch (Exception e) {
            log.error("用户串关可用额度查询异常:", e);
            return HttpResponse.failToMsg("用户串关可用额度查询异常", linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }
}
