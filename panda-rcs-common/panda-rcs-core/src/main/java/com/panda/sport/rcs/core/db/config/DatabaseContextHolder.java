package com.panda.sport.rcs.core.db.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据源上下文支持类
 * @author kane
 * @since 2019-09-03
 * @version v1.1
 */
@Slf4j
public class DatabaseContextHolder {
    /**
     * 设置线程隔离的全局上下文对象
     */
    private static final ThreadLocal<DBTypeEnum> contextHolder = new ThreadLocal<>();

    private static final AtomicInteger counter = new AtomicInteger(-1);

    private static final Integer ROUNTING_DATABASE_MOD = 2;

    private static final Integer MAX_NUMBER = 9999;
    /**
     * 将DBKey 设置到当前线程下
     * @param dbType db类型
     */
    public static void setDBKey(final DBTypeEnum dbType) {
        contextHolder.set(dbType);
    }

    /**
     * 获取当前线程下的DBKey
     * @return
     */
    public static DBTypeEnum getDBKey() {
        return contextHolder.get();
    }

    public static void clearDBKey() {
        contextHolder.remove();
    }

    /**
     * 主库
     */
    public static void master() {
        setDBKey(DBTypeEnum.DB1_MASTER);
    }
}
