package com.panda.sport.rcs.db.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;

import java.util.List;

/**
 * <p>
 * service
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-02
 */
public interface IUserProfileUserTagChangeRecordService extends IService<UserProfileUserTagChangeRecord> {

    /**
     * @Description queryRecordList
     * @Param Page, UserProfileUserTagChangeRecordReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<UserProfileUserTagChangeRecordResVo>
     **/
    IPage<UserProfileUserTagChangeRecordResVo> queryRecordList(Page<UserProfileUserTagChangeRecordResVo> page, UserProfileUserTagChangeRecordReqVo vo);

    UserProfileUserTagChangeRecordResVo selectByUserId(Long userId);

    /**
     * @Description queryBetTagChangeRecord
     * @Param Page, UserBetTagChangeRecordReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<UserBetTagChangeRecordResVo>
     **/
    IPage<UserBetTagChangeRecordResVo> queryBetTagChangeRecord(Page<UserBetTagChangeRecordResVo> page, UserBetTagChangeRecordReqVo vo);

    /**
     * @Description queryAutoTagLogRecord
     * @Param Page, AutoTagLogRecordReqVo
     * @Author Kir
     * @Date  2022-02-26
     * @return IPage<AutoTagLogRecordResVo>
     **/
    IPage<AutoTagLogRecordResVo> queryAutoTagLogRecord(Page<AutoTagLogRecordResVo> page, AutoTagLogRecordReqVo vo);

    IPage<UserExceptionResVo> queryUserExceptionRecord(Page<UserExceptionResVo> page, UserExceptionReqVo vo);

    /**
     * @Description queryBetTagChangeRecordByUserId
     * @Param Page, UserInfoAndRecordReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<UserBetTagChangeRecordResVo>
     **/
    IPage<UserBetTagChangeRecordResVo> queryBetTagChangeRecordByUserId(Page<UserBetTagChangeRecordResVo> page, UserInfoAndRecordReqVo reqVo);

    /**
     * @param reqVo
     * @return
     */
    UserInfoResVo queryUserInfoByUserId(UserInfoAndRecordReqVo reqVo);

    /**
     * 修改投注特征标签数据-接收与忽略
     * @param vo
     * @return
     */
    Integer updateUserBetTagChangeRecord(UserBetTagChangeReqVo vo);

    void doLastTime(Long userId, Long tagId);

    void editRecord(UserBetTagChangeReqVo reqVo);

    /**
     * 批量迁移用户标签变更数据 t_user_level_relation_history
     */
    void batchRelationHistoryData();
}
