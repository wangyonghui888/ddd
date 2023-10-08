package com.panda.sport.rcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.sport.rcs.common.bean.Result;
import com.panda.sport.rcs.common.utils.ExcelUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.vo.api.request.IpListReqVo;
import com.panda.sport.rcs.common.vo.api.request.UpdateByRiskUserVisitIpReqVo;
import com.panda.sport.rcs.common.vo.api.request.UserStatisticReqVo;
import com.panda.sport.rcs.common.vo.api.response.IpListResVo;
import com.panda.sport.rcs.common.vo.api.response.ListByUserByGroupIdResVo;
import com.panda.sport.rcs.common.vo.api.response.UserListByIpResVo;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIpTag;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.panda.sport.rcs.db.service.IRiskOrderTagIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 投注IP管理
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@Api(tags = "数据维护-投注IP管理")
@RestController
@RequestMapping("/visitIpTag")
public class RiskUserVisitIpTagController {

    @Autowired
    private IRiskUserVisitIpTagService ipTagService;

    @Autowired
    private IRiskUserVisitIpService ipService;

    @Autowired
    private IOrderStaticsForIpService service;

    @Autowired
    private IRiskOrderTagIpService tagIpService;

    Logger log = LoggerFactory.getLogger(RiskUserVisitIpTagController.class);

    @ApiOperation(value = "查询IP投注标签列表")
    @RequestMapping(value = "/ipTagList", method = {RequestMethod.POST})
    public Result<List<RiskUserVisitIpTag>> ipTagList() {
        List<RiskUserVisitIpTag> list;
        try {
            list = ipTagService.list();
        }catch (Exception e){
            log.info("查询异常");
            return Result.fail( "查询异常");
        }
        return Result.succes(list);
    }

