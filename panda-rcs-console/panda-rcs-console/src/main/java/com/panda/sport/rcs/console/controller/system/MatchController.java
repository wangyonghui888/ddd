package com.panda.sport.rcs.console.controller.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.panda.sport.rcs.console.dao.RcsMatchPlayConfigLogsMapper;
import com.panda.sport.rcs.console.dao.StandardMatchInfoMapper;
import com.panda.sport.rcs.console.response.PageDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.console.controller.system
 * @Description :  TODO
 * @Date: 2020-07-29 16:12
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Controller
@RequestMapping(value = "match")
@Slf4j
public class MatchController {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private RcsMatchPlayConfigLogsMapper playConfigLogsMapper;

    @RequestMapping(value = "info")
    public String info() {
        log.info("进入赛事查询页面");
        return "match/info";
    }

    @RequestMapping(value = "market")
    public String market() {
        log.info("进入赛事查询页面");
        return "match/market";
    }

    @RequestMapping(value = "odds")
    public String odds() {
        log.info("进入赛事查询页面");
        return "match/odds";
    }

    @RequestMapping(value = "getList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult getList(@RequestParam("pageNum") Integer pageNum,
                                  @RequestParam("pageSize") Integer pageSize, Long matchId) {
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) {
                pageNum = 1;
            }
            if (null == pageSize) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, Object>> standardMatchInfo = standardMatchInfoMapper.selectStandardMatchInfoById(matchId);
            if (standardMatchInfo != null && standardMatchInfo.size() > 0) {
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(standardMatchInfo, pageNum);
                pdr.setTotals((int) pageInfo.getTotal());
                pdr.setList(pageInfo.getList());
            }
            return pdr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pdr;
    }


    @RequestMapping(value = "marketList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult marketList(@RequestParam("pageNum") Integer pageNum,
                                     @RequestParam("pageSize") Integer pageSize, Long matchId, Long marketId) {
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) {
                pageNum = 1;
            }
            if (null == pageSize) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, Object>> standardSportMarkets = standardMatchInfoMapper.selectStandardSportMarket(matchId, marketId);
            if (standardSportMarkets != null && standardSportMarkets.size() > 0) {
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(standardSportMarkets, pageNum);
                pdr.setTotals((int) pageInfo.getTotal());
                pdr.setList(pageInfo.getList());
            }
            return pdr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pdr;
    }


    @RequestMapping(value = "oddList", method = RequestMethod.POST)
    @ResponseBody
    public PageDataResult oddList(@RequestParam("pageNum") Integer pageNum,
                                  @RequestParam("pageSize") Integer pageSize, Long marketId, Long oddId) {
        PageDataResult pdr = new PageDataResult();
        try {
            if (null == pageNum) {
                pageNum = 1;
            }
            if (null == pageSize) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, Object>> standardSportMarkets = standardMatchInfoMapper.selectStandardSportMarketOdds(marketId, oddId);
            if (standardSportMarkets != null && standardSportMarkets.size() > 0) {
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(standardSportMarkets, pageNum);
                pdr.setTotals((int) pageInfo.getTotal());
                pdr.setList(pageInfo.getList());
            }
            return pdr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pdr;
    }
}
