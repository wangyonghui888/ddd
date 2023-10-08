package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.ListByGroupAndUserNumResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserByGroupIdResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserResVo;
import com.panda.sport.rcs.db.entity.TUserGroupBetRate;
import com.panda.sport.rcs.db.entity.UserProfileGroup;
import com.panda.sport.rcs.db.entity.UserProfileGroupUserRelation;
import com.panda.sport.rcs.db.service.ITUserGroupBetRateService;
import com.panda.sport.rcs.db.service.IUserProfileGroupService;
import com.panda.sport.rcs.db.service.IUserProfileGroupUserRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户组管理
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Api(tags = "数据维护-用户组管理")
@RestController
@RequestMapping("/userProfileGroup")
public class UserProfileGroupController {

    Logger log = LoggerFactory.getLogger(UserProfileGroupController.class);

    @Autowired
    IUserProfileGroupService profileGroupService;

    @Autowired
    IUserProfileGroupUserRelationService userRelationService;

    @Autowired
    ITUserGroupBetRateService userGroupBetRateService;

    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;

    @Autowired
    private IUserProfileGroupUserRelationService relationService;

    @ApiOperation(value = "查询玩家组列表及用户数")
    @RequestMapping(value = "/groupList", method = {RequestMethod.POST})
    public Result<IPage<ListByGroupAndUserNumResVo>> groupList(@RequestBody @Valid UserGroupListReqVo vo) {
        //分页数据
        Page<ListByGroupAndUserNumResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<ListByGroupAndUserNumResVo> list;
        try{
            list = profileGroupService.queryUserGroups(pageParam, vo);
        } catch (Exception e) {
            log.info("查询异常{}", e);
            return Result.fail("查询异常");
        }
        return Result.succes(list);
    }

