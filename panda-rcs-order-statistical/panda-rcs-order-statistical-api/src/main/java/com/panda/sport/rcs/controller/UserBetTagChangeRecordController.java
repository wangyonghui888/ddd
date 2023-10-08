package com.panda.sport.rcs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.ProducerSendMessageUtils;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.api.request.*;
import com.panda.sport.rcs.common.vo.api.response.AutoTagLogRecordResVo;
import com.panda.sport.rcs.common.vo.api.response.UserBetTagChangeRecordResVo;
import com.panda.sport.rcs.common.vo.api.response.UserInfoAndRecordResVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileUserTagChangeRecordResVo;
import com.panda.sport.rcs.db.entity.UserProfileTagUserRelation;
import com.panda.sport.rcs.db.entity.UserProfileTags;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.service.IUserProfileTagUserRelationService;
import com.panda.sport.rcs.db.service.IUserProfileTagsService;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 投注特征预警管理控制器
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@Api(tags = "投注特征预警管理")
@RestController
@RequestMapping("/userBetTagChangeRecord")
public class UserBetTagChangeRecordController {
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;
    @Autowired
    IUserProfileTagUserRelationService tagUserRelationService;
    @Autowired
    IUserProfileTagsService tagsService;

    Logger log = LoggerFactory.getLogger(UserBetTagChangeRecordController.class);

    @Autowired
    ProducerSendMessageUtils producerSendMessageUtils;

    @ApiOperation(value = "根据条件查询自动化标签日志记录")
    @RequestMapping(value = "/queryAutoTagLogRecord", method = {RequestMethod.POST})
    public Result<IPage<AutoTagLogRecordResVo>> queryAutoTagLogRecord(@RequestBody @Valid AutoTagLogRecordReqVo vo) {
        Page<AutoTagLogRecordResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<AutoTagLogRecordResVo> userBetTagChangeRecordResVoIPage;
        try {
            userBetTagChangeRecordResVoIPage = userProfileUserTagChangeRecordService.queryAutoTagLogRecord(pageParam, vo);
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(userBetTagChangeRecordResVoIPage);
    }

    @ApiOperation(value = "根据条件导出自动化标签日志记录(导出)")
    @PostMapping(value = "/excelAutoTagLogRecord")
    public Result<String> excelAutoTagLogRecord(@RequestBody @Valid AutoTagLogRecordReqVo vo) {
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("用户标签日志1");
            String[] title = new String[]{"用户ID","用户名","商户","操作时间","操作人","日志类型","原值","新值","备注"};
            ExcelUtils.createTitle(workbook, sheet, title);
            Page<AutoTagLogRecordResVo> pageParam = new Page<>(1, -1);
            IPage<AutoTagLogRecordResVo> userBetTagChangeRecordResVoIPage = userProfileUserTagChangeRecordService.queryAutoTagLogRecord(pageParam, vo);
            int rowNum=1;
            int tab=2;
            for (int i = 0; i < userBetTagChangeRecordResVoIPage.getRecords().size(); i++) {
                AutoTagLogRecordResVo resVo = userBetTagChangeRecordResVoIPage.getRecords().get(i);
                if (i >= 50000 && i % 50000 == 0) {
                    sheet = workbook.createSheet("用户标签日志" + tab++);
                    rowNum = 1;
                }
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(String.valueOf(resVo.getUserId()));
                row.createCell(1).setCellValue(resVo.getUserName());
                row.createCell(2).setCellValue(resVo.getMerchantCode());
                row.createCell(3).setCellValue(LocalDateTimeUtil.milliToDateTime(resVo.getOperateTime()));
                row.createCell(4).setCellValue(resVo.getChangeManner());
                row.createCell(5).setCellValue("投注特征标签");
                row.createCell(6).setCellValue(resVo.getBeforeTagName());
                row.createCell(7).setCellValue(resVo.getChangeTagName());
                String realityValue = "";
                if(ObjectUtils.isNotEmpty(resVo.getRealityValue())){
                    try {
                        JSONArray jsonArray = JSONArray.parseArray(resVo.getRealityValue());
                        for (Object obj : jsonArray) {
                            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(obj));
                            realityValue = jsonObject.get("result").toString().replace("@;@","") + "   ";
                        }
                    } catch (Exception e) {
                        realityValue = resVo.getRealityValue();
                    }
                }

                row.createCell(8).setCellValue(realityValue);
                rowNum++;
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
            sheet.autoSizeColumn(7);
            sheet.autoSizeColumn(8);
            String fileName = ExcelUtils.generatorFileName("自动化标签日志列表");
            //生成excel
            return Result.succes(ExcelUtils.buildExcelFile(fileName, workbook));
        } catch (Exception e) {
            log.error("赛事注单玩法统计导出Excel异常", e.getMessage(), e);
            e.printStackTrace();
            return Result.fail("导出失败");
        }
    }

    @ApiOperation(value = "根据条件查询投注特征记录")
    @RequestMapping(value = "/queryBetTagChangeRecord", method = {RequestMethod.POST})
    public Result<IPage<UserBetTagChangeRecordResVo>> queryBetTagChangeRecord(@RequestBody @Valid UserBetTagChangeRecordReqVo vo) {
        Page<UserBetTagChangeRecordResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserBetTagChangeRecordResVo> userBetTagChangeRecordResVoIPage;
        try {
            userBetTagChangeRecordResVoIPage = userProfileUserTagChangeRecordService.queryBetTagChangeRecord(pageParam, vo);
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(userBetTagChangeRecordResVoIPage);
    }

    @ApiOperation(value = "根据用户id查询用户信息")
    @RequestMapping(value = "/getOneByUserId", method = {RequestMethod.POST})
    public Result<UserInfoAndRecordResVo> getOneByUserId(@RequestBody @Valid UserInfoAndRecordReqVo reqVo) {
        UserInfoAndRecordResVo resVo = new UserInfoAndRecordResVo();
        Page<UserBetTagChangeRecordResVo> pageParam = new Page<>(reqVo.getPageNum(), reqVo.getPageSize());
        try {
            resVo.setRecordResVos(userProfileUserTagChangeRecordService.queryBetTagChangeRecordByUserId(pageParam, reqVo));
            resVo.setUserInfoResVo(userProfileUserTagChangeRecordService.queryUserInfoByUserId(reqVo));
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(resVo);
    }

    @ApiOperation(value = "处理")
    @RequestMapping(value = "/edit", method = {RequestMethod.POST})
    public Result<Boolean> edit(@RequestBody @Valid UserBetTagChangeReqVo reqVo) {
        try {
            userProfileUserTagChangeRecordService.editRecord(reqVo);
        } catch (Exception e) {
            log.error("修改异常{}" + e);
            return Result.fail("修改异常");
        }
        return Result.succes(true);
    }
}
