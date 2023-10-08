package com.panda.sport.rcs.limit.controller.api;

import com.panda.sport.data.rcs.api.Request;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.limit.dto.api.UserSeriesAvailableLimitReqDto;
import com.panda.sport.rcs.limit.dto.api.UserSeriesAvailableLimitResDto;
import com.panda.sport.rcs.limit.dto.api.UserSingleAvailableLimitReqDto;
import com.panda.sport.rcs.limit.dto.api.UserSingleAvailableLimitResDto;
import com.panda.sport.rcs.limit.service.UserLimitServiceImpl;
import com.panda.sport.rcs.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 额度查询 API
 * @Author : Paca
 * @Date : 2021-12-26 21:36
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@RestController
@RequestMapping("/limit/api")
public class LimitApiController {

    @Autowired
    private UserLimitServiceImpl userLimitService;

    @PostMapping("/userSeriesAvailableLimit")
    public Response<UserSeriesAvailableLimitResDto> userSeriesAvailableLimit(@RequestBody Request<UserSeriesAvailableLimitReqDto> request) {
        String linkId = request.getGlobalId();
        if (StringUtils.isBlank(linkId)) {
            linkId = CommonUtils.mdcPut();
        } else {
            CommonUtils.mdcPut(linkId);
        }
        try {
            UserSeriesAvailableLimitReqDto reqDto = request.getData();
            UserSeriesAvailableLimitResDto resDto = userLimitService.userSeriesAvailableLimit(reqDto);
            Response success = Response.success(resDto);
            success.setMsg(linkId);
            return success;
        } catch (RcsServiceException e) {
            return Response.error(Response.FAIL, e.getErrorMassage() + "：" + linkId);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(Response.FAIL, "用户串关可用额度查询异常：" + linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }

    @PostMapping("/userSingleAvailableLimit")
    public Response<UserSingleAvailableLimitResDto> userSingleAvailableLimit(@RequestBody Request<UserSingleAvailableLimitReqDto> request) {
        String linkId = request.getGlobalId();
        if (StringUtils.isBlank(linkId)) {
            linkId = CommonUtils.mdcPut();
        } else {
            CommonUtils.mdcPut(linkId);
        }
        try {
            UserSingleAvailableLimitReqDto reqDto = request.getData();
            UserSingleAvailableLimitResDto resDto = userLimitService.userSingleAvailableLimit(reqDto);
            Response success = Response.success(resDto);
            success.setMsg(linkId);
            return success;
        } catch (RcsServiceException e) {
            return Response.error(Response.FAIL, e.getErrorMassage() + "：" + linkId);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(Response.FAIL, "用户单关可用额度查询异常：" + linkId);
        } finally {
            CommonUtils.mdcRemove();
        }
    }
}
