package com.panda.sport.rcs.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.enums.OperationLogFlagEnum;
import com.panda.sport.rcs.mapper.RcsOperationLogMapper;
import com.panda.sport.rcs.pojo.RcsOperationLog;
import com.panda.sport.rcs.wrapper.IRcsOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.wrapper.impl
 * @Description :  日志类
 * @Date: 2020-05-13 14:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsOperationLogServiceImpl extends ServiceImpl<RcsOperationLogMapper, RcsOperationLog> implements IRcsOperationLogService {
    @Autowired
    private RcsOperationLogMapper rcsOperationLogMapper;
    /**
     * 查询数据
     * @param operationLogFlagEnum
     * @return
     */
    @Override
    public List<RcsOperationLog> query(OperationLogFlagEnum operationLogFlagEnum,String other){
        String handleCode= operationLogFlagEnum.getCode() +"_" + other;
        //查询日志
        QueryWrapper<RcsOperationLog> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.lambda().eq(RcsOperationLog::getHandleCode,handleCode);
        return rcsOperationLogMapper.selectList(logQueryWrapper);
    }

    /**
     * 插入数据
     * @param operationLogFlagEnu
     * @param updatePreContent
     * @param updateContent
     * @param showContent
     */
    @Override
    public void insert(OperationLogFlagEnum operationLogFlagEnu,String suffixHandleCode,String hanlerId,String updatePreContent,String updateContent,String showContent){
        RcsOperationLog rcsOperationLog = new RcsOperationLog();
        rcsOperationLog.setCrtTime(new Date());
        rcsOperationLog.setUpdateTime(new Date());
        rcsOperationLog.setHandleCode(operationLogFlagEnu.getCode() +"_"+suffixHandleCode);
        rcsOperationLog.setHanlerId(hanlerId);
        rcsOperationLog.setUpdatePreContent(updatePreContent);
        rcsOperationLog.setUpdateContent(updateContent);
        rcsOperationLog.setShowContent(showContent);
        rcsOperationLogMapper.insert(rcsOperationLog);
    }
}
