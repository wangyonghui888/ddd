package com.panda.rcs.logService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.rcs.logService.vo.RcsLanguageInternation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface RcsLanguageInternationMapper extends BaseMapper<RcsLanguageInternation> {


    /**
     * 根據玩法集合、球種ID查詢對應名字
     * @param playIds
     * @param sportId
     * @return
     */
    List<LanguageInternation> getPlayNameByPlayIds(@Param("playIds") List<Long> playIds, @Param("sportId") Integer sportId);



    /**
     * 根據玩法ID、球種ID查詢玩法名稱
     * @param playId
     * @param sportId
     * @return
     */
    LanguageInternation getPlayNameByCategoryIdAndSportId(@Param("playId") Long playId, @Param("sportId") Integer sportId);


    LanguageInternation getTournamentNameByIdAndSprotId(@Param("tournamentId")Long tournamentId,  @Param("sportId")Integer sportId);



    /**
     * 根據玩法集合、球種ID查詢對應名字 英文
     * @param playIds
     * @param sportId
     * @return
     */
    List<LanguageInternation> getPlayNameByPlayIdsEn(@Param("playIds") List<Long> playIds, @Param("sportId") Integer sportId);



    /**
     * 根據玩法ID、球種ID查詢玩法名稱 英文
     * @param playId
     * @param sportId
     * @return
     */
    LanguageInternation getPlayNameByCategoryIdAndSportIdEn(@Param("playId") Long playId, @Param("sportId") Integer sportId);


    LanguageInternation getTournamentNameByIdAndSprotIdEn(@Param("tournamentId")Long tournamentId,  @Param("sportId")Integer sportId);




    /**
     * 根據玩法ID、球種ID查詢玩法名稱 返回中英文
     * @param playId
     * @param sportId
     * @return
     */
    LanguageInternation getPlayNameByCategoryIdAndSportIdZcEn(@Param("playId") Long playId, @Param("sportId") Integer sportId);


    LanguageInternation getTournamentNameByIdAndSprotIdZcEn( @Param("tournamentId")Long tournamentId,  @Param("sportId")Integer sportId);
}
