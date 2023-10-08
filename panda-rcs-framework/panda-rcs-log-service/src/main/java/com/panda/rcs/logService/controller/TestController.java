package com.panda.rcs.logService.controller;

import com.panda.rcs.logService.dto.RcsBusinessLogReqVo;
import com.panda.rcs.logService.mapper.RcsOperateLogMapper;
import com.panda.rcs.logService.mapper.RcsQuotaBusinessLimitLogMapper;
import com.panda.rcs.logService.service.impl.OperateLogServiceImpl;
import com.panda.rcs.logService.utils.EasyExcelUtils;
import com.panda.rcs.logService.utils.ExcelUtils;
import com.panda.rcs.logService.vo.ExportRcsBusinessLog;
import com.panda.rcs.logService.vo.RcsQuotaBusinessLimitLog;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private OperateLogServiceImpl operateLogServiceImpl;
    @Autowired
    private RcsQuotaBusinessLimitLogMapper rcsBusinessLogMapper;

    private final  String EXCEL_TITLE="风控日志";

    @GetMapping("/sqlTest")
    public Map<String, Object> sqlTest() {

        Map<String, Object> result = new HashMap<String, Object>();
        String referenceKey = "4825888055361118665"+"4"+"NNNN";
        RcsOperateLog operateLog = new RcsOperateLog();
        operateLog.setOperatePageCode(14);
        operateLog.setObjectId("123456");
        operateLog.setObjectName("南苏丹主 +28.5");
        operateLog.setExtObjectId("4825888055361118665 / 4 / NNNN");
        operateLog.setExtObjectName("南苏丹朱vs塞内加尔 / 全场让分 / 28.5");
        operateLog.setBehavior("调赔率");
        operateLog.setParameterName("-");
        operateLog.setBeforeVal("0.97\n" +
                "\n" +
                "0.87");
        operateLog.setAfterVal("0.98\n" +
                "\n" +
                "0.86");
        operateLog.setOperateTime(new Date());
        operateLogServiceImpl.saveLog(operateLog);
        result.put("result", "");

        return result;
    }

    @GetMapping("/queryTest")
    public Map<String, Object> queryTest() {
        Map<String, Object> result = new HashMap<String, Object>();
        String referenceKey = "4825888055361118665"+"4"+"NNNN";

        RcsOperateLog operateLog = new RcsOperateLog();
//        operateLog.setOperatePage("早盘操盘");
//        operateLog.setReferenceKey(referenceKey);
//        operateLog.setObjectId("123456");
//        operateLog.setObjectName("南苏丹主 +28.5");
//        operateLog.setExtObjectId("4825888055361118665 / 4 / NNNN");
//        operateLog.setExtObjectName("南苏丹朱vs塞内加尔 / 全场让分 / 28.5");
        operateLog.setBehavior("调赔率");
//        operateLog.setParameterName("-");
//        operateLog.setBeforeVal("0.97\n" +
//                "\n" +
//                "0.87");
//        operateLog.setAfterVal("0.98\n" +
//                "\n" +
//                "0.86");
//        operateLog.setUserName("cptest");
//        operateLog.setOperateTime(new Date());
        //RcsOperateLog rol = rcsOperateLogMapper.queryLastData(referenceKey);

//        List<RcsOperateLog> rol = operateLogServiceImpl.cleanup(operateLog);
        result.put("data", null);

        return result;
    }
    @GetMapping("/excelTest")
    public Map<String, Object> excelTest(HttpServletResponse response) {
        Map<String, Object> result = new HashMap<String, Object>();
        RcsBusinessLogReqVo reqVo=new RcsBusinessLogReqVo();
        reqVo.setStartTime("2023-06-02");
        reqVo.setEndTime("2023-06-30");
        List<ExportRcsBusinessLog> exportList = new ArrayList<>();

            List<RcsQuotaBusinessLimitLog> limitLogList = rcsBusinessLogMapper.queryByExport(reqVo);
            limitLogList.forEach(itemLog ->{
                ExportRcsBusinessLog exportRcsBusinessLog = new ExportRcsBusinessLog();
                BeanUtils.copyProperties(itemLog, exportRcsBusinessLog);
                exportList.add(exportRcsBusinessLog);
            });
            System.out.println("exportList导入数据条数"+exportList.size());
            ExcelUtils.export(response,EXCEL_TITLE,exportList, ExportRcsBusinessLog.class);
            //EasyExcelUtils.writeExcel(response, exportList, EXCEL_TITLE, EXCEL_TITLE, ExportRcsBusinessLog.class);


        return result;
    }


}
