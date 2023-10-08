package com.panda.sport.rcs.mongo;

import com.panda.sport.rcs.vo.I18nItemVo;
import com.panda.sport.rcs.vo.MarketLiveOddsQueryVo;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mongo
 * @Description :  TODO
 * @Date: 2019-11-12 16:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentMatchBean implements Serializable {

    @Id
    @Field(value = "id")
    private String id;

    /**
     * 联赛收藏状态
     */
    private boolean tournamentCollectStatus;
    /**
     * 大单数
     */
    private Long overLimitNum;

    /**
     * 当前联赛延迟用户数量
     */
    private Long delayUserNum;

    /**
     * 标准赛事联赛ID
     */
    private Long standardTournamentId;
    /**
     * 联赛 级别。 对应标准联赛表的联赛级别
     */
    private Integer tournamentLevel;
    /**
     * 保存多语言信息  key 语言类型    value 文本信息
     **/
    private List<I18nItemVo> tournamentNames;
    /**
     * 联赛名称及编码
     */
    private Long tournamentNameCode;
    private List<MatchMarketLiveBean> marketLiveBeanList;
    private List<MatchMarketLiveOddsVo> matchMarketLiveOddsVos;
}
