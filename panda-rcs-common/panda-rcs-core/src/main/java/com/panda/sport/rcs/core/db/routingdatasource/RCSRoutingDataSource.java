package com.panda.sport.rcs.core.db.routingdatasource;

import com.panda.sport.rcs.core.db.config.DatabaseContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

/***
        * <p>用于连接物理数据源的工厂
        * {@code RCSRoutingDataSource}对象代表。替代
        * {@code DriverManager}工具，{@code RCSRoutingDataSource}对象
        *是获得连接的首选方式。实现的对象
        * {@code RCSRoutingDataSource}界面通常是
        *注册基于的命名服务
        * <P>
 * {@code RCSRoutingDataSource}接口由驱动程序供应商实现。
        *有三种类型的实现：
        * <OL>
 * <LI>基本实现 - 生成标准{@code Connection}
        *对象
        * <LI>连接池实现 - 生成{@code Connection}
        *将自动参与连接池的对象。这个
        *实现与中间层连接池管理器一起使用。
        * <LI>分布式事务实现 - 生成一个
        * {@code Connection}对象，可用于分布式
        *交易，几乎总是参与连接池。
        *此实现适用于中间层
        *事务管理器，几乎总是与连接
        *汇集经理。
        * </ OL>
        * <P>
 * {@code RCSRoutingDataSource}对象具有可以修改的属性
        * 必要时。例如，如果将数据源移动到其他位置
        * server，服务器的属性可以更改。好处是
        *因为数据源的属性可以更改，任何代码访问
        *不需要更改数据源。
        * <P>
 *通过{@code RCSRoutingDataSource}对象访问的驱动程序不会
        *使用{@code DriverManager}注册自己。相反，一个
        * {@code DataSource}对象通过查找操作检索
        *然后用于创建{@code Connection}对象。基本的
        *实现，通过{@code DataSource}获得的连接
        *对象与通过该对象获得的连接相同
        * {@code DriverManager}设施。
        * <p>
**/
public class RCSRoutingDataSource extends AbstractRoutingDataSource {
    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        return DatabaseContextHolder.getDBKey();
    }
}
