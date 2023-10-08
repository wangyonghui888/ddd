package com.panda.sport.rcs.third.monitor;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HttpPoolStatus {

    //最大连接数
    private int maxTotal;
    //可用的链接
    private int available;
    //被使用的链接数
    private int leased;
    //存放等待获取连接的线程的Future
    private int pending;
    //默认的每个路由的最大连接数
    private int defaultMaxPerRoute;
    @Override
    public String toString() {
        return Layout.Table.of(
                Layout.Row.of("最大连接数", maxTotal),
                Layout.Row.of("可用的链接", available),
                Layout.Row.of("被使用的链接数", leased),
                Layout.Row.of("等待获取连接的线程数", pending),
                Layout.Row.of("路由的最大连接数", defaultMaxPerRoute)
        ).toString();
    }
}
