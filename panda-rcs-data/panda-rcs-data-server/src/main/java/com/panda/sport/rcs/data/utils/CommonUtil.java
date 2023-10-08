package com.panda.sport.rcs.data.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用工具类
 */
@Slf4j
public class CommonUtil {


    /**
     * 判断字符串 null 或 ‘null’
     *
     * @param str
     * @return
     */
    public static boolean isBlankOrNull(String str) {
        return StringUtils.isBlank(str) || str.equals("null");
    }

    /**
     * 判断字符串不为 null 或 ‘null’
     *
     * @param str
     * @return
     */
    public static boolean isNotBlankAndNull(String str) {
        return StringUtils.isNotBlank(str) && !str.equals("null");
    }

    /**
     * 获取 linkId
     * @param message
     * @return
     */
    public static String getLinkId(String message) {
        try {
            String linkId = "";
            if (isBlankOrNull(message)) {
                return "";
            }
            JSONObject jsonObject = JSONObject.parseObject(message);
            linkId = jsonObject.getString("linkId");
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("globalId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchInfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardMatchInfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardMatchId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("msgId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchinfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchinfoid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardmatchinfoid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("linkid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("globalid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("msgid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = "";
            } else {
                linkId = "_" + linkId;
            }
            return linkId;
        } catch (JSONException e){
            log.info("非Json:{}", message);
        } catch (Exception e) {
            log.error("getLinkId报错{},{},{}", message, e.getMessage(), e);
        }
        return "";
    }



    public static void main(String[] args) {
     /*   boolean blankOrNull = isBlankOrNull("");
        boolean blankOrNull1 = isBlankOrNull("null");
        boolean blankOrNull2 = isBlankOrNull(null);
        boolean blankOrNull3 = isBlankOrNull("v");

        String linkId = getLinkId("");
        String linkId1 = getLinkId("{\"linkId\":\"vvvdvdvdv\"}");
        String linkId2 = getLinkId("{\"linkId\":222323}");
        String linkId3 = getLinkId("{\"linkid\":222323}");
        String linkId4 = getLinkId("{\"globalId\":\"vvvdvdvdv\"}");
        String linkId5 = getLinkId("{\"globalid\":222323}");
        String linkId7 = getLinkId("{\"linkId\":\"vvvdvdvdv\",\"linkId\":222323}");
        String linkId8 = getLinkId("{\"linkId\":222323,\"linkid\":222324,\"globalId\":\"vvvdvdvdv\",\"globalid\":222325}");
        String linkId9 = getLinkId("{\"globalId\":\"vvvdvdvdv\",\"globalid\":222325}");
        String linkId10 = getLinkId("{\"gloalId\":\"vvvdvdvdv\",\"glovbalid\":222325}");
        String linkId11 = getLinkId(null);
*/
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "AllPlay");
        getLinkId(map,null);
        System.out.println(1);
    }

    /**
     * 获取 linkId
     * @param message
     * @param keys
     */
    public static String getLinkId(Object message, String keys) {
        try {
            if(!isBlankOrNull(keys)){
                return keys;
            }
            String linkId = "";
            if (null==message) {
                return "";
            }
            JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(message));
            linkId = jsonObject.getString("linkId");
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("globalId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchInfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardMatchInfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardMatchId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("msgId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchinfoId");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("matchinfoid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("standardmatchinfoid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("linkid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("globalid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = jsonObject.getString("msgid");
            }
            if (isBlankOrNull(linkId)) {
                linkId = "";
            } else {
                linkId = "_" + linkId;
            }
            return linkId;
        } catch (JSONException e){
            log.info("getLinkId非Json:{}", message);
        } catch (Exception e) {
            log.error("getLinkId报错:{},{},{}", message, e.getMessage(), e);
        }
        return "";
    }
}