    @ApiOperation(value = "查询用户列表")
    @RequestMapping(value = "/userList", method = {RequestMethod.POST})
    public Result<IPage<ListByUserResVo>> userList(@RequestBody @Valid UserListReqVo vo) {
        //分页数据
        Page<ListByUserResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<ListByUserResVo> list;
        try{
            list = profileGroupService.queryUsers(pageParam, vo);
            List<String> userList = new ArrayList<>();
            if(!ObjectUtils.isEmpty(list.getRecords())){
                for (ListByUserResVo record : list.getRecords()) {
                    userList.add(record.getUid());
                }
                if(!ObjectUtils.isEmpty(userList)){
                    //构造玩家组信息
                    List<ListByUserResVo> listByUserResVos = profileGroupService.queryGroupInfo(userList);
                    if(!ObjectUtils.isEmpty(listByUserResVos)){
                        for (ListByUserResVo record : list.getRecords()) {
                            for (ListByUserResVo listByUserResVo : listByUserResVos) {
                                if(record.getUid().equals(listByUserResVo.getUid())){
                                    record.setGroupId(listByUserResVo.getGroupId());
                                    record.setGroupName(listByUserResVo.getGroupName());
                                }
                            }
                        }
                    }
                    //构造二级标签信息
                    List<ListByUserResVo> listByUserResVos1 = profileGroupService.queryUserLevelInfo(userList);
                    if(!ObjectUtils.isEmpty(listByUserResVos1)){
                        for (ListByUserResVo record : list.getRecords()) {
                            for (ListByUserResVo listByUserResVo : listByUserResVos1) {
                                if(record.getUid().equals(listByUserResVo.getUid())){
                                    record.setSportJson(listByUserResVo.getSportJson());
                                    record.setTournamentJson(listByUserResVo.getTournamentJson());
                                    record.setOrderTypeJson(listByUserResVo.getOrderTypeJson());
                                    record.setPlayJson(listByUserResVo.getPlayJson());
                                    record.setOrderStageJson(listByUserResVo.getOrderStageJson());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("查询异常{}", e);
            return Result.fail("查询异常");
        }
        return Result.succes(list);
    }

    @ApiOperation(value = "根据玩家组ID查询用户列表")
    @RequestMapping(value = "/getUserListByGroupId", method = {RequestMethod.POST})
    public Result<IPage<ListByUserByGroupIdResVo>> getUserListByGroupId(@RequestBody @Valid UserListByGroupIdReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getGroupId())){
            return Result.fail("分组id不能为空");
        }
        //分页数据
        Page<ListByUserByGroupIdResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<ListByUserByGroupIdResVo> list;
        try{
            list = profileGroupService.queryUsersByGroupId(pageParam, vo.getGroupId());
            if(!ObjectUtils.isEmpty(list)){
                for (ListByUserByGroupIdResVo record : list.getRecords()) {
                    record.setProfit(record.getProfit().divide(BigDecimal.valueOf(100)));
                    record.setSevenDayAmount(record.getSevenDayAmount().divide(BigDecimal.valueOf(100)));
                }
            }
        } catch (Exception e) {
            log.info("查询异常{}", e);
            return Result.fail("查询异常");
        }
        return Result.succes(list);
    }

    @ApiOperation(value = "查询单个玩家组基本信息")
    @RequestMapping(value = "/getGroupById", method = {RequestMethod.POST})
    public Result<UserProfileGroup> getGroupById(@RequestBody @Valid UserProfileGroup vo) {
        if(ObjectUtils.isEmpty(vo.getId())){
            return Result.fail("id不能为空");
        }
        UserProfileGroup userProfileGroup;
        try{
            userProfileGroup = profileGroupService.getById(vo.getId());
        } catch (Exception e) {
            log.info("查询异常{}", e);
            return Result.fail("查询异常");
        }
        return Result.succes(userProfileGroup);
    }

    @ApiOperation(value = "新增玩家组（包含用户的移入，移出）")
    @RequestMapping(value = "/saveGroup", method = {RequestMethod.POST})
    public Result<Boolean> saveGroup(@RequestBody @Valid UserProfileGroupReqVo vo) {
//        if(ObjectUtils.isEmpty(request.getHeader("user-id"))){
//            return Result.fail("请先登录");
//        }
        if(ObjectUtils.isEmpty(vo.getGroupName())){
            return Result.fail("玩家组名称不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getRemake())){
            return Result.fail("理由不能为空");
        }

        try {
            if(vo.getIsSaveBetRate()==2){
                LambdaQueryWrapper<UserProfileGroup> warpper = new LambdaQueryWrapper<>();
                warpper.eq(UserProfileGroup::getGroupName, vo.getGroupName());
                //根据玩家组名称去数据库查询是否存在
                UserProfileGroup one = profileGroupService.getOne(warpper);
                if(!ObjectUtils.isEmpty(one)){
                    //如果存在则提示
                    return Result.fail("玩家组名称已存在");
                }
                //vo.setModifyUser(request.getHeader("user-id"));//前端直接传操作人名称，数据库存名称不存操作人ID
                vo.setModifyTime(System.currentTimeMillis());

                profileGroupService.save(CopyUtils.clone(vo, UserProfileGroup.class));
                UserProfileGroup newOne = profileGroupService.getOne(warpper);
                if(vo.getUserId().length>0){
                    LambdaQueryWrapper<UserProfileGroupUserRelation> warpperRemove = new LambdaQueryWrapper<>();
                    warpperRemove.in(UserProfileGroupUserRelation::getUserId, vo.getUserId());
                    userRelationService.remove(warpperRemove);
                    List<UserProfileGroupUserRelation> list = new ArrayList<>();
                    for (int i = 0; i < vo.getUserId().length; i++) {
                        UserProfileGroupUserRelation save = new UserProfileGroupUserRelation();
                        save.setUserId(vo.getUserId()[i]);
                        save.setGroupId(newOne.getId());
                        list.add(save);
                    }
                    userRelationService.saveBatch(list);
                }
            }
        } catch (Exception e) {
            log.info("新增玩家组异常{}", e);
            return Result.fail("新增玩家组异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "修改玩家组（包含用户的移入，移出）")
    @RequestMapping(value = "/updateGroup", method = {RequestMethod.POST})
    public Result<Boolean> updateGroup(@RequestBody @Valid UserProfileGroupReqVo vo) {
//        if(ObjectUtils.isEmpty(request.getHeader("user-id"))){
//            return Result.fail("请先登录");
//        }
        if(ObjectUtils.isEmpty(vo.getGroupName())){
            return Result.fail("玩家组名称不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getRemake())){
            return Result.fail("理由不能为空");
        }
        LambdaQueryWrapper<UserProfileGroup> warpper = new LambdaQueryWrapper<>();
        warpper.eq(UserProfileGroup::getGroupName, vo.getGroupName());
        //根据玩家组名称去数据库查询是否存在
        UserProfileGroup one = profileGroupService.getOne(warpper);
        if(!ObjectUtils.isEmpty(one)){
            //如果存在则判断查询出来的ID是否与所传ID一样
            if(!one.getId().equals(vo.getId())){
                //如果不一样则提示
                return Result.fail("玩家组名称已存在");
            }
        }
        //vo.setModifyUser(request.getHeader("user-id"));//前端直接传操作人名称，数据库存名称不存操作人ID
        vo.setModifyTime(System.currentTimeMillis());
        try {

            if(vo.getIsSaveBetRate()==2){
                if(vo.getUserId().length>0) {//删除参数中玩家组里面所有用户在表中的的关联关系,然后根据玩家组ID关联所有用户ID
                    LambdaQueryWrapper<UserProfileGroupUserRelation> warpperRemove = new LambdaQueryWrapper<>();
                    warpperRemove.in(UserProfileGroupUserRelation::getUserId, vo.getUserId()).or().eq(UserProfileGroupUserRelation::getGroupId, vo.getId());
                    userRelationService.remove(warpperRemove);

                    List<UserProfileGroupUserRelation> list = new ArrayList<>();
                    for (Long aLong : vo.getUserId()) {
                        UserProfileGroupUserRelation save = new UserProfileGroupUserRelation();
                        save.setUserId(aLong);
                        save.setGroupId(vo.getId());
                        list.add(save);
                    }
                    userRelationService.saveBatch(list);
                }else{
                    LambdaQueryWrapper<UserProfileGroupUserRelation> warpperRemove = new LambdaQueryWrapper<>();
                    warpperRemove.eq(UserProfileGroupUserRelation::getGroupId, vo.getId());
                    userRelationService.remove(warpperRemove);
                }
                profileGroupService.updateById(CopyUtils.clone(vo, UserProfileGroup.class));


            }else if(vo.getIsSaveBetRate()==1){
                //修改玩家组风控措施
                HashMap<String, Object> rmv = new HashMap<>();
                rmv.put("group_id", vo.getId());
                userGroupBetRateService.removeByMap(rmv);

                updateUserGroupBetRate(vo.getUserId(), vo.getUserGroupBetRateReqVo(), vo.getModifyUser());
            }
        } catch (Exception e) {
            log.info("修改玩家组异常{}", e);
            return Result.fail("修改玩家组异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "删除玩家组")
    @RequestMapping(value = "/deleteGroup", method = {RequestMethod.POST})
    public Result<Boolean> deleteGroup(@RequestBody @Valid UserProfileGroup vo) {
        if(ObjectUtils.isEmpty(vo.getId())){
            return Result.fail("id不能为空");
        }

        LambdaQueryWrapper<UserProfileGroupUserRelation> warpper = new LambdaQueryWrapper<>();
        warpper.eq(UserProfileGroupUserRelation::getGroupId, vo.getId());

        LambdaQueryWrapper<TUserGroupBetRate> betRateWrapper = new LambdaQueryWrapper<>();
        betRateWrapper.eq(TUserGroupBetRate::getGroupId, vo.getId());
        try {
            //删除玩家组
            profileGroupService.removeById(vo.getId());
            //删除用户和玩家组关联表数据
            userRelationService.remove(warpper);
            //删除玩家组风控措施与玩家组关联表数据
            userGroupBetRateService.remove(betRateWrapper);
        } catch (Exception e) {
            log.info("删除玩家组异常{}", e);
            return Result.fail("删除玩家组异常");
        }
        return Result.succes();
    }


    @ApiOperation(value = "根据用户ID查询玩家组关联信息对象")
    @RequestMapping(value = "/getUserGroupRelationByUserId", method = {RequestMethod.POST})
    public Result<Boolean> getUserGroupRelationByUserId(@RequestBody @Valid UserProfileGroupUserRelation vo) {
        if(ObjectUtils.isEmpty(vo.getUserId())){
            return Result.fail("userId不能为空");
        }
        //根据ID查询玩家组关联信息对象
        LambdaQueryWrapper<UserProfileGroupUserRelation> warpper = new LambdaQueryWrapper<>();
        warpper.eq(UserProfileGroupUserRelation::getUserId, vo.getUserId());
        try {
            if(ObjectUtils.isEmpty(userRelationService.getOne(warpper))){
                return Result.succes(false);
            }
        } catch (Exception e) {
            log.info("移入用户至玩家组异常{}", e);
            return Result.fail("根据ID查询玩家组关联信息对象异常");
        }
        return Result.succes(true);
    }

    @ApiOperation(value = "根据玩家组id查询玩家组风控措施")
    @RequestMapping(value = "/getBetRateByGroupId", method = {RequestMethod.POST})
    public Result<List<TUserGroupBetRate>> getBetRateByGroupId(@RequestBody @Valid TUserGroupBetRate vo) {
        if(ObjectUtils.isEmpty(vo.getGroupId())){
            return Result.fail("groupId不能为空");
        }
        LambdaQueryWrapper<TUserGroupBetRate> warpper = new LambdaQueryWrapper<>();
        warpper.eq(TUserGroupBetRate::getGroupId, vo.getGroupId());
        List<TUserGroupBetRate> list;
        try {
            list = userGroupBetRateService.list(warpper);
        } catch (Exception e) {
            log.info("根据玩家组id查询玩家组风控措施异常{}", e);
            return Result.fail("根据玩家组id查询玩家组风控措施异常");
        }
        return Result.succes(list);
    }

    /**
     * 修改玩家组风控措施
     * @param vo
     */
    public void updateUserGroupBetRate(Long[] userId, UserGroupBetRateReqVo vo, String modifyUser){
        try {
            List<TUserGroupBetRate> list = CopyUtils.clone(vo.getUserGroupBetRateList(), TUserGroupBetRate.class);
            for (TUserGroupBetRate tUserGroupBetRate : list) {
                LambdaQueryWrapper<TUserGroupBetRate> warpper = new LambdaQueryWrapper<>();
                warpper.eq(TUserGroupBetRate::getGroupId, tUserGroupBetRate.getGroupId());
                warpper.eq(TUserGroupBetRate::getSportId, tUserGroupBetRate.getSportId());
                TUserGroupBetRate one = userGroupBetRateService.getOne(warpper);
                if(ObjectUtils.isEmpty(one)){
                    userGroupBetRateService.save(tUserGroupBetRate);
                }else{
                    one.setBetRate(tUserGroupBetRate.getBetRate());
                    userGroupBetRateService.updateById(one);
                }
            }

            //根据当前玩家组id查询出所有用户
//            List<Long> userList = Lists.newArrayList();
//            LambdaQueryWrapper<UserProfileGroupUserRelation> warpper = new LambdaQueryWrapper<>();
//            warpper.eq(UserProfileGroupUserRelation::getGroupId, vo.getUserGroupBetRateList().get(0).getGroupId());
//            List<UserProfileGroupUserRelation> relationList = relationService.list(warpper);
//            if(relationList.size()>0){
//                for (UserProfileGroupUserRelation relation : relationList) {
//                    userList.add(relation.getUserId());
//                }
//            }

            //组装数据
            Map<String, Object> map = new HashMap<>();
            map.put("userList", userId);
            map.put("betRateConfig", vo);
            map.put("modifyUser", modifyUser);

            //发消息到trade，修改该玩家组内所有用户按照当前玩家组的“风控措施”配置进行更新
            producerSendMessageUtils.sendMessage("USER_GROUP_BET_RATE_TOPIC", "USER_GROUP_BET_RATE_TAG", JSONObject.toJSONString(map));
        } catch (Exception e) {
            log.info("修改玩家组风控措施异常{}", e);
        }
    }
}
