package com.panda.sport.rcs.console.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.console.dao.StandardMatchInfoDTOMapper;
import com.panda.sport.rcs.console.dto.MatchHomeAwayDTO;
import com.panda.sport.rcs.console.dto.StandardMatchInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("matchInfo")
@Slf4j
public class MatchInfoController {

    final static String VS = " VS ";
    @Autowired
    private StandardMatchInfoDTOMapper standardMatchInfoDTOMapper;

    @RequestMapping("match")
    public String market() {
        log.info("进入赛事查询页面");
        return "matchInfo/match";
    }

    @PostMapping("/show")
    @ResponseBody
    public StandardMatchInfoDTO getUserList(StandardMatchInfoDTO matchInfo) {
        StandardMatchInfoDTO standardMatchInfo = null;
        try {
            if (ObjectUtils.isEmpty(matchInfo)) return null;
            if (StringUtils.isEmpty(matchInfo.getMarketId()) && StringUtils.isEmpty(matchInfo.getPlayId())) matchInfo.setPlayId("1");
            // 获取赛事信息基本列表、开售信息、盘口信息
            standardMatchInfo = standardMatchInfoDTOMapper.queryMatchAndMarketInfo(matchInfo);
            if (ObjectUtils.isEmpty(standardMatchInfo)) return null;
            List<MatchHomeAwayDTO> teams = standardMatchInfoDTOMapper.queryMatchTeams(standardMatchInfo.getMatchId());
            String home = "";
            String away = "";
            if (CollectionUtils.isNotEmpty(teams)) {
                for (MatchHomeAwayDTO team : teams) {
                    if ("home".equalsIgnoreCase(team.getTeamPosition())) {
                        if ("zs".equalsIgnoreCase(team.getLanguageType())) {
                            home = team.getTeamName();
                        } else if (StringUtils.isEmpty(home)
                            && "en".equalsIgnoreCase(team.getLanguageType())) {
                            home = team.getTeamName();
                        }
                        continue;
                    }
                    if ("away".equalsIgnoreCase(team.getTeamPosition())) {
                        if ("zs".equalsIgnoreCase(team.getLanguageType())) {
                            away = team.getTeamName();
                        } else if (StringUtils.isEmpty(away)
                            && "en".equalsIgnoreCase(team.getLanguageType())) {
                            away = team.getTeamName();
                        }
                    }
                }
            }
            standardMatchInfo.setHomeAwayInfo(home + VS + away);
            log.info("MarketList查询={}", JSONObject.toJSONString(standardMatchInfo));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("MarketList查询异常！", e);
        }
        return standardMatchInfo;
    }
}
