package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.entity.MatchTournamentVo;
import com.panda.rcs.cleanup.mapper.MatchTournamentMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@JobHandler(value = "matchTournamentLinkedInitDelJob")
public class MatchTournamentLinkedInitDelJob extends IJobHandler {

    private static final int delete_rows = 500;

    @Autowired
    private MatchTournamentMapper matchTournamentMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        List<MatchTournamentVo> list = matchTournamentMapper.queryInvalidTournamentId();
        if(list != null && list.size() > 0){
            log.info("::联赛模板关联数据初始清理::联赛模板Id总数={}", list.size());
            int tempPage = 0;
            int rows;
            for (int i = 0; i < list.size(); i ++) {
                try {
                    boolean isEnd = (i == (list.size() - 1));
                    if (i != 0 && i % delete_rows == 0 || isEnd) {
                        List<MatchTournamentVo> pageData = list.subList(tempPage * delete_rows, isEnd ? list.size() : i);
                        log.info("::联赛模板关联数据初始清理::本批清理联赛模板Id={}", pageData);

                        rows = matchTournamentMapper.deleteTimeSharingNode(pageData);
                        log.info("::rcs_tournament_template_play_margain_ref数据清理::::第{}批，清理数据={}条", tempPage, rows);
                        rows = matchTournamentMapper.deletePlayMargain(pageData);
                        log.info("::rcs_tournament_template_play_margain数据清理::::第{}批，清理数据={}条", tempPage, rows);

                        rows = matchTournamentMapper.deleteAcceptEvent(pageData);
                        log.info("::rcs_tournament_template_accept_event数据清理::::第{}批，清理数据={}条", tempPage, rows);
                        rows = matchTournamentMapper.deleteAcceptConfig(pageData);
                        log.info("::rcs_tournament_template_accept_config数据清理::::第{}批，清理数据={}条", tempPage, rows);

                        rows = matchTournamentMapper.deleteAcceptEventSettle(pageData);
                        log.info("::rcs_tournament_template_accept_event_settle数据清理::::第{}批，清理数据={}条", tempPage, rows);
                        rows = matchTournamentMapper.deleteAcceptConfigSettle(pageData);
                        log.info("::rcs_tournament_template_accept_config_settle数据清理::::第{}批，清理数据={}条", tempPage, rows);

                        rows = matchTournamentMapper.deleteTemplateEvent(pageData);
                        log.info("::rcs_tournament_template_event数据清理::::第{}批，清理数据={}条", tempPage, rows);

                        tempPage ++;
                    }
                } catch (Exception e){
                    log.error("matchTournamentLinkedInitDelJob异常信息={}, 全量：", e.getMessage(), e);
                }
            }

            rows = matchTournamentMapper.deleteTemplateNotMatch();
            log.info("::rcs_tournament_template数据清理，清理数据={}条", rows);
        }

        log.info("::联赛模板关联表初始清理结束，工消耗：{}", System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }

}
