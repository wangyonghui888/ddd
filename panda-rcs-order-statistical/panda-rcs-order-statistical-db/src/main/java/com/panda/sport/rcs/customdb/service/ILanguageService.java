package com.panda.sport.rcs.customdb.service;

import com.panda.sport.rcs.customdb.entity.LanguageInfo;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Set;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.service
 * @description :  语言信息服务接口
 * @date: 2020-07-22 16:44
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface ILanguageService {

    List<LanguageInfo> getLanguageInfo(Set<Long> nameCodeSet);

    String getLanguageByNameCode(Long nameCode);

    String getSportName(Long sportId);

    String getSportName(Integer sportId);

    String getPlayName(Long sportId, Long playId) ;

    String getPlayName(Integer sportId, Long playId) ;

    String getPlayName(Integer sportId, Integer playId) ;

    String getTournamentName(Long tournamenId ) ;

    String getTeamName(Long teamId)  ;


}
