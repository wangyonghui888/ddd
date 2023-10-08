package com.panda.sport.rcs.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.HttpResponse;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.UserExceptionResVo;
import com.panda.sport.rcs.common.vo.api.response.UserExceptionVo;
import com.panda.sport.rcs.config.ObserveNameNumProperties;
import com.panda.sport.rcs.config.UserNameTagProperties;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 投注特征预警管理控制器
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@Api(tags = "用户中心-异常用户记录")
@RestController
@RequestMapping("/userException")
public class UserExceptionController {
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;
    @Value("${statistical.observe.ch.name:观察名单}")
    String ch_observeName;
    @Value("${statistical.exception.ch.user:异常会员}")
    String ch_exceptionUser;

    @Value("${statistical.observe.en.name:observed}")
    String en_observeName;
    @Value("${statistical.exception.en.user:abnormal}")
    String en_exceptionUser;

    @Value("${statistical.observe.array:3,4,5}")
    String observeArray;
    @Value("${statistical.exception.array:6,7,8}")
    String exceptionArray;
    @Autowired
    ObserveNameNumProperties observeNameNumProperties;

    @Autowired
    UserNameTagProperties userNameTagProperties;
    Logger log = LoggerFactory.getLogger(UserExceptionController.class);
    private String  SEPARATOR = ",";
    @ApiOperation(value = "根据条件查询异常用户记录")
    @RequestMapping(value = "/queryUserExceptionList", method = {RequestMethod.POST})
    public Result<IPage<UserExceptionResVo>> queryUserExceptionList(@RequestBody @Valid UserExceptionReqVo vo) {
        if(vo.getUserType()==1){
            String[] observeList=observeArray.split(SEPARATOR);
            vo.setNameTags(observeList);
        }else if (vo.getUserType()==2){
            String[] exceptionList=exceptionArray.split(SEPARATOR);
            vo.setNameTags(exceptionList);
        }else{
            List<String> tagsNameList=new ArrayList<>();
            String[] observeList=observeArray.split(SEPARATOR);

            for (String s : observeList) {
                tagsNameList.add(s);
            }
            String[] exceptionList=exceptionArray.split(SEPARATOR);
            for (String s : exceptionList) {
                tagsNameList.add(s);
            }
            vo.setNameTags(tagsNameList.toArray(new String[tagsNameList.size()]));
        }
        Page<UserExceptionResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        //根据条件设置值
        if(!StringUtils.isEmpty(vo.getUserId())){
            if(!StringUtils.isNumber(vo.getUserId())){
                vo.setUserName(vo.getUserId());
                vo.setUserId(null);
            }
        }
        long itimes1=System.currentTimeMillis();
        log.info("queryUserExceptionList::根据条件查询异常用户记录:传入参数{}",JSONObject.toJSONString(vo));
        IPage<UserExceptionResVo> userExceptionRecordResVoIPage;
        try {
            userExceptionRecordResVoIPage = userProfileUserTagChangeRecordService.queryUserExceptionRecord(pageParam, vo);
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        if(CollectionUtil.isNotEmpty(userExceptionRecordResVoIPage.getRecords())){
            List<Long> integers=new ArrayList<>();
            String[] observeList=observeArray.split(SEPARATOR);
            for (String s : observeList) {
                integers.add(Long.valueOf(s));
            }
            userExceptionRecordResVoIPage.getRecords().forEach(t->{
                List<Long>  nameTags=integers.stream().filter(e->e.equals(t.getChangeTag())).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(nameTags)){
                if("en".equals(vo.getLanguage()))
                  t.setRiskType(en_exceptionUser);
                else
                  t.setRiskType(ch_exceptionUser);
            }
            else{
                if("en".equals(vo.getLanguage()))
                    t.setRiskType(en_observeName);
                else
                    t.setRiskType(ch_observeName);
            }
            String orgValue=t.getMerchantCode()+"_";
            Pattern p=Pattern.compile(orgValue);
            Matcher m=p.matcher(t.getUserName());
            String userName=m.replaceAll("").trim();
            t.setUserName(userName.trim());
            });
        }
        long itimes2=System.currentTimeMillis();
        log.info("queryUserExceptionList::根据条件查询异常用户记录:结果{},用时{}ms",userExceptionRecordResVoIPage.getTotal(),itimes2-itimes1);
        return Result.succes(userExceptionRecordResVoIPage);
    }
}
