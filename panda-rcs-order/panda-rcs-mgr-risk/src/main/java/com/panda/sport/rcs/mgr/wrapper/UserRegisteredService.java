package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.TUser;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  用户注册服务类
 * @Date: 2019-10-21 15:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface UserRegisteredService {
    /**
     * @Description   保存用户注册信息
     * @Param [userRegistered]
     * @Author  Sean
     * @Date  16:21 2019/10/21
     * @return void
     */
    Integer saveUserRegistered(TUser user);
}
