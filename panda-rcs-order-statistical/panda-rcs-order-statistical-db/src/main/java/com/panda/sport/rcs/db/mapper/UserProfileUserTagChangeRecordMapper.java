package com.panda.sport.rcs.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.*;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-02
 */
public interface UserProfileUserTagChangeRecordMapper extends BaseMapper<UserProfileUserTagChangeRecord> {

    /**
     * @Description queryRecordList
     * @Param Page, UserProfileUserTagChangeRecordReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<UserProfileUserTagChangeRecordResVo>
     **/
    IPage<UserProfileUserTagChangeRecordResVo> queryRecordList(Page<UserProfileUserTagChangeRecordResVo> page, @Param("vo") UserProfileUserTagChangeRecordReqVo vo);

    /**
     * 根据用户ID查询用户名称和商户编码
     * @param userId
     * @return
     */
    UserProfileUserTagChangeRecordResVo selectByUserId(@Param("userId") Long userId);

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


    /**
     * @Description queryUserExceptionRecord
     * @Param Page, UserExceptionReqVo
     * @Author sykKong
     * @Date  2022-06-15
     * @return IPage<UserExceptionResVo>
     **/
    IPage<UserExceptionResVo> queryUserExceptionRecord(Page<UserExceptionResVo> page, UserExceptionReqVo vo);

    /**
     * @Description queryBetTagChangeRecordByUserId
     * @Param Page, UserInfoAndRecordReqVo
     * @Author Kir
     * @Date  2021-01-29
     * @return IPage<UserBetTagChangeRecordResVo>
     **/
    IPage<UserBetTagChangeRecordResVo> queryBetTagChangeRecordByUserId(Page<UserBetTagChangeRecordResVo> page, @Param("vo") UserInfoAndRecordReqVo reqVo);

    IPage<UserBetTagChangeRecordResVo> queryNewBetTagChangeRecordByUserId(Page<UserBetTagChangeRecordResVo> page, @Param("vo") UserInfoAndRecordReqVo reqVo);

    /**
     * @param reqVo
     * @return
     */
    UserInfoResVo queryUserInfoByUserId(@Param("vo") UserInfoAndRecordReqVo reqVo);

    /**
     * 修改投注特征标签数据-接收与忽略
     * @param vo
     * @return
     */
    Integer updateUserBetTagChangeRecord(@Param("vo") UserBetTagChangeReqVo vo);


    /**
     * 查询历史用户迁移数据总数据 t_user_level_relation_history
     *
     * @return
     */
    Integer selectUserLevelRelationHistoryCount();

    /**
     * 查询历史用户迁移数据集合 t_user_level_relation_history
     *
     * @return
     */
    List<UserProfileUserTagChangeRecord> selectUserLevelRelationHistoryList();

    /**
     * 根据用户idc查询当前用户层级
     * @param uid
     * @return
     */
    Long selectUserLevelId(Long uid);

}
