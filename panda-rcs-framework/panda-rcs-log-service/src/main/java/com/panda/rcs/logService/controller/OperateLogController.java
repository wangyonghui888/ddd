package com.panda.rcs.logService.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.service.impl.OperateLogServiceImpl;
import com.panda.rcs.logService.vo.HttpResponse;
import com.panda.rcs.logService.vo.RcsOperateLogResponseVO;
import com.panda.rcs.logService.vo.RcsOperateLogVO;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/query")
@Slf4j
public class OperateLogController {

    @Autowired
    private OperateLogServiceImpl operateLogServiceImpl;

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;



    @PostMapping("/operate-log")
    public HttpResponse getOperateLogList(@RequestBody RcsOperateLogVO rcsOperateLogVO, @RequestHeader(value = "lang",required = false) String lang) {
        RcsOperateLogResponseVO response = new RcsOperateLogResponseVO();
        try {
            // 设置语言参数
            rcsOperateLogVO.setLang(lang);
            response.setPageSize(rcsOperateLogVO.getPageSize());
            response.setPageNum(rcsOperateLogVO.getPageNum());
            if(Objects.nonNull(rcsOperateLogVO.getMatchManageId())){
                QueryWrapper<StandardMatchInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(StandardMatchInfo::getMatchManageId, rcsOperateLogVO.getMatchManageId());
                StandardMatchInfo info=  standardMatchInfoMapper.selectOne(queryWrapper);
                if(Objects.nonNull(info)){
                    rcsOperateLogVO.setMatchId(info.getId().intValue());
                }
            }
            operateLogServiceImpl.paramterCheck(rcsOperateLogVO);
            response.setTotal(operateLogServiceImpl.queryCount(rcsOperateLogVO));
            response.setList(operateLogServiceImpl.query(rcsOperateLogVO));
        } catch (Exception e) {
            log.error("查詢操盤日誌失敗" + e.getMessage(), e);
            return HttpResponse.fail("查詢操盤日誌失敗");
        }
        return HttpResponse.success(response);
    }

    @PostMapping("/operate-log/simple")
    public HttpResponse getOperateLogListByMatchIdAndPlayId(@RequestBody RcsOperateLogVO rcsOperateLogVO) {
        RcsOperateLogResponseVO response = new RcsOperateLogResponseVO();
        try {
            response.setPageSize(rcsOperateLogVO.getPageSize());
            response.setPageNum(rcsOperateLogVO.getPageNum());
            operateLogServiceImpl.paramterCheck(rcsOperateLogVO);
            response.setTotal(operateLogServiceImpl.querySimpleLogCount(rcsOperateLogVO));
            response.setList(operateLogServiceImpl.querySimpleLog(rcsOperateLogVO));
        } catch (Exception e) {
            log.error("查詢賽事玩法操盤日誌失敗" + e.getMessage(), e);
            return HttpResponse.fail("查詢賽事玩法操盤日誌失敗");
        }
        return HttpResponse.success(response);
    }
}