    @ApiOperation(value = "根据IP查询所有关联用户")
    @RequestMapping(value = "/queryUserListByIp", method = {RequestMethod.POST})
    public Result<IPage<UserListByIpResVo>> queryUserListByIp(@RequestBody @Valid UserStatisticReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getIp())){
            return Result.fail("IP不能为空");
        }
        Page<UserListByIpResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<UserListByIpResVo> userListByIpResVoIPage;
        try {
            userListByIpResVoIPage = ipService.queryUserListByIp(pageParam, vo.getIp());
            if(!ObjectUtils.isEmpty(userListByIpResVoIPage)){
                for (UserListByIpResVo record : userListByIpResVoIPage.getRecords()) {
                    record.setSevenDayBetAmount(record.getSevenDayBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setSevenDayProfitAmount(record.getSevenDayProfitAmount().divide(BigDecimal.valueOf(100)));
                }
            }
        }catch (Exception e){
            log.info("查询异常");
            return Result.fail( "查询异常");
        }
        return Result.succes(userListByIpResVoIPage);
    }

    @ApiOperation(value = "根据IP查询投注统计信息")
    @RequestMapping(value = "/queryStatistics", method = {RequestMethod.POST})
    public Result<IPage<IpListResVo>> queryStatistics(@RequestBody @Valid IpListReqVo vo) throws Exception {
        if(ObjectUtils.isEmpty(vo.getStartFinalTime()) || ObjectUtils.isEmpty(vo.getEndFinalTime())){
            return Result.fail("查询时间不能为空");
        }
        vo.setEndFinalTime(vo.getEndFinalTime() + LocalDateTimeUtil.dayMill);
        Page<IpListResVo> pageParam = new Page<>(vo.getPageNum(), vo.getPageSize());
        IPage<IpListResVo> list;
        try {
            List<String> ips = new ArrayList<>();
            if(!ObjectUtils.isEmpty(vo.getIds()) && vo.getIds().length>0){
                List<RiskOrderTagIp> tagIps = tagIpService.queryListByUserId(vo.getIds(), vo.getIds().length);
                if(tagIps.size()>0){
                    ips = tagIps.stream().map(RiskOrderTagIp::getIp).distinct().collect(Collectors.toList());
                }
            }
            list = service.queryIpList(pageParam, vo, ips);
            if(!ObjectUtils.isEmpty(list)){
                for (IpListResVo record : list.getRecords()) {
                    record.setSevenDaysBetAmount(record.getSevenDaysBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setSevenDaysProfitAmount(record.getSevenDaysProfitAmount().divide(BigDecimal.valueOf(100)));
                    record.setBetAmount(record.getBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setProfitAmount(record.getProfitAmount().divide(BigDecimal.valueOf(100)));
                }
            }

        }catch (Exception e){
            log.info("查询异常{}", e);
            return Result.fail( "查询异常");
        }
        return Result.succes(list);
    }

    @ApiOperation(value = "新增IP投注标签")
    @RequestMapping(value = "/saveIpTagList", method = {RequestMethod.POST})
    public Result<Long> saveIpTagList(@RequestBody @Valid RiskUserVisitIpTag vo) {
        if(ObjectUtils.isEmpty(vo.getTag())){
            return Result.fail("名称不能为空");
        }
        LambdaQueryWrapper<RiskUserVisitIpTag> warpper = new LambdaQueryWrapper<>();
        warpper.eq(RiskUserVisitIpTag::getTag, vo.getTag());
        if(!ObjectUtils.isEmpty(ipTagService.getOne(warpper))){
            return Result.fail("名称已存在");
        }
        vo.setCreateTime(System.currentTimeMillis());
        try {
            ipTagService.save(vo);
        } catch (Exception e) {
            log.info("新增IP投注标签异常{}", e);
            return Result.fail("新增IP投注标签异常");
        }
        return Result.succes(ipTagService.getOne(warpper).getId());
    }

    @ApiOperation(value = "修改IP投注标签")
    @RequestMapping(value = "/updateIpTagList", method = {RequestMethod.POST})
    public Result<Boolean> updateIpTagList(@RequestBody @Valid RiskUserVisitIpTag vo) {
        if(ObjectUtils.isEmpty(vo.getId())){
            return Result.fail("Id不能为空");
        }
        if(ObjectUtils.isEmpty(vo.getTag())){
            return Result.fail("名称不能为空");
        }
        LambdaQueryWrapper<RiskUserVisitIpTag> warpper = new LambdaQueryWrapper<>();
        warpper.eq(RiskUserVisitIpTag::getTag, vo.getTag());
        //warpper.eq(RiskUserVisitIpTag::getId, vo.getId());

        //如果根据“名称”所查询出来的ID不等于前端所传ID，则表示名称已存在
        RiskUserVisitIpTag one = ipTagService.getOne(warpper);
        if(!ObjectUtils.isEmpty(one)){
            if(!one.getId().equals(vo.getId())){
                return Result.fail("名称已存在");
            }
        }
        try {
            ipTagService.updateById(vo);
        } catch (Exception e) {
            log.info("修改IP投注标签异常{}", e);
            return Result.fail("修改IP投注标签异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "删除IP投注标签")
    @RequestMapping(value = "/delIpTagList", method = {RequestMethod.POST})
    public Result<Boolean> delIpTagList(@RequestBody @Valid RiskUserVisitIpTag vo) {
        if(ObjectUtils.isEmpty(vo.getId())){
            return Result.fail("id不能为空");
        }
        try {
            ipTagService.removeById(vo);
            LambdaUpdateWrapper<RiskUserVisitIp> warpper = new LambdaUpdateWrapper<>();
            warpper.eq(RiskUserVisitIp::getTagId, vo.getId());
            warpper.set(RiskUserVisitIp::getTagId, null);
            ipService.update(warpper);
        } catch (Exception e) {
            log.info("删除IP投注标签异常{}", e);
            return Result.fail("删除IP投注标签异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "单个/批量修改IP标签")
    @RequestMapping(value = "/updateVisitIpTagIdByIp", method = {RequestMethod.POST})
    public Result<Boolean> updateVisitIpTagIdByIp(@RequestBody @Valid UpdateByRiskUserVisitIpReqVo vo) {
        if(ObjectUtils.isEmpty(vo.getIp())){
            return Result.fail("ip不能为空");
        }
//        if(ObjectUtils.isEmpty(vo.getTagId())){
//            return Result.fail("tagId不能为空");
//        }
        try {
            for (String i : vo.getIp()) {
                LambdaUpdateWrapper<RiskOrderStatisticsByIp> warpper = new LambdaUpdateWrapper<>();
                warpper.eq(RiskOrderStatisticsByIp::getIp, i);
                warpper.set(RiskOrderStatisticsByIp::getTagId, vo.getTagId());
                service.update(warpper);
            }
        } catch (Exception e) {
            log.info("修改IP标签异常{}", e);
            return Result.fail("修改IP标签异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "修改IP对应备注信息")
    @RequestMapping(value = "/updateRemakeByIp", method = {RequestMethod.POST})
    public Result<Boolean> updateRemakeByIp(@RequestBody @Valid RiskOrderStatisticsByIp vo) {
        if(ObjectUtils.isEmpty(vo.getId())){
            return Result.fail("id不能为空");
        }
        try {
            LambdaUpdateWrapper<RiskOrderStatisticsByIp> updateWarpper = new LambdaUpdateWrapper<>();
            updateWarpper.eq(RiskOrderStatisticsByIp::getId, vo.getId());
            updateWarpper.set(RiskOrderStatisticsByIp::getRemark, vo.getRemark());
            service.update(updateWarpper);
        } catch (Exception e) {
            log.info("修改IP对应备注信息异常{}", e);
            return Result.fail("修改IP对应备注信息异常");
        }
        return Result.succes();
    }

    @ApiOperation(value = "根据IP查询所有关联用户(导出)")
    @PostMapping(value = "/excelForUserListByIp")
    public Result<String> excelForUserListByIp(HttpServletResponse response, @RequestBody @Valid UserStatisticReqVo vo) throws Exception {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("会员列表");
            String[] title = new String[]{"用户ID", "用户名称", "所属商户", "一级标签", "二级标签", "7天内总投注", "7天内总输赢", "关联天数"};
            ExcelUtils.createTitle(workbook, sheet, title);
            Page<UserListByIpResVo> pageParam = new Page<>(1, 999999);
            IPage<UserListByIpResVo> userListByIpResVos = ipService.queryUserListByIp(pageParam, vo.getIp());
            if(!ObjectUtils.isEmpty(userListByIpResVos)){
                for (UserListByIpResVo record : userListByIpResVos.getRecords()) {
                    record.setSevenDayBetAmount(record.getSevenDayBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setSevenDayProfitAmount(record.getSevenDayProfitAmount().divide(BigDecimal.valueOf(100)));
                }
            }
            int rowNum = 1;
            for (UserListByIpResVo resVo : userListByIpResVos.getRecords()) {
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(String.valueOf(resVo.getUserId()));
                row.createCell(1).setCellValue(resVo.getUserName());
                row.createCell(2).setCellValue(resVo.getMerchantCode());
                row.createCell(3).setCellValue(resVo.getTagName());

                //把所有二级标签合成一列
                StringBuffer stb = new StringBuffer();
                stb.append(resVo.getSportJson()==null?"":resVo.getSportJson());
                stb.append(resVo.getTournamentJson()==null?"":resVo.getTournamentJson());
                stb.append(resVo.getOrderStageJson()==null?"":resVo.getOrderStageJson());
                stb.append(resVo.getOrderTypeJson()==null?"":resVo.getOrderTypeJson());
                stb.append(resVo.getPlayJson()==null?"":resVo.getPlayJson());
                row.createCell(4).setCellValue(String.valueOf(stb));

                row.createCell(5).setCellValue(String.valueOf(resVo.getSevenDayBetAmount()));
                row.createCell(6).setCellValue(String.valueOf(resVo.getSevenDayProfitAmount()));
                row.createCell(7).setCellValue(resVo.getDays());
                rowNum++;
            }
            String fileName = ExcelUtils.generatorFileName("会员列表");
            //生成excel
            return Result.succes(ExcelUtils.buildExcelFile(fileName, workbook));
        } catch (Exception e) {
            log.error("赛事注单玩法统计导出Excel异常",e.getMessage(),e);
            e.printStackTrace();
            return Result.fail("导出失败");
        }
    }

    @ApiOperation(value = "根据IP查询投注统计信息(导出)")
    @PostMapping(value = "/excelForStatistics")
    public Result<String> excelForStatistics(HttpServletResponse response, @RequestBody @Valid IpListReqVo vo) throws Exception {
        vo.setEndFinalTime(vo.getEndFinalTime() + LocalDateTimeUtil.dayMill);
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("IP列表");
            String[] title = new String[]{"IP地址","地区","IP标签","关联用户","累计投注金额","累计亏盈","盈利率","近7日有效投注","近7日输赢金额","最后下注时间","备注"};
            ExcelUtils.createTitle(workbook, sheet, title);
            Page<IpListResVo> pageParam = new Page<>(1, 999999);

            List<String> ips = new ArrayList<>();
            if(!ObjectUtils.isEmpty(vo.getIds()) && vo.getIds().length>0){
                LambdaQueryWrapper<RiskOrderTagIp> warpper = new LambdaQueryWrapper<>();
                warpper.in(RiskOrderTagIp::getUserId,vo.getIds());
                List<RiskOrderTagIp> tagIps = tagIpService.list(warpper);
                ips = tagIps.stream().map(RiskOrderTagIp::getIp).distinct().collect(Collectors.toList());
            }

            IPage<IpListResVo> List = service.queryIpList(pageParam, vo, ips);
            if(!ObjectUtils.isEmpty(List)){
                for (IpListResVo record : List.getRecords()) {
                    record.setSevenDaysBetAmount(record.getSevenDaysBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setSevenDaysProfitAmount(record.getSevenDaysProfitAmount().divide(BigDecimal.valueOf(100)));
                    record.setBetAmount(record.getBetAmount().divide(BigDecimal.valueOf(100)));
                    record.setProfitAmount(record.getProfitAmount().divide(BigDecimal.valueOf(100)));
                }
            }
            int rowNum=1;
            for(IpListResVo resVo: List.getRecords()){
                HSSFRow row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(resVo.getIp());
                row.createCell(1).setCellValue(resVo.getArea());
                row.createCell(2).setCellValue(resVo.getTagName());
                row.createCell(3).setCellValue(resVo.getUserNum());
                row.createCell(4).setCellValue(String.valueOf(resVo.getBetAmount()));
                row.createCell(5).setCellValue(String.valueOf(resVo.getProfitAmount()));
                row.createCell(6).setCellValue(resVo.getProfitProbability() + "%");
                row.createCell(7).setCellValue(String.valueOf(resVo.getSevenDaysBetAmount()));
                row.createCell(8).setCellValue(String.valueOf(resVo.getSevenDaysProfitAmount()));
                row.createCell(9).setCellValue(LocalDateTimeUtil.milliToDateTime(resVo.getFinalBetTime()));
                row.createCell(10).setCellValue(resVo.getRemark());
                rowNum++;
            }
            String fileName = ExcelUtils.generatorFileName("IP列表");
            //生成excel
            return Result.succes(ExcelUtils.buildExcelFile(fileName, workbook));
        } catch (Exception e) {
            log.error("赛事注单玩法统计导出Excel异常",e.getMessage(),e);
            e.printStackTrace();
            return Result.fail("导出失败");
        }
    }

    @RequestMapping("/down")
    @ApiOperation(value = "Excel下载", notes = "Excel下载  name为文件名")
    public ResponseEntity<FileSystemResource> downFile(String name) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        File file = new File(ExcelUtils.filePath,name);
        //name = new String(Base64.decodeBase64(name.replace(".xls","")));
        name=name.substring(0,name.indexOf("_"));
        headers.add("Content-Disposition", "attachment;filename="+ URLEncoder.encode(name,"utf-8") +".xls");
        return ResponseEntity
                .ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(headers)
                .body(new FileSystemResource(file));
    }
}
