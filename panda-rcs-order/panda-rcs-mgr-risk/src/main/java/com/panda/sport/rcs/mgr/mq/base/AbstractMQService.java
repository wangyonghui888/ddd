package com.panda.sport.rcs.mgr.mq.base;

import com.panda.sport.rcs.mq.utils.ConsumerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

/**
 * API接口实现基类
 */
@Slf4j
public abstract class AbstractMQService<R,T>  extends ConsumerAdapter<R> {

    public AbstractMQService(String consumerConfig,String keyName){
        super(consumerConfig,keyName);
    };

    @Resource(name = "asyncPoolTaskExecutor")
    protected ThreadPoolTaskExecutor asyncPoolTaskExecutor;


    /**
     * 接口入参校验
     *
     * @return
     */
    protected abstract void validation(T requestData);

    /**
     * 处理业务逻辑
     *
     * @param requestData
     * @return
     */
    protected abstract void doRequest(T requestData);

    /**
     * 请求处理驱动方法
     *
     * @param requestData
     * @return
     */
    public Boolean dispatch(T requestData) {

        try {
            // 校验数据合法性
            this.validation(requestData);
            // 异步通知下游系统
            /*log.info("Notify downstream system...");
            asyncPoolTaskExecutor.submit(() -> notifyDownstreamSystem(requestData));*/
            // 处理风控业务逻辑
            this.doRequest(requestData);
            return true;
        } catch (Exception e) {
            log.error("请求处理驱动方法异常{}",e.getMessage(),e);
            return false;
        }
    }

    /**
     * 通知下游系统：异步
     *
     * @param requestData
     */
    protected abstract void notifyDownstreamSystem(T requestData);


}
