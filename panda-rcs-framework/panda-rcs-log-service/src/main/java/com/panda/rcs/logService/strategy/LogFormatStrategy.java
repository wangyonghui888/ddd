package com.panda.rcs.logService.strategy;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.mapper.StandardSportTeamMapper;
import com.panda.rcs.logService.vo.LanguageInternation;
import com.panda.rcs.logService.vo.MatchTeamInfo;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import com.panda.sport.rcs.utils.CategoryParseUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.*;

/**
 * 操盤日誌 OperateLog 抽象類
 *
 * @param <T>
 */
public abstract class LogFormatStrategy<T> {

    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private StandardSportTeamMapper standardSportTeamMapper;

    public abstract RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean bean);

    public void beforeProcess(Object[] args) {
    }

    /**
     * 透過隊伍列表組出 賽事名稱 中文
     *
     * @param teamList
     * @return
     */
    protected String getMatchName(List<MatchTeamInfo> teamList,Long matchId) {
        //防止前端未传值处理
        teamList= (null==teamList||teamList.size()==0)?
                standardSportTeamMapper.queryTeamListByMatchIdZs(matchId):teamList;
        //取隊伍名稱
        String home = "", away = "";
        for (MatchTeamInfo teamVo : teamList) {
            String name = Optional.ofNullable(teamVo.getText()).orElse("");
            if (StringUtils.isEmpty(name)&&null!=teamVo.getNames()) {
                name = teamVo.getNames().get("zs");
            }else{
                name= teamVo.getText();
            }
            if ("home".equals(teamVo.getMatchPosition())) {
                home = name;
            } else if ("away".equals(teamVo.getMatchPosition())) {
                away = name;
            }
        }
        return home + " (主)VS " + away;
    }

    /**
     * 透過隊伍列表組出 賽事名稱 英文
     *
     * @param teamList
     * @return
     */
    protected String getMatchNameEn(List<MatchTeamInfo> teamList,Long matchId) {
        //防止前端未传值处理
        teamList= (null==teamList||teamList.size()==0)?
                standardSportTeamMapper.queryTeamListByMatchIdEn(matchId):teamList;
        //取隊伍名稱
        String home = "", away = "";
        for (MatchTeamInfo teamVo : teamList) {
            String name = Optional.ofNullable(teamVo.getText()).orElse("");
            if (StringUtils.isEmpty(name)&&null!=teamVo.getNames()) {
                name=teamVo.getNames().get("en");
            }else{
                name= teamVo.getText();
            }
            if ("home".equals(teamVo.getMatchPosition())) {
                home = name;
            } else if ("away".equals(teamVo.getMatchPosition())) {
                away = name;
            }
        }
        return home + " (host)VS " + away;
    }
     //中英文处理
    protected String montageEnAndZs(String en, String zs ){
        Map<String,String> map=new HashMap<String, String>();
           map.put("zs",zs);
           map.put("en",en);
           return JSONObject.toJSONString(map);
    }

    //队伍中英文处理 只有队伍
    protected String montageEnAndZsIs(List<MatchTeamInfo> teamList,Long matchId ){
        Map<String,String> map=new HashMap<String, String>();
        map.put("zs",getMatchName(teamList,matchId));
        map.put("en",getMatchNameEn(teamList,matchId));
        return JSONObject.toJSONString(map);
    }

    //玩法中英文处理  只有玩法的时候
    protected String getPlayNameZsEn(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportIdZcEn(playId, sportId);
        return Objects.nonNull(playName) ? playName.getText() : "";
    }

    //玩法中文处理
    protected String getPlayNameZs(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

    //玩法英文处理
    protected String getPlayNameEn(Long playId, Integer sportId) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportIdEn(playId, sportId);
        return Objects.nonNull(playName) ? playName.getText() : "";
    }
    //模板中英文处理 只有模板
    protected String templateIdEnAndZsIs(Long tournamentId, Integer sportId ){
        LanguageInternation playName = rcsLanguageInternationMapper.getTournamentNameByIdAndSprotIdZcEn(tournamentId, sportId);
        return Objects.nonNull(playName) ? playName.getText() : "";
    }



    /**
     * 盤口值轉換
     */
    protected String transMarketValue(BigDecimal marketValue) {
        BigDecimal unit = new BigDecimal("0.25");
        if (marketValue.compareTo(BigDecimal.ZERO) == 0) {
            return String.format("%s", marketValue.stripTrailingZeros());

            // 判斷盤口值除0.25為偶數
        } else if (marketValue.divide(unit).remainder(new BigDecimal("2")).compareTo(BigDecimal.ZERO) == 0) {
            if (marketValue.compareTo(BigDecimal.ZERO) == 1) {
                return String.format("%s", marketValue.stripTrailingZeros());
            } else {
                return String.format("(-) %s", marketValue.abs().stripTrailingZeros());
            }
        } else {
            // 判斷盤口值除0.25為奇數
            if (marketValue.compareTo(BigDecimal.ZERO) == 1) {
                return String.format("%s/%s", marketValue.subtract(unit).stripTrailingZeros(), marketValue.add(unit).stripTrailingZeros());
            } else {
                return String.format("(-) %s/%s", marketValue.abs().subtract(unit).stripTrailingZeros(), marketValue.abs().add(unit).stripTrailingZeros());
            }
        }
    }

    /**
     *  赛事信息
     * @param id
     * @return
     */
    protected StandardMatchInfo getStandardMatchInfo(Long id){
        return standardMatchInfoMapper.selectById(id);
    }
    /**
     * 秒轉換成時分秒
     *
     * @param second
     * @return
     */
    protected String secondToTime(long second) {

        long minutes = second / 60;//轉換分鐘
        second = second % 60;//剩餘秒數
        return (minutes >= 10L ? minutes : "0" + minutes) + ":" + (second >= 10L ? second : "0" + second);
    }

    /**
     * @title com.panda.sport.rcs.trade.strategy.LogFormatStrategy#oddsRuleCovert
     * @description 赔率规则处理:小数位不满补0
     * 1 大于100无需展示小数
     * 2 大于10展示1位小数
     * 3 小于10展示2位小数
     * @params [oddsValue]
     * @return java.math.BigDecimal
     * @throws
     * @date 2023/2/24 16:00
     * @author jstyChandler
     */
    protected BigDecimal oddsRuleCovert(Integer oddsValue){
        if(null == oddsValue){
            return new BigDecimal("0.00");
        }
        return new BigDecimal(oddsValue + "").divide(new BigDecimal("100000"));
    }
}
