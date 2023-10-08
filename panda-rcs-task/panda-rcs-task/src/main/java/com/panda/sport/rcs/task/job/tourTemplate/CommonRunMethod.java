package com.panda.sport.rcs.task.job.tourTemplate;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.panda.sport.rcs.exeception.RcsServiceException;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author :  myname
 * @Project Name :  panda-rcs-task
 * @Package Name :  com.panda.sport.rcs.task.job.tourTemplate
 * @Description :  同步模板公共方法
 * @Date: 2022-02-16 10:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
public class CommonRunMethod {
    private final IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl;

    public CommonRunMethod(IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl) {
        this.rcsMatchMarketConfigServiceImpl = rcsMatchMarketConfigServiceImpl;
    }

    public static ExecutorService threadPool = new ThreadPoolExecutor(200, 250,
            1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("sync-football-template-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public static ExecutorService basketballThreadPool = new ThreadPoolExecutor(200, 250,
            1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("sync-basketball2-template-%d").build(), new ThreadPoolExecutor.AbortPolicy());

    public void handleTemplateData(String linkId, List<RcsTournamentTemplateComposeModel> list) throws Exception {
        if (!CollectionUtils.isEmpty(list)) {
            log.info("::{}:TemplateSyncService:-插入模板:{}", linkId, list.size());
            for (RcsTournamentTemplateComposeModel model : list) {
                try {
                    rcsMatchMarketConfigServiceImpl.insertFromTemplate(model,"");
                } catch (Exception e){
                    log.error("::{}::,分时节点同步失败, 异常信息：", linkId, e);
                }
            }
        }
    }

    public void handleTemplateDataThread(String linkId,List<RcsTournamentTemplateComposeModel> list) throws Exception {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
       final CountDownLatch doneLatch = new CountDownLatch(list.size());

        for (RcsTournamentTemplateComposeModel model : list) {
//            threadPool.execute(()-> rcsMatchMarketConfigServiceImpl.insertFromTemplate(model));
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        rcsMatchMarketConfigServiceImpl.insertFromTemplate(model);
                    } catch (Exception e) {
                        log.error("::{}::,分时节点同步失败, 异常信息：", linkId, e);
                    }finally {
                        doneLatch.countDown();
                    }
                }
            });
        }
        try {
            doneLatch.await();
        }catch (InterruptedException e){
            log.error("::{}::,分时节点同步失败, 异常信息：", linkId, e);
        }
    }

    public void handleBasketBallTemplateDataThread(String linkId,List<RcsTournamentTemplateComposeModel> list) throws Exception {
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        final CountDownLatch basketballDoneLatch = new CountDownLatch(list.size());

        for (RcsTournamentTemplateComposeModel model : list) {
//            threadPool.execute(()-> rcsMatchMarketConfigServiceImpl.insertFromTemplate(model));
            basketballThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        rcsMatchMarketConfigServiceImpl.insertFromTemplate(model);
                    } catch (Exception e) {
                        log.error("::{}::,分时节点同步失败, 异常信息：", linkId, e);
                    }finally {
                        basketballDoneLatch.countDown();
                    }
                }
            });
        }
        try {
            basketballDoneLatch.await();
        }catch (InterruptedException e){
            log.error("::{}::,分时节点同步失败, 异常信息：", linkId, e);
        }
    }

}
