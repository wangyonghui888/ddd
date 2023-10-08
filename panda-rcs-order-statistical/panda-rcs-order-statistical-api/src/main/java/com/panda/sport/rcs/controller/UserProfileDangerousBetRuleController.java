package com.panda.sport.rcs.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.vo.api.request.DangerousListReqVo;
import com.panda.sport.rcs.common.vo.api.request.IdReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserProfileDangerousBetRuleAddReqVo;
import com.panda.sport.rcs.common.vo.api.response.DangerousListResVo;
import com.panda.sport.rcs.customdb.service.ISportTypeService;
import com.panda.sport.rcs.db.entity.SSport;
import com.panda.sport.rcs.db.entity.UserProfileDangerousBetRule;
import com.panda.sport.rcs.db.entity.UserProfileRule;
import com.panda.sport.rcs.db.service.IUserProfileDangerousBetRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 危险投注表 前端控制器
 * </p>
 *
 * @author lithan auto
 * @since 2020-06-24
 */
@Api(tags = "数据维护-危险投注")
@RestController
@RequestMapping("/dangerous")
public class UserProfileDangerousBetRuleController {

    @Autowired
    IUserProfileDangerousBetRuleService dangerousBetRuleService;

    @Autowired
    ISportTypeService sportTypeService;


    @ApiOperation(value = "列表 分页")
    @RequestMapping(value = "/list", method = {RequestMethod.POST})
    public Result<IPage<DangerousListResVo>> list(@RequestBody @Valid DangerousListReqVo vo, @RequestHeader("lang") String lang) {
        //条件
        LambdaQueryWrapper<UserProfileDangerousBetRule> wrapper = new LambdaQueryWrapper<>();
        /*** 如果运动种类为,则需要全部查询 ***/
        if(null != vo.getSportId() && 0!= vo.getSportId()) {
            wrapper.eq(ObjectUtils.isNotEmpty(vo.getSportId()), UserProfileDangerousBetRule::getSportId, vo.getSportId());
        }
        if(!StringUtils.isEmpty(vo.getRuleName())) {
            wrapper.like(ObjectUtils.isNotEmpty(vo.getRuleName()), UserProfileDangerousBetRule::getRuleName, vo.getRuleName());
        }

        //分页数据
        Page<UserProfileDangerousBetRule> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserProfileDangerousBetRule> pageData = dangerousBetRuleService.page(pageParam, wrapper);
        //vo转换
        List<DangerousListResVo> list = CopyUtils.clone(pageData.getRecords(), DangerousListResVo.class);
        IPage<DangerousListResVo> pageResult = CopyUtils.copyPage(pageData, list);
        
        /*** 补充运动种类名称 ***/
        List<SSport> sports = sportTypeService.query();
        Map<Long, String> sSportMap = sports.stream().collect(Collectors.toMap(SSport::getId,SSport::getName));
        sSportMap.put(0L,"全部");

        pageResult.getRecords().forEach(e -> {
            e.setSportName(sSportMap.get(new Long(e.getSportId())));
            //国际化
//            if(lang.equals("en")){
//                e.setRuleName(e.getEnglishRuleName());
//            }
        });
        /*** 增加sportName  ***/ 
        return Result.succes(pageResult);
    }

    
    @ApiOperation(value = "查询")
    @RequestMapping(value = "/getById", method = {RequestMethod.POST})
    public Result<UserProfileDangerousBetRule> getById(@RequestBody @Valid IdReqVo vo, @RequestHeader("lang") String lang) {
        UserProfileDangerousBetRule entity = dangerousBetRuleService.getById(vo.getId());
        //国际化
        if(lang.equals("en")){
            entity.setRuleName(entity.getEnglishRuleName());
        }

        if (ObjectUtils.isEmpty(entity)) {
            return Result.fail("数据不存在");
        }
        return Result.succes(entity);
    }

    
    @ApiOperation(value = "新增", notes = "新增")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Result<Boolean> add(@RequestBody @Valid UserProfileDangerousBetRuleAddReqVo vo) {
        //检查code是否重复
        UserProfileDangerousBetRule entity = CopyUtils.clone(vo, UserProfileDangerousBetRule.class);
        LambdaUpdateWrapper<UserProfileDangerousBetRule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.orderByDesc(UserProfileDangerousBetRule::getId);
        wrapper.last("limit 1");
        UserProfileDangerousBetRule last = dangerousBetRuleService.getOne(wrapper);
        String lastCode = last.getDangerousCode().replace("d", "");
        lastCode = "d" + (Integer.valueOf(lastCode) + 1);
        entity.setDangerousCode(lastCode);
        boolean flag = dangerousBetRuleService.save(entity);
        return Result.succes(flag);
    }

    
    @ApiOperation(value = "编辑", notes = "编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<Boolean> edit(@RequestBody @Valid UserProfileDangerousBetRule vo) {
        //检查code是否重复
        if(ObjectUtils.isEmpty(vo.getId())) {
            return Result.fail("id不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getDangerousCode())) {
            return Result.fail("规则代码不能为空");
        }
        LambdaUpdateWrapper<UserProfileDangerousBetRule> wrapper = new LambdaUpdateWrapper<>();
        wrapper.ne(UserProfileDangerousBetRule::getId, vo.getId());
        wrapper.eq(UserProfileDangerousBetRule::getDangerousCode, vo.getDangerousCode());
        List<UserProfileDangerousBetRule> list = dangerousBetRuleService.list(wrapper);
        if (ObjectUtils.isNotEmpty(list)) {
            return Result.fail("规则代码不能重复" + vo.getDangerousCode());
        }
        boolean flag = dangerousBetRuleService.updateById(vo);
        if (!flag) {
            return Result.fail("数据未修改");
        }
        return Result.succes(flag);
    }


    @ApiOperation(value = "删除", notes = "")
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public Result<Boolean> del(@RequestBody @Valid IdReqVo vo) {
        boolean flag = dangerousBetRuleService.removeById(vo.getId());
        if (!flag) {
            return Result.fail("数据不存在");
        }
        return Result.succes(flag);
    }
}
