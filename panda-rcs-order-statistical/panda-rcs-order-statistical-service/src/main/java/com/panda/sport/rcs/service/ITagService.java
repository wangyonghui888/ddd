package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.bean.RuleResult;
import com.panda.sport.rcs.common.vo.UserProfileTagsExtVo;
import com.panda.sport.rcs.common.vo.rule.RuleResultDataVo;

import java.util.List;

/**
 * 标签扫描 Service
 *
 * @author :  lithan
 * @date: 2020-07-02
 */
public interface ITagService {

    /**
     * 处理 用户 和标签的关系
     *
     * @return
     */
    void execute(Long time, String changeManner, Long tagId);

    void addUserTag(Long userId, UserProfileTagsExtVo tag, List<RuleResult<List<RuleResultDataVo>>> ruleResultList, String changeManner, String remark, Integer changeType);

    List<UserProfileTagsExtVo> getTags(Long parentId);

    void checkUserTag(UserProfileTagsExtVo tag, Long userId, Long time, String changeManner) throws Exception;

    void addUserTag(Long userId, UserProfileTagsExtVo tag, List<RuleResult<List<RuleResultDataVo>>> ruleResultList, String changeManner, String remark, Integer changeType,boolean isAddLog);

}
