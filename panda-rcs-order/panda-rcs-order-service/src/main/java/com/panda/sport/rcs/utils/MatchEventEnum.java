package com.panda.sport.rcs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2019-10-24 15:02
 */
@Slf4j
public enum MatchEventEnum {
//	事件分类，1：进球拒单事件  2：危险延迟事件  3：安全事件
	//进球类拒单处理
	Goal("goal", "进球", -1, 1, "1"),
	RedCard("red_card", "红牌", -1, 1, "1"),
	YellowRedCard("yellow_red_card", "黄红牌",-1, 1, "1"),
	PossibleGoal("possible_goal", "疑似进球", -1, 1, "1"),
	StartPenalty("take_penalty", "开始点球", -1, 1, "1"),
	PenaltyAwarded("penalty_awarded", "点球", -1, 1, "1"),
//	Corner("corner", "角球", 5, false, false),//暂时不处理此事件
//	YellowCard("yellow_card", "黄牌", 5, false, false),//暂时不处理此事件
	
	//危险类
	Betstop("betstop", "封盘", 120, 2, "1"),
	DangerousAttack("dangerous_attack", "危险进攻", 120, 2, "1"),
	PossibleRedCard("possible_red_card", "可能红牌", 120, 2, "1"),
	PossiblePenalty("possible_penalty", "疑似点球", 120, 2, "1"),
	VideoAssistantReferee("video_assistant_referee", "视频辅助裁判", 120, 2, "1"),
	PossibleVideoAssistantReferee("possible_video_assistant_referee", "封盘", 120, 2, "1"),
	
	//安全类
	TemporaryInterrupt("temporary_interruption", "临时中断", 0, 3, "1"),
	GameOn("game_on", "继续比赛",  0, 3, "1"),
	SuspensionOver("suspension_over", "暂停结束",  0, 3, "1"),
	Suspension("suspension", "比赛暂停", 0, 3, "1"),
	Substitution("substitution", "换人", 0, 3, "1"),
	InjuryTime("injury_time", "伤停补时", 0, 3, "1"),
	Possession("possession", "控球权", 0, 3, "1"),
	FreeKickCount("free_kick_count", "任意球次数", 0, 3, "1"),
	GoalKickCount("goal_kick_count", "球门球次数", 0, 3, "1"),
	ThrowInCount("throw_in_count", "掷界外球次数", 0, 3, "1"),
	OffSideCount("off_side_count", "越位次数", 0, 3, "1"),
	CornerKickCount("corner_kick_count", "角球次数", 0, 3, "1"),
	ShotOnTargetCount("shot_on_target_count", "射正次数", 0, 3, "1"),
	ShotOffTargetCount("shot_off_target_count", "射偏次数", 0, 3, "1"),
	GoalKeeperSaveCount("goal_keeper_save_count", "守门员扑救数", 0, 3, "1"),
//	FreeKick("free_kick", "任意球", 0, 3, "1"),
	GoalKick("goal_kick", "球门球", 0, 3, "1"),
	ThrowIn("throw_in", "掷界外球", 0, 3, "1"),
	Offside("offside", "越位", 0, 3, "1"),
	ShotOnTarget("shot_on_target", "射正", 0, 3, "1"),
	ShotOffTarget("shot_off_target", "射偏", 0, 3, "1"),
//	GoalKeeperSave("goal_keeper_save", "守门员扑救", 0, 3, "1"),
	Injury("injury", "受伤", 0, 3, "1"),
	Attendance("attendance", "出席", 0, 3, "1"),
	PlayerBackFromInjury("player_back_from_injury", "受伤后回归", 0, 3, "1"),
	ShotsBlockedCounts("shots_blocked_counts", "被挡出射门次数", 0, 3, "1"),
	ShotBlocked("shot_blocked", "被挡出射门", 0, 3, "1"),
	PenaltyMissed("penalty_missed", "点球未进", 0, 3, "1"),
	betstart("betstart", "开盘", 0, 3, "1"),
	KickOffTeam("kick_off_team", "开球球队", 0, 3, "1"),
	MatchStatus("match_status", "比赛状态", 0, 3, "1"),
	FreeComment("free_comment", "赛事评论", 0, 3, "1"),
	CanceledCorner("canceled_corner", "取消角球", 0, 3, "1"),
	CanceledGoal("canceled_goal", "取消进球", 0, 3, "1"),
	MatchAboutToStart("match_about_to_start", "即将开赛", 0, 3, "1"),
	BallSafe("ball_safe", "己方半场控球", 0, 3, "1"),
	ManualTimeAdjustment("manual_time_adjustment", "手工调时", 0, 3, "1"),
	CanceledRedCard("canceled_red_card", "取消红牌", 0, 3, "1"),
	CanceledPenalty("canceled_penalty", "点球取消", 0, 3, "1"),
	PlayResumesAfterGoal("play_resumes_after_goal", "进球后继续比赛", 0, 3, "1"),
	CanceledYellowCard("canceled_yellow_card", "取消黄牌", 0, 3, "1"),
	PossibleFreeKick("possible_free_kick", "可能任意球", 0, 3, "1"),
	CoverageStatus("coverage_status", "报道状态", 0, 3, "1"),
	Attack("attack", "进攻", 0, 3, "1"),
	CanceledVideoAssistantReferee("canceled_video_assistant_referee", "视频辅助裁判取消", 0, 3, "1");


	
//    BetStop("betstop", "Betstop", 120, true,true),
//    PossiblePenalty("possible_penalty", "疑似点球", 120, true, true),
//    FreeKick("free_kick", "任意球", 8, true, false),
//    DangerousAttack("dangerous_attack", "危险进攻", 120, true,true),
//    PossibleRedCard("possible_red_card", "疑似红牌", 120, true,true),
//    VAR("video_assistant_referee", "VAR", 120, true,true);
    
    
    
