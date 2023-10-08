package com.panda.sport.rcs;

import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplateComposeModel;
import com.panda.sport.rcs.task.wrapper.IRcsMatchMarketConfigService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl;

    public TestController(IRcsMatchMarketConfigService rcsMatchMarketConfigServiceImpl) {
        this.rcsMatchMarketConfigServiceImpl = rcsMatchMarketConfigServiceImpl;
    }

    @ApiOperation(value = "手动触发任务")
    @GetMapping("/run")
    public void execute() {
        RcsTournamentTemplateComposeModel m = new RcsTournamentTemplateComposeModel();
        m.setMatchId(3397202L);
        m.setMatchType(1);
        m.setMarketCount(1);
        m.setTemplateId(6737206L);
        m.setMarginId(28692252L);
        m.setPlayId(153L);
        m.setTimeVal(3600);
        m.setMarginRefId(30506325L);
        m.setValidMarginId(30506325L);
        rcsMatchMarketConfigServiceImpl.insertFromTemplate(m);
    }
}
