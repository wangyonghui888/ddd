package com.panda.rcs.logService.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panda.rcs.logService.Enum.BusinessLogTypeEnum;
import com.panda.rcs.logService.dto.RcsBusinessLogReqVo;
import com.panda.rcs.logService.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.rcs.logService.utils.EasyExcelUtils;
import com.panda.rcs.logService.utils.ExcelUtils;
import com.panda.rcs.logService.vo.ExportRcsBusinessLog;
import com.panda.rcs.logService.vo.HttpResponse;
import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import com.panda.rcs.logService.vo.RcsOperateLogResponseVO;
import com.panda.sport.rcs.common.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/query-business")
@Slf4j
public class BusinessLogController {
    @Autowired
    private RcsQuotaBusinessLimitLogMapper rcsBusinessLogMapper;

    private final  String EXCEL_TITLE="风控日志";
    /**
     * 日志查询
     * */
    @PostMapping("/log")
    public HttpResponse getBusinessLogList(@RequestBody RcsBusinessLogReqVo reqVo) {
        RcsOperateLogResponseVO response = new RcsOperateLogResponseVO();
        if (StringUtils.isNotBlank(reqVo.getOperateType())){
            reqVo.setOperateType(BusinessLogTypeEnum.getValue(Integer.valueOf(reqVo.getOperateType())));
            //用户二级标签变更查询时间转换
            if(BusinessLogTypeEnum.ejbqbg.getValue().equals(reqVo.getOperateType())){
                reqVo.setCurrent(reqVo.getPageNum());
                reqVo.setSize(reqVo.getPageSize());
                       if(StringUtils.isNotBlank(reqVo.getStartTime())){
                           reqVo.setStartTime(DateUtils.transferLongToDateStrings(Long.parseLong(reqVo.getStartTime())));
                       }if(StringUtils.isNotBlank(reqVo.getEndTime())){
                    reqVo.setEndTime(DateUtils.transferLongToDateStrings(Long.parseLong(reqVo.getEndTime())));

                }
            }
        }
        log.info("BusinessLogList::参数信息{}", JSONObject.toJSONString(reqVo));
        try {
            //兼容前端页码传值错误处理
           Integer totalCount=rcsBusinessLogMapper.selectCountByParam(reqVo);
           if(null!=totalCount&&totalCount<=reqVo.getSize()){
               reqVo.setCurrent(1);
               response.setPageNum(1);
           }
            List<RcsQuotaBusinessLimitLog> limitLogList = rcsBusinessLogMapper.queryByPage(reqVo);
            if(!CollectionUtils.isEmpty(limitLogList)){
                limitLogList.forEach(item->{
                    item.setOperateType(setBusinessTypeName(item.getOperateType()));
                });
                response.setList(limitLogList);
            }
          response.setTotal(totalCount);
          log.info("BusinessLogList::返回数据{}", limitLogList.size());
        } catch (Exception e) {
            log.error("BusinessLogList::查詢业务通用日誌失敗" + e.getMessage(), e);
            return HttpResponse.fail("查詢业务通用日誌失敗");
        }
        return HttpResponse.success(response);
    }
    /**
     * 日志导出
     * */
    @RequestMapping(value = "/excelBusinessLog", method = RequestMethod.GET)
    public void downExcelBusinessLimitLog(RcsBusinessLogReqVo reqVo, HttpServletResponse response) throws Exception {
            if(null == reqVo.getTotalCount()){
                reqVo.setTotalCount(1L);
            }
            if(null == reqVo.getCurrent() || 0 == reqVo.getCurrent()){
                reqVo.setCurrent(1);
            }
            if(null == reqVo.getSize() || 0 == reqVo.getSize()){
                if(reqVo.getTotalCount() > 10000L){
                    reqVo.setSize(10000);
                }else {
                    reqVo.setSize(reqVo.getTotalCount().intValue());
                }
            }
        if (StringUtils.isNotBlank(reqVo.getOperateType())){
            reqVo.setOperateType(BusinessLogTypeEnum.getValue(Integer.valueOf(reqVo.getOperateType())));
            //用户二级标签变更查询时间转换
            if(BusinessLogTypeEnum.ejbqbg.getValue().equals(reqVo.getOperateType())){
                reqVo.setCurrent(reqVo.getPageNum());
                reqVo.setSize(reqVo.getPageSize()> 10000L ?10000:reqVo.getPageSize());
                if(StringUtils.isNotBlank(reqVo.getStartTime())){
                    reqVo.setStartTime(DateUtils.transferLongToDateStrings(Long.parseLong(reqVo.getStartTime())));
                }if(StringUtils.isNotBlank(reqVo.getEndTime())){
                    reqVo.setEndTime(DateUtils.transferLongToDateStrings(Long.parseLong(reqVo.getEndTime())));

                }
            }
        }
        log.info("excelBusinessLog 参数信息{}", JSONObject.toJSONString(reqVo));
        List<ExportRcsBusinessLog> exportList = new ArrayList<>();
        try {
             List<RcsQuotaBusinessLimitLog> limitLogList = rcsBusinessLogMapper.queryByExport(reqVo);
             limitLogList.forEach(itemLog ->{
                 //导出紧急开关换行处理
                 if(itemLog.getOperateType().equals(BusinessLogTypeEnum.rzkg.getValue())||
                         itemLog.getOperateType().equals(BusinessLogTypeEnum.qlzskg.getValue())){
                     itemLog.setParamName(itemLog.getParamName().replace("<br>",String.valueOf((char)10)));
                 }
                itemLog.setOperateType(setBusinessTypeName(itemLog.getOperateType()));
                ExportRcsBusinessLog exportRcsBusinessLog = new ExportRcsBusinessLog();
                BeanUtils.copyProperties(itemLog, exportRcsBusinessLog);
                exportList.add(exportRcsBusinessLog);
              });
              ExcelUtils.export(response,EXCEL_TITLE,exportList, ExportRcsBusinessLog.class);
             //EasyExcelUtils.writeExcel(response, exportList, EXCEL_TITLE, EXCEL_TITLE, ExportRcsBusinessLog.class);

        }catch (Exception e){
                log.error("excelBusinessLog::日志导出Excel异常{}",e.getMessage(), e);
            }
    }
    /**
     * 特殊处理
     * */
    private String setBusinessTypeName(String value){
        if(Objects.isNull(value)){
            return "";
        }
        String levelName = "";
        switch (value){
            case "10000":
                levelName = "二级标签";
                break;
            case "10010":
                levelName = "商户管理";
                break;
            case "10020":
                levelName = "标签管理";
                break;
            case "10030":
                levelName = "投注特征标签风控措施";
                break;
            case "10040":
                levelName = "动态风控设置";
                break;
            case "10041":
                levelName = "用户提前结算动态抽水规则";
                break;
            case "10042":
                levelName = "动态赔率分组";
                break;
            case "10043":
                levelName = "全局开关";
                break;
            case "10044":
                levelName = "标签风控措施";
                break;
            case "10045":
                levelName = "赔率分组";
                break;
            case "10050":
                levelName = "危险联赛池管理";
                break;
            case "10060":
                levelName = "危险球队池管理";
                break;
            case "10080":
                levelName = "外部备注历史记录";
                break;
            case "10090":
                levelName = "切换";
                break;
            case "10091":
                levelName = "恢复";
                break;
            case "10092":
                levelName = "投注开关";
                break;
            case "10093":
                levelName = "球类展示开关";
                break;

            case "10100":
                levelName = "通用设置";
                break;
            case "10101":
                levelName = "批量设置";
                break;
            case "10102":
                levelName = "例外批量设置";
                break;
            case "10103":
                levelName = "单商户变更";
                break;
            case "10104":
                levelName="数据传输设置";
                break;
            default:
                levelName = value;
                break;
        }
        return levelName;
    }
}
