package com.panda.sport.rcs.db.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.db.entity.UserProfileTagUserRelation;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.entity.UserTagLastTime;
import com.panda.sport.rcs.db.mapper.UserProfileUserTagChangeRecordMapper;
import com.panda.sport.rcs.db.mapper.UserTagLastTimeMapper;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileTagsService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 标签变更记录表 每次变更只记录一条  服务实现类
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-02
 */

@Service
public class UserProfileUserTagChangeRecordServiceImpl extends ServiceImpl<UserProfileUserTagChangeRecordMapper, UserProfileUserTagChangeRecord> implements IUserProfileUserTagChangeRecordService{
    Logger log = LoggerFactory.getLogger(UserProfileUserTagChangeRecordServiceImpl.class);

    //创建自适应机器本身线程数量的线程池
//    Integer process = Runtime.getRuntime().availableProcessors();
//    ExecutorService executorService = new ThreadPoolExecutor(
//            10,
//            process,
//            2L,
//            TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(3),
//            Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.CallerRunsPolicy()
//    );


    private static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("threadName-%d").build();
    private static ThreadPoolExecutor executorService  = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(), threadFactory);

    @Autowired
    private UserProfileUserTagChangeRecordMapper mapper;

    @Autowired
    UserTagLastTimeMapper userTagLastTimeMapper;
    @Autowired
    IUserProfileUserTagChangeRecordService recordService;

    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;
    @Autowired
    IUserProfileTagUserRelationService tagUserRelationService;
    @Autowired
    IUserProfileTagsService tagsService;
    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @Override
    public IPage<UserProfileUserTagChangeRecordResVo> queryRecordList(Page<UserProfileUserTagChangeRecordResVo> page, UserProfileUserTagChangeRecordReqVo vo) {
        return mapper.queryRecordList(page, vo);
    }


    @Override
    public UserProfileUserTagChangeRecordResVo selectByUserId(Long userId) {
        return mapper.selectByUserId(userId);
    }

    @Override
    public IPage<UserBetTagChangeRecordResVo> queryBetTagChangeRecord(Page<UserBetTagChangeRecordResVo> page, UserBetTagChangeRecordReqVo vo) {
        return mapper.queryBetTagChangeRecord(page, vo);
    }

    @Override
    public IPage<AutoTagLogRecordResVo> queryAutoTagLogRecord(Page<AutoTagLogRecordResVo> page, AutoTagLogRecordReqVo vo) {
        return mapper.queryAutoTagLogRecord(page, vo);
    }

    @Override
    public IPage<UserExceptionResVo> queryUserExceptionRecord(Page<UserExceptionResVo> page, UserExceptionReqVo vo) {
        return mapper.queryUserExceptionRecord(page, vo);
    }

    @Override
    public IPage<UserBetTagChangeRecordResVo> queryBetTagChangeRecordByUserId(Page<UserBetTagChangeRecordResVo> page, UserInfoAndRecordReqVo reqVo) {
        return mapper.queryBetTagChangeRecordByUserId(page, reqVo);
    }

    @Override
    public UserInfoResVo queryUserInfoByUserId(UserInfoAndRecordReqVo reqVo) {
        return mapper.queryUserInfoByUserId(reqVo);
    }

    @Override
    public Integer updateUserBetTagChangeRecord(UserBetTagChangeReqVo vo) {
        return mapper.updateUserBetTagChangeRecord(vo);
    }

    @Override
    public void doLastTime(Long userId, Long tagId) {
        //记录最后跑任务的时间
        UserTagLastTime userTagLastTime = new UserTagLastTime();
        userTagLastTime.setUserId(userId);
        userTagLastTime.setLastTime(System.currentTimeMillis());
        userTagLastTime.setRamark("");

        LambdaQueryWrapper<UserTagLastTime> userTagLastTimeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userTagLastTimeLambdaQueryWrapper.eq(UserTagLastTime::getUserId, userId);
        UserTagLastTime queryEntity = userTagLastTimeMapper.selectOne(userTagLastTimeLambdaQueryWrapper);
        if (queryEntity == null) {
            try {
                userTagLastTimeMapper.insert(userTagLastTime);
            } catch (DuplicateKeyException e) {
                //并发情况下会发生重复查询userid 的情况 重新根据userid update
                userTagLastTimeMapper.update(userTagLastTime, new LambdaQueryWrapper<UserTagLastTime>().eq(UserTagLastTime::getUserId, userId));
            }
        } else {
            queryEntity.setRamark("");
            queryEntity.setLastTime(System.currentTimeMillis());
            userTagLastTimeMapper.updateById(queryEntity);
        }
        log.info("用户::{}::用戶标签时间记录  tagId:{}", userId, tagId);
    }

    @Override
    public void editRecord(UserBetTagChangeReqVo reqVo) {

        UserProfileTags userProfileTags = tagsService.getById(reqVo.getChangeTag());
        reqVo.setChangeTagName(userProfileTags != null ? userProfileTags.getTagName() : null);
        log.info("editRecord:{}",JSONObject.toJSONString(reqVo));
        /*******************商户审核逻辑***********************/
        if (!org.springframework.util.ObjectUtils.isEmpty(reqVo.getSubmitType()) && reqVo.getSubmitType().equals(1) && reqVo.getStatus() == 1) {
            producerSendMessageUtils.sendMessage("rcs_risk_merchant_manager_tag", JSONObject.toJSONString(reqVo));
            //需要提交到商户后台审核
            return;
        } else if (!org.springframework.util.ObjectUtils.isEmpty(reqVo.getSubmitType()) && reqVo.getSubmitType().equals(2)) {
            producerSendMessageUtils.sendMessage("rcs_risk_merchant_manager_tag", JSONObject.toJSONString(reqVo));
            //强制执行，不需要审核
        } else {
            //否则type=3的时候走正常逻辑
        }


            /*******************商户审核逻辑***********************/


            long systemTimeStr = System.currentTimeMillis();
            /**
             * 1.修改user_profile_user_tag_change_record表数据
             */
            //判断是忽略还是接受
            if(reqVo.getStatus().equals(1)){
                UserProfileUserTagChangeRecord one = userProfileUserTagChangeRecordService.getById(reqVo.getId());
                if(ObjectUtils.isNotEmpty(one)){//判断是否有需要设置为接收的记录，若没有则全部忽略
                    one.setChangeManner(reqVo.getChangeManner());
                    one.setOperateTime(systemTimeStr);
                    one.setRemark(reqVo.getRemark());
                    one.setStatus(1);
                    one.setChangeTag(reqVo.getChangeTag());
                    log.info("当前用户{},当前数据设为接受{}", one.getId(), JSONObject.toJSONString(one));
                    userProfileUserTagChangeRecordService.updateById(one);
                }

                //处理忽略数据
                LambdaQueryWrapper<UserProfileUserTagChangeRecord> loseWrapper = new LambdaQueryWrapper<>();
                loseWrapper.eq(UserProfileUserTagChangeRecord::getUserId, reqVo.getUserId());
                loseWrapper.eq(UserProfileUserTagChangeRecord::getStatus, 0);
                List<UserProfileUserTagChangeRecord> loseList = userProfileUserTagChangeRecordService.list(loseWrapper);
                if(!CollectionUtils.isEmpty(loseList)){
                    //获取到上面修改为接收的记录主键id，如果没有设置则对所有数据进行忽略处理
                    Long finalRecordId = one.getId();
                    if(!finalRecordId.equals(0L)){//判断是否有需要设置为接收的记录，若没有则全部忽略
                        loseList = loseList.stream().filter(e -> !e.getId().equals(finalRecordId)).collect(Collectors.toList());
                    }
                    if(!CollectionUtils.isEmpty(loseList)){
                        for (UserProfileUserTagChangeRecord loseRecord : loseList) {
                            loseRecord.setChangeManner(reqVo.getChangeManner());
                            loseRecord.setOperateTime(systemTimeStr);
                            loseRecord.setRemark(reqVo.getRemark());
                            loseRecord.setStatus(2);
                        }
                        log.info("当前用户{},除了recordId={}外,其他所有数据都设为忽略{}", reqVo.getUserId(), finalRecordId, JSONObject.toJSONString(loseList));
                        userProfileUserTagChangeRecordService.updateBatchById(loseList);
                    }
                }

                userProfileUserTagChangeRecordService.doLastTime(Long.valueOf(reqVo.getUserId()), reqVo.getChangeTag());
            }else{//如果点击忽略，则该用户所有未处理消息全部更为忽略
                LambdaQueryWrapper<UserProfileUserTagChangeRecord> loseWrapper = new LambdaQueryWrapper<>();
                loseWrapper.eq(UserProfileUserTagChangeRecord::getUserId, reqVo.getUserId());
                loseWrapper.eq(UserProfileUserTagChangeRecord::getStatus, 0);
                List<UserProfileUserTagChangeRecord> loseList = userProfileUserTagChangeRecordService.list(loseWrapper);
                for (UserProfileUserTagChangeRecord loseRecord : loseList) {
                    loseRecord.setChangeManner(reqVo.getChangeManner());
                    loseRecord.setOperateTime(systemTimeStr);
                    loseRecord.setRemark(reqVo.getRemark());
                    loseRecord.setStatus(2);
                }
                log.info("当前用户{},所有预警全都设为忽略{}", reqVo.getUserId(), JSONObject.toJSONString(loseList));
                userProfileUserTagChangeRecordService.updateBatchById(loseList);
            }

            /**
             * 2.修改用户标签user_profile_tag_user_relation表数据
             */
            //拿到当前所有投注特征标签
            LambdaQueryWrapper<UserProfileTags> tagsWrapper = new LambdaQueryWrapper<>();
            tagsWrapper.eq(UserProfileTags::getTagType, 2);
            List<UserProfileTags> list = tagsService.list(tagsWrapper);
            //拿到当前用户所有标签
            LambdaQueryWrapper<UserProfileTagUserRelation> userTagWrapper = new LambdaQueryWrapper<>();
            userTagWrapper.eq(UserProfileTagUserRelation::getUserId, reqVo.getUserId());
            List<UserProfileTagUserRelation> userTagList = tagUserRelationService.list(userTagWrapper);
            //所有投注特征标签与当前用户的所有标签对比，取得当前用户对应的投注特征标签（正常来说只有一条，避免报错这里取多条）
            List<Long> tags = new ArrayList<>();
            for (UserProfileTags tag : list) {
                for (UserProfileTagUserRelation tagUserRelation : userTagList) {
                    if(tag.getId().equals(tagUserRelation.getTagId())){
                        tags.add(tag.getId());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(tags)){
                //清空当前用户的所有投注特征标签
                LambdaQueryWrapper<UserProfileTagUserRelation> removeRelationWrapper = new LambdaQueryWrapper<>();
                removeRelationWrapper.eq(UserProfileTagUserRelation::getUserId, reqVo.getUserId());
                removeRelationWrapper.in(UserProfileTagUserRelation::getTagId, tags);
                tagUserRelationService.remove(removeRelationWrapper);
            }
            UserProfileTagUserRelation relation = new UserProfileTagUserRelation();
            relation.setUserId(reqVo.getUserId());
            //正常用户tagId=1
            relation.setTagId(reqVo.getChangeTag());
            relation.setStatus(1);
            tagUserRelationService.save(relation);

            /**
             * 3.修改风控库以及业务库对应用户的标签（通过MQ）
             */
            //这里需要前端直接调用 用户列表中 修改标签的接口


    }

    public int saveUserTagChange(UserSaveTagChangeReqVo tagChangeReqVo) {
        UserProfileUserTagChangeRecord record = new UserProfileUserTagChangeRecord();
        BeanUtils.copyProperties(tagChangeReqVo, record);
        record.setChangeType(1);
        record.setTagType(2);
        record.setChangeSuggest("2");
        record.setOperateTime(System.currentTimeMillis());
        record.setChangeManner("System");
        record.setStatus(1);
        record.setChangeSuggest("1");
        return mapper.insert(record);
    }

    public List<UserTagChangeRecordResVo> geTagChangeByUserId(Long userId) {
        List<UserTagChangeRecordResVo> list = new ArrayList<>();
        List<UserProfileUserTagChangeRecord> userProfileUserTagChangeRecords = mapper.selectList(new QueryWrapper<UserProfileUserTagChangeRecord>()
                .lambda().eq(UserProfileUserTagChangeRecord::getUserId, userId));
        if (!CollectionUtils.isEmpty(userProfileUserTagChangeRecords)) {
            userProfileUserTagChangeRecords.forEach(obj -> {
                UserProfileUserTagChangeRecord record = new UserProfileUserTagChangeRecord();
                BeanUtils.copyProperties(obj, record);
            });
        }
        return list;
    }

    /**
     * 批量迁移用户标签变更数据 t_user_level_relation_history
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRelationHistoryData() {
        log.info("进入batchRelationHistoryData====start");
        try {
            List<UserProfileUserTagChangeRecord> allList = mapper.selectUserLevelRelationHistoryList();
            log.info("allList=={}", allList.size());
            if (CollectionUtils.isEmpty(allList)) {
                return;
            }
            Map<Long, List<UserProfileUserTagChangeRecord>> collect = allList.stream().collect(Collectors.groupingBy(UserProfileUserTagChangeRecord::getUserId));
            log.info("collect size：{},分组后获取数据{}", collect.size());
            List<UserProfileUserTagChangeRecord> executeList = new ArrayList<>();
            collect.forEach((k, v) -> {
                //用户id
                Long userId = k;
                //对应的用户ID的所有变更记录
                List<UserProfileUserTagChangeRecord> recordList = v;
                //只是为了构造"修改后标签"
                List<Long> oldTagList = recordList.stream().map(UserProfileUserTagChangeRecord::getChangeBefore).collect(Collectors.toList());
                log.info("recordList=={}，oldTagList{}", recordList.size(), JSON.toJSON(oldTagList));
                for (int i = 0; i < recordList.size(); i++) {
                    UserProfileUserTagChangeRecord record = recordList.get(i);
                    Integer count = mapper.selectCount(new QueryWrapper<UserProfileUserTagChangeRecord>().lambda().eq(UserProfileUserTagChangeRecord::getChangeDetail, record.getChangeDetail()));
                    //log.info("record.getChangeDetail()：{}, count={},分组后获取数据", record.getChangeDetail(), count);
                    if (count > 0) {
                        continue;
                    }
                    if (i == 0) {
                        //查表
                        Long levelId = mapper.selectUserLevelId(userId);
                        record.setChangeAfter(levelId);
                    } else {
                        record.setChangeAfter(oldTagList.get(i - 1));
                    }
                    record.setChangeType(2);
                    record.setTagType(2);
                    record.setChangeSuggest("1");
                    record.setStatus(1);
                    executeList.add(record);
                }
            });
            log.info("batchRelationHistoryData 进入线程总数量：{}", executeList.size());
            /**
             * submit与execute 都是向线程池提交任务。
             * submit提交后执行提交类实现callable方法后重写的call方法，execute提交后执行实现Runnable的run方法
             * Runnable任务没有返回值，而Callable任务有返回值。
             * 并且Callable的call()方法只能通过ExecutorService的submit(Callable <T> task) 方法来执行
             * 多人同时提交时的线程控制：
             */
            Future<Boolean> a = executorService.submit(new BatchInsertServiceImpl(executeList, recordService));
            log.info("==batchRelationHistoryData==》end 当前分批查询数据，当前执行结果：{},总数量：{}", a.get(), executeList.size());
        } catch (Exception e) {
            log.error("批量迁移用户标签变更数据异常：{}", e.getMessage());
            e.printStackTrace();
        }
        log.info("进入batchRelationHistoryData====end");
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<UserProfileUserTagChangeRecord> allList = new ArrayList<>();
//        UserProfileUserTagChangeRecord record0 = new UserProfileUserTagChangeRecord();
//        record0.setChangeBefore(7L);
//        record0.setUserId(2L);
//        UserProfileUserTagChangeRecord record1 = new UserProfileUserTagChangeRecord();
//        record1.setChangeBefore(5L);
//        record1.setUserId(2L);
//        UserProfileUserTagChangeRecord record2 = new UserProfileUserTagChangeRecord();
//        record2.setChangeBefore(4L);
//        record2.setUserId(2L);
//        UserProfileUserTagChangeRecord record3 = new UserProfileUserTagChangeRecord();
//        record3.setChangeBefore(1L);
//        record3.setUserId(2L);
//        UserProfileUserTagChangeRecord record4 = new UserProfileUserTagChangeRecord();
//        record4.setChangeBefore(6L);
//        record4.setUserId(4L);
//        UserProfileUserTagChangeRecord record5 = new UserProfileUserTagChangeRecord();
//        record5.setChangeBefore(1L);
//        record5.setUserId(4L);
//
//        allList.add(record0);
//        allList.add(record1);
//        allList.add(record2);
//        allList.add(record3);
//        allList.add(record4);
//        allList.add(record5);

        for (int i = 0; i < 100000; i++) {
            UserProfileUserTagChangeRecord record = new UserProfileUserTagChangeRecord();
            record.setChangeBefore(7L);
            Long userId = 10L;
            if (i % 2 == 0) {
                userId = Long.valueOf(i + 1);
            }
            record.setUserId(userId);
            allList.add(record);
        }

        if (CollectionUtils.isEmpty(allList)) {
            return;
        }
        long start = System.currentTimeMillis();
        Map<Long, List<UserProfileUserTagChangeRecord>> collect = allList.stream().collect(Collectors.groupingBy(UserProfileUserTagChangeRecord::getUserId));
        //System.out.println("当前查询总历史数据：{},分组后获取数据" + allList.size() + "=111=" + collect.size());

        List<UserProfileUserTagChangeRecord> executeList = new ArrayList<>();

        collect.forEach((k, v) -> {
            //用户id
            Long userId = k;
            //对应的用户ID的所有变更记录
            List<UserProfileUserTagChangeRecord> recordList = v;
            //只是为了构造"修改后标签"
            List<Long> oldTagList = recordList.stream().map(UserProfileUserTagChangeRecord::getChangeBefore).collect(Collectors.toList());
            //System.out.println(JSON.toJSON(oldTagList));
            for (int i = 0; i < recordList.size(); i++) {
                UserProfileUserTagChangeRecord record = recordList.get(i);
                if (i == 0) {
                    //查表
                    record.setChangeAfter(99L);
                    //System.out.println("变更前：" + record.getChangeBefore() + " ； 变更后查询用户id：now_levelId=" + record.getChangeAfter());
                } else {
                    //System.out.println("变更前：" + record.getChangeBefore() + " ； 变更后：" + oldTagList.get(i - 1));
                    record.setChangeAfter(oldTagList.get(i - 1));
                }
                record.setChangeType(2);
                record.setTagType(2);
                record.setChangeSuggest("1");
                record.setStatus(1);
                executeList.add(record);
            }
            //保存操作
            System.out.println("result===" + JSON.toJSON(recordList));
        });
        new UserProfileUserTagChangeRecordServiceImpl().elist(executeList);
        long end = System.currentTimeMillis();
        System.out.println("总时间 = " + (end - start));
    }

    private void elist(List<UserProfileUserTagChangeRecord> executeList ){
        Future<Boolean> a = executorService.submit(new BatchInsertServiceImpl(executeList, null));
        try {
            System.out.println("doList==" + executeList.size() + "r==" + a.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