    private String code;
    private String name;
    private Integer waitTime;
    //事件分类，1：进球拒单事件  2：危险延迟事件  3：安全事件
    private Integer eventType;
    
    //体育类型，用逗号分隔
    private String sportIds;
    
    private static Map<String, Map<String, MatchEventEnum>> allEventMap = new HashMap<String, Map<String,MatchEventEnum>>();
    
    static {
    	log.info("接拒单默认事件初始化开始...");
    	init();
    	log.info("接拒单默认事件初始化结束：allEventMap：{}",JSONObject.toJSONString(allEventMap));
    }
    
    private static boolean isInit = false;

    MatchEventEnum(String code, String name, Integer waitTime, Integer eventType,String sportIds) {
        this.code = code;
        this.name = name;
        this.waitTime = waitTime;
        this.eventType = eventType;
        this.sportIds = sportIds;
    }
    
    public String toString() {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("code", this.code);
    	map.put("name", this.name);
    	map.put("waitTime", this.waitTime);
    	map.put("eventType", this.eventType);
    	map.put("sportIds", this.sportIds);
		return JSONObject.toJSONString(map);
    }
    
    public static void main(String[] args) {
    	System.out.println(getEventEnum("1", "goal_keeper_save").toString());
	}
    
    private static void init() {
    	if(isInit) return;
    	
    	for(MatchEventEnum event : MatchEventEnum.values()) {
    		for(String sportId : event.getSportIds().split(",")) {
    			
    			Map<String, MatchEventEnum> eventMap = new HashMap<String, MatchEventEnum>();
    			if(!allEventMap.containsKey(sportId)) {
    				allEventMap.put(sportId, eventMap);
    			}
    			eventMap = allEventMap.get(sportId);
    			
    			eventMap.put(event.getCode(), event);
    		}
    	}
    	
    	isInit = true;
    }
    
    public static MatchEventEnum getEventEnum(String sportId , String eventCode) {
    	if(!allEventMap.containsKey(sportId)) return null;
    	
    	if(!allEventMap.get(sportId).containsKey(eventCode)) return null;
    	
    	return allEventMap.get(sportId).get(eventCode);
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public String getSportIds() {
		return sportIds;
	}

	public void setSportIds(String sportIds) {
		this.sportIds = sportIds;
	}
    
	
//    public String getCode() {
//        return code;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public Integer getWaitTime() {
//        return waitTime;
//    }
//
//    public boolean isValidate() {
//		return isValidate;
//	}

//	public Map<String, MatchEventEnum> getDangerousEvents(String sportId){
//        return new String[]{BetStop.getCode(),PossiblePenalty.getCode(),
//                Corner.getCode(),FreeKick.getCode(),
//                DangerousAttack.getCode(),PossibleRedCard.getCode(),VAR.getCode()
//        };
//    }
	
	private static Map<String, List<MatchEventEnum>> allDangerousEvents = null;
	
	public static List<MatchEventEnum> getAllDangerousEvents(String sportId){
		if(allDangerousEvents != null &&
				allDangerousEvents.containsKey(sportId)) return allDangerousEvents.get(sportId);
		
		if(!allEventMap.containsKey(sportId)) {
    		return null;
    	}

		List<MatchEventEnum> list = new ArrayList<MatchEventEnum>(); 
		for(String code : allEventMap.get(sportId).keySet()) {
			MatchEventEnum eventEnum = allEventMap.get(sportId).get(code);
			if("2".equals(String.valueOf(eventEnum.getEventType()))) {
				list.add(eventEnum);
			}
		}
		
		if(allDangerousEvents == null ) allDangerousEvents = new HashMap<String, List<MatchEventEnum>>();
		
		allDangerousEvents.put(sportId, list);
		
		return list;
	}
	
    public static MatchEventEnum getEvents(String sportId,String eventCode){
    	if(!allEventMap.containsKey(sportId)) {
    		return null;
    	}
    	
    	if(!allEventMap.get(sportId).containsKey(eventCode)) {
    		return null;
    	}
    	
    	return allEventMap.get(sportId).get(eventCode);
    }

//    public static String getNameByCode(String code) {
//        for (MatchEventEnum matchEventEnum : MatchEventEnum.values()) {
//            if (matchEventEnum.getCode().equals(code)) {
//                return matchEventEnum.getName();
//            }
//        }
//        return null;
//    }
//
//    public boolean isaBoolean() {
//        return aBoolean;
//    }
}
