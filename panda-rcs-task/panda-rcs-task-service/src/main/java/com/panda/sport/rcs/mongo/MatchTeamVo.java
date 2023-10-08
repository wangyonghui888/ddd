package com.panda.sport.rcs.mongo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * @author :  Administrator
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2020-07-19 9:53
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchTeamVo {
    @Field(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Map<String, String> names;
    private Long nameCode;
    /**
     * 比赛中的作用：主客队或者其他
     */
    private String matchPosition;
    /**
     * 红牌数
     */
    private Integer redCardNum;
}
