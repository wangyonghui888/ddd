package com.panda.sport.rcs.mongo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author :  enzo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :
 * @Date: 2020-07-15 19:15
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@Accessors(chain = true)
@Document(collection = "category_collection")
public class CategoryCollection {

    @Field(value = "id")
    /**
     *玩法集ID
     */
    private Long id;
    /**
     * 赛种
     */
    private Long sportId;
    /**
     * 标准联赛ID
     */
    private Long matchId;
    /**
     * 玩法集自动手动
     */
    private Integer tradeType;
    /**
     * 玩法集状态
     */
    private Integer status;
    /**
     * 更新時間
     */
    private String updateTime;
    /**
     * 玩法集下的所有玩法ID
     */
    private List<Long> categoryIds;

}
