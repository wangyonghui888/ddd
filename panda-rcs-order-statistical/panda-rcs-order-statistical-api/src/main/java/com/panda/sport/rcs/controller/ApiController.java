package com.panda.sport.rcs.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.vo.HttpResponse;
import com.panda.sport.rcs.common.vo.ResultVo;
import com.panda.sport.rcs.common.vo.api.request.UserExceptionReqVo;
import com.panda.sport.rcs.common.vo.api.response.UserExceptionResVo;
import com.panda.sport.rcs.common.vo.api.response.UserExceptionVo;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import com.panda.sport.rcs.utils.UserProfileUserTagChangeNacosConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : api
 * @Author : skyKong
 * @Date : 2022-08-18 19:40
 * @ModificationHistory Who When What -------- ---------
 *                      --------------------------
 */
@RestController
@RequestMapping(value = "/api/")
@Api(tags = "提供给业务的接口")
public class ApiController {
	@Autowired
	IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

	@Autowired
	UserProfileUserTagChangeNacosConfig userProfileUserTagChangeNacosConfig;
	Logger log = LoggerFactory.getLogger(UserExceptionController.class);
	private String  SEPARATOR = ",";
    /**
	 * @param vo 请求实体
	 *    查询异常用户
	 * @return 返回实体
	 * */
	@PostMapping("/queryUserExceptions")
	@ApiOperation("查询异常用户")
	public HttpResponse<UserExceptionVo> queryUserExceptions(@RequestBody @Valid UserExceptionReqVo vo) {
		if(vo.getUserType()==1){
			String[] observeList=userProfileUserTagChangeNacosConfig.getObserveArray().split(SEPARATOR);
			vo.setNameTags(observeList);
		}else if (vo.getUserType()==2){
			String[] exceptionList=userProfileUserTagChangeNacosConfig.getExceptionArray().split(SEPARATOR);
			vo.setNameTags(exceptionList);
		}else{
			List<String> tagsNameList=new ArrayList<>();
			String[] observeList=userProfileUserTagChangeNacosConfig.getObserveArray().split(SEPARATOR);

			for (String s : observeList) {
				tagsNameList.add(s);
			}
			String[] exceptionList=userProfileUserTagChangeNacosConfig.getExceptionArray().split(SEPARATOR);
			for (String s : exceptionList) {
				tagsNameList.add(s);
			}
			vo.setNameTags(tagsNameList.toArray(new String[tagsNameList.size()]));
		}
		Page<UserExceptionResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
		//根据条件设置值
		if(!StringUtils.isEmpty(vo.getUserId()) && !StringUtils.isNumber(vo.getUserId())){
			vo.setUserName(vo.getUserId());
			vo.setUserId(null);
		}
		long itimes1=System.currentTimeMillis();
		log.info("queryUserExceptions::根据条件查询异常用户记录:传入参数{}", JSONObject.toJSONString(vo));
		IPage<UserExceptionResVo> userExceptionRecordResVoIPage;
		try {
			userExceptionRecordResVoIPage = userProfileUserTagChangeRecordService.queryUserExceptionRecord(pageParam, vo);
		}catch (Exception e){
			log.error("查询异常{}" + e);
			return HttpResponse.error(500,"查询异常");
		}
		if(CollectionUtil.isNotEmpty(userExceptionRecordResVoIPage.getRecords())){
			List<Long> integers=new ArrayList<>();
			String[] observeList=userProfileUserTagChangeNacosConfig.getObserveArray().split(SEPARATOR);
			for (String s : observeList) {
				integers.add(Long.valueOf(s));
			}
			userExceptionRecordResVoIPage.getRecords().forEach(t->{
				List<Long>  nameTags=integers.stream().filter(e->e.equals(t.getChangeTag())).collect(Collectors.toList());
				if(CollectionUtil.isEmpty(nameTags)){
					if("en".equals(vo.getLanguage()))
						t.setRiskType(userProfileUserTagChangeNacosConfig.getEn_exceptionUser());
					else
						t.setRiskType(userProfileUserTagChangeNacosConfig.getCh_exceptionUser());
				}
				else{
					if("en".equals(vo.getLanguage()))
						t.setRiskType(userProfileUserTagChangeNacosConfig.getEn_observeName());
					else
						t.setRiskType(userProfileUserTagChangeNacosConfig.getCh_observeName());
				}
                Long settingTime=Long.valueOf(userProfileUserTagChangeNacosConfig.getDateSetting());
				if(t.getOperateTime() > settingTime){
					if (!Objects.isNull(t.getRemark1()) && !"".equals(t.getRemark1())){
						t.setRemark(t.getRemark1());
					}else {
						if (!Objects.isNull(t.getRemark()) && !"".equals(t.getRemark())){
							try {
								t.setRemark(t.getRemark());
//								List<ResultVo> resultVoList= JSONArray.parseArray(t.getRemark(),ResultVo.class);
//								if(CollectionUtil.isNotEmpty(resultVoList)){
//									ResultVo resultVo= resultVoList.get(0);
//									String remark=resultVo.getResult().replace("@;@","").trim();
//									t.setRemark(remark);
//								}else {
//									t.setRemark("");
//								}
							} catch (Exception e) {
								t.setRemark("");
							}
						}
					}
				}else{
					t.setRemark("");
				}
				String orgValue=t.getMerchantCode()+"_";
				Pattern p=Pattern.compile(orgValue);
				Matcher m=p.matcher(t.getUserName());
				String userName=m.replaceAll("").trim();
				t.setUserName(userName.trim());
			});
		}
		UserExceptionVo userExceptionVo=new UserExceptionVo();
		userExceptionVo.setTotal(userExceptionRecordResVoIPage.getTotal());
		userExceptionVo.setUserExceptionResVoList(userExceptionRecordResVoIPage.getRecords());
		long itimes2=System.currentTimeMillis();
		log.info("queryUserExceptions::根据条件查询异常用户记录:结果{},用时{}ms",userExceptionRecordResVoIPage.getTotal(),itimes2-itimes1);
		return HttpResponse.success(userExceptionVo);
	}
}
