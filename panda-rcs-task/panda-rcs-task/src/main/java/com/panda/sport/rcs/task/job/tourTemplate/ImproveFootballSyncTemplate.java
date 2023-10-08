package com.panda.sport.rcs.task.job.tourTemplate;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainRefMapper;
import com.panda.sport.rcs.mq.utils.ProducerSendMessageUtils;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.config.RedissonManager;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ImproveFootballSyncTemplate {

    private final  RedisClient redisClient;

    private final  ProducerSendMessageUtils producerSendMessageUtils;

    public ImproveFootballSyncTemplate( RedisClient redisClient, ProducerSendMessageUtils producerSendMessageUtils) {
        this.redisClient = redisClient;
        this.producerSendMessageUtils = producerSendMessageUtils;
    }
    public void syncTemplate(List<RcsTournamentTemplateComposeModel> querylist,String linkId,String redis_data_key){
        log.info("::{}::-开始同步足球赛事模板", linkId);
        // 第一步：获取2000条 BCD
        if(!CollectionUtils.isEmpty(querylist)){
            // 第二步：获取缓存信息 ABC
            String lastData = redisClient.get(redis_data_key);
            log.info("::{}::-获取缓存信息{}" ,linkId,lastData);
            if(StringUtils.isEmpty(lastData)){
                lastData = "";
            }
            String sendData = getSendData(querylist,lastData);
            // 第三步：获取未处理过的数据 D
            List<RcsTournamentTemplateComposeModel>  unSendList = getUnSendList(lastData,querylist);
            log.info("::{}::-获取未处理过的数据{}" ,linkId, JSONObject.toJSONString(unSendList));
            // 第四步：比对结果
         /*   int sendSizePercent = getSendSizePercent(querylist,unSendList,lastData);
            // 第五步：比对发送
             int unSendSizePercent = getUnSendSizePercent(querylist,unSendList);

            log.info("::{}::-当前节点，待发送数据占上次发送数据的百分之{}" ,linkId,sendSizePercent);
            if(sendSizePercent>50 ){
                //超过50%，放弃本次执行
                log.info("::{}::-结束同步{}足球赛事模板,上一次执行的数据有{}%未执行完成,占总数的{}%，等下一次执行" ,linkId,DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"),sendSizePercent,unSendSizePercent);
            }else{*/
            // 第六步：每次推送不存在的数据D  取消4，5步，防止分时同步停顿
            sendMsg(unSendList);
            // 第五步：更新缓存
            redisClient.setExpiry(redis_data_key,sendData,300L);
            log.info("::{}::-结束同步足球赛事模板,结束时间：{}" ,linkId,DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
//            }
        }else{
            log.info("::{}::-结束同步足球赛事模板,当前时间点未有数据待同步" ,linkId);
        }
    }

    private int getUnSendSizePercent(List<RcsTournamentTemplateComposeModel> querylist, List<RcsTournamentTemplateComposeModel> unSendList) {
        if(CollectionUtils.isEmpty(unSendList)) return 0;
         return unSendList.size()*100/querylist.size();
    }

    /**
     * 获取发送数据
     * @param querylist
     * @return
     */
    private String getSendData(List<RcsTournamentTemplateComposeModel> querylist,String lastData) {
        StringBuffer sb = new StringBuffer();
        sb.append(querylist.size());
        sb.append(":");
        querylist.forEach(s->{
            String label = s.getMatchId()+"-"+s.getMatchType()+"-"+s.getPlayId()+"-"+s.getTimeVal()+"-0";
            String label1= s.getMatchId()+"-"+s.getMatchType()+"-"+s.getPlayId()+"-"+s.getTimeVal()+"-1";
            String label2 = s.getMatchId()+"-"+s.getMatchType()+"-"+s.getPlayId()+"-"+s.getTimeVal()+"-2";
            if(lastData.indexOf(label)>-1){
                sb.append(label1+",");
            }else if(lastData.indexOf(label1)>-1){
                sb.append(label2+",");
            }else if(lastData.indexOf(label2)>-1){
                // nothing
            }else{
                sb.append(label+",");
            }
        });
        return sb.toString();
    }

    /**
     * 发送topic处理，每200条发送一次
     * @param unSendList
     */
    private void sendMsg(List<RcsTournamentTemplateComposeModel> unSendList) {
        if(CollectionUtils.isEmpty(unSendList)) return;
        for(int i=0;i<unSendList.size();i=i+200){
            int end = unSendList.size()>(i+200)?i+200: unSendList.size();
            List subList = unSendList.subList(i,end);
            producerSendMessageUtils.sendMessage("FOOTBALL_SYNC_TEMPLATE","FOOTBALL_SYNC_TEMPLATE","",subList);
        }
    }

    /**
     * 比对结果 BC/ABC
     * @param querylist
     * @param unSendList
     * @param lastData
     * @return
     */
    private int getSendSizePercent(List<RcsTournamentTemplateComposeModel> querylist, List<RcsTournamentTemplateComposeModel> unSendList, String lastData) {
        int ret = 0 ;
        if(StringUtils.isEmpty(lastData)){
            return ret;
        }
        int repeatSize = querylist.size() - unSendList.size();
        if(repeatSize == 0){
            return ret;
        }
        int lastSize = Integer.valueOf(lastData.split(":")[0]);
        ret = repeatSize*100/lastSize;
        return ret;
    }

    /**
     * 获取未处理过的数据
     * @param lastData
     * @param querylist
     * @return
     */
    private List<RcsTournamentTemplateComposeModel> getUnSendList(String lastData, List<RcsTournamentTemplateComposeModel> querylist) {
        if(StringUtils.isEmpty(lastData)){
            return querylist;
        }
        List<RcsTournamentTemplateComposeModel> unSendList = new ArrayList<>();
        querylist.forEach(s->{
            String label = s.getMatchId()+"-"+s.getMatchType()+"-"+s.getPlayId()+"-"+s.getTimeVal();
            if(lastData.indexOf(label)<0){
                unSendList.add(s);
            }
        });
        return unSendList;
    }


}
