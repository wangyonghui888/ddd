package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 限额参考值
 * @Param
 * @Author lithan
 * @Date 2020-12-1 15:38:31
 */
@Data
public class UserLimitReferenceResVo implements Serializable {

    private static final long serialVersionUID = 3312124506068991616L;

    /**
     * 用户串关限额
     */
    private Long userQuotaCrossLimit;

    /**
     * 单注赔付限额  单场培训限额
     */
    private List<UserReferenceLimitVo> list;

    @Data
    public class UserReferenceLimitVo implements Serializable {

        private static final long serialVersionUID = 3312124506068991696L;

        /**
         * 赛种 -1表示其他
         */
        private Long sportId;
        /**
         * 用户单注
         */
        private Long userSingleLimit;
        /**
         * 用户单场
         */
        private Long userMatchLimit;
    }

}

