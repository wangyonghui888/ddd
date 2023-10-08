package com.panda.sport.rcs.service;

import com.panda.sport.rcs.db.entity.*;

import java.io.Serializable;

/**
 * 通用日志对象
 */
public class UserProfileSecondTagsLogVo implements Serializable {

    public UserProfileSecondTagsLogVo(UserProfileSecondTags oldUserProfileSecondTags,
                                      UserProfileSecondTags newUserProfileSecondTags,
                                      String userId) {
        this.newUserProfileSecondTags = newUserProfileSecondTags;
        this.oldUserProfileSecondTags = oldUserProfileSecondTags;
        this.userId = userId;
    }

    /**
     * 旧数据
     */
    private UserProfileSecondTags oldUserProfileSecondTags;
    /**
     * 新数据
     */
    private UserProfileSecondTags newUserProfileSecondTags;
    private String userId;

    public String getUserId() {
        return this.userId;
    }

    public UserProfileSecondTags getOldUserProfileSecondTags() {
        return this.oldUserProfileSecondTags;
    }

    public UserProfileSecondTags getNewUserProfileSecondTags() {
        return this.newUserProfileSecondTags;
    }
}
