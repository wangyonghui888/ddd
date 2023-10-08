package com.panda.sport.rcs.pojo.odds;

import com.panda.sport.rcs.enums.SportIdEnum;
import com.panda.sport.rcs.enums.YesNoEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 跳赔lua脚本入参
 * @Author : Paca
 * @Date : 2021-02-05 18:55
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class JumpOddsLuaDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ARGV[1]：1-两项盘，2-三项盘
     */
    private Integer jumpType;
    /**
     * ARGV[2]：投注金额
     */
    private Long betAmount;
    /**
     * ARGV[3]：赔率
     */
    private Double odds;
    /**
     * ARGV[4]：跳赔一级限额
     */
    private Long jumpOddsOneLimit;
    /**
     * ARGV[5]：跳赔二级限额
     */
    private Long jumpOddsSecondLimit;
    /**
     * ARGV[6]：投注项类型
     */
    private String oddsType;
    /**
     * ARGV[7]：1-上盘，3-下盘
     */
    private Integer isHome;
    /**
     * ARGV[8]：累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
     */
    private Integer balanceOption;
    /**
     * ARGV[9]：跳分机制，0-累计/单枪跳分，1-累计差值跳分
     */
    private Integer oddChangeRule;
    /**
     * ARGV[10]：足球-盘口ID，篮球-位置ID
     */
    private String keySuffix;
    /**
     * ARGV[11]：赛事账务日
     */
    private String dateExpect;
    /**
     * ARGV[12]：赛种
     */
    private Integer sportId;
    /**
     * 是否开启跳赔，0-否，1-是
     */
    private Integer isOpenJumpOdds;
    /**
     * 是否倍数跳赔，0-否，1-是
     */
    private Integer isMultipleJumpOdds;
    /**
     * 累计跳盘限额/一级累计跳盘限额
     */
    private BigDecimal jumpMarketOneLimit;
    /**
     * 单枪跳盘限额/二级累计跳盘限额
     */
    private BigDecimal jumpMarketSecondLimit;
    /**
     * 是否开启跳盘，0-否，1-是
     */
    private Integer isOpenJumpMarket;
    /**
     * 是否倍数跳盘，0-否，1-是
     */
    private Integer isMultipleJumpMarket;

    public Long getJumpOddsOneLimit() {
        if (jumpOddsOneLimit == null) {
            return -1L;
        }
        return jumpOddsOneLimit;
    }

    public Long getJumpOddsSecondLimit() {
        if (jumpOddsSecondLimit == null) {
            return -1L;
        }
        return jumpOddsSecondLimit;
    }

    public Integer getIsHome() {
        if (isHome == null) {
            return -1;
        }
        return isHome;
    }

    public Integer getBalanceOption() {
        if (balanceOption == null) {
            return 0;
        }
        return balanceOption;
    }

    public Integer getOddChangeRule() {
        if (oddChangeRule == null) {
            return 1;
        }
        return oddChangeRule;
    }

    public BigDecimal getJumpMarketOneLimit() {
        if (jumpMarketOneLimit == null) {
            return BigDecimal.ONE.negate();
        }

        return jumpMarketOneLimit;
    }

    public BigDecimal getJumpMarketSecondLimit() {
        if (jumpMarketSecondLimit == null) {
            return BigDecimal.ONE.negate();
        }
        return jumpMarketSecondLimit;
    }

    public Integer getIsOpenJumpOdds() {
        if (isOpenJumpOdds == null) {
            return 1;
        }
        return isOpenJumpOdds;
    }

    public Integer getIsMultipleJumpOdds() {
        if (isMultipleJumpOdds == null) {
            return 1;
        }
        return isMultipleJumpOdds;
    }

    public Integer getIsOpenJumpMarket() {
        if (isOpenJumpMarket == null) {
            return 1;
        }
        return isOpenJumpMarket;
    }

    public Integer getIsMultipleJumpMarket() {
        if (isMultipleJumpMarket == null) {
            return 1;
        }
        return isMultipleJumpMarket;
    }

    public List<String> getLuaArgs() {
        List<String> args = new ArrayList<>(20);
        // ARGV[1]：1-两项盘，2-三项盘
        args.add(getJumpType().toString());
        // ARGV[2]：投注金额
        args.add(getBetAmount().toString());
        // ARGV[3]：欧赔赔率
        args.add(getOdds().toString());
        // ARGV[4]：跳赔一级限额
        args.add(getJumpOddsOneLimit().toString());
        // ARGV[5]：跳赔二级限额
        args.add(getJumpOddsSecondLimit().toString());
        // ARGV[6]：投注项类型
        args.add(getOddsType());
        // ARGV[7]：1-上盘，3-下盘
        args.add(getIsHome().toString());
        // ARGV[8]：累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
        args.add(getBalanceOption().toString());
        // ARGV[9]：跳分机制，0-累计/单枪跳分，1-累计差值跳分
        args.add(getOddChangeRule().toString());
        // ARGV[10]：足球-盘口ID，篮球-位置ID
        args.add(getKeySuffix());
        // ARGV[11]：赛事账务日
        args.add(getDateExpect());
        // ARGV[12]：赛种
        args.add(getSportId().toString());
        if (!SportIdEnum.isFootball(getSportId())) {
            // ARGV[13]：是否开启跳赔，0-否，1-是
            args.add(getIsOpenJumpOdds().toString());
            // ARGV[14]：是否倍数跳赔，0-否，1-是
            args.add(getIsMultipleJumpOdds().toString());
        } else {
            // 足球必开启跳赔
            args.add(YesNoEnum.Y.getValue().toString());
            // 足球没有倍数跳赔
            args.add(YesNoEnum.N.getValue().toString());
        }

        return args;
    }

    public List<String> getJumpMarketLuaArgs() {
        List<String> args = new ArrayList<>(20);
        // ARGV[1]：1-两项盘，2-三项盘
        args.add(getJumpType().toString());
        // ARGV[2]：投注金额
        args.add(getBetAmount().toString());
        // ARGV[3]：欧赔赔率
        args.add(getOdds().toString());
        // ARGV[4]：投注项类型
        args.add(getOddsType());
        // ARGV[5]：赛种
        args.add(getSportId().toString());
        // ARGV[6]：赛事账务日
        args.add(getDateExpect());
        // ARGV[7]：足球-盘口ID，篮球-位置ID
        args.add(getKeySuffix());
        // ARGV[8]：累计差额计算方式，0-投注额差值，1-投注/赔付混合差值
        args.add(getBalanceOption().toString());
        // ARGV[9]：跳分机制，0-累计/单枪跳分，1-累计差值跳分
        args.add(getOddChangeRule().toString());
        // ARGV[10]：累计跳盘限额/一级累计跳盘限额
        args.add(getJumpMarketOneLimit().toPlainString());
        // ARGV[11]：单枪跳盘限额/二级累计跳盘限额
        args.add(getJumpMarketSecondLimit().toPlainString());
        // ARGV[12]：是否开启跳盘，0-否，1-是
        args.add(getIsOpenJumpMarket().toString());
        // ARGV[13]：是否倍数跳盘，0-否，1-是
        args.add(getIsMultipleJumpMarket().toString());
        return args;
    }
}
