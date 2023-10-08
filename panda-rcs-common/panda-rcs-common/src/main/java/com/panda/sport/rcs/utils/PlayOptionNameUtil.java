package com.panda.sport.rcs.utils;

import com.panda.sport.rcs.common.MatchConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author :  Sean
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.utils
 * @Description :  TODO
 * @Date: 2020-02-22 14:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class PlayOptionNameUtil {

    public static String assemblyMarketValue(String matchInfo, String playOptions,String playName,String playOptionName,
                                         String marketValue, String optionValue, Pattern pattern) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(playOptionName)) playOptionName = "";
        String home = "";
        String away = "";
        Matcher m;

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(matchInfo)) {
            home =  matchInfo.split("VS")[0].trim();
            away = matchInfo.split("VS")[1].trim();
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(optionValue) && optionValue.contains(MatchConstant.TITLE_SHOW_NAME_COMPETITOR1_AHCP)) {
            optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_COMPETITOR1_AHCP, home);
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(optionValue) && optionValue.contains(MatchConstant.TITLE_SHOW_NAME_COMPETITOR2_AHCP)) {
            optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_COMPETITOR2_AHCP, away);
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(optionValue) && optionValue.contains(MatchConstant.TITLE_SHOW_NAME_TOTAL)) {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(marketValue)) {
                if (marketValue.contains("大") || marketValue.contains("小")) {
                    marketValue = marketValue.replace("大 ", "");
                    marketValue = marketValue.replace("小 ", "");
                }
            }
            optionValue = optionValue.replace(MatchConstant.TITLE_SHOW_NAME_TOTAL, "") + marketValue;
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(optionValue) && optionValue.contains(MatchConstant.HCP)) {
            m = pattern.matcher(optionValue);
            if (optionValue.contains("+")) {
                optionValue = m.replaceAll("").trim().replace("+", "");
            } else {
                optionValue = m.replaceAll("").trim().replace("-", "");
            }
            optionValue = optionValue.replaceAll(MatchConstant.HCP, "");

        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(playName)) {
            if (playName.contains("让") || playName.contains("净胜")){
            	if(!playOptions.contains("Other")) {
            		optionValue += playOptionName;
            	}
            }
        }

        if (StringUtils.isEmpty(optionValue) || "null".equals(optionValue)) {
            optionValue = playOptions;
        }
        if (optionValue.contains("null") || optionValue.contains(":") || optionValue.contains("Other")) {
            optionValue = optionValue.replaceAll("null", "");
            optionValue = optionValue.replaceAll(":", "-");
            optionValue = optionValue.replaceAll("Other", "其他");

        }
        return optionValue;
    }
    
    public static void main(String[] args) {
    	String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    	Pattern p = Pattern.compile(regEx);
//    	PlayOptionNameUtil.assemblyMarketValue(item.getMatchInfo(),item.getPlayOptions(),item.getPlayName(),item.getPlayOptionsName(),item.getMarketValue(),optionValue,p);
    	String str = PlayOptionNameUtil.assemblyMarketValue("Belyye Rozy VS Sinie Ptitsy", 
    			"2", "让球盘", "+0/0.5", "-0/0.5", "{$competitor1} ({+hcp})", p);
    	System.out.println(str);
	}
}
