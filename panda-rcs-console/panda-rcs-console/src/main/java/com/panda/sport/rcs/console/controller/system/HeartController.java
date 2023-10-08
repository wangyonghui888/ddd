package com.panda.sport.rcs.console.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.panda.sport.rcs.console.dao.HeartMapper;
import com.panda.sport.rcs.console.pojo.HeartMqBean;
import com.panda.sport.rcs.console.response.PageDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("heart")
@Slf4j
public class HeartController {

    @Autowired
    private HeartMapper heartMapper;

    private List<HeartMqBean> serviceList;

    @RequestMapping("list")
    public String market() {
        log.info("进入心跳查询页面");
        return "heart/heart";
    }

    @RequestMapping("detail")
    public String detail() {
        log.info("进入心跳查询页面");
        return "heart/heart_detail";
    }

    @RequestMapping("service")
    public String service() {
        log.info("进入心跳查询页面");
        return "heart/heart_service";
    }

    @PostMapping("/show")
    @ResponseBody
    public PageDataResult getUserList(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize, HeartMqBean bean) {
        PageDataResult jsonObject = new PageDataResult();
        try {
        	serviceList = initServiceList();
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 20;
            PageHelper.startPage(pageNum, pageSize);
            List<HeartMqBean> list = heartMapper.heartList(bean,pageNum,pageSize);
            for (HeartMqBean heart : serviceList){
                Integer count = 0;
                String currentTime = null;
                for (HeartMqBean h : list){
                    if (!heart.getServerName().equalsIgnoreCase(h.getServerName())) continue;
                    
                    count ++;
                    currentTime = h.getCurrentTime();
                }
                if(count > 0) {
                	heart.setServerStatus("1");
                    heart.setInstanceCount(count);
                    heart.setCurrentTime(currentTime);
                    heart.setHealthStatus("2");
                    if(Integer.parseInt(heart.getStartNode()) != -1 && 
                    		Integer.parseInt(heart.getStartNode()) <= count) {//健康状态  1：正常 
                    	heart.setHealthStatus("1");
                    }
                }
            }
            if (list.size() > 0) {
                Long count = heartMapper.heartListCount(bean);
                jsonObject.setList(serviceList);
                jsonObject.setTotals(count.intValue());
            }
        }catch (Exception e){
            log.info("查询错误列表报错{}",JSONObject.toJSONString(bean));
            log.error("查询错误列表报错" + e);
        }
        return jsonObject;
    }
    @PostMapping("/show_service")
    @ResponseBody
    public PageDataResult showService(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize, HeartMqBean bean) {
        PageDataResult jsonObject = new PageDataResult();
        try {
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 10;
            Integer pageStart = (pageNum - 1) * pageSize;
            List<HeartMqBean> list = heartMapper.heartServiceList(bean,pageStart,pageSize);
            if (list.size() > 0) {
                Long count = heartMapper.heartServiceListCount(bean);
                jsonObject.setList(list);
                jsonObject.setTotals(count.intValue());
            }
        }catch (Exception e){
            log.info("查询错误列表报错{}",JSONObject.toJSONString(bean));
            log.error("查询错误列表报错" + e);
        }
        return jsonObject;
    }
    @PostMapping("/show_detail")
    @ResponseBody
    public PageDataResult showDetail(@RequestParam("pageNum") Integer pageNum,
                                      @RequestParam("pageSize") Integer pageSize, HeartMqBean bean) {
        PageDataResult jsonObject = new PageDataResult();
        try {
            if (null == pageNum) pageNum = 1;
            if (null == pageSize) pageSize = 10;
            Integer pageStart = (pageNum - 1) * pageSize;
            List<HeartMqBean> list = heartMapper.heartDetailList(bean,pageStart,pageSize);
            if (list.size() > 0) {
                Long count = heartMapper.heartDetailListCount(bean);
                jsonObject.setList(list);
                jsonObject.setTotals(count.intValue());
            }
        }catch (Exception e){
            log.info("查询错误列表报错{}",JSONObject.toJSONString(bean));
            log.error("查询错误列表报错" + e);
        }
        return jsonObject;
    }

    @GetMapping("/init")
    @ResponseBody
    public List<HeartMqBean> initServiceList(){
        List<HeartMqBean> list = heartMapper.queryServiceList();
        return list;
    }
}
