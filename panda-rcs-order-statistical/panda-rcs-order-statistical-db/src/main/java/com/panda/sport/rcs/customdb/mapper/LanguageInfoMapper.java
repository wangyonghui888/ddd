package com.panda.sport.rcs.customdb.mapper;

import com.panda.sport.rcs.common.vo.api.response.UserResVo;
import com.panda.sport.rcs.customdb.entity.DataEntity;
import com.panda.sport.rcs.customdb.entity.LanguageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.mapper
 * @description :
 * @date: 2020-07-22 16:34
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface LanguageInfoMapper {

    List<LanguageInfo> getLanguageInfo(@Param("nameCodeSet") Set<Long> nameCodeSet);


    List<LanguageInfo> getEsportLanguageInfo(@Param("nameCodeSet") Set<Long> nameCodeSet);

    List<DataEntity> getSportName();

    List<DataEntity> getPlayNameCode();

    DataEntity getTournamentCodeNameById(@Param("id") Long id);

    DataEntity getTeamNameCodeById(@Param("id") Long id);


    UserResVo getUser(@Param("uid") Long id);
}
