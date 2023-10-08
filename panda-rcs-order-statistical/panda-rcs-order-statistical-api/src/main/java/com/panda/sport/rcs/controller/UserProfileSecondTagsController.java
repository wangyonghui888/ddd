package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.UserProfileSecondTagListResVo;
import com.panda.sport.rcs.db.entity.*;
import com.panda.sport.rcs.db.service.IUserProfileSecondTagsService;
import com.panda.sport.rcs.service.UserProfileSecondTagsLogService;
import com.panda.sport.rcs.service.UserProfileSecondTagsLogVo;
import com.panda.sport.rcs.utils.IPUtil;
import com.panda.sport.rcs.utils.TradeUserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.sport.rcs.utils.Constants.UPDATE_USER_SECOND_TAGS_TOPIC;
import static java.util.Objects.nonNull;

@Api(tags = "数据维护-二级标签维护")
@RestController
@RequestMapping("/userProfileSecondTags")
@Slf4j
public class UserProfileSecondTagsController {
    @Autowired
    private IUserProfileSecondTagsService userProfileSecondTagsService;
    @Autowired
    private ProducerSendMessageUtils producerSendMessageUtils;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @ApiOperation(value = "列表 分页")
    @RequestMapping(value = "/page", method = {RequestMethod.POST})
    public Result<IPage<UserProfileSecondTagListResVo>> page(@RequestBody @Valid TagListReqVo vo) {
        LambdaQueryWrapper<UserProfileSecondTags> wrapper = new LambdaQueryWrapper<>();
        //分页数据
        Page<UserProfileSecondTags> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserProfileSecondTags> pageData = userProfileSecondTagsService.page(pageParam, wrapper);
        //vo转换
        List<UserProfileSecondTagListResVo> list = new ArrayList<>();
        pageData.getRecords().forEach(userProfileSecondTags -> list.add(build(userProfileSecondTags)));
        IPage<UserProfileSecondTagListResVo> resList = CopyUtils.copyPage(pageData, list);
        return Result.succes(resList);
    }

