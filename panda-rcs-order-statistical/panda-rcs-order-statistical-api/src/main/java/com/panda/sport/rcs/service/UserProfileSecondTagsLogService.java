package com.panda.sport.rcs.service;

import com.panda.sport.rcs.common.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.db.entity.*;
import com.panda.sport.rcs.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.panda.sport.rcs.utils.Constants.*;
import static java.util.Objects.*;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 标签管理日志清洗
 */
@Slf4j
public class UserProfileSecondTagsLogService implements Callable<List<RcsQuotaBusinessLimitLog>> {
    private final UserProfileSecondTagsLogVo userProfileSecondTagsLogVo;

    private static final String TAG_FORMAT = "%s-%s";

    @Autowired
    public UserProfileSecondTagsLogService(UserProfileSecondTagsLogVo userProfileSecondTagsLogVo) {
        this.userProfileSecondTagsLogVo = userProfileSecondTagsLogVo;
    }

    /**
     * 多线程风控处理
     */
    @Override
    public List<RcsQuotaBusinessLimitLog> call() {
        log.info("标签日誌作業開始");
        List<RcsQuotaBusinessLimitLog> finalList = new ArrayList<>();
        try {
            finalList = addUserProfileTagsLog(userProfileSecondTagsLogVo.getNewUserProfileSecondTags(),
                    userProfileSecondTagsLogVo.getOldUserProfileSecondTags());
        } catch (Exception e) {
            log.error("标签管理->风控措施管理处理异常", e);
        }
        log.info("标签日誌作業結束");
        return finalList;
    }

    /**
     * 标签操作
     */
    private List<RcsQuotaBusinessLimitLog> addUserProfileTagsLog(UserProfileSecondTags news,
                                                                 UserProfileSecondTags old) {
        List<RcsQuotaBusinessLimitLog> list;
        if (nonNull(old) && isNull(news)) {
            // 刪除標籤
            log.info("标签刪除 ::{}:: ", old);
            list = setDelUserProfileTagsLog(old);
        } else if (isNull(old) && nonNull(news)) {
            // 新增標籤
            log.info("标签新增 ::{}::", news);
            list = setAddUserProfileTagsLog(news);
        } else {
            // 修改標籤
            log.info("标签修改 變更前::{}::, 變更後::{}::", news, old);
            list = setEditUserProfileTagsLog(news, old);
        }
        return list;
    }

    /**
     * 新增标签
     */
    private List<RcsQuotaBusinessLimitLog> setAddUserProfileTagsLog(UserProfileSecondTags news) {
        List<RcsQuotaBusinessLimitLog> list = new ArrayList<>();
        list.add(setTagRuleRelationLog(news, ADD_TAG, NONE, news.getTagName()));
        return list;
    }

    /**
     * 修改标签
     */
    private List<RcsQuotaBusinessLimitLog> setEditUserProfileTagsLog(UserProfileSecondTags news,
                                                                     UserProfileSecondTags old) {
        List<RcsQuotaBusinessLimitLog> list = new ArrayList<>();
        if (!news.getEnglishTagName().equals(old.getEnglishTagName())) {
            list.add(setTagRuleRelationLog(news, EDIT_EN_TAG, old.getEnglishTagName(), news.getEnglishTagName()));
        }
        //TODO 確認實際的參數會不會有null
        if ((isNotBlank(news.getEnglishTagDetail()) || isNotBlank(old.getEnglishTagDetail())) &&
                !news.getEnglishTagDetail().equals(old.getEnglishTagDetail())) {
            String newEnglishTagDetail = nonNull(news.getEnglishTagDetail()) ? news.getEnglishTagDetail() : "";
            String oldEnglishTagDetail = nonNull(old.getEnglishTagDetail()) ? old.getEnglishTagDetail() : "";
            list.add(setTagRuleRelationLog(news, EDIT_EN_DETAIL, oldEnglishTagDetail, newEnglishTagDetail));
        }
        if (!news.getTagName().equals(old.getTagName())) {
            list.add(setTagRuleRelationLog(news, EDIT_ZS_TAG, old.getTagName(), news.getTagName()));
        }
        if ((isNotBlank(news.getTagDetail()) || isNotBlank(old.getTagDetail())) &&
                !news.getTagDetail().equals(old.getTagDetail())) {
            String newTagDetail = nonNull(news.getTagDetail()) ? news.getTagDetail() : "";
            String oldTagDetail = nonNull(old.getTagDetail()) ? old.getTagDetail() : "";
            list.add(setTagRuleRelationLog(news, EDIT_ZS_DETAIL, oldTagDetail, newTagDetail));
        }
        return list;
    }

    /**
     * 刪除標籤
     */
    private List<RcsQuotaBusinessLimitLog> setDelUserProfileTagsLog(UserProfileSecondTags old) {
        List<RcsQuotaBusinessLimitLog> list = new ArrayList<>();
        list.add(setTagRuleRelationLog(old, DELETE_TAG, old.getTagName(), DELETE));
        return list;
    }

    /**
     * 二級标签日誌生成
     */
    private RcsQuotaBusinessLimitLog setTagRuleRelationLog(UserProfileSecondTags userProfileSecondTags,
                                                           String paramName,
                                                           String beforeVal,
                                                           String afterVal) {
        RcsQuotaBusinessLimitLog limitLoglog = new RcsQuotaBusinessLimitLog();
        limitLoglog.setOperateCategory(TAG_TITLE);
        limitLoglog.setObjectId(userProfileSecondTags.getId().toString());
        limitLoglog.setObjectName(userProfileSecondTags.getTagName());
        limitLoglog.setExtObjectId("-");
        limitLoglog.setExtObjectName("-");
        limitLoglog.setOperateType(Constants.SECOND_TAG);
        limitLoglog.setParamName(paramName);
        limitLoglog.setBeforeVal(beforeVal);
        limitLoglog.setAfterVal(afterVal);
        limitLoglog.setUserId(userProfileSecondTagsLogVo.getUserId());
        return limitLoglog;
    }
}
