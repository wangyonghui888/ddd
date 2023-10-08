package com.panda.sport.rcs.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.constants.Constants;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.vo.api.request.IdReqVo;
import com.panda.sport.rcs.common.vo.api.request.RuleListReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserProfileRuleAddReqVo;
import com.panda.sport.rcs.common.vo.api.response.RuleListResVo;
import com.panda.sport.rcs.common.vo.api.response.RuleTypeResVo;
import com.panda.sport.rcs.db.entity.UserProfileRule;
import com.panda.sport.rcs.db.service.IUserProfileRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 规则管理表 前端控制器
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-25
 */
@Api(tags = "数据维护-规则维护")
@RestController
@RequestMapping("/userProfileRule")
public class UserProfileRuleController {
    @Autowired
    IUserProfileRuleService userProfileRuleService;

    @ApiOperation(value = "列表 分页")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public Result<IPage<RuleListResVo>> list(@RequestBody @Valid RuleListReqVo vo) {
        //条件
        LambdaQueryWrapper<UserProfileRule> wrapper = new LambdaQueryWrapper<>();
        if (0 != vo.getRuleType()) {
            wrapper.eq(ObjectUtils.isNotEmpty(vo.getRuleType()), UserProfileRule::getRuleType, vo.getRuleType());
        }
        if(!StringUtils.isEmpty(vo.getRuleName())) {
            wrapper.like(ObjectUtils.isNotEmpty(vo.getRuleName()), UserProfileRule::getRuleName, vo.getRuleName());
        }
        //分页数据
        Page<UserProfileRule> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserProfileRule> pageData = userProfileRuleService.page(pageParam, wrapper);
        //vo转换
        List<RuleListResVo> list = CopyUtils.clone(pageData.getRecords(), RuleListResVo.class);
        IPage<RuleListResVo> pageResult = CopyUtils.copyPage(pageData, list);
        return Result.succes(pageResult);
    }

    @ApiOperation(value = "查询")
    @RequestMapping(value = "/getById", method = {RequestMethod.POST})
    public Result<UserProfileRule> getById(@RequestBody @Valid IdReqVo vo) {
        UserProfileRule entity = userProfileRuleService.getById(vo.getId());
        return Result.succes(entity);
    }

    @ApiOperation(value = "新增", notes = "")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Result<Boolean> add(@RequestBody @Valid UserProfileRuleAddReqVo vo) {
//        LambdaUpdateWrapper<UserProfileRule> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(UserProfileRule::getRuleCode, vo.getRuleCode());
//        List<UserProfileRule> list = userProfileRuleService.list(wrapper);
//        if (ObjectUtils.isNotEmpty(list)) {
//            return Result.fail("规则代码不能重复" + vo.getRuleCode());
//        }
        UserProfileRule entity = CopyUtils.clone(vo, UserProfileRule.class);

        LambdaUpdateWrapper<UserProfileRule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.orderByDesc(UserProfileRule::getId);
        wrapper.last("limit 1");
        UserProfileRule last = userProfileRuleService.getOne(wrapper);
        String lastCode = last.getRuleCode().replace("R", "");
        lastCode = "R" + (Integer.valueOf(lastCode) + 1);
        entity.setRuleCode(lastCode);

        boolean flag = userProfileRuleService.save(entity);
        return Result.succes(flag);
    }

    @ApiOperation(value = "编辑", notes = "")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<Boolean> edit(@RequestBody @Valid UserProfileRule vo) {
        //检查code是否重复
        if(ObjectUtils.isEmpty(vo.getId())) {
            return Result.fail("id不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getRuleCode())) {
            return Result.fail("规则代码不能为空");
        }
        LambdaUpdateWrapper<UserProfileRule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserProfileRule::getRuleCode, vo.getRuleCode());
        wrapper.ne(UserProfileRule::getId, vo.getId());
        List<UserProfileRule> list = userProfileRuleService.list(wrapper);
        if (ObjectUtils.isNotEmpty(list)) {
            return Result.fail("规则代码不能重复" + vo.getRuleCode());
        }
        UserProfileRule entity = CopyUtils.clone(vo, UserProfileRule.class);
        boolean flag = userProfileRuleService.updateById(entity);
        return Result.succes(flag);
    }


    @ApiOperation(value = "删除", notes = "")
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public Result<Boolean> del(@RequestBody @Valid IdReqVo vo) {
        boolean flag = userProfileRuleService.removeById(vo.getId());
        if (!flag) {
            return Result.fail("数据不存在");
        }
        return Result.succes(flag);
    }

    @ApiOperation(value = "类型获取", notes = "")
    @RequestMapping(value = "/getRuleTypeList", method = {RequestMethod.POST})
    public Result<List<RuleTypeResVo>> getRuleTypeList(@RequestHeader("lang") String lang) {
        List<RuleTypeResVo> list = new ArrayList<>();
        Constants.ruleTypeMap.forEach((key, value) -> {
            RuleTypeResVo typeResVo = new RuleTypeResVo();
            typeResVo.setTypeId(key);
            typeResVo.setTypeName(value);
            if(key==1){
                typeResVo.setEnglishTypeName("BasicFeatureType");
            }else if(key==2){
                typeResVo.setEnglishTypeName("BetFeatureType");
            }else if(key==3){
                typeResVo.setEnglishTypeName("IPFeatureType");
            }else if(key==4){
                typeResVo.setEnglishTypeName("FinanceFeatureType");
            }
            //国际化
            if(lang.equals("en")){
                typeResVo.setTypeName(typeResVo.getEnglishTypeName());
            }
            list.add(typeResVo);
        });
        return Result.succes(list);
    }


}
