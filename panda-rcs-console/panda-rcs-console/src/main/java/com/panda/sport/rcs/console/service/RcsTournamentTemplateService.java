package com.panda.sport.rcs.console.service;

import com.panda.sport.rcs.console.dto.RcsSpecialPumpingDTO;
import com.panda.sport.rcs.console.dto.TournamentTemplateUpdateParam;
import com.panda.sport.rcs.console.pojo.ExcelVO;
import com.panda.sport.rcs.console.pojo.TournamentTemplateExcelVO;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface RcsTournamentTemplateService {

    /**
     * 初始化对应玩法的特殊限额数据
     * @param dto
     * @return
     */
    void updateSpecialPumping(RcsSpecialPumpingDTO dto);

    /**
     * 新增玩法
     * @param param
     * @return
     */
    void addTournamentTemplatePlay(TournamentTemplateUpdateParam param);

    /**
     * 初始化联赛模板
     * @param sportId
     */
    void initTournamentTemplate(Integer sportId);

    /**
     * 导入初始化模板所需数据
     * @param collect
     * @param countDownLatch
     */
    void importTemplate(List<TournamentTemplateExcelVO> collect, CountDownLatch countDownLatch);;
}
