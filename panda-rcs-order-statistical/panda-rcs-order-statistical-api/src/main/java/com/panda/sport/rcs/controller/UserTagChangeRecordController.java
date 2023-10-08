package com.panda.sport.rcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.api.request.UserProfileUserTagChangeRecordReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserSaveTagChangeReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserTagChangeReqVo;
import com.panda.sport.rcs.common.vo.api.response.UserProfileUserTagChangeRecordResVo;
import com.panda.sport.rcs.common.vo.api.response.UserTagChangeRecordResVo;
import com.panda.sport.rcs.db.entity.UserProfileUserTagChangeRecord;
import com.panda.sport.rcs.db.service.IUserProfileUserTagChangeRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 标签变更记录控制器
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@Api(tags = "标签变更记录管理")
@RestController
@RequestMapping("/userTagChangeRecord")
public class UserTagChangeRecordController {
    @Autowired
    IUserProfileUserTagChangeRecordService userProfileUserTagChangeRecordService;

    Logger log = LoggerFactory.getLogger(UserTagChangeRecordController.class);

    @ApiOperation(value = "根据条件查询标签变更记录")
    @RequestMapping(value = "/queryRecordList", method = {RequestMethod.POST})
    public Result<IPage<UserProfileUserTagChangeRecordResVo>> queryRecordList(@RequestBody @Valid UserProfileUserTagChangeRecordReqVo vo) {
        Page<UserProfileUserTagChangeRecordResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserProfileUserTagChangeRecordResVo> userListByIpResVoIPage;
        try {
            userListByIpResVoIPage = userProfileUserTagChangeRecordService.queryRecordList(pageParam, vo);

            for (UserProfileUserTagChangeRecordResVo record : userListByIpResVoIPage.getRecords()) {
                record.setChangeTimeForDate(LocalDateTimeUtil.milliToDateTime(record.getChangeTime()));
            }

        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(userListByIpResVoIPage);
    }

    @ApiOperation(value = "根据财务标签当前自然日新增记录")
    @RequestMapping(value = "/queryRecordCount", method = {RequestMethod.POST})
    public Result<Integer> queryRecordCount() {
        Integer currentDayCount = 0;
        try {
            LambdaQueryWrapper<UserProfileUserTagChangeRecord> warpper = new LambdaQueryWrapper<>();
            warpper.ge(UserProfileUserTagChangeRecord::getChangeTime, LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()));
            warpper.eq(UserProfileUserTagChangeRecord::getTagType, 4);
            List<UserProfileUserTagChangeRecord> list = userProfileUserTagChangeRecordService.list(warpper);
            if(!ObjectUtils.isEmpty(list)){
                currentDayCount = list.size();
            }
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(currentDayCount);
    }

    @ApiOperation(value = "根据投注标签当前自然日新增记录")
    @RequestMapping(value = "/queryBetTagRecordCount", method = {RequestMethod.POST})
    public Result<Integer> queryBetTagRecordCount() {
        Integer currentDayCount = 0;
        try {
            LambdaQueryWrapper<UserProfileUserTagChangeRecord> warpper = new LambdaQueryWrapper<>();
            warpper.ge(UserProfileUserTagChangeRecord::getChangeTime, LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()));
            warpper.eq(UserProfileUserTagChangeRecord::getTagType, 2);
            warpper.eq(UserProfileUserTagChangeRecord::getStatus, 0);
            List<UserProfileUserTagChangeRecord> list = userProfileUserTagChangeRecordService.list(warpper);
            if(!ObjectUtils.isEmpty(list)){
                currentDayCount = list.size();
            }
        }catch (Exception e){
            log.error("查询异常{}" + e);
            return Result.fail("查询异常");
        }
        return Result.succes(currentDayCount);
    }

    @ApiOperation(value = "根据条件导出标签变更记录(导出)")
    @PostMapping(value = "/excelRecord")
    public Result<String> excelRecord(@RequestBody @Valid UserProfileUserTagChangeRecordReqVo vo) {
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("IP列表");
            String[] title = new String[]{"报警ID","用户ID","用户名","所属商户","玩家组","原有标签","变更标签","触发时间","变更方式","操作人","变更理由"};
            ExcelUtils.createTitle(workbook, sheet, title);
            Page<UserProfileUserTagChangeRecordResVo> pageParam = new Page<>(1, 9999);
            IPage<UserProfileUserTagChangeRecordResVo> userListByIpResVoIPage = userProfileUserTagChangeRecordService.queryRecordList(pageParam, vo);
            if(!ObjectUtils.isEmpty(userListByIpResVoIPage) && userListByIpResVoIPage.getSize() > 5000){
                return Result.fail("一次只能导出5000条数据");
            }
            int rowNum=1;
            for(UserProfileUserTagChangeRecordResVo resVo: userListByIpResVoIPage.getRecords()){
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(resVo.getId());
                row.createCell(1).setCellValue(String.valueOf(resVo.getUserId()));
                row.createCell(2).setCellValue(resVo.getUserName());
                row.createCell(3).setCellValue(resVo.getMerchantCode());
                row.createCell(4).setCellValue(resVo.getGroupName());
                row.createCell(5).setCellValue(resVo.getChangeBefore());
                row.createCell(6).setCellValue(resVo.getChangeAfter());
                row.createCell(7).setCellValue(LocalDateTimeUtil.milliToDateTime(resVo.getChangeTime()));
                row.createCell(8).setCellValue(resVo.getChangeType().equals(1) ? "自动" : "手动");
                row.createCell(9).setCellValue(resVo.getChangeManner());
                row.createCell(10).setCellValue(resVo.getChangeReason());
                rowNum++;
            }
            String fileName = ExcelUtils.generatorFileName("财务特征变更记录");
            //生成excel
            return Result.succes(ExcelUtils.buildExcelFile(fileName, workbook));
        } catch (Exception e) {
            log.error("赛事注单玩法统计导出Excel异常", e.getMessage(), e);
            e.printStackTrace();
            return Result.fail("导出失败");
        }
    }

    @ApiOperation(value = "用户初始化标签历史导入数据")
    @RequestMapping(value = "/initHistoryData", method = {RequestMethod.GET})
    public Result<Boolean> initHistoryData() {
        userProfileUserTagChangeRecordService.batchRelationHistoryData();
        return Result.succes();
    }


}
