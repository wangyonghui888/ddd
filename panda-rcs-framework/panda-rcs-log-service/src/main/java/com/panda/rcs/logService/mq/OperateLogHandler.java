package com.panda.rcs.logService.mq;
import com.alibaba.fastjson.JSONObject;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.mapper.*;
import com.panda.rcs.logService.service.impl.LogDataChangeServiceImpl;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogParameters;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "rcs_log_service", topic = "rcs_log_operate")
public class OperateLogHandler implements RocketMQListener<LogParameters> {

    @Autowired
    private RcsOperateLogMapper rcsOperateLogMapper;
    @Autowired
    private LogDataChangeServiceImpl logDataChangeServiceImpl;
    private static final String SPORT_ID_NAME = "sportId";
    private static final String SET_WEIGHTS  ="setWeights";
    private static final  String Delete_CategorySet="deleteCategorySet";
    private static final String addCategorySetAndCategory ="addCategorySetAndCategory";
    private static  final String updateStandardMatchSortValue ="updateStandardMatchSortValue";
    private static final String OPERATE_PAGE_CODE_NAME = "operatePageCode";
    @Override
    public void onMessage(LogParameters joinPoint) {
        try{
        log.info("::::操盤日誌消費開始-消费数据->{}::{}", joinPoint.getMethodName(),joinPoint.getArgs()[0].toString());
        //特殊处理
        if(joinPoint.getMethodName().equals(SET_WEIGHTS)){
            logDataChangeServiceImpl.setWeights(joinPoint);
           return;
        }
        //
            if(joinPoint.getMethodName().equals("updateCategorySet")){
                logDataChangeServiceImpl.updateCategorySet(joinPoint);
                return;
            }
            if(joinPoint.getMethodName().equals(updateStandardMatchSortValue)){
                logDataChangeServiceImpl.updateStandardMatchSortValue(joinPoint);
                return;
            }
        if(joinPoint.getMethodName().equals(addCategorySetAndCategory)){
                logDataChangeServiceImpl.addCategory(joinPoint);
                return;
            }
            if(joinPoint.getMethodName().equals("updateCategorySetAndCategory")){
                logDataChangeServiceImpl.updateCategory(joinPoint);
                return;
            }
            if(joinPoint.getMethodName().equals("modifyMatchPendingOrderParam")){
                LogAllBean bean=BaseUtils.setObject(joinPoint.getArgs()[0]);
                bean.setBeforeParams(joinPoint.getMap());
                logDataChangeServiceImpl.modifyMatchPendingOrderParam(joinPoint.getLog(),bean);
                return;
            }

        if(joinPoint.getMethodName().equals(Delete_CategorySet)){
            logDataChangeServiceImpl.deleteCategorySet(joinPoint.getLog(),Integer.parseInt(joinPoint.getArgs()[0].toString()));
           return;
            }
        LogAllBean bean=BaseUtils.setObject(joinPoint.getArgs()[0]);
            RcsOperateLog rcsOperateLog=joinPoint.getLog();
            if(Objects.nonNull(rcsOperateLog)){
                setCommonPropertiesIfExists(rcsOperateLog, joinPoint.getArgs());
            }
            bean.setBeforeString(joinPoint.getBeforeString());
            if(Objects.isNull(bean.getBeforeParams())){
                bean.setBeforeParams(joinPoint.getMap());
            }
            rcsOperateLog = logDataChangeServiceImpl.filterMethod(joinPoint.getMethodName(),rcsOperateLog ,bean);
            if(Objects.nonNull(rcsOperateLog)){
                // 如果参数有sportId/operatePageCode,则转换为map,设置sportId,区分足球/篮球
                setCommonPropertiesIfExists(rcsOperateLog, joinPoint.getArgs());
            rcsOperateLogMapper.insert(rcsOperateLog);
             }
        }catch (Exception e){
            log.error("::{}::操盘日志-设置值异常",e);
        }
    }


    /**
     * 2204需求:记录篮球赛事日志,之前只有足球赛事日志,而且sportID固定为1
     * 如果参数有sportId则转换为map,设置sportId,区分足球/篮球
     * 公共属性还有:operatePageCode
     * @param preLogBean 日志实体
     * @param args 请求参数
     */
    private void setCommonPropertiesIfExists(RcsOperateLog preLogBean, Object[] args) {
        try {
            if (Objects.isNull(args) || Objects.isNull(args[0])) {
                return;
            }
            Object arg = args[0];
            JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(arg));
            if (obj.containsKey(SPORT_ID_NAME)) {
                preLogBean.setSportId(obj.getInteger(SPORT_ID_NAME));
            }
            if (obj.containsKey(OPERATE_PAGE_CODE_NAME)&&null==preLogBean.getOperatePageCode()) {
                preLogBean.setOperatePageCode(obj.getInteger(OPERATE_PAGE_CODE_NAME));
            }
        }catch (Exception e) {
            log.error("::{}::操盘日志-设置 {} 值异常", SPORT_ID_NAME, e);
        }
    }


}
