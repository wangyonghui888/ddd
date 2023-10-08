package com.panda.sport.rcs.predict.controller;


import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.service.IRcsPredictForecastSnapshotService;
import com.panda.sport.rcs.vo.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Forecast快照 前端控制器
 * </p>
 *
 * @author Kir
 * @since 2022-07-29
 */
@Slf4j
@RestController
@RequestMapping("/forecast")
public class RcsPredictForecastSnapshotController {

    @Autowired
    private IRcsPredictForecastSnapshotService rcsPredictForecastSnapshotService;

    @RequestMapping(value = "/querySnapshot", method = RequestMethod.POST)
    public HttpResponse goQueryBetForMarket(@RequestBody QueryForecastPlayReqVo vo) {
        return HttpResponse.success(rcsPredictForecastSnapshotService.querySnapshot(vo));
    }

}
