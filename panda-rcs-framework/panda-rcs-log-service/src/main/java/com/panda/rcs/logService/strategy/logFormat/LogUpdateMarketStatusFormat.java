package com.panda.rcs.logService.strategy.logFormat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.panda.rcs.logService.Enum.CategorySetIdEnum;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.Enum.TradeStatusEnum;
import com.panda.rcs.logService.mapper.MarketCategorySetMapper;
import com.panda.rcs.logService.mapper.RcsLanguageInternationMapper;

import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.mapper.StandardSportMarketMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.utils.BaseUtils;
import com.panda.rcs.logService.vo.*;
import com.panda.sport.rcs.enums.OperateLogEnum;

import com.panda.sport.rcs.log.format.RcsOperateLog;

import com.panda.sport.rcs.utils.CategoryParseUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.panda.rcs.logService.utils.Placeholder.*;



/**
 * 操盤日誌(updateMarketStatus)
 * 開關封鎖
 */

@Slf4j
@Service
public class LogUpdateMarketStatusFormat extends LogFormatStrategy {

    @Autowired
    private RcsLanguageInternationMapper rcsLanguageInternationMapper;


    @Autowired
    private MarketCategorySetMapper marketCategorySetMapper;

    @Autowired
    private StandardSportMarketMapper standardSportMarketMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;



