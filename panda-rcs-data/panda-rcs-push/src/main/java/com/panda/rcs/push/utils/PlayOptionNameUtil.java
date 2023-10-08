package com.panda.rcs.push.utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.common.MatchConstant;
import com.panda.sport.rcs.common.NumberUtils;
import com.panda.sport.rcs.enums.OddsTypeEnum;
import com.panda.sport.rcs.pojo.StandardSportMarket;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.panda.rcs.push.utils.Placeholder.*;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.util
 * @Description :  TODO
 * @Date: 2020-08-04 16:08
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class PlayOptionNameUtil {

    static String REG = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

    public static List<Integer> XPLAY = Arrays.asList(256, 162, 163, 164, 165, 166, 253, 254, 255, 32, 33, 34, 231, 232, 233, 145, 146, 147, 215, 274, 277, 278, 279, 175, 176, 177, 178, 179,
            213, 167, 168, 276, 280, 281, 287, 288, 289, 275, 283, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 196, 197, 195, 262, 263, 264, 265, 268, 297, 298, 203);

    public static void main(String[] args) {
        Pattern p = Pattern.compile(REG);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("homeName", new HashMap<String, String>() {{
            put("zs", "主队");
        }});
        map.put("awayName", new HashMap<String, String>() {{
            put("zs", "客队");
        }});

        String plama = "{!framenr} Frame - {$competitor1} Break 100+";
        plama = replace(plama, WHICH_FRAME, "1");
        plama = replace(plama, WHICH_FRAME, "2");
        System.out.println(plama);
        String bean = "{\"betAmount\": 500,\"betAmount1\": 5,\"betNo\": \"24409180487680\",\"betTime\": 1598529496046,\"createTime\": 1598529496046,\"createUser\": \"系统\",\"dataSourceCode\": \"SR\",\"dateExpect\": \"2020-08-27\",\"handleAfterOddsValue\": 1.37,\"handleAfterOddsValue1\": 1.37,\"handleStatus\": 0,\"handledBetAmout\": 5,\"marketId\": 1298906765773807618,\"marketType\": \"EU\",\"marketValue\": \"-1\",\"matchId\": 1188773,\"matchInfo\": \"主队 v 客队\",\"matchName\": \"奔驰赞助超级联赛88\",\"matchProcessId\": 0,\"matchType\": 1,\"maxWinAmount\": 185.0,\"modifyTime\": 1598529496046,\"modifyUser\": \"系统\",\"oddFinally\": \"1.37\",\"oddsValue\": 137000.0,\"orderNo\": \"14409180487681\",\"orderStatus\": 1,\"paidAmount\": 685.00,\"paidAmount1\": 6.85,\"platform\": \"PA\",\"playId\": 71,\"playName\": \"下半场让球赛果\",\"playOptions\": \"2\",\"playOptionsId\": 1298906767606718465,\"playOptionsName\": \"客队 ((主队+1))\",\"recType\": 1,\"recVal\": \"185\",\"riskChannel\": 1,\"scoreBenchmark\": \"0:0\",\"sportId\": 1,\"sportName\": \"足球\",\"tournamentId\": 881,\"tradeType\": 0,\"turnamentLevel\": 2,\"uid\": 199341280913203200,\"validateResult\": 1}";
        OrderItem itemBean = JSONObject.parseObject(bean, OrderItem.class);
        //System.out.println(replaceFromAndTo("46","60",plama));
    }

    public static String assemblyMarketValue(Map<String, Object> map, OrderItem item, String optionValue, String languageType) {
        String home = "";
        String away = "";
        if (ObjectUtils.isNotEmpty(map.get("homeName"))) {
            Map<String, String> teamMap = (Map<String, String>) map.get("homeName");
            home = teamMap.get(languageType) == null ? "" : teamMap.get(languageType);
        }
        if (ObjectUtils.isNotEmpty(map.get("awayName"))) {
            Map<String, String> teamMap = (Map<String, String>) map.get("awayName");
            away = teamMap.get(languageType) == null ? "" : teamMap.get(languageType);
        }
        String marketValue = item.getMarketValue();
        String playName = item.getPlayName();
        String playOptions = item.getPlayOptions();
        if (StringUtils.isNotEmpty(optionValue)) {
            if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_COMPETITOR1_AHCP)) {
                optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_COMPETITOR1_AHCP, home);
            }
            if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_COMPETITOR2_AHCP)) {
                optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_COMPETITOR2_AHCP, away);
            }
            if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_TOTAL)) {
                if (StringUtils.isNotEmpty(marketValue)) {
                    if (marketValue.contains("大") || marketValue.contains("小")) {
                        marketValue = marketValue.replace("大 ", "");
                        marketValue = marketValue.replace("小 ", "");
                    }
                }
                optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_TOTAL, "") + marketValue;
            }
            if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_HCP)) {
                if ("1".equals(playOptions)) {
                    optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_HCP, marketValue);
                } else if ("2".equals(playOptions)) {
                    String key = "+";
                    if (!marketValue.contains("-")) {
                        key = "-";
                    }
                    optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_HCP, key + marketValue.replace("-", "").replace("+", ""));
                } else {
                    optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_HCP, marketValue);
                }
            } else if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_HCP2)) {
                Integer index = item.getPlayOptionsName().indexOf("  ");
                if(index == -1){
                    index = item.getPlayOptionsName().indexOf(" ");
                }
                optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_HCP2, item.getPlayOptionsName().substring(index).trim());
            } else if (optionValue.contains(MatchConstant.TITLE_SHOW_NAME_HCP1)) {
                Integer index = item.getPlayOptionsName().indexOf("  ");
                if(index == -1){
                    index = item.getPlayOptionsName().indexOf(" ");
                }
                optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_HCP1, item.getPlayOptionsName().substring(index).trim());
            } else if (optionValue.contains(MatchConstant.HCP)) {
                optionValue = optionValue.replace(MatchConstant.HCP, marketValue);
            }
            if (optionValue.contains("null") || optionValue.contains(":") || optionValue.contains("Other")) {
                optionValue = optionValue.replace("null", "");
                optionValue = optionValue.replace(":", "-");
                optionValue = optionValue.replace("Other", "其他");
            }
        }
        if (StringUtils.isNotEmpty(playName)) {
            if (playName.contains("净胜") || playName.contains("winning margin")) {
                if (!playOptions.contains("Other")) {
                    String[] ands = playOptions.split("And");
                    if (ands != null && ands.length > 1) {

                        if (ands[0].equals("1")) {
                            optionValue = home + (languageType.equals("zs") ? "-净胜  " : "-winning margin ");
                        } else if (ands[0].equals("2")) {
                            optionValue = away + (languageType.equals("zs") ? "-净胜  " : "-winning margin ");
                        }
                        optionValue = optionValue + "  " + ands[1];
                    } else {
                        optionValue = ands[0];
                    }
                } else {
                    optionValue = languageType.equals("zs") ? "其他" : "Other";
                }
            }
        }
        if (StringUtils.isEmpty(optionValue) || "null".equals(optionValue)) {
            optionValue = item.getPlayOptionsName();
        }
        return optionValue;
    }

    public static String assemblyPlayName(String home, String away, String playName, OrderItem item, StandardSportMarket market) {

        Integer playId = item.getPlayId();
        String marketValue = item.getMarketValue() == null ? "" : item.getMarketValue();
        if (StringUtils.isNotBlank(playName)) {
            if (XPLAY.contains(playId) && market != null) {
                String addition1 = market.getAddition1() == null ? "" : market.getAddition1();
                String addition2 = market.getAddition2() == null ? "" : market.getAddition2();
                if (Arrays.asList(255, 256).contains(playId)) {
                    playName = replace(playName, WHICH_SET, addition1);
                    playName = replace(playName, POINT2, addition2);
                } else if (Arrays.asList(162, 163, 164, 165, 166, 253, 254).contains(playId)) {
                    playName = replace(playName, WHICH_SET, addition2);
                } else if (Arrays.asList(32, 33, 34, 231, 232, 233).contains(playId)) {
                    if(playId == 33 || playId == 232){
                        String addi = (!"".equals(market.getAddition5()) || market.getAddition5() != null) ? market.getAddition5() : "0,0";
                        String [] addArr = addi.split(",");
                        playName = replaceFromAndTo(addArr[0], addArr[1], playName);
                    } else {
                        playName = replaceFromAndTo(addition2, market.getAddition3(), playName);
                    }
                } else if (Arrays.asList(145, 146).contains(playId)) {
                    playName = replace(playName, WHICH_QUARTER, addition2);
                } else if (Arrays.asList(147).contains(playId)) {
                    playName = replace(playName, WHICH_QUARTER, addition2);
                    playName = replace(playName, POINT2, addition1);
                } else if (Arrays.asList(215).contains(playId)) {
                    playName = replace(playName, WHICH_QUARTER, addition1);
                    playName = replace(playName, POINT2, addition2);
                } else if (Arrays.asList(274, 277, 278, 279).contains(playId)) {
                    playName = replace(playName, FROM, addition2);
                    playName = replace(playName, TO, market.getAddition3());
                } else if (Arrays.asList(175, 176, 177, 178).contains(playId)) {
                    playName = replace(playName, WHICH_GAME, addition2);
                } else if (Arrays.asList(179).contains(playId)) {
                    playName = replace(playName, WHICH_GAME, addition2);
                    playName = replace(playName, POINT, addition1);
                } else if (Arrays.asList(213, 203).contains(playId)) {
                    playName = replace(playName, WHICH_GAME, addition1);
                    playName = replace(playName, POINT2, addition2);
                } else if (Arrays.asList(167).contains(playId)) {
                    playName = replace(playName, WHICH_SET, addition1);
                    playName = replace(playName, WHICH_GAME_X, addition2);
                    playName = replace(playName, WHICH_GAME_Y, (Integer.parseInt(addition2) + 1) + "");
                } else if (Arrays.asList(168).contains(playId)) {
                    playName = replace(playName, WHICH_SET, addition1);
                    playName = replace(playName, WHICH_GAME2, addition2);
                } else if (Arrays.asList(276, 280, 281, 287, 288, 289).contains(playId)) {
                    playName = replace(playName, INNING, addition2);
                } else if (Arrays.asList(275, 283).contains(playId)) {
                    playName = replace(playName, INNING, addition1);
                } else if (Arrays.asList(184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 196, 197).contains(playId)) {
                    playName = replace(playName, WHICH_FRAME, addition2);
                } else if (Arrays.asList(195).contains(playId)) {
                    playName = replace(playName, WHICH_FRAME, addition2);
                    playName = replace(playName, WHICH_XTH, addition1);
                } else if (Arrays.asList(262, 263, 264, 265, 268, 297, 298).contains(playId)) {
                    playName = replace(playName, WHICH_PERIOD, addition2);
                }
            }

            playName = replace(playName, HOME, home);
            playName = replace(playName, AWAY, away);
            playName = replace(playName, HOME2, home);
            playName = replace(playName, AWAY2, away);
            playName = replace(playName, HOME3, home);
            playName = replace(playName, AWAY3, home);
            playName = replace(playName, WHICH_GOAL, marketValue);
            playName = replace(playName, WHICH_GOAL2, marketValue);
            playName = replace(playName, HCP, "");
            playName = replace(playName, TOTAL, marketValue);
            playName = replace(playName, WHICH_SET, marketValue);
            playName = replace(playName, WHICH_SET2, marketValue);
            playName = replace(playName, WHICH_FRAME, marketValue);
            playName = replace(playName, WHICH_GAME, marketValue);
            playName = replace(playName, WHICH_GAME2, marketValue);
            playName = replace(playName, WHICH_QUARTER, marketValue);
            playName = replace(playName, WHICH_QUARTER2, marketValue);
            playName = replace(playName, WHICH_CORNER, marketValue);
            playName = replace(playName, WHICH_CORNER2, marketValue);
            playName = replace(playName, WHICH_FRAME2, marketValue);
            playName = replace(playName, WHICH_PENALTY, marketValue);
            playName = replace(playName, WHICH_PENALTY2, marketValue);
            playName = replace(playName, WHICH_SCORE, marketValue);
            playName = replace(playName, WHICH_SCORE2, marketValue);
            playName = replace(playName, WHICH_BOOKING, marketValue);
            playName = replace(playName, WHICH_BOOKING2, marketValue);
            playName = replace(playName, WHICH_PERIOD2, marketValue);
            playName = replace(playName, WHICH_PERIOD3, marketValue);
            playName = replace(playName, POINT, marketValue);
            playName = replace(playName, POINT2, marketValue);

        }

        return playName;
    }


    public static String replace(String playName, String replaceName, String text) {
        if (playName.contains(replaceName)) {
            playName = playName.replace(replaceName, text);
        }
        return playName;
    }

    public static String replaceFromAndTo(String from, String to, String playName) {
        if (NumberUtils.isNumber(from)) {
            int fromNum = Double.valueOf(from).intValue();
            fromNum = fromNum - 1;
            from = String.format("%02d", fromNum) + ":00";
        }
        if (NumberUtils.isNumber(to)) {
            int toNum = Double.valueOf(to).intValue();
            toNum = toNum - 1;
            to = String.format("%02d", toNum) + ":59";
        }

        //开场，中场、完赛因为特殊处理
        if("00:00".equals(from)){
            from = "kick off";
        }

        if("45:00".equals(from)){
            from = "2H";
        }

        if("44:59".equals(to)){
            to = "half time";
        }

        if("89:59".equals(to)){
            to = "full time";
        }

        playName = replace(playName, FROM, from);

        playName = replace(playName, TO, to);

        return playName;
    }
}
