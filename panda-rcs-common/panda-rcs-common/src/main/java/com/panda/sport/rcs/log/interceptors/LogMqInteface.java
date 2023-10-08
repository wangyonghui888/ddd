package com.panda.sport.rcs.log.interceptors;

public interface LogMqInteface {

    public void sendMsg(String topic,String tag,String key ,Object msg);

}
