package com.panda.sport.rcs.console.controller.monitorData;

import com.panda.sport.rcs.console.dto.MatchFlowingDTO;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.RcsMonitorDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("monitorData")
@Slf4j
public class RCSMonitorDataController {
    @Autowired
    private RcsMonitorDataService rcsMonitorDataService;


    @RequestMapping("/api")
    public String api() {
        return "monitorData/api";
    }

    @RequestMapping("/order")
    public String order() {
        return "monitorData/order";
    }

    @RequestMapping("/graph")
    public String graph() {
        return "monitorData/graph";
    }

    @RequestMapping("/monitorData/api")
    @ResponseBody
    public PageDataResult api(MatchFlowingDTO matchFlowingDTO) {
        PageDataResult pageDataResult = new PageDataResult();
        pageDataResult.setList(rcsMonitorDataService.queryRate(matchFlowingDTO));
        return pageDataResult;

    }

    @RequestMapping("/monitorData/order")
    @ResponseBody
    public PageDataResult order(MatchFlowingDTO matchFlowingDTO) {
        PageDataResult pageDataResult = new PageDataResult();
        pageDataResult.setList(rcsMonitorDataService.queryRate(matchFlowingDTO));
        return pageDataResult;
    }


    @RequestMapping("/monitorData/group")
    @ResponseBody
    public List group() {
        return rcsMonitorDataService.group();
    }


    @RequestMapping("/monitorData/graph")
    @ResponseBody
    public Map graphaData(MatchFlowingDTO matchFlowingDTO) {
        try {
            HashMap<String, Map> stringMapHashMap = rcsMonitorDataService.graphaData(matchFlowingDTO);
            return stringMapHashMap;
        } catch (Exception e) {
            log.error("图表数据异常：", e);
            return null;
        }
    }
}
