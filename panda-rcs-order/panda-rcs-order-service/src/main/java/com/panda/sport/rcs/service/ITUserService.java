package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.TUser;

/**
 * <p>
 * TUser
 * </p>
 *
 * @author magic
 * @since 2022-10-19
 */
public interface ITUserService extends IService<TUser> {

    int updateUserTagId(long userId,  int tagId);

}
