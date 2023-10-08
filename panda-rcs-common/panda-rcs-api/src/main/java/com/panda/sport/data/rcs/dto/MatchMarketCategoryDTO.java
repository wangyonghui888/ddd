package com.panda.sport.data.rcs.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Data
public class MatchMarketCategoryDTO extends MatchMarketBaseDTO {
    /**
     * 赛事ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long matchId;

    /**
     * 玩法类型
     */
    private String type;

    /**
     * 盘口
     */
    private List<MatchMarketDTO> matchMarketVoList;


    /**
     * 盘口对象
     */
    @Data
    public static class MatchMarketDTO extends MatchMarketBaseDTO {
        /**
         * 盘口赔率
         */
        private List<MatchMarketOddsFieldDTO> oddsFieldsList;
        /**
         * 玩法ID
         */
        @JSONField(serializeUsing = LongToStringSerializer.class)
        @JsonSerialize(using = ToStringSerializer.class)
        private Long marketCategoryId;
        /**
         * 盘口状态0-5. 0:active, 1:suspended, 2:deactivated, 3:settled, 4:cancelled, 5:handedOver
         */
        private Integer status;

    }

    /**
     * 赔率对象
     */
    @Data
    public static class MatchMarketOddsFieldDTO extends MatchMarketBaseDTO {

        /**
         * 投注项名称中包含的表达式的值
         */
        private String nameExpressionValue;

        /**
         * 投注给哪一方：T1主队，T2客队
         */
        private String targetSide;

        /**
         * 赔率显示值
         */
        private String fieldOddsValue;

        /**
         * 赔率原始值
         */
        private transient Integer fieldOddsOriginValue;

        /**
         * 投注项类型
         */
        private String oddsType;

        /**
         * 降级后的欧赔数据
         */
        private String nextLevelOddsValue;

        /**
         * 当前投注项是否被激活.1激活; 0未激活(锁盘)
         */
        private Integer active;
    }


}


