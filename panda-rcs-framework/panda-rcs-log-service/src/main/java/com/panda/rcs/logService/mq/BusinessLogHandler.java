package com.panda.rcs.logService.mq;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.rcs.logService.Enum.BusinessLogTypeEnum;
import com.panda.rcs.logService.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.rcs.logService.mapper.RcsSysUserMapper;
import com.panda.rcs.logService.service.impl.BusinessLogServiceImpl;
import com.panda.rcs.logService.service.impl.BusinessLogServiceImpl;
import com.panda.rcs.logService.mapper.TUserMapper;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import com.panda.rcs.logService.vo.RcsSysUser;
import com.panda.rcs.logService.vo.TUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RocketMQMessageListener(topic = BaseUtils.RCS_BUSINESS_LOG_SAVE, consumerGroup = BaseUtils.RCS_BUSINESS_LOG_SAVE)
public class BusinessLogHandler implements RocketMQListener<String> {
    @Autowired
    private RcsQuotaBusinessLimitLogMapper rcsBusinessLogMapper;
    @Autowired
    private RcsSysUserMapper rcsSysUserMapper;
    @Autowired
    private BusinessLogServiceImpl businessLogServiceImpl;

    private final  String METHOD="method";
    @Autowired
    private TUserMapper tUserMapper;
    @Override
    public void onMessage(String s) {
        if(Objects.isNull(s)){
            log.warn("::业务日誌消費開始-消费数据->数据为空{}", s);
            return;
        }
        log.info("::业务操作誌消費開始-消费数据记录->{}", s);
        try{
             //兼容单个对象参数
            if(!s.contains("[")){
                s="["+s+"]";
            }
            String unescapeJava = StringEscapeUtils.unescapeJava(s);
            String format = "\"[{";
            if (unescapeJava.contains(format)) {
                unescapeJava = unescapeJava.substring(1, unescapeJava.length() - 1);
            }
            if(unescapeJava.contains(METHOD)){
                businessLogServiceImpl.addBusinessLog(unescapeJava);
                return;
            }
          List<RcsQuotaBusinessLimitLog> list = JSONArray.parseArray(unescapeJava, RcsQuotaBusinessLimitLog.class);
            log.info("::业务操作誌消費開始-消费数据记录->{}", list.size());
          if(!CollectionUtils.isEmpty(list)) {

             RcsSysUser sysUser=rcsSysUserMapper.selectById(Integer.valueOf(list.get(0).getUserId()==null?"0":list.get(0).getUserId()));
             list.forEach(item->{
                if(StringUtils.isEmpty(item.getUserName())){
                    item.setUserName(sysUser==null?"-":sysUser.getUserCode());
                }
                if(StringUtils.isEmpty(item.getExtObjectId())){
                    item.setExtObjectId("-");
                }
                if(StringUtils.isEmpty(item.getExtObjectName())){
                    item.setExtObjectName("-");
                }
                if(StringUtils.isEmpty(item.getCreateTime())){
                    item.setCreateTime(DateUtils.format(new Date()));
                 }
                //用户二级标签变更补充数据
                if(!StringUtils.isEmpty(item.getOperateType())&& BusinessLogTypeEnum.ejbqbg.getValue().equals(item.getOperateType())){
                    setBusinessLog(item);
                }//二级标签变更补充数据
                 if(!StringUtils.isEmpty(item.getOperateType())&& BusinessLogTypeEnum.rjbq.getValue().equals(item.getOperateType())){
                    item.setOperateCategory("风控设置-标签管理");
                 }


            });
            rcsBusinessLogMapper.bathInserts(list);
           }
          }catch (Exception ex){
            log.error("操盤日誌消費開始-消费数据异常{}",ex.getMessage(),ex);
          }


    }

    /**
     * 处理用户二级标签变更日志
     * @param rcsLimitLogLog
     */
    private void setBusinessLog(RcsQuotaBusinessLimitLog rcsLimitLogLog){
        if(!StringUtils.isEmpty(rcsLimitLogLog.getObjectId())){
            LambdaQueryWrapper<TUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(TUser::getUid, rcsLimitLogLog.getObjectId());
            TUser user = tUserMapper.selectOne(userLambdaQueryWrapper);
             if(Objects.nonNull(user)){
                 rcsLimitLogLog.setObjectName(user.getUsername());
                 rcsLimitLogLog.setExtObjectName(user.getMerchantCode());
                 StringBuffer paramName=new StringBuffer(rcsLimitLogLog.getBeforeVal()==null?"":rcsLimitLogLog.getBeforeVal()).append(" -> ")
                         .append(rcsLimitLogLog.getAfterVal()==null?"":rcsLimitLogLog.getAfterVal()).append(" - ").append(rcsLimitLogLog.getCreateTime());
                 rcsLimitLogLog.setParamName(paramName.toString());
             }


        }


    }

}
