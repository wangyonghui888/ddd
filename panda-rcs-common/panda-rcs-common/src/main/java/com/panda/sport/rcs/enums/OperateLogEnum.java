package com.panda.sport.rcs.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperateLogEnum {
    NONE("none", "-", "{\"en\":\"\",\"zs\":\"-\"}"),

    //操作類型
    ODDS_UPDATE("oddsUpdate", "调赔率", "{\"en\":\"Adjust Odds\",\"zs\":\"调赔率\"}"),
    DATA_SOURCE_CHANGE("dataSourceChange", "切换数据源", "{\"en\":\"Switch Feed\",\"zs\":\"切换数据源\"}"),
    CONFIG_UPDATE("configUpdate", "调整参数", "{\"en\":\"Configuring\",\"zs\":\"调整参数\"}"),
    MARKET_UPDATE("marketUpdate", "调盘口", "{\"en\":\"Adjust Handicap\",\"zs\":\"调盘口\"}"),
    MARKET_TRADE_TYPE_UPDATE("marketTradeTypeUpdate", "切换操盘模式", "{\"en\":\"Switching Trading Mode\",\"zs\":\"切换操盘模式\"}"),
    MARKET_CREATE("marketCreate", "新增盘口", "{\"en\":\"Add Market\",\"zs\":\"新增盘口\"}"),
    LEAGUE_ATTRIBUTES("leagueAttributes", "联赛属性", "{\"en\":\"League - Edit\",\"zs\":\"联赛属性\"}"),
    TEMPLATE_SELECTION("templateSelection", "模板选择", "{\"en\":\"Select Template\",\"zs\":\"模板选择\"}"),
    TOURNAMENT_CONFIG("tournamentConfig", "联赛属性", "{\"en\":\"League Attributes\n\",\"zs\":\"联赛属性\"}"),
    TEMPLATE_CREATE("templateCreate", "模板新增", "{\"en\":\"Add Template\",\"zs\":\"模板新增\"}"),
    TEMPLATE_DELETE("templateDelete", "模板删除", "{\"en\":\"Delete Template\",\"zs\":\"模板删除\"}"),
    TEMPLATE_UPDATE("templateUpdate", "模板修改", "{\"en\":\"Modify Template\",\"zs\":\"模板修改\"}"),
    MARKET_STATUS("marketStatus", "开关封锁", "{\"en\":\"Offer Status\",\"zs\":\"开关封锁\"}"),
    PER_TIME_NODE("perTimeNode", "分时节点", "{\"en\":\"Settings by Timer\",\"zs\":\"分时节点\"}"),
    OPERATE_SETTING("operateSetting", "赛事模板调整", "{\"en\":\"Adjust Template Settings\",\"zs\":\"赛事模板调整\"}"),
    MATCH_SETTING("matchSetting", "修改赛事设置", "{\"en\":\"Adjust Match Settings\",\"zs\":\"修改赛事设置\"}"),
    FEED_WEIGHT_SETTINGS("FEED_WEIGHT_SETTINGS", "数据源权重设置", "{\"en\":\"Feed Weight Settings\",\"zs\":\"数据源权重设置\"}"),
    OPERATE_SPECEVENT_GOALPROB("OPERATE_SPECEVENT_GOALPROB", "特殊事件概率修改", "{\"en\":\"Spec Event Double Check\",\"zs\":\"特殊事件概率修改\"}"),
    OPERATE_SPECEVENT_SWITCH("OPERATE_SPECEVENT_SWITCH", "是否切换特殊事件", "{\"en\":\"Spec Event Switch\",\"zs\":\"是否切换特殊事件\"}"),
    OPERATE_SPECEVENT_QUIT("OPERATE_SPECEVENT_QUIT", "退出特殊事件", "{\"en\":\"Quit Spec Event\",\"zs\":\"退出特殊事件\"}"),
    OPERATE_AOAUTO_OPEN("OPERATE_AOAUTO_OPEN", "AO自动开盘", "{\"en\":\"AO Auto Open\",\"zs\":\"AO自动开盘\"}"),

    //操作參數
    MAX_SINGLE_BET_AMOUNT("maxSingleBetAmount", "最大投注最大赔付", "{\"en\":\"Max Bet Max Payout\",\"zs\":\"最大投注最大赔付\"}"),
    MALAY_SPREAD("malaySpread", "Malay Spread", "{\"en\":\"Malay Spread\",\"zs\":\"Malay Spread\"}"),
    HOME_SINGLE_ODDS_RATE("homeSingleOddsRate", "累计上盘变化（单枪跳分）", "{\"en\":\"Cumulative Upper Change (Single Bet)\",\"zs\":\"累计上盘变化（单枪跳分）\"}"),
    HOME_MULTI_ODDS_RATE("homeMultiOddsRate", "累计上盘变化（累计跳分）", "{\"en\":\"Cumulative Upper Change (Cumulative)\",\"zs\":\"累计上盘变化（累计跳分）\"}"),
    AWAY_SINGLE_ODDS_RATE("awaySingleOddsRate", "累计下盘变化（单枪跳分）", "{\"en\":\"Cumulative Lower Change (Single Bet)\",\"zs\":\"累计下盘变化（单枪跳分）\"}"),
    AWAY_MULTI_ODDS_RATE("awayMultiOddsRate", "累计下盘变化（累计跳分）", "{\"en\":\"Cumulative Lower Change (Cumulative)\",\"zs\":\"累计下盘变化（累计跳分）\"}"),
    AWAY_AUTO_CHANGE_RATE("awayAutoChangeRate", "自动水差", "{\"en\":\"Auto Margin Gap\",\"zs\":\"自动水差\"}"),
    HOME_LEVEL_FIRST_ODDS_RATE_PERCENTAGE("homeLevelFirstOddsRatePercentage", "概率变化(%)", "{\"en\":\"Probability Change\",\"zs\":\"概率变化(%)\"}"),
    DATA_SOURCE_CODE("dataSourceCode", "赔率源", "{\"en\":\"Odds Feed\",\"zs\":\"赔率源\"}"),
    BUSINES_MATCH_PAY_VAL("businesMatchPayVal", "商户单场赔付限额", "{\"en\":\"Merchant Single Match Payout\",\"zs\":\"商户单场赔付限额\"}"),
    USER_MATCH_PAY_VAL("userMatchPayVal", "用户单场赔付限额", "{\"en\":\"Player Single Match Payout\",\"zs\":\"用户单场赔付限额\"}"),
    MATCH_PRE_STATUS("matchPreStatus", "提前结算开关", "{\"en\":\"Cashout Switch\",\"zs\":\"提前结算开关\"}"),
    SYNC_MATCH_TEMPLATE("syncMatchTemplate", "同步联赛模板", "{\"en\":\"Template Synchronized\",\"zs\":\"同步联赛模板\"}"),
    IS_SELL("isSell", "是否开售", "{\"en\":\"Offer?\",\"zs\":\"是否开售\"}"),
    PLAY_ODDS_SOURCE_CONFIG("playOddsSourceConfig", "玩法赔率源设置", "{\"en\":\"Odds Feed\",\"zs\":\"玩法赔率源设置\"}"),
    MARKET_COUNT("marketCount", "最大盘口数", "{\"en\":\"Max Market Lines\",\"zs\":\"最大盘口数\"}"),
    VICE_MARKET_RATIO("viceMarketRatio", "副盘限额比列", "{\"en\":\"Alternate Line Limit Ratio\",\"zs\":\"副盘限额比列\"}"),
    MARKET_WARN("marketWarn", "盘口出涨预警", "{\"en\":\"Handicap Rise Warning\",\"zs\":\"盘口出涨预警\"}"),
    IS_SERIES("isSeries", "支持串关", "{\"en\":\"Support Parlay\",\"zs\":\"支持串关\"}"),
    ODDS_ADJUST_RANGE("oddsAdjustRange", "赔率（水差）变动幅度", "{\"en\":\"Odds (Margin Gap) Change Range\",\"zs\":\"赔率（水差）变动幅度\"}"),
    IS_SPECIAL_PUMPING("isSpecialPumping", "是否生效", "{\"en\":\"Activate\",\"zs\":\"是否生效\"}"),
    SPECIAL_PUMPING("specialPumping", "特殊抽水", "{\"en\":\"Special Spread\",\"zs\":\"特殊抽水\"}"),
    SPECIAL_ODDS_INTERVAL("specialOddsInterval", "Malay Spread", "{\"en\":\"Malay Spread\",\"zs\":\"Malay Spread\"}"),
    SPECIAL_ODDS_INTERVAL_STATUS("specialOddsIntervalStatus", "限额生效", "{\"en\":\"Limit Valid\",\"zs\":\"限额生效\"}"),
    SPECIAL_ODDS_INTERVAL_LOW("specialOddsIntervalLow", "低赔:单注赔付限额", "{\"en\":\"Low: Single Bet Payout Limit\",\"zs\":\"低赔:单注赔付限额\"}"),
    SPECIAL_ODDS_INTERVAL_HIGH("specialOddsIntervalHigh", "高赔:单注投注赔付限额", "{\"en\":\"High: Single Bet Placement & Payout Limit\",\"zs\":\"高赔:单注投注赔付限额\"}"),
    EVENT_HANDLE_TIME("eventHandleTime", "事件审核时间", "{\"en\":\"Event Time\",\"zs\":\"事件审核时间\"}"),
    SETTLE_HANDLE_TIME("settleHandleTime", "结算审核时间", "{\"en\":\"Settlement Time\",\"zs\":\"结算审核时间\"}"),
    SCORE_SOURCE("scoreSource", "比分源", "{\"en\":\"Score Feed\",\"zs\":\"比分源\"}"),
    AUTO_CLOSE_MARKET("autoCloseMarket", "自动关盘时间设置", "{\"en\":\"Market Close Time Settling\",\"zs\":\"自动关盘时间设置\"}"),
    MATCH_PROGRESS_TIME("matchProgressTime", "自动关盘时间", "{\"en\":\"Market Close Time\",\"zs\":\"自动关盘时间\"}"),
    MANUAL_MARKET_NEAR_DIFF("manualMarketNearDiff", "手动操盘相邻盘口差", "{\"en\":\"Manual Trading Adjacent Markets Difference\",\"zs\":\"手动操盘相邻盘口差\"}"),
    MANUAL_MARKET_NEAR_ODDS_DIFF("manualMarketNearOddsDiff", "手动操盘相邻盘口赔率差", "{\"en\":\"Manual Trading Adjacent Markets Odds Difference\",\"zs\":\"手动操盘相邻盘口赔率差\"}"),
    INJURY_TIME("injuryTime", "补时时间", "{\"en\":\"Injury Time\",\"zs\":\"补时时间\"}"),
    PRE_TEMPLATE("preTemplate", "早盘模板", "{\"en\":\"Pre-Match Template\",\"zs\":\"早盘模板\"}"),
    LIVE_TEMPLATE("LiveTemplate", "滚球模板", "{\"en\":\"In-Play Template\",\"zs\":\"滚球模板\"}"),
    BALANCE_OPTION("balanceOption", "累计差额计算方式", "{\"en\":\"Cumulative Difference Calculation Method\",\"zs\":\"累计差额计算方式\"}"),
    BALANCE_OPTION_RULE("balanceOptionRule", "平衡值", "{\"en\":\"Balance Rate\",\"zs\":\"平衡值\"}"),
    LINKAGE_MODE("linkageMode", "联动模式", "{\"en\":\"Sync Mode\",\"zs\":\"联动模式\"}"),
    MIN_MALAY_ODDS("minMalayOdds", "最小马来赔", "{\"en\":\"Min Malay Odds\",\"zs\":\"最小马来赔\"}"),
    MAX_MALAY_ODDS("maxMalayOdds", "最大马来赔", "{\"en\":\"Max Malay Odds\",\"zs\":\"最大马来赔\"}"),
    MIN_DECIMAL_ODDS("minDecimalOdds", "最小欧赔", "{\"en\":\"Min Euro Odds\",\"zs\":\"最小欧赔\"}"),
    MAX_DECIMAL_ODDS("maxDecimalOdds", "最大欧赔", "{\"en\":\"Max Euro Odds\",\"zs\":\"最大欧赔\"}"),
    ODD_CHANGE_RULE("oddChangeRule", "跳分机制", "{\"en\":\"Trigger Point\",\"zs\":\"跳分机制\"}"),
    MAX_AMOINT("maxAmount", "限额", "{\"en\":\"Limit\",\"zs\":\"限额\"}"),
    HOME_MULTI_MAX_AMOUNT("homeMultiMaxAmount", "累计限额值", "{\"en\":\"Cumulative Limit\",\"zs\":\"累计限额值\"}"),
    HOME_SINGLE_MAX_AMOUNT("HomeSingleMaxAmount", "单枪限额值", "{\"en\":\"Single Bet Limit\",\"zs\":\"单枪限额值\"}"),
    HOME_LEVEL_FIRST_MAX_AMOUNT("homeLevelFirstMaxAmount", "一级累计跳分限额值", "{\"en\":\"1st Level Cumulative Trigger Point Limit\",\"zs\":\"一级累计跳分限额值\"}"),
    SINGLE_ODD_CHANGE_AMOUNT("singleOddChangeAmount", "累计 / 单枪跳分", "{\"en\":\"Cumulative / Single Bet\",\"zs\":\"累计 / 单枪跳分\"}"),
    DIFF_ODD_CHANGE_AMOUNT("diffOddChangeAmount", "累计 / 差值跳分", "{\"en\":\"Cumulative / Difference,\"zs\":\"累计 / 差值跳分\"}"),
    HOME_LEVEL_FIRST_ODDS_RATE("homeLevelFirstOddsRate", "累计上盘变化（一级累计跳分）", "{\"en\":\"Cumulative Upper Change ( 1st Level Cumulative)\",\"zs\":\"累计上盘变化（一级累计跳分）\"}"),
    AWAY_LEVEL_FIRST_ODDS_RATE("awayLevelFirstOddsRate", "累计下盘变化（一级累计跳分）", "{\"en\":\"Cumulative Lower Change (1st Level Cumulative)\",\"zs\":\"累计下盘变化（一级累计跳分）\"}"),
    HOME_LEVEL_SECOND_MAX_AMOUNT("homeLevelSecondMaxAmount", "二级累计跳分限额值", "{\"en\":\"2nd Level Cumulative Trigger Point Limit\",\"zs\":\"二级累计跳分限额值\"}"),
    HOME_LEVEL_SECOND_ODDS_RATE("homeLevelSecondOddsRate", "累计上盘变化（二级累计跳分）", "{\"en\":\"Cumulative Upper Change (2nd Level Cumulative)\",\"zs\":\"累计上盘变化（二级累计跳分）\"}"),
    AWAY_LEVEL_SECOND_ODDS_RATE("awayLevelSecondOddsRate", "累计下盘变化（二级累计跳分）", "{\"en\":\"Cumulative Lower Change (2nd Level Cumulative)\",\"zs\":\"累计下盘变化（二级累计跳分）\"}"),
    ORDER_SINGLE_PAY_VAL("orderSinglePayVal", "单注投注/赔付限额", "{\"en\":\"Single Bet Payout Limit\",\"zs\":\"单注投注/赔付限额\"}"),
    USER_MULTI_PAY_VAL("userMultiPayVal", "用户累计赔付限额", "{\"en\":\"Player Cumulative Payout Limit\",\"zs\":\"用户累计赔付限额\"}"),
    DIFF_ORDER_VAL("diffOrderVal", "投注额差值", "{\"en\":\"Bet Amount Difference\",\"zs\":\"投注额差值\"}"),
    DIFF_ORDER_AND_PAY_VAL_COMBINE("diffOrderAndPayValCombine", "投注额/赔付组合差值", "{\"en\":\"Bet Amount / Payout Combination Difference\",\"zs\":\"投注额/赔付组合差值\"}"),
    CATEGORY_PRE_STATUS("categoryPreStatus", "提前结算开关", "{\"en\":\"Cashout Switch\",\"zs\":\"提前结算开关\"}"),
    CASH_OUT_MARGIN("cashOutMargin", "CashOut Margin", "{\"en\":\"CashOut Margin\",\"zs\":\"CashOut Margin\"}"),
    STATUS_SOURCE("statusSource", "赛事状态源", "{\"en\":\"Feed Status\",\"zs\":\"赛事状态源\"}"),
    EVENT_SOURCE("eventSource", "实时事件源", "{\"en\":\"Event Feed\",\"zs\":\"实时事件源\"}"),
    MARKET_DIFF("marketDiff", "水差", "{\"en\":\"Margin Gap\",\"zs\":\"水差\"}"),
    KICK_OFF_TEAM("kick_off_team", "谁先开球事件", "{\"en\":\"Kick Off Team\",\"zs\":\"谁先开球事件\"}"),
    GOAL("goal", "进球事件", "{\"en\":\"Goal\",\"zs\":\"进球事件\"}"),
    CORNER("corner", "角球事件", "{\"en\":\"Corner\",\"zs\":\"角球事件\"}"),
    FA_CARD("fa_card", "罚牌事件", "{\"en\":\"Bookings\",\"zs\":\"罚牌事件\"}"),
    CAUTION("caution", "警示值", "{\"en\":\"Warning Value\",\"zs\":\"警示值\"}"),
    BAI_JIA_CONFIG("baiJiaConfig", "百家赔权重", "{\"en\":\"General Comp Weight Settings\",\"zs\":\"百家赔权重\"}"),
    T_NORMAL("tNormal", "T常规", "{\"en\":\"T Routine\",\"zs\":\"T常规\"}"),
    T_MIN_WAIT("tMinWait", "T延时", "{\"en\":\"T Delay\",\"zs\":\"T延时\"}"),
    T_MAX_WAIT("tMaxWait", "Tmax", "{\"en\":\"T Max\",\"zs\":\"Tmax\"}"),
    ACCEPT_DATA_SOURCE("acceptDataSource", "接拒数据源", "{\"en\":\"Auto Accept Reject Settings\",\"zs\":\"接拒数据源\"}"),
    SAFETY_EVENT("safetyEvent", "安全事件", "{\"en\":\"Safe Events\",\"zs\":\"安全事件\"}"),
    DANGER_EVENT("dangerEvent", "危险事件", "{\"en\":\"Dangerous Events\",\"zs\":\"危险事件\"}"),
    CLOSING_EVENT("closingEvent", "封盘事件", "{\"en\":\"Market Closed Events\",\"zs\":\"封盘事件\"}"),
    REJECT_EVENT("rejectEvent", "拒单事件", "{\"en\":\"Bet Rejection Events\",\"zs\":\"拒单事件\"}"),
    COPY_EVENT_AND_TIME_CONFIG("copyEventAndTimeConfig", "复制接拒设置", "{\"en\":\"Copy configuration\",\"zs\":\"复制接拒设置\"}"),
    MARKET_DISABLE("marketDisable", "盘口弃用", "{\"en\":\"Disable Market\",\"zs\":\"盘口弃用\"}"),
    TEMPLATE_NAME("templateName", "模板名称", "{\"en\":\"Template Name\",\"zs\":\"模板名称\"}"),
    TOURNAMENT_LEVEL("tournamentLevel", "等级选择", "{\"en\":\"Level\",\"zs\":\"等级选择\"}"),
    IS_POPULAR("isPopular", "热门", "{\"en\":\"Hot Category\",\"zs\":\"热门\"}"),
    TARGET_PROFIT_RATE("targetProfitRate", "目标咬度", "{\"en\":\"Target W/L %\",\"zs\":\"目标咬度\"}"),
    MARGIN_GAP_LINKING("MARGIN_GAP_LINKING", "联动水差", "{\"en\":\"Margin Gap linking\",\"zs\":\"联动水差\"}"),
    TRIGGER_POINT_MULTIPLE_CHANGE_ODDS("TRIGGER_POINT_MULTIPLE_CHANGE_ODDS", "自动跳分机制-倍数跳分", "{\"en\":\"Trigger Point - Multiple Change Odds\",\"zs\":\"自动跳分机制-倍数跳分\"}"),
    TRIGGER_POINT_LEVEL_2_ACCUMULATIVE_JUMP_POINTS("TRIGGER_POINT_LEVEL_2_ACCUMULATIVE_JUMP_POINTS", "自动跳分机制-二级累值跳分", "{\"en\":\"Trigger Point - Level 2 Accumulative Jump Points\",\"zs\":\"自动跳分机制-二级累值跳分\"}"),
    AUTO_LINE_CHANGE_MULTIPLE_CHANGE_HANDICAP("AUTO_LINE_CHANGE_MULTIPLE_CHANGE_HANDICAP", "自动跳盘机制-倍数跳盘", "{\"en\":\"Auto Line Change - Multiple Change Handicap\",\"zs\":\"自动跳盘机制-倍数跳盘\"}"),
    AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE("AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE", "自动跳盘机制-二级累值跳盘", "{\"en\":\"Auto Line Change - Level 2 Accumulated Change\",\"zs\":\"自动跳盘机制-二级累值跳盘\"}"),
    AUTO_LINE_CHANGE_LEVEL_1_ACCUMULATED_CHANGE_LIMIT("AUTO_LINE_CHANGE_LEVEL_1_ACCUMULATED_CHANGE_LIMIT", "自动跳盘机制-一级累值跳盘限额", "{\"en\":\"Auto Line Change - Level 1 Accumulated Change Limit\",\"zs\":\"自动跳盘机制-一级累值跳盘限额\"}"),
    AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE("AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE", "自动跳盘机制-累计上盘变化 (一级累值跳盘)", "{\"en\":\"Auto Line Change - Cumulative Upper Change (Level 1 Accumulated Change)\",\"zs\":\"自动跳盘机制-累计上盘变化 (一级累值跳盘)\"}"),
    AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE("AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_1_ACCUMULATED_CHANGE", "自动跳盘机制-累计下盘变化 (一级累值跳盘)", "{\"en\":\"Auto Line Change - Cumulative Lower Change (Level 1 Accumulated Change)\",\"zs\":\"自动跳盘机制-累计下盘变化 (一级累值跳盘)\"}"),
    AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE_LIMIT("AUTO_LINE_CHANGE_LEVEL_2_ACCUMULATED_CHANGE_LIMIT", "自动跳盘机制-二级累值跳盘限额", "{\"en\":\"Auto Line Change - Level 2 Accumulated Change Limit\",\"zs\":\"自动跳盘机制-二级累值跳盘限额\"}"),
    AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE("AUTO_LINE_CHANGE_CUMULATIVE_UPPER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE", "自动跳盘机制-累计上盘变化 (二级累值跳盘)", "{\"en\":\"Auto Line Change - Cumulative Upper Change (Level 2 Accumulated Change)\",\"zs\":\"自动跳盘机制-累计上盘变化 (二级累值跳盘)\"}"),
    AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE("AUTO_LINE_CHANGE_CUMULATIVE_LOWER_CHANGE_LEVEL_2_ACCUMULATED_CHANGE", "自动跳盘机制-累计下盘变化 (二级累值跳盘)", "{\"en\":\"Auto Line Change - Cumulative Lower Change (Level 2 Accumulated Change)\",\"zs\":\"自动跳盘机制-累计下盘变化 (二级累值跳盘)\"}"),
    AUTO_PAUSE_INCREASING("AUTO_PAUSE_INCREASING", "出涨自动封盘", "{\"en\":\"Auto Pause Increasing\",\"zs\":\"出涨自动封盘\"}"),
    HANDICAP_DIFFERENCE("HANDICAP_DIFFERENCE", "相邻盘口赔率分差", "{\"en\":\"Handicap difference\",\"zs\":\"相邻盘口赔率分差\"}"),
    BET_BOOKING_SWITCH("BET_BOOKING_SWITCH", "预约投注开关", "{\"en\":\"Bet booking switch\",\"zs\":\"预约投注开关\"}"),
    MAXIMUM_SPREAD_DROP("MAXIMUM_SPREAD_DROP", "跳水最大值", "{\"en\":\"Maximum spread drop\",\"zs\":\"跳水最大值\"}"),
    ODDS_DIFF_PERCENTAGE("ODDS_DIFF_PERCENTAGE", "拒单赔率百分比差值", "{\"en\":\"Odds Diff. Percentage\",\"zs\":\"拒单赔率百分比差值\"}"),

    // 操作页面
    OPERATE_PAGE_ZPSS("OPERATE_PAGE_ZPSS", "早盘赛事", "{\"en\":\"Pre-Match\",\"zs\":\"早盘赛事\"}"),
    OPERATE_PAGE_ZPCP("OPERATE_PAGE_ZPCP", "早盘操盘", "{\"en\":\"Pre-Match Trading\",\"zs\":\"早盘操盘\"}"),
    OPERATE_PAGE_ZPCP_CYWF("OPERATE_PAGE_ZPCP_CYWF", "早盘操盘-次要玩法", "{\"en\":\"Pre-Match Trading - Special\",\"zs\":\"早盘操盘-次要玩法\"}"),
    OPERATE_PAGE_ZPCP_TJCK("OPERATE_PAGE_ZPCP_TJCK", "早盘操盘-调价窗口", "{\"en\":\"Pre-Match Trading - Market Settings\",\"zs\":\"早盘操盘-调价窗口\"}"),
    OPERATE_PAGE_ZPCP_CYWF_TJCK("OPERATE_PAGE_ZPCP_CYWF_TJCK", "早盘操盘-次要玩法-调价窗口", "{\"en\":\"Pre-Match Trading - Special - Market Settings\",\"zs\":\"早盘操盘-次要玩法-调价窗口\"}"),
    OPERATE_PAGE_ZPCP_SZ("OPERATE_PAGE_ZPCP_SZ", "早盘操盘-设置", "{\"en\":\"Pre-Match Trading - Settings\",\"zs\":\"早盘操盘-设置\"}"),
    OPERATE_PAGE_GQSS("OPERATE_PAGE_GQSS", "滚球赛事", "{\"en\":\"In-Play Match\",\"zs\":\"滚球赛事\"}"),
    OPERATE_PAGE_GQCP("OPERATE_PAGE_GQCP", "滚球操盘", "{\"en\":\"In-Play Trading\",\"zs\":\"滚球操盘\"}"),
    OPERATE_PAGE_GQCP_CYWF("OPERATE_PAGE_GQCP_CYWF", "滚球操盘-次要玩法", "{\"en\":\"滚球操盘-次要玩法\",\"zs\":\"滚球操盘-次要玩法\"}"),
    OPERATE_PAGE_GQCP_TJCK("OPERATE_PAGE_GQCP_TJCK", "滚球操盘-调价窗口", "{\"en\":\"In-Play Trading - Market Settings\",\"zs\":\"滚球操盘-调价窗口\"}"),
    OPERATE_PAGE_GQCP_CYWF_TJCK("OPERATE_PAGE_GQCP_CYWF_TJCK", "滚球操盘-次要玩法-调价窗口", "{\"en\":\"In-Play Trading - Special - Market Settings\",\"zs\":\"滚球操盘-次要玩法-调价窗口\"}"),
    OPERATE_PAGE_GQCP_SZ("OPERATE_PAGE_GQCP_SZ", "滚球操盘-设置", "{\"en\":\"In-Play Trading - Settings\",\"zs\":\"滚球操盘-设置\"}"),
    OPERATE_PAGE_LSCSSZ("OPERATE_PAGE_LSCSSZ", "联赛参数设置", "{\"en\":\"League Settings\",\"zs\":\"联赛参数设置\"}"),
    OPERATE_PAGE_WFJGL_ZQ("OPERATE_PAGE_WFJGL_ZQ", "玩法集管理-足球", "{\"en\":\"Market Group Management\",\"zs\":\"玩法集管理-足球\"}"),
    AO_FB("AO_FB", "AO FB", "{\"en\":\"AO FB\",\"zs\":\"AO FB\"}"),


    // 修改前后值
    EDIT_VALUE_QY("EDIT_VALUE_QY", "启用", "{\"en\":\"Enable\",\"zs\":\"启用\"}"),
    EDIT_VALUE_QIY("EDIT_VALUE_QIY", "弃用", "{\"en\":\"Disable\",\"zs\":\"弃用\"}"),
    EDIT_VALUE_ZDMS("EDIT_VALUE_ZDMS", "自动模式(A)", "{\"en\":\"A\",\"zs\":\"自动模式(A)\"}"),
    EDIT_VALUE_SDMS("EDIT_VALUE_SDMS", "手动模式(M)", "{\"en\":\"M\",\"zs\":\"手动模式(M)\"}"),
    EDIT_VALUE_ZDJQMS("EDIT_VALUE_ZDJQMS", "自动加强模式(A+)", "{\"en\":\"A+\",\"zs\":\"自动加强模式(A+)\"}"),
    EDIT_VALUE_LDMS("EDIT_VALUE_LDMS", "联动模式(L)", "{\"en\":\"L\",\"zs\":\"联动模式(L)\"}"),
    EDIT_VALUE_F("EDIT_VALUE_F", "否", "{\"en\":\"No\",\"zs\":\"否\"}"),
    EDIT_VALUE_S("EDIT_VALUE_S", "是", "{\"en\":\"Yes\",\"zs\":\"是\"}"),
    EDIT_VALUE_SC("EDIT_VALUE_SC", "刪除", "{\"en\":\"Delete\",\"zs\":\"刪除\"}"),
    EDIT_VALUE_ZDPP("EDIT_VALUE_ZDPP", "自動匹配", "{\"en\":\"Auto Matching\",\"zs\":\"自動匹配\"}"),
    EDIT_VALUE_G("EDIT_VALUE_G", "关", "{\"en\":\"Off\",\"zs\":\"关\"}"),
    EDIT_VALUE_K("EDIT_VALUE_K", "开", "{\"en\":\"On\",\"zs\":\"开\"}"),
    EDIT_VALUE_TZECZ("EDIT_VALUE_TZECZ", "投注额差值", "{\"en\":\"Bet Amount Difference\",\"zs\":\"投注额差值\"}"),
    EDIT_VALUE_TZE_PFHHCZ("EDIT_VALUE_TZE_PFHHCZ", "投注额/赔付混合差值", "{\"en\":\"Mixed betting/payout\",\"zs\":\"投注额/赔付混合差值\"}"),
    EDIT_VALUE_LJ_DQTF("EDIT_VALUE_LJ_DQTF", "累计/单枪跳分", "{\"en\":\"Cumulative/single bet spread change\",\"zs\":\"累计/单枪跳分\"}"),
    EDIT_VALUE_LJCZTF("EDIT_VALUE_LJCZTF", "累计差值跳分", "{\"en\":\"Cumulative spread change\",\"zs\":\"累计差值跳分\"}"),
    EDIT_VALUE_FE("EDIT_VALUE_FE", "封", "{\"en\":\"Suspend\",\"zs\":\"封\"}"),
    EDIT_VALUE_SU("EDIT_VALUE_SU", "锁", "{\"en\":\"Lock\",\"zs\":\"锁\"}"),
    EDIT_VALUE_SP("EDIT_VALUE_SP", "收盘", "{\"en\":\"Closed\",\"zs\":\"收盘\"}"),
    EDIT_VALUE_GB("EDIT_VALUE_GB", "关闭", "{\"en\":\"Off\",\"zs\":\"关闭\"}"),
    EDIT_VALUE_KQ("EDIT_VALUE_KQ", "开启", "{\"en\":\"On\",\"zs\":\"开启\"}"),

    //球种多语言,name添加SPORT_前缀,防止ID和修改前/后的值重复冲突
    FOOTBALL("FOOTBALL", "SPORT_1", "{\"en\":\"Football\",\"zs\":\"足球\",\"zh\":\"足球\"}"),
    BASKETBALL("BASKETBALL", "SPORT_2", "{\"en\":\"Basketball\",\"zs\":\"篮球\",\"zh\":\"籃球\"}"),
    BASEBALL("BASEBALL", "SPORT_3", "{\"en\":\"Baseball\",\"zs\":\"棒球\",\"zh\":\"棒球\"}"),
    ICE_HOCKEY("ICE HOCKEY", "SPORT_4", "{\"en\":\"Ice Hockey\",\"zs\":\"冰球\",\"zh\":\"冰球\"}"),
    TENNIS("TENNIS", "SPORT_5", "{\"en\":\"Tennis\",\"zs\":\"网球\",\"zh\":\"網球\"}"),
    AMERICAN_FOOTBALL("AMERICAN FOOTBALL", "SPORT_6", "{\"en\":\"American Football\",\"zs\":\"美式足球\",\"zh\":\"美式足球\"}"),
    SNOOKER("SNOOKER", "SPORT_7", "{\"en\":\"Snooker\",\"zs\":\"斯诺克\",\"zh\":\"斯諾克\"}"),
    TABLE_TENNIS("TABLE TENNIS", "SPORT_8", "{\"en\":\"Table Tennis\",\"zs\":\"乒乓球\",\"zh\":\"乒乓球\"}"),
    VOLLEYBALL("VOLLEYBALL", "SPORT_9", "{\"en\":\"Volleyball\",\"zs\":\"排球\",\"zh\":\"排球\"}"),
    BADMINTON("BADMINTON", "SPORT_10", "{\"en\":\"Badminton\",\"zs\":\"羽毛球\",\"zh\":\"羽毛球\"}"),
    HANDBALL("HANDBALL", "SPORT_11", "{\"en\":\"Handball\",\"zs\":\"手球\",\"zh\":\"手球\"}"),
    BOXING("BOXING", "SPORT_12", "{\"en\":\"Boxing\",\"zs\":\"拳击/格斗\",\"zh\":\"拳擊/格鬥\"}"),
    BEACH_VOLLEYBALL("BEACH VOLLEYBALL", "SPORT_13", "{\"en\":\"Beach Volleyball\",\"zs\":\"沙滩排球\",\"zh\":\"沙灘排球\"}"),
    RUGBY_UNION("RUGBY UNION", "SPORT_14", "{\"en\":\"Rugby Union\",\"zs\":\"联合式橄榄球\",\"zh\":\"聯合式橄欖球\"}"),
    HOCKEY("HOCKEY", "SPORT_15", "{\"en\":\"Hockey\",\"zs\":\"曲棍球\",\"zh\":\"曲棍球\"}"),
    WATER_POLO("WATER POLO", "SPORT_16", "{\"en\":\"Water Polo\",\"zs\":\"水球\",\"zh\":\"水球\"}"),
    ATHLETICS("ATHLETICS", "SPORT_17", "{\"en\":\"Athletics\",\"zs\":\"田径\",\"zh\":\"田徑\"}"),
    FOUR_X_10_KM_RELAY("FOUR_X_10_KM_RELAY", "SPORT_18", "{\"en\":\"4 x 10 km Relay\",\"zs\":\"4 x 10 km Relay\",\"zh\":\"娛樂\"}"),
    SWIMMING("SWIMMING", "SPORT_19", "{\"en\":\"Swimming\",\"zs\":\"游泳\",\"zh\":\"遊泳\"}"),
    GYMNASTICS("GYMNASTICS", "SPORT_20", "{\"en\":\"Gymnastics\",\"zs\":\"体操\",\"zh\":\"體操\"}"),
    DIVING("DIVING", "SPORT_21", "{\"en\":\"Diving\",\"zs\":\"跳水\",\"zh\":\"跳水\"}"),
    SHOOTING("SHOOTING", "SPORT_22", "{\"en\":\"Shooting\",\"zs\":\"射击\",\"zh\":\"射擊\"}"),
    WEIGHTLIFTING("WEIGHTLIFTING", "SPORT_23", "{\"en\":\"Weightlifting\",\"zs\":\"举重\",\"zh\":\"舉重\"}"),
    ARCHERY("ARCHERY", "SPORT_24", "{\"en\":\"Archery\",\"zs\":\"射箭\",\"zh\":\"射箭\"}"),
    FENCING("FENCING", "SPORT_25", "{\"en\":\"Fencing\",\"zs\":\"击剑\",\"zh\":\"擊劍\"}"),
    CURLING("CURLING", "SPORT_26", "{\"en\":\"Curling\",\"zs\":\"冰壶\",\"zh\":\"冰壺\"}"),
    TAEKWONDO("TAEKWONDO", "SPORT_27", "{\"en\":\"Taekwondo\",\"zs\":\"跆拳道\",\"zh\":\"跆拳道\"}"),
    GOLF("GOLF", "SPORT_28", "{\"en\":\"Golf\",\"zs\":\"高尔夫\",\"zh\":\"高爾夫\"}"),
    CYCLING("CYCLING", "SPORT_29", "{\"en\":\"Cycling\",\"zs\":\"自行车\",\"zh\":\"自行車\"}"),
    HORSE_RACING("HORSE RACING", "SPORT_30", "{\"en\":\"Horse Racing\",\"zs\":\"赛马\",\"zh\":\"賽馬\"}"),
    SAILING("SAILING", "SPORT_31", "{\"en\":\"Sailing\",\"zs\":\"帆船\",\"zh\":\"帆船\"}"),
    ROWING("ROWING", "SPORT_32", "{\"en\":\"Rowing\",\"zs\":\"划船\",\"zh\":\"劃船\"}"),
    MOTORSPORT("MOTORSPORT", "SPORT_33", "{\"en\":\"Motorsport\",\"zs\":\"赛车运动\",\"zh\":\"賽車運動\"}"),
    JUDO("JUDO", "SPORT_34", "{\"en\":\"Judo\",\"zs\":\"柔道\",\"zh\":\"柔道\"}"),
    KARATE("KARATE", "SPORT_35", "{\"en\":\"karate\",\"zs\":\"空手道\",\"zh\":\"空手道\"}"),
    WRESTLING("WRESTLING", "SPORT_36", "{\"en\":\"Wrestling\",\"zs\":\"摔跤\",\"zh\":\"摔跤\"}"),
    CRICKET("CRICKET", "SPORT_37", "{\"en\":\"Cricket\",\"zs\":\"板球\",\"zh\":\"板球\"}"),
    DARTS("DARTS", "SPORT_38", "{\"en\":\"Darts\",\"zs\":\"飞镖\",\"zh\":\"飛鏢\"}"),
    BEACH_FOOTBALL("BEACH_FOOTBALL", "SPORT_39", "{\"en\":\"Beach Football\",\"zs\":\"沙滩足球\",\"zh\":\"沙灘足球\"}"),
    OTHERS("OTHERS", "SPORT_40", "{\"en\":\"Others\",\"zs\":\"其他\",\"zh\":\"其他\"}"),
    RUGBY_LEAGUE("RUGBY_LEAGUE", "SPORT_41", "{\"en\":\"Rugby League\",\"zs\":\"联盟式橄榄球\",\"zh\":\"聯盟式橄欖球\"}"),
    FUN("FUN", "SPORT_50", "{\"en\":\"Fun\",\"zs\":\"趣味\",\"zh\":\"趣味\"}"),
    LOL("LOL", "SPORT_100", "{\"en\":\"LOL\",\"zs\":\"英雄联盟\",\"zh\":\"英雄聯盟\"}"),
    DOTA2("DOTA2", "SPORT_101", "{\"en\":\"Dota2\",\"zs\":\"Dota2\",\"zh\":\"Dota2\"}"),
    CS_GO("CS:GO", "SPORT_102", "{\"en\":\"CS:GO\",\"zs\":\"CS:GO\",\"zh\":\"CS:GO\"}"),
    HONOR_OF_KINGS("HONOR_OF_KINGS", "SPORT_103", "{\"en\":\"Honor of Kings\",\"zs\":\"王者荣耀\",\"zh\":\"王者榮耀\"}"),
    PUBG("PUBG", "SPORT_104", "{\"en\":\"PUBG\",\"zs\":\"绝地求生\",\"zh\":\"絕地求生\"}"),
    //結尾
    ;


    private String id;
    private String name;
    private String langJson;

    OperateLogEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    OperateLogEnum() {
    }
    OperateLogEnum(String id, String name, String langJson) {
        this.id = id;
        this.name = name;
        this.langJson = langJson;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLangJson() {
        return langJson;
    }

    // 做本地缓存,方便根据name获取JSON
    private static final Map<String, OperateLogEnum> logEnumMap = new HashMap<>();
    static {
        for (OperateLogEnum logEnum : values()) {
            logEnumMap.put(logEnum.getName(), logEnum);
        }
    }
    public static OperateLogEnum getByName(String name){
        return logEnumMap.get(name);
    }

}
