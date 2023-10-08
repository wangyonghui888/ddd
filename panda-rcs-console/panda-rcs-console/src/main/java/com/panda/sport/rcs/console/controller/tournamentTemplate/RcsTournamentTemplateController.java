package com.panda.sport.rcs.console.controller.tournamentTemplate;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.console.common.utils.ExcelListener;
import com.panda.sport.rcs.console.dto.RcsSpecialPumpingDTO;
import com.panda.sport.rcs.console.dto.TournamentTemplateUpdateParam;
import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.pojo.TournamentTemplateExcelVO;
import com.panda.sport.rcs.console.response.ResponseResult;
import com.panda.sport.rcs.console.service.RcsTournamentTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Controller
@RequestMapping("tournamentTemplate")
@Slf4j
public class RcsTournamentTemplateController {

    @Autowired
    private RcsTournamentTemplateService templateService;

    /**
     * 进入联赛模板设置界面
     * @return
     */
    @RequestMapping("/templateSetting")
    public String templateSetting() {
        return "tournamentTemplate/templateSetting";
    }

    /**
     * 初始化对应玩法的特殊限额数据
     * @return
     */
    @PostMapping("/updateSpecialPumping")
    @ResponseBody
    public ResponseResult updateSpecialPumping(RcsSpecialPumpingDTO dto){
        ResponseResult rs = new ResponseResult();
        if(ObjectUtils.isEmpty(dto.getPlayId())){
            rs.setCode("-1");
            rs.setMessage("玩法id不能为空");
            return rs;
        }
        if(ObjectUtils.isEmpty(dto.getLiveStr())){
            rs.setCode("-1");
            rs.setMessage("滚球Spread值不能为空");
            return rs;
        }
        if(ObjectUtils.isEmpty(dto.getPreStr())){
            rs.setCode("-1");
            rs.setMessage("早盘Spread值不能为空");
            return rs;
        }
        try {
            templateService.updateSpecialPumping(dto);
            rs.setCode("1");
            rs.setMessage("保存成功");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("初始化对应玩法的特殊限额数据异常"+e);
            log.error("初始化对应玩法的特殊限额数据异常",e);
        }
        return rs;
    }

    /**
     * 新增玩法
     * @return
     */
    @PostMapping("/addTournamentTemplatePlay")
    @ResponseBody
    public ResponseResult addTournamentTemplatePlay(String param){
        ResponseResult rs = new ResponseResult();
        try {
            if(param != null && param.equals("")){
                rs.setCode("-1");
                rs.setMessage("参数不能为空,请和开发人员验证正确的json串后提交");
                return rs;
            }
            templateService.addTournamentTemplatePlay(JSONObject.parseObject(param, TournamentTemplateUpdateParam.class));
            rs.setCode("1");
            rs.setMessage("保存成功");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("新增玩法异常"+e);
            log.error("新增玩法异常",e);
        }
        return rs;
    }

    /**
     * 初始化联赛模板(需导入后执行)
     * @return
     */
    @PostMapping("/initTournamentTemplate")
    @ResponseBody
    public ResponseResult initTournamentTemplate(Integer sportId){
        ResponseResult rs = new ResponseResult();
        try {
            if(sportId != null && sportId.equals("")){
                rs.setCode("-1");
                rs.setMessage("赛种ID不能为空");
                return rs;
            }
            templateService.initTournamentTemplate(sportId);
            rs.setCode("1");
            rs.setMessage("保存成功");
        } catch (Exception e) {
            rs.setCode("-1");
            rs.setMessage("初始化联赛模板异常"+e);
            log.error("初始化联赛模板异常",e);
        }
        return rs;
    }

    /**
     * 导入初始化模板所需数据
     * @param file
     * @return
     */
    @PostMapping("/importTemplate")
    public String importTemplate(@RequestParam("file") MultipartFile file) {
        long startOrigin = System.currentTimeMillis();
        String linkId = UUID.randomUUID().toString().replaceAll("-", "");
        MDC.put("linkId", linkId);
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (!".xls".equalsIgnoreCase(suffix) && !".xlsx".equalsIgnoreCase(suffix)) {
            log.error("::{}::excel格式不正确", linkId);
            MDC.remove("linkId");
            return "/error";
        }
        log.info("::{}::excle导入开始", linkId);
        ExcelListener listener = new ExcelListener();
        List<TournamentTemplateExcelVO> collect = new ArrayList<>();
        InputStream inputStream = null;
        ExcelReader excelReader = null;
        try {
            inputStream = file.getInputStream();
            excelReader = new ExcelReader(inputStream, suffix.equals("xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX, null, listener);
            excelReader.read(new Sheet(1, 1, TournamentTemplateExcelVO.class));
            List<Object> list = listener.getDatas();
            if (list.size() > 1) {
                collect = list.stream().map(m -> (TournamentTemplateExcelVO) m).collect(Collectors.toList());
                listener.clearDatas();
            }
            if (!CollectionUtils.isEmpty(collect)) {
                List<List<TournamentTemplateExcelVO>> lists = Lists.partition(collect, 20000);
                CountDownLatch countDownLatch = new CountDownLatch(lists.size());
                log.info("::{}::入库分批处理开始,批次数共:{}", linkId, lists.size());
                for (List<TournamentTemplateExcelVO> listSub : lists) {
                    templateService.importTemplate(listSub, countDownLatch);
                }
                try {
                    countDownLatch.await(); //保证之前的所有的线程都执行完成，才会走下面的；
                } catch (Exception e) {
                    log.error("::{}::入库分批处理异常:{}", linkId, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("::{}::入库分批处理异常:{}", linkId, e.getMessage());
            return "/error";
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ex) {
                    log.error("::{}::excle导入,关闭输入流异常:{}", linkId, ex.getMessage());
                }
            }
            if (excelReader != null) {
                excelReader.finish();
            }
            MDC.remove("linkId");
        }
        long endOrigin = System.currentTimeMillis();
        log.info("::{}::excle导入成功,总计耗时：{}", linkId, endOrigin - startOrigin);
        return templateSetting();
    }

}
