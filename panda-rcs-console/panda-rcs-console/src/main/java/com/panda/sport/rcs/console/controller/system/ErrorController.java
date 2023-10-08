package com.panda.sport.rcs.console.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.panda.sport.rcs.console.dao.ErrorMapper;
import com.panda.sport.rcs.console.pojo.ErrorMqBean;
import com.panda.sport.rcs.console.response.PageDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.controller.system
 * @Description :  TODO
 * @Date: 2020-02-10 11:48
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Controller
@RequestMapping("error")
@Slf4j
public class ErrorController {

    @Autowired
    private ErrorMapper errorMapper;

    @RequestMapping("list")
    public String market() {
        log.info("进入错误查询页面");
        return "error/error";
    }


//    @Autowired
//    JvmDataCollectImpl data;
//    @GetMapping("init")
//    @ResponseBody
//    public String init() {
//        JSONObject object = new  JSONObject();
//        object.put("currentDate",System.currentTimeMillis());
//        object.put("logContent","测试数据");
//        Map<String, String> paramsMap = new HashMap<>();
//        paramsMap.put("KEYS","ERROR_LOG_172.18.178.166_13779");
//        paramsMap.put("IP","172.18.178.166");
//        paramsMap.put("PID","13779");
//        paramsMap.put("SEND_SERVER_NAME","panda-rcs-risk");
//        data.handleMs(object,paramsMap);
//        return "finish";
//    }

    @PostMapping("/show")
    @ResponseBody
    public PageDataResult getUserList(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize, ErrorMqBean bean) {
        PageDataResult jsonObject = new PageDataResult();
        try {
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 10;
            PageHelper.startPage(pageNum, pageSize);
            List<ErrorMqBean> list = errorMapper.errorList(bean,pageNum,pageSize);
            if (list.size() > 0) {
                Long count = errorMapper.errorListCount(bean);
                jsonObject.setList(list);
                jsonObject.setTotals(count.intValue());
            }
        }catch (Exception e){
            log.info("查询错误列表报错{}",JSONObject.toJSONString(bean));
            log.error("查询错误列表报错" + e);
        }
        return jsonObject;
    }

}
