package com.panda.sport.rcs.constants;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2019-10-24 15:02
 */
public enum MatchEventEnum {
    BetStop("betstop", "Betstop", 120, true,true),
    PossiblePenalty("possible_penalty", "疑似点球", 120, true, true),
    Corner("corner", "角球", 5, true, false),
    FreeKick("free_kick", "任意球", 8, true, false),
    DangerousAttack("dangerous_attack", "危险进攻", 120, true,true),
    PossibleRedCard("possible_red_card", "疑似红牌", 120, true,true),
    VAR("video_assistant_referee", "VAR", 120, true,true),
    RedCard("red_card", "红牌", 12, false, false),
    Goal("goal", "进球", 12, false, false),
    PossibleGoal("possible_goal", "疑似进球", 10, false,false),
    PenaltyAwarded("penalty_awarded", "点球", 12, false, false);

    private String code;
    private String name;
    private Integer waitTime;
    //是否初始化到危险球里面
    private boolean aBoolean;
    
    private boolean isValidate;

    MatchEventEnum(String code, String name, Integer waitTime, boolean aBoolean,boolean isValidate) {
        this.code = code;
        this.name = name;
        this.waitTime = waitTime;
        this.aBoolean = aBoolean;
        this.isValidate = isValidate;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getWaitTime() {
        return waitTime;
    }

    public boolean isValidate() {
		return isValidate;
	}

	public String[] getDangerousEvents(){
        return new String[]{BetStop.getCode(),PossiblePenalty.getCode(),
                Corner.getCode(),FreeKick.getCode(),
                DangerousAttack.getCode(),PossibleRedCard.getCode(),VAR.getCode()
        };
    }
    public String[] getInfluenceEvents(){
        return new String[]{RedCard.getCode(),Goal.getCode(),PenaltyAwarded.getCode(),PossibleGoal.getCode()};
    }

    public static String getNameByCode(String code) {
        for (MatchEventEnum matchEventEnum : MatchEventEnum.values()) {
            if (matchEventEnum.getCode().equals(code)) {
                return matchEventEnum.getName();
            }
        }
        return null;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }
}
