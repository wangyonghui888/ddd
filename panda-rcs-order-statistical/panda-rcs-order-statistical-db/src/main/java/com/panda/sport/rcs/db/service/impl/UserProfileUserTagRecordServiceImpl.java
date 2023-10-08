package com.panda.sport.rcs.db.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.data.rcs.api.Response;
import com.panda.sport.data.rcs.api.statistical.UserBetTagChangeRecordService;
import com.panda.sport.data.rcs.dto.QueryUserGroupWarnVo;
import com.panda.sport.rcs.common.utils.HttpUtil;
import com.panda.sport.rcs.common.vo.api.response.UserBetTagChangeRecordResVo;
import com.panda.sport.rcs.common.vo.api.response.danger.PageResult;
import com.panda.sport.rcs.db.mapper.UserProfileUserTagChangeRecordMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Service(connections = 5, retries = 0, timeout = 3000)
@Component
public class UserProfileUserTagRecordServiceImpl implements UserBetTagChangeRecordService {

    @Value("${user.portrait.http.url.prefix}")
    String urlPrefix;
    @Value("${user.portrait.risk.http.url.prefix}")
    String urlRiskPrefix;
    @Value("${user.portrait.http.appId}")
    String appId;
    Logger log = LoggerFactory.getLogger(UserProfileUserTagChangeRecordServiceImpl.class);

    @Autowired
    private UserProfileUserTagChangeRecordMapper mapper;

    @Override
    public Response<String> queryBetTagChangeRecord(com.panda.sport.data.rcs.dto.UserBetTagChangeRecordReqVo vo) {
        Page<UserBetTagChangeRecordResVo> pageParam = new Page<>();
        try {
            log.info("查询投注特征数据开始：{}", JSON.toJSONString(vo));
            IPage<UserBetTagChangeRecordResVo> userBetTagChangeRecordResVoIPage = mapper.queryNewBetTagChangeRecordByUserId(pageParam,null);
            long total = userBetTagChangeRecordResVoIPage.getTotal();
            return Response.success(String.valueOf(total));
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }


//    @Override
    public Response<String> comboMatches(com.panda.sport.data.rcs.dto.MatchListReqVo vo) {
        log.info(String.format( "危险串关赛事监控 comboMatches %s --start", JSON.toJSON( vo ) ) );

        //empty check
        if( StringUtils.isBlank( vo.getStartTime() ) || StringUtils.isBlank( vo.getEndTime() ) ){
            return Response.fail(" params, startTime and endTime, are required ");
        }

        //date format validation
        try{
            DateUtils.parseDate( vo.getStartTime(), "yyyy-MM-dd HH:mm" );
            DateUtils.parseDate( vo.getEndTime(), "yyyy-MM-dd HH:mm" );
        }catch ( Exception e ){
            return Response.fail(" params, startTime and endTime, must be formatted yyyy-MM-dd HH:mm ");
        }

        String responseRawData = null;
        try{
            responseRawData = HttpUtil.post(urlRiskPrefix.concat("/danger/matchList"), JSON.toJSONString(vo), appId);
        }catch (Exception e){
            log.error("大数据接口请求异常", e );
            return Response.fail("大数据接口请求异常, 请在试一次");
        }

        if( StringUtils.isBlank( responseRawData ) ){
            return Response.fail("大数据接口请求异常, 请在试一次");
        }

        PageResult pageResult = JSON.parseObject( responseRawData, PageResult.class );

        if( pageResult.getCode() != 200 ){
            log.error( "大数据接口请求异常, response from bigData api: %s", responseRawData );
            return Response.fail("大数据接口请求异常, 请在试一次");
        }

        return Response.success(String.valueOf(pageResult.getTotalCount()));
    }

//    @Override
    public Response<String> abnormalSingleOrderList(com.panda.sport.data.rcs.dto.AbnormalSingleOrderListReqVo vo) {
        log.info("start 危险单关监控 abnormalComboDateList:" + JSON.toJSONString(vo));
        String data="";
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/danger/abnormalSingleOrderList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
            PageResult pageResult = JSON.parseObject( data, PageResult.class );
            return Response.success(String.valueOf(pageResult.getTotalCount()));
        } catch (Exception e) {
            log.error("危险单关监控{}" + e);
            return Response.fail("危险单关监控异常");
        }
    }

//    @Override
    public Response<String> queryUserGroupWarnList(QueryUserGroupWarnVo vo) {
        log.info("start 玩家组预警用户列表 queryUserGroupWarnList:" + JSON.toJSONString(vo));

        String data="";
        try {
            data = HttpUtil.post(urlRiskPrefix.concat("/tyUserGroupWarn/queryUserGroupWarnList"), JSON.toJSONString(vo), appId);
            log.info("data:" + data);
            PageResult pageResult = JSON.parseObject( data, PageResult.class );
            return Response.success(String.valueOf(pageResult.getTotalCount()));
        }catch (Exception e){
            log.error("玩家组预警用户列表",  e);
            return Response.fail("玩家组预警用户列表异常");
        }
    }
}
