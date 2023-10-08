package com.panda.sport.rcs.controller;


import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.customdb.service.ISportTypeService;
import com.panda.sport.rcs.db.entity.SSport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 标准球类表. 【数据来自融合表：standard_sport_type】 前端控制器
 * </p>
 *
 * @author dorich
 * @since 2020-07-17
 */
@RestController
@RequestMapping("/sport")
@Api(tags = "运动种类查询控制器")
public class SSportController {

    @Autowired
    ISportTypeService sportTypeService;

    @ApiOperation(value = "列表分页")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public Result<List<SSport>> list() {

        return Result.succes(sportTypeService.query());

    }


}
