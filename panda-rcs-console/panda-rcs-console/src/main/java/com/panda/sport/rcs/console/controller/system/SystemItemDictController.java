package com.panda.sport.rcs.console.controller.system;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import com.panda.sport.rcs.console.common.utils.ExcelListener;
import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.pojo.SystemItemDict;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.SystemItemDictService;
import com.panda.sport.rcs.console.vo.SystemItemDictVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.controller.system
 * @ClassName: SystemItemDictController
 * @Description: 字典表controller
 * @Date: 2023/3/14 20:21
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Controller
@RequestMapping("dict")
public class SystemItemDictController {

    private static final Logger logger = LoggerFactory.getLogger(SystemItemDictController.class);

    @Resource
    private SystemItemDictService systemItemDictService;

    @RequestMapping("dictManage")
    public String toPage() {
        logger.info("进入字典管理");
        return "dict/dictManage";
    }

    @RequestMapping(value = "/getDictList", method = RequestMethod.GET)
    @ResponseBody
    public PageDataResult getSystemItemDict(SystemItemDictVo param) {
        PageDataResult pageDataResult = new PageDataResult();
        try {
            pageDataResult = systemItemDictService.getDictPage(param);
        } catch (Exception e) {
            logger.error("字典列表查询异常:", e);
        }
        return pageDataResult;
    }

    @RequestMapping(value = "/delDict", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> delDict(Long id) {
        try {
            return systemItemDictService.delDict(id);
        } catch (Exception e) {
            logger.error("字典列表删除异常:", e);
        }
        return new HashMap<>();
    }

    @RequestMapping(value = "/editDict", method = RequestMethod.POST)
    @ResponseBody
    public Map <String, Object> editDict(SystemItemDictVo param) {
        try {
            return systemItemDictService.editDict(param);
        } catch (Exception e) {
            logger.error("字典列表编辑异常:", e);
        }
        return new HashMap<>();
    }

    @RequestMapping(value = "/importDict", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> importDict(@RequestParam(value = "file") MultipartFile file) {
        Map<String, Object> msg = new HashMap<>();
        try {
            String filename = file.getOriginalFilename();
            assert filename != null;
            String suffix = filename.substring(filename.lastIndexOf("."));
            if (!".xls".equalsIgnoreCase(suffix) && !".xlsx".equalsIgnoreCase(suffix)) {
                msg.put("code",0);
                msg.put("msg","数据字典数据导入异常-excel格式不正确");
                return msg;
            }
            ExcelListener listener = new ExcelListener();
            List<SystemItemDictVo> collect = new ArrayList<>();
            InputStream inputStream = file.getInputStream();
            ExcelReader excelReader = new ExcelReader(inputStream, suffix.equals("xls") ? ExcelTypeEnum.XLS : ExcelTypeEnum.XLSX, null, listener);
            excelReader.read(new Sheet(1, 1, ExcelVO.class));
            List<Object> list = listener.getDatas();
            if (list.size() > 1) {
                collect = list.stream().map(m -> (SystemItemDictVo) m).collect(Collectors.toList());
                listener.clearDatas();
            }
            for (SystemItemDictVo excelVO : collect) {
                systemItemDictService.addDict(excelVO);
            }
        } catch (Exception e) {
            msg.put("code",0);
            msg.put("msg","导入字典异常");
            logger.error("字典列表导入异常:", e);
            return msg;
        }
        msg.put("code",1);
        msg.put("msg","导入字典成功");
        return msg;
    }
}