    @ApiOperation(value = "新增二级标签")
    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    public Result<Boolean> add(@RequestBody @Valid List<UserProfileSecondTagListReqVo> voList, HttpServletRequest request) {
        // 傳MQ給業務 更新二級標籤緩存用
        List<Long> idsList = new ArrayList<>();
        if (ObjectUtils.isEmpty(voList)) {
            return Result.fail("内容不能为空");
        }
        for (UserProfileSecondTagListReqVo vo : voList) {
            try {
                //只能有一个标签
                if (vo.getTagNames().get("zs").isEmpty()) {
                    return Result.fail("标签名不能为空");
                } else {
                    QueryWrapper<UserProfileSecondTags> querySecondTags = new QueryWrapper<>();
                    querySecondTags.lambda().eq(UserProfileSecondTags::getTagName, vo.getTagNames().get("zs"));
                    if (!ObjectUtils.isEmpty(userProfileSecondTagsService.getOne(querySecondTags))) {
                        return Result.fail("标签名重复:" + vo.getTagNames().get("zs"));
                    }
                }
                //只能有一个标签
                if (vo.getTagNames().get("en").isEmpty()) {
                    return Result.fail("英文标签名不能为空");
                } else {
                    QueryWrapper<UserProfileSecondTags> querySecondTags = new QueryWrapper<>();
                    querySecondTags.lambda().eq(UserProfileSecondTags::getEnglishTagName, vo.getTagNames().get("en"));
                    if (!ObjectUtils.isEmpty(userProfileSecondTagsService.getOne(querySecondTags))) {
                        return Result.fail("英文标签名重复:" + vo.getTagNames().get("en"));
                    }
                }
                vo.setUpdateTime(System.currentTimeMillis());
                UserProfileSecondTags userProfileSecondTags = CopyUtils.clone(vo, UserProfileSecondTags.class);
                userProfileSecondTags.setTagName(vo.getTagNames().get("zs"));
                userProfileSecondTags.setEnglishTagName(vo.getTagNames().get("en"));
                userProfileSecondTags.setTagDetail(vo.getTagDetails().get("zs"));
                userProfileSecondTags.setEnglishTagDetail(vo.getTagDetails().get("en"));
                int result = userProfileSecondTagsService.insert(userProfileSecondTags);
                if (result > 0) {
                    QueryWrapper<UserProfileSecondTags> queryUserTags = new QueryWrapper<>();
                    queryUserTags.lambda().orderByDesc(UserProfileSecondTags::getId).last("limit 1");
                    UserProfileSecondTags newUserProfileSecondTags = userProfileSecondTagsService.getOne(queryUserTags);
                    idsList.add(newUserProfileSecondTags.getId());

                    //修改日志收集
                    String userId = TradeUserUtils.getUserId().toString();
                    UserProfileSecondTagsLogVo userProfileSecondTagsLogVo = new UserProfileSecondTagsLogVo(
                            null, newUserProfileSecondTags, userId);
                    List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(
                            new UserProfileSecondTagsLogService(userProfileSecondTagsLogVo)).get();
                    if (CollectionUtils.isNotEmpty(listFuture)) {
                        //添加操作IP
                        String ip = IPUtil.getRequestIp(request);
                        for (RcsQuotaBusinessLimitLog ipLog : listFuture) {
                            ipLog.setIp(ip);
                        }
                        String arrString = JSONArray.toJSONString(listFuture);
                        log.info("标签管理->新增标签管理处理条数{}", listFuture.size());
                        producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,
                                null,
                                com.panda.sport.rcs.utils.Constants.logCode,
                                arrString);
                    }
                }
            } catch (Exception e) {
                log.error("二级标签新增操作异常:" + e);
                return Result.fail("二级标签新增操作异常");
            }
        }
        Map<String, Object> idsMap = new HashMap<>();
        idsMap.put("secondTagIds", idsList);
        // 通知業務 同步用戶二級標籤緩存
        producerSendMessageUtils.sendMessage(UPDATE_USER_SECOND_TAGS_TOPIC, UPDATE_USER_SECOND_TAGS_TOPIC, JSONObject.toJSONString(idsMap));
        return Result.succes();
    }

    @ApiOperation(value = "编辑", notes = "")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> edit(@RequestBody @Valid UserProfileSecondTagListReqVo vo, HttpServletRequest request) {
        //检查code是否重复
        if (null == vo.getId() || 0 == vo.getId()) {
            return Result.fail("id为空,编辑失败");
        }
        if (ObjectUtils.isEmpty(vo.getUpdateUserId())) {
            return Result.fail("操作人为空,编辑失败");
        }
        try {
            //只能有一个默认标签
            //只能有一个标签
            if (vo.getTagNames().get("zs").isEmpty()) {
                return Result.fail("标签名不能为空");
            } else {
                QueryWrapper<UserProfileSecondTags> querySecondTags = new QueryWrapper<>();
                querySecondTags.lambda().eq(UserProfileSecondTags::getTagName, vo.getTagNames().get("zs"));
                UserProfileSecondTags vo1 = userProfileSecondTagsService.getOne(querySecondTags);
                if (nonNull(vo1) && !vo1.getId().equals(vo.getId())) {
                    return Result.fail("标签名重复：" + vo.getTagNames().get("zs"));
                }
            }
            //只能有一个标签
            if (vo.getTagNames().get("en").isEmpty()) {
                return Result.fail("英文标签名不能为空");
            } else {
                QueryWrapper<UserProfileSecondTags> querySecondTags = new QueryWrapper<>();
                querySecondTags.lambda().eq(UserProfileSecondTags::getEnglishTagName, vo.getTagNames().get("en"));
                UserProfileSecondTags vo1 = userProfileSecondTagsService.getOne(querySecondTags);
                if (nonNull(vo1) && !vo1.getId().equals(vo.getId())) {
                    return Result.fail("英文标签名重复：" + vo.getTagNames().get("en"));
                }
            }
            UserProfileSecondTags userProfileSecondTags = CopyUtils.clone(vo, UserProfileSecondTags.class);
            userProfileSecondTags.setTagName(vo.getTagNames().get("zs"));
            userProfileSecondTags.setEnglishTagName(vo.getTagNames().get("en"));
            userProfileSecondTags.setTagDetail(vo.getTagDetails().get("zs"));
            userProfileSecondTags.setEnglishTagDetail(vo.getTagDetails().get("en"));

            QueryWrapper<UserProfileSecondTags> queryOldUserTags = new QueryWrapper<>();
            queryOldUserTags.lambda().eq(UserProfileSecondTags::getId, vo.getId());
            UserProfileSecondTags oldTagEntity = userProfileSecondTagsService.getOne(queryOldUserTags);

            boolean result = userProfileSecondTagsService.updateById(userProfileSecondTags);
            if (result) {
                // 日誌生成
                String userId = TradeUserUtils.getUserId().toString();
                UserProfileSecondTagsLogVo userProfileSecondTagsLogVo = new UserProfileSecondTagsLogVo(
                        oldTagEntity, userProfileSecondTags, userId);
                List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(new UserProfileSecondTagsLogService(
                        userProfileSecondTagsLogVo)).get();
                if (CollectionUtils.isNotEmpty(listFuture)) {
                    //添加操作IP
                    String ip = IPUtil.getRequestIp(request);
                    for (RcsQuotaBusinessLimitLog ipLog : listFuture) {
                        ipLog.setIp(ip);
                    }
                    String arrString = JSONArray.toJSONString(listFuture);
                    log.info("标签管理->修改标签管理处理条数{}", listFuture.size());
                    producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,
                            null,
                            com.panda.sport.rcs.utils.Constants.logCode,
                            arrString);
                }
            }
        } catch (Exception e) {
            log.error("二级标签修改操作异常:" + e);
            return Result.fail("二级标签修改操作异常");
        }
        return Result.succes();
    }


    @ApiOperation(value = "删除", notes = "")
    @RequestMapping(value = "/del", method = {RequestMethod.POST})
    public Result<Boolean> del(@RequestBody @Valid List<IdReqVo> vo, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(vo)) {
            return Result.fail("内容不能为空");
        }
        // 先取出將被刪除的資料 日誌使用
        List<Long> voList = vo.stream().map(IdReqVo::getId).collect(Collectors.toList());
        Map<Long, UserProfileSecondTags> userProfileSecondMap = userProfileSecondTagsService.listByIds(voList).stream()
                .collect(Collectors.toMap(UserProfileSecondTags::getId, userProfileSecondTags -> userProfileSecondTags));

        boolean flag = userProfileSecondTagsService.removeByIds(voList);

        if (!flag) {
            return Result.fail("数据不存在");
        } else {
            for (Long v : voList) {
                try {
                    //日誌生成
                    String userId = TradeUserUtils.getUserId().toString();
                    UserProfileSecondTagsLogVo userProfileSecondTagsLogVo = new UserProfileSecondTagsLogVo(
                            userProfileSecondMap.get(v), null, userId);
                    List<RcsQuotaBusinessLimitLog> listFuture = taskExecutor.submit(new UserProfileSecondTagsLogService(
                            userProfileSecondTagsLogVo)).get();
                    if (CollectionUtils.isNotEmpty(listFuture)) {
                        //添加操作IP
                        String ip = IPUtil.getRequestIp(request);
                        listFuture.get(0).setIp(ip);
                        String arrString = JSONArray.toJSONString(listFuture);
                        log.info("标签管理->刪除标签管理处理条数{}", listFuture.size());
                        producerSendMessageUtils.sendMessage(com.panda.sport.rcs.utils.Constants.RCS_BUSINESS_LOG_SAVE,
                                null, com.panda.sport.rcs.utils.Constants.logCode, arrString);
                    }
                } catch (Exception e) {
                    log.error("操作异常", e);
                    return Result.fail("操作异常" + e);
                }
            }
            Map<String, Object> idsMap = new HashMap<>();
            idsMap.put("secondTagIds", voList);
            // 通知業務 同步用戶二級標籤緩存
            producerSendMessageUtils.sendMessage(UPDATE_USER_SECOND_TAGS_TOPIC, UPDATE_USER_SECOND_TAGS_TOPIC, JSONObject.toJSONString(idsMap));
        }
        return Result.succes();
    }

    @ApiOperation(value = "根据标签名称查询标签列表")
    @RequestMapping(value = "/listByName", method = {RequestMethod.POST})
    public Result<List<UserProfileSecondTagListResVo>> listByType(@RequestBody @Valid UserProfileSecondTags vo) {
        LambdaQueryWrapper<UserProfileSecondTags> wrapper = new LambdaQueryWrapper<>();
        if (Strings.isNotBlank(vo.getEnglishTagName())) {
            wrapper.like(UserProfileSecondTags::getEnglishTagName, vo.getEnglishTagName());
        } else if (Strings.isNotBlank(vo.getTagName())) {
            wrapper.like(UserProfileSecondTags::getTagName, vo.getTagName());
        }
        List<UserProfileSecondTags> pageData = userProfileSecondTagsService.list(wrapper);
        List<UserProfileSecondTagListResVo> result = new ArrayList<>();
        pageData.forEach(userProfileSecondTags -> result.add(build(userProfileSecondTags)));
        return Result.succes(result);
    }

    /**
     * 重组中英文标签和说明
     */
    private UserProfileSecondTagListResVo build(UserProfileSecondTags userProfileSecondTags) {
        UserProfileSecondTagListResVo secondTagListResVo = CopyUtils.clone(userProfileSecondTags, UserProfileSecondTagListResVo.class);
        Map<String, String> tagNameMap = new HashMap<>();
        tagNameMap.put("en", userProfileSecondTags.getEnglishTagName());
        tagNameMap.put("zs", userProfileSecondTags.getTagName());

        Map<String, String> tagDetailMap = new HashMap<>();
        tagDetailMap.put("en", userProfileSecondTags.getEnglishTagDetail());
        tagDetailMap.put("zs", userProfileSecondTags.getTagDetail());

        secondTagListResVo.setTagNames(tagNameMap);
        secondTagListResVo.setTagDetails(tagDetailMap);
        return secondTagListResVo;
    }
}
