package com.panda.sport.sdk.common;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OddsScope {

    /**
     * 早盘、冠军盘赔率区间  非滚球
     */
    static List<OddsScopeDeatil> list = new ArrayList();

    /**
     * 滚球赔率区间
     */
    static List<OddsScopeDeatil> livelist = new ArrayList();

    static {
        OddsScopeDeatil preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.01"));
        preDeatil.setEnd(new BigDecimal("1.25"));
        list.add(preDeatil);

        preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.26"));
        preDeatil.setEnd(new BigDecimal("1.39"));
        list.add(preDeatil);

        preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.40"));
        preDeatil.setEnd(new BigDecimal("1.59"));
        list.add(preDeatil);

        preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.60"));
        preDeatil.setEnd(new BigDecimal("1.79"));
        list.add(preDeatil);

        preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.80"));
        preDeatil.setEnd(new BigDecimal("1.85"));
        list.add(preDeatil);

        preDeatil = new OddsScopeDeatil();
        preDeatil.setStart(new BigDecimal("1.86"));
        preDeatil.setEnd(new BigDecimal("2.00"));
        list.add(preDeatil);
    }

    static {
        OddsScopeDeatil liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.01"));
        liveDeatil.setEnd(new BigDecimal("1.05"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.06"));
        liveDeatil.setEnd(new BigDecimal("1.25"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.26"));
        liveDeatil.setEnd(new BigDecimal("1.39"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.40"));
        liveDeatil.setEnd(new BigDecimal("1.60"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.61"));
        liveDeatil.setEnd(new BigDecimal("1.85"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.86"));
        liveDeatil.setEnd(new BigDecimal("1.88"));
        livelist.add(liveDeatil);

        liveDeatil = new OddsScopeDeatil();
        liveDeatil.setStart(new BigDecimal("1.89"));
        liveDeatil.setEnd(new BigDecimal("2.00"));
        livelist.add(liveDeatil);
    }

    //1.01-1.19 1.20-1.39 1.40-1.59 1.60-1.79 1.80-1.85 1.86-2.00
    public static String getScope(String oddsValue,String matchType) {
        BigDecimal odd = new BigDecimal(oddsValue);

        if (matchType.equals("0")) {
            for (OddsScopeDeatil deatil : livelist) {
                if (odd.compareTo(deatil.getStart()) >= 0 && odd.compareTo(deatil.getEnd()) <= 0) {
                    return deatil.getStart() + "-" + deatil.getEnd();
                }
            }
        }

        if (matchType.equals("1")) {
            for (OddsScopeDeatil deatil : list) {
                if (odd.compareTo(deatil.getStart()) >= 0 && odd.compareTo(deatil.getEnd()) <= 0) {
                    return deatil.getStart() + "-" + deatil.getEnd();
                }
            }
        }

        return "---";
    }

    @Data
    public static class OddsScopeDeatil {
        BigDecimal start;
        BigDecimal end;
    }


}
