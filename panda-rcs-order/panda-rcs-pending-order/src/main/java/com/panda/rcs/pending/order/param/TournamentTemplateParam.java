package com.panda.rcs.pending.order.param;

import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/2 18:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentTemplateParam extends RcsTournamentTemplate{
    /**
     * 玩法id
     */
    private Integer playId;

}
