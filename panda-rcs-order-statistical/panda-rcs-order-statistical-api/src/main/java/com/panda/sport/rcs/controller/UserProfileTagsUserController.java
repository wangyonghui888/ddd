package com.panda.sport.rcs.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.api.request.TagUserModifyReqVo;
import com.panda.sport.rcs.db.entity.UserProfileTagUserRelation;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户画像 标签 用户关系维护 前端控制器
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-02 13:31:24
 */
@Api(tags = "数据维护-用户-标签关系维护")
@RestController
@RequestMapping("/userProfileTagsUser")
public class UserProfileTagsUserController {

    Logger log = LoggerFactory.getLogger(UserProfileTagsUserController.class);

    @Autowired
    IUserProfileTagUserRelationService userProfileTagUserRelationService;
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    @ApiOperation(value = "手工确认用户标签", notes = "")
    @RequestMapping(value = "/updateUserTage", method = {RequestMethod.POST})
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> updateUserTage(@RequestBody @Valid TagUserModifyReqVo vo) {
        //更新关系表
        LambdaUpdateWrapper<UserProfileTagUserRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserProfileTagUserRelation::getUserId, vo.getUserId());
        updateWrapper.eq(UserProfileTagUserRelation::getTagId, vo.getTagId());
        updateWrapper.set(UserProfileTagUserRelation::getStatus, 1);
        boolean flag = userProfileTagUserRelationService.update(updateWrapper);
        log.info("手工确认用户标签完成{},{}", JSONObject.toJSONString(vo), flag);
        if (flag) {
            //记录变更记录
            LambdaQueryWrapper<UserProfileUserTagChangeRecord> changeRecordUpdateWrapper = new LambdaQueryWrapper<>();
            changeRecordUpdateWrapper.eq(UserProfileUserTagChangeRecord::getUserId, vo.getUserId());
            changeRecordUpdateWrapper.eq(UserProfileUserTagChangeRecord::getChangeAfter, vo.getTagId());
            UserProfileUserTagChangeRecord bean = userProfileUserTagChangeRecordService.getOne(changeRecordUpdateWrapper);
            bean.setChangeManner(vo.getManager());
            bean.setChangeDetail(bean.getChangeDetail() + "手工确认完成" + LocalDateTimeUtil.now());
            userProfileUserTagChangeRecordService.updateById(bean);
            return Result.succes(flag);
        }
        return Result.fail("操作失败,未找到需要更新的记录");
    }

}
