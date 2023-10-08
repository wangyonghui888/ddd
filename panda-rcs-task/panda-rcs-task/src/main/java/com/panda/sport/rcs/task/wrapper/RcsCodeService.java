package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsCode;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author admin
 * @since 2020-01-13
 */
public interface RcsCodeService extends IService<RcsCode> {
    /**
     * 根据主key 子key 获取值
     * @param fatherKey
     * @param childKey
     * @return
     */
    String getValue(String fatherKey, String childKey);

    /**
     * 根据主key获取字典集合
     * @param fatherKey
     * @return
     */
    List<RcsCode> selectRcsCods(String fatherKey);
}
