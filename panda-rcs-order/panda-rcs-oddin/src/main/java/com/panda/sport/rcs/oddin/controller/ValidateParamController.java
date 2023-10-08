package com.panda.sport.rcs.oddin.controller;

import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.rcs.oddin.entity.ValidateParamDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: Conway
 * @Date: 2023/08/13 17:33
 * 用标签自动校验入参测试controller
 **/
@Slf4j
@RestController
@RequestMapping("/v1/test")
@Api(value = "提供给电竞请求oddIn数据商接口")
public class ValidateParamController {

    /**
     * 限额接口
     *
     * @param requestParam
     * @return
     */
    @PostMapping(value = "/validatedParam")
    @ResponseBody
    @ApiOperation(value = "限额请求接口")
    @ApiParam(name = "TicketDto", value = "requestParam入参")
    public Response queryMaxBetMoneyBySelect(@RequestBody @Validated ValidateParamDto dto) {

        return Response.success(dto);
    }

}