    private static final List<Long> FROM_TO_PLAYS = Lists.newArrayList(32L, 33L, 34L, 231L, 232L, 233L);
    private static final List<Long> A1_PLAYS = Lists.newArrayList(28L, 30L, 31L, 109L, 110L, 120L, 125L, 133L, 148L, 201L, 208L, 214L, 222L, 224L, 225L, 230L, 235L, 237L, 255L, 261L, 265L, 266L, 267L, 344L);
    private static final List<Long> A2_PLAYS = Lists.newArrayList(145L, 146L, 162L, 163L, 164L, 165L, 166L, 170L, 175L, 176L, 177L, 178L, 184L, 185L, 186L, 187L, 189L, 190L, 191L, 192L, 193L, 194L, 196L, 197L, 253L, 254L, 262L, 263L, 264L, 268L);
    private static final List<Long> A3_PLAYS = Lists.newArrayList( 357L, 336L);

    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean vo ) {
        if (Objects.isNull(vo)) {
            return null;
        }if(Objects.isNull(vo.getOperatePageCode())){
          return  updateMarketStatus(rcsOperateLog,vo);
        }

        //根據不同操作頁面組裝不同格式
        switch (vo.getOperatePageCode()) {
            case 14:
                //早盤操盤
            case 15:
                //早盤操盤 次要玩法
            case 17:
                //滾球操盤
            case 18:
                //滾球操盤 次要玩法
                return updateMarketStatusFormat(rcsOperateLog, vo);

        }
        return null;
    }

    private RcsOperateLog updateMarketStatusFormat(RcsOperateLog rcsOperateLog, LogAllBean vo) {
        LogAllBean oriVo = BaseUtils.mapObject(vo.getBeforeParams(),LogAllBean.class) ;
        rcsOperateLog.setOperatePageCode(vo.getOperatePageCode());
        rcsOperateLog.setMatchId(vo.getMatchId());
        rcsOperateLog.setPlayId(vo.getCategoryId());
        String matchName = montageEnAndZsIs(vo.getTeamList(),vo.getMatchId());
        switch (vo.getTradeLevel()) {
            case 1:
                //赛事级别
                if (checkStatusDiff(vo)) return null;
                rcsOperateLog.setObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setObjectNameByObj(matchName);
                rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
                rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
                break;
            case 2:
                //玩法级别
                rcsOperateLog.setObjectIdByObj(vo.getCategoryId());
                rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(
                        vo.getCategoryId(),
                        vo.getSportId()));
                rcsOperateLog.setExtObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setExtObjectNameByObj(matchName);
                break;
            case 3:
                if (checkStatusDiff(vo)) return null;
                //盘口级别 (有些Level盤非盤口級別)
                if (Objects.nonNull(vo.getMarketId())) {
                    rcsOperateLog.setObjectIdByObj(vo.getMarketId());
                    rcsOperateLog.setObjectNameByObj(
                            StringUtils.isNoneEmpty(vo.getMarketValue()) ?
                                    transMarketValue(new BigDecimal(vo.getMarketValue())) :setCategoryName(vo.getMarketId(),vo.getCategoryId(),vo.getSportId()));
                } else {
                    rcsOperateLog.setObjectIdByObj(vo.getCategoryId());
                    rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(
                            vo.getCategoryId(),
                            vo.getSportId()));
                }
                StringBuilder extObjectId = new StringBuilder()
                        .append(vo.getMatchManageId()).append(" / ")
                        .append(vo.getCategoryId());
                StringBuilder extObjectName = new StringBuilder(getMatchName(vo.getTeamList(),vo.getMatchId()))
                        .append(" / ").append(getPlayNameZs(
                        vo.getCategoryId(),
                        vo.getSportId()));
                StringBuilder extObjectNameEn = new StringBuilder(getMatchNameEn(vo.getTeamList(),vo.getMatchId()))
                        .append(" / ").append(getPlayNameEn(
                                vo.getCategoryId(),
                                vo.getSportId()));
                rcsOperateLog.setExtObjectIdByObj(extObjectId);
                rcsOperateLog.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),
                        extObjectName.toString()));
                break;
            case 5:
                //批量玩法级别
                rcsOperateLog.setObjectIdByObj(vo.getCategoryIdList());
                rcsOperateLog.setObjectNameByObj(OperateLogEnum.NONE.getName());

                String matchId = StringUtils.isBlank(vo.getMatchManageId()) ? null : vo.getMatchManageId();
                matchId = ObjectUtils.defaultIfNull(matchId, String.valueOf(vo.getMatchId()));

                rcsOperateLog.setExtObjectIdByObj(matchId);
                rcsOperateLog.setExtObjectNameByObj(matchName);

                //既沒有玩法集名称,也沒有玩法集ID, case 9 就別跑了, 以防資料被覆蓋
                if (Objects.isNull(vo.getPlaySetName()) && Objects.isNull(vo.getCategorySetId())) {
                    break;
                }

            case 9:
                //玩法集编码
                if (Objects.nonNull(vo.getPlaySetName())) {
                    //次要玩法
                    rcsOperateLog.setObjectIdByObj(vo.getCategorySetId());
                    rcsOperateLog.setObjectNameByObj(CategorySetIdEnum.getValue(vo.getCategorySetId()).equals("-")?vo.getPlaySetName():CategorySetIdEnum.getValue(vo.getCategorySetId()));

                } else {
                    //主玩法 主玩法開關有狀態所以可判斷
                    if (checkStatusDiff(vo)) return null;
                    rcsOperateLog.setObjectIdByObj(vo.getCategorySetId());
                    rcsOperateLog.setObjectNameByObj(getPlaySetName(vo.getPlaySetCode(),vo.getSportId()));
                }
                rcsOperateLog.setExtObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setExtObjectNameByObj(matchName);
                break;
        }

        rcsOperateLog.setBeforeValByObj(Objects.nonNull(oriVo) ? getTradeStatusName(oriVo.getMarketStatus()) : OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(getTradeStatusName(vo.getMarketStatus()));
        return rcsOperateLog;
    }
   private String setCategoryName(Long markerId,Long playId, Integer sportId){
       StandardSportMarket standardSportMarket = standardSportMarketMapper.selectById(markerId);
       if(Objects.nonNull(standardSportMarket)){
           if(Objects.isNull(standardSportMarket.getOddsValue())){
            return getPlayName(playId,sportId);
           }
           return "(-)"+standardSportMarket.getOddsValue();
       }
       return getPlayName(playId,sportId);
   }

    /**
     * 查詢玩法名稱
     *
     * @param playId
     * @return
     */
    private String getPlayName(Long playId, Integer sportId) {
        return getPlayNameZsEn(playId,sportId);
    }

    /**
     * 透過隊伍列表組出 賽事名稱
     *
     * @param teamList
     * @return
     */
    private String setMatchName(List<MatchTeamInfo> teamList) {
        //取隊伍名稱
        String home = "", away = "";
        for (MatchTeamInfo teamVo : teamList) {
            String name = Optional.ofNullable(teamVo.getNames().get("zs")).orElse("");
            if ("home".equals(teamVo.getMatchPosition())) {
                home = name;
            } else if ("away".equals(teamVo.getMatchPosition())) {
                away = name;
            }
        }
        return home + " (主)VS " + away;
    }


    /**
     * 確認開關盤狀態是否異動
     *
     * @param vo
     * @return
     */
    private boolean checkStatusDiff(LogAllBean vo) {
        //開關盤不一定有狀態，不一定會有before值，無before值則直接紀錄
        if (Objects.nonNull(vo.getBeforeParams())) {
            if (Objects.nonNull(vo.getMarketStatus()) &&
                    !vo.getMarketStatus().equals(vo.getBeforeParams().get("marketStatus"))) {
                return false;
            } else
                return true;
        }
        return false;
    }

    /**
     * 查詢玩法名稱
     *
     * @param playId
     * @return
     */
    private String getPlayName(Long playId, Integer sportId, String addition1, String addition2) {
        LanguageInternation playName = rcsLanguageInternationMapper.getPlayNameByCategoryIdAndSportId(playId, sportId);
        if (playId == 147L) {
            //  -> 第{!quarternr}节首先获得{pointnr}分
            playName.setText(playName.getText().replace(WHICH_QUARTER, addition1));
            playName.setText(playName.getText().replace(WHICH_QUARTER2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (playId == 167L) {
            //  -> 第{!setnr}盘第{!gamenrX}局和第{!gamenrY}局谁获胜多
            playName.setText(playName.getText().replace(WHICH_SET, addition1));
            playName.setText(playName.getText().replace(WHICH_SET2, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME_X, addition2));
            BigDecimal gameY = new BigDecimal(addition2).add(BigDecimal.ONE);
            playName.setText(playName.getText().replace(WHICH_GAME_Y, gameY.toPlainString()));
        } else if (playId == 168L) {
            //  -> 第{!setnr}盘第{gamenr}局获胜
            playName.setText(playName.getText().replace(WHICH_SET, addition1));
            playName.setText(playName.getText().replace(WHICH_SET2, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME, addition2));
            playName.setText(playName.getText().replace(WHICH_GAME2, addition2));
        } else if (playId == 179L) {
            //  -> 第{!gamenr}局第{!pointnr}分
            playName.setText(playName.getText().replace(WHICH_GAME, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (playId == 188L) {
            //  -> 第{!framenr}局首先到达{pointnr}分
            playName.setText(playName.getText().replace(WHICH_FRAME, addition1));
            playName.setText(playName.getText().replace(WHICH_FRAME2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (playId == 195L) {
            //  -> 第{!framenr}局第{!xth}个进球的选手
            playName.setText(playName.getText().replace(WHICH_FRAME, addition1));
            playName.setText(playName.getText().replace(WHICH_FRAME2, addition1));
            playName.setText(playName.getText().replace(WHICH_XTH, addition2));
            playName.setText(playName.getText().replace(WHICH_XTH2, addition2));
        } else if (playId == 203L) {
            //  -> 第{!gamenr}局首先获得{pointnr}分
            playName.setText(playName.getText().replace(WHICH_GAME, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (playId == 215L) {
            //  -> 第{!quarternr}节首先获得{pointnr}分
            playName.setText(playName.getText().replace(WHICH_QUARTER, addition1));
            playName.setText(playName.getText().replace(WHICH_QUARTER2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (playId == 256L) {
            //  -> 第{!setnr}局谁先获得{pointnr}分
            playName.setText(playName.getText().replace(WHICH_SET, addition1));
            playName.setText(playName.getText().replace(WHICH_SET2, addition1));
            playName.setText(playName.getText().replace(POINT, addition2));
            playName.setText(playName.getText().replace(POINT2, addition2));
        } else if (FROM_TO_PLAYS.contains(playId)) {
            // 替换 15分钟 玩法中的 {from} 和 {to} 占位符
            playName.setText(playName.getText().replace(FROM, addition1));
            playName.setText(playName.getText().replace(TO, addition2));
        } else if (A1_PLAYS.contains(playId)) {
            // A1
            playName.setText(playName.getText().replace(WHICH_GOAL, addition1));
            playName.setText(playName.getText().replace(WHICH_GOAL2, addition1));
            playName.setText(playName.getText().replace(TOTAL, addition1));
            playName.setText(playName.getText().replace(WHICH_CORNER, addition1));
            playName.setText(playName.getText().replace(WHICH_CORNER2, addition1));
            playName.setText(playName.getText().replace(WHICH_PENALTY, addition1));
            playName.setText(playName.getText().replace(WHICH_PENALTY2, addition1));
            playName.setText(playName.getText().replace(WHICH_QUARTER, addition1));
            playName.setText(playName.getText().replace(WHICH_QUARTER2, addition1));
            playName.setText(playName.getText().replace(POINT, addition1));
            playName.setText(playName.getText().replace(POINT2, addition1));
            playName.setText(playName.getText().replace(WHICH_SET, addition1));
            playName.setText(playName.getText().replace(WHICH_SET2, addition1));
            playName.setText(playName.getText().replace(WHICH_SCORE, addition1));
            playName.setText(playName.getText().replace(WHICH_SCORE2, addition1));
            playName.setText(playName.getText().replace(WHICH_BOOKING, addition1));
            playName.setText(playName.getText().replace(WHICH_BOOKING2, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD2, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD3, addition1));
        } else if (A2_PLAYS.contains(playId)) {
            // A2
            playName.setText(playName.getText().replace(WHICH_QUARTER, addition1));
            playName.setText(playName.getText().replace(WHICH_QUARTER2, addition1));
            playName.setText(playName.getText().replace(WHICH_SET, addition1));
            playName.setText(playName.getText().replace(WHICH_SET2, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME, addition1));
            playName.setText(playName.getText().replace(WHICH_GAME2, addition1));
            playName.setText(playName.getText().replace(WHICH_FRAME, addition1));
            playName.setText(playName.getText().replace(WHICH_FRAME2, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD2, addition1));
            playName.setText(playName.getText().replace(WHICH_PERIOD3, addition1));
        } else if (A3_PLAYS.contains(playId)) {
            playName.setText(playName.getText().replace(WHICH_GOAL_x, addition1));
            playName.setText(playName.getText().replace(WHICH_GOAL_CHINESE_x, addition1));
        }

        return CategoryParseUtils.parseName(Objects.nonNull(playName) ? playName.getText() : "");
    }

    private String getPlaySetName(String playSetCode, Integer sportId) {
        QueryWrapper wrapper = Wrappers.query();
        wrapper.eq("play_set_code", playSetCode);
        wrapper.eq("sport_id", sportId);
        RcsMarketCategorySet marketCategorySet = marketCategorySetMapper.selectOne(wrapper);
        if(Objects.nonNull(marketCategorySet)){
            QueryWrapper wrapperd = Wrappers.query();
            wrapperd.eq("name_code", marketCategorySet.getNameCode());
            RcsLanguageInternation rcsLanguageInternation=rcsLanguageInternationMapper.selectOne(wrapperd);
        return   Objects.nonNull(rcsLanguageInternation)?rcsLanguageInternation.getText():"";
        }
        return Objects.nonNull(marketCategorySet) ? marketCategorySet.getName() : "";
    }


    /**
     * 根據盤口狀態碼轉換名稱
     *
     * @param stateCode
     * @return
     */
    public static String getTradeStatusName(Integer stateCode) {
        switch (stateCode) {
            case 0:
                return TradeStatusEnum.OPEN.getName();
            case 2:
                return TradeStatusEnum.CLOSE.getName();
            case 1:
                return TradeStatusEnum.SEAL.getName();
            case 11:
                return TradeStatusEnum.LOCK.getName();
            case 12:
                return TradeStatusEnum.DISABLE.getName();
            case 13:
                return TradeStatusEnum.END.getName();
            default:
                return OperateLogEnum.NONE.getName();
        }
    }

    private RcsOperateLog updateMarketStatus(RcsOperateLog rcsOperateLog, LogAllBean vo) {
        StandardMatchInfo matchMarketLiveBean = standardMatchInfoMapper.selectById(vo.getMatchId());
        rcsOperateLog.setSportId(matchMarketLiveBean.getSportId().intValue());
        vo.setSportId(matchMarketLiveBean.getSportId().intValue());
        vo.setMatchManageId(matchMarketLiveBean.getMatchManageId());
        if(MatchTypeEnum.EARLY.getId().equals(matchMarketLiveBean.getMatchStatus())){
            rcsOperateLog.setOperatePageCode(17);
        }else {
            rcsOperateLog.setOperatePageCode(14);
        }
        rcsOperateLog.setUserId(vo.getUpdateUserId()==null?"-1":vo.getUpdateUserId()+"");
        rcsOperateLog.setMatchId(vo.getMatchId());
        rcsOperateLog.setOperateTime(new Date());
        rcsOperateLog.setBehavior("开关封锁");
        rcsOperateLog.setPlayId(vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId());
        if(vo.getCategoryId()==null){
            vo.setCategoryId(vo.getPlayId());
        }
        String matchName = montageEnAndZsIs(vo.getTeamList(),vo.getMatchId());
        switch (vo.getTradeLevel()) {
            case 1:
                //赛事级别
                if (checkStatusDiff(vo)) return null;
                rcsOperateLog.setObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setObjectNameByObj(matchName);
                rcsOperateLog.setExtObjectIdByObj(OperateLogEnum.NONE.getName());
                rcsOperateLog.setExtObjectNameByObj(OperateLogEnum.NONE.getName());
                break;
            case 2:
                //玩法级别
                rcsOperateLog.setObjectIdByObj(vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId());
                rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(
                        vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId(),
                        vo.getSportId()));
                rcsOperateLog.setExtObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setExtObjectNameByObj(matchName);
                break;
            case 3:
                if (checkStatusDiff(vo)) return null;
                //盘口级别 (有些Level盤非盤口級別)
                if (Objects.nonNull(vo.getMarketId())) {
                    rcsOperateLog.setObjectIdByObj(vo.getMarketId());
                    rcsOperateLog.setObjectNameByObj(
                            StringUtils.isNoneEmpty(vo.getMarketValue()) ?
                                    transMarketValue(new BigDecimal(vo.getMarketValue())) :setCategoryName(vo.getMarketId(),vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId(),vo.getSportId()));
                } else {
                    rcsOperateLog.setObjectIdByObj(vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId());
                    rcsOperateLog.setObjectNameByObj(getPlayNameZsEn(
                            vo.getCategoryId()==null?vo.getPlayId():vo.getCategoryId(),
                            vo.getSportId()));
                }
                StringBuilder extObjectId = new StringBuilder()
                        .append(vo.getMatchManageId()).append(" / ")
                        .append(vo.getCategoryId());
                StringBuilder extObjectName = new StringBuilder(getMatchName(vo.getTeamList(),vo.getMatchId()))
                        .append(" / ").append(getPlayNameZs(
                                vo.getCategoryId(),
                                vo.getSportId()));
                StringBuilder extObjectNameEn = new StringBuilder(getMatchNameEn(vo.getTeamList(),vo.getMatchId()))
                        .append(" / ").append(getPlayNameEn(
                                vo.getCategoryId(),
                                vo.getSportId()));
                rcsOperateLog.setExtObjectIdByObj(extObjectId);
                rcsOperateLog.setExtObjectNameByObj(montageEnAndZs(extObjectNameEn.toString(),
                        extObjectName.toString()));
                break;
            case 5:
                //批量玩法级别
                rcsOperateLog.setObjectIdByObj(vo.getCategoryIdList());
                rcsOperateLog.setObjectNameByObj(OperateLogEnum.NONE.getName());

                String matchId = StringUtils.isBlank(vo.getMatchManageId()) ? null : vo.getMatchManageId();
                matchId = ObjectUtils.defaultIfNull(matchId, String.valueOf(vo.getMatchId()));

                rcsOperateLog.setExtObjectIdByObj(matchId);
                rcsOperateLog.setExtObjectNameByObj(matchName);

                //既沒有玩法集名称,也沒有玩法集ID, case 9 就別跑了, 以防資料被覆蓋
                if (Objects.isNull(vo.getPlaySetName()) && Objects.isNull(vo.getCategorySetId())) {
                    break;
                }

            case 9:
                //玩法集编码
                if (Objects.nonNull(vo.getPlaySetName())) {
                    //次要玩法
                    rcsOperateLog.setObjectIdByObj(vo.getCategorySetId());
                    rcsOperateLog.setObjectNameByObj(CategorySetIdEnum.getValue(vo.getCategorySetId()).equals("-")?vo.getPlaySetName():CategorySetIdEnum.getValue(vo.getCategorySetId()));
                } else {
                    //主玩法 主玩法開關有狀態所以可判斷
                    if (checkStatusDiff(vo)) return null;
                    rcsOperateLog.setObjectIdByObj(vo.getCategorySetId());
                    rcsOperateLog.setObjectNameByObj(getPlaySetName(vo.getPlaySetCode(),vo.getSportId()));
                }
                rcsOperateLog.setExtObjectIdByObj(vo.getMatchManageId());
                rcsOperateLog.setExtObjectNameByObj(matchName);
                break;
        }

        rcsOperateLog.setBeforeValByObj(OperateLogEnum.NONE.getName());
        rcsOperateLog.setAfterValByObj(getTradeStatusName(vo.getMarketStatus()));
        return rcsOperateLog;
    }


}
