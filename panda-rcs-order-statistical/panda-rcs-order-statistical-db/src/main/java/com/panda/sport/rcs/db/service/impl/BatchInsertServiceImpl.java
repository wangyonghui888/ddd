package com.panda.sport.rcs.db.service.impl;

import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class BatchInsertServiceImpl implements Callable<Boolean> {

    Logger log = LoggerFactory.getLogger(BatchInsertServiceImpl.class);

    /**
     * 1000条为分界批量导入
     */
    private int batchSize = 1000;
    /**
     * lsit对象
     */
    private List<UserProfileUserTagChangeRecord> list;

    private IUserProfileUserTagChangeRecordService changeRecordService;

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(10,
                    Runtime.getRuntime().availableProcessors(),
                    2L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(100),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    /**
     * 这里的对象batchOperateMysqlDao是从实现类传过来的，因为这个类本身没有纳入容器管理
     * 所以不能直接用Autowire引入dao层对象
     **/
    public BatchInsertServiceImpl(List<UserProfileUserTagChangeRecord> list, IUserProfileUserTagChangeRecordService changeRecordService) {
        this.list = list;
        this.changeRecordService = changeRecordService;
    }

    public BatchInsertServiceImpl(List<UserProfileUserTagChangeRecord> list) {
        this.list = list;
    }

    @Override
    public Boolean call() {
        try {
            batchOp(list);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void batchOp(List<UserProfileUserTagChangeRecord> list) {
        //log.info("batchOp线程：{}", Thread.currentThread().getName());
        if (!list.isEmpty()) {
            Integer size = list.size();
            if (size <= batchSize) {
                boolean result = changeRecordService.saveBatch(list);
                log.info("batchOp==Thread线程：{}, save size:{},result:{}", Thread.currentThread().getName(), list.size(), result);
            } else if (size > batchSize) {
                batchOpSpilit(list, batchSize);
            }
        }
    }

    //切割
    private void batchOpSpilit(List<UserProfileUserTagChangeRecord> list, int batch100) {
        Long t1 = System.currentTimeMillis();
        List<List<UserProfileUserTagChangeRecord>> list1 = pagingList(list, batch100);
        try {
            for (List<UserProfileUserTagChangeRecord> list2 : list1) {
                //再调batchOp方法，这里的多线程是多个小集合往数据库插
                threadPoolExecutor.execute(() -> {
                    //System.out.println("我是线程：" + Thread.currentThread().getName());
                    batchOp(list2);
                });
            }
        } catch (Exception e) {
            log.error("batchOpSpilit 执行异常：{}", e.getMessage());
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
            Long t2 = System.currentTimeMillis();
            log.info("batchOpSpilit执行完成,用时:" + (t2 - t1) + "m");
        }
    }

    public static <T> List<List<T>> pagingList(List<T> list, int pageSize) {
        int length = list.size();
        int num = (length + pageSize - 1) / pageSize;
        List<List<T>> newList = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            int fromIndex = i * pageSize;
            int toIndex = (i + 1) * pageSize < length ? (i + 1) * pageSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }
}
