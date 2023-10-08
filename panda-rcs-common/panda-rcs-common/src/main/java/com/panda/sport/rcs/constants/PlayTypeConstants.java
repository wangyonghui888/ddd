package com.panda.sport.rcs.constants;

import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.constants
 * @Description :  TODO
 * @Date: 2019-10-24 14:15
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class PlayTypeConstants {
    /**
     * @Description 有盘口值的玩法
     **/
    public static final int[] HAVE_MARKET_VALUE = new int[]{2, 3, 4, 10, 11, 13, 18, 19, 26, 33, 34};
    /**
     * @Description 两项盘 namerxpreNameExpressionValue 相反
     **/
    public static final List<Long> TWO_MARKET_ODDS = new LinkedList<Long>() {{
        add(4L);
        add(19L);
        add(33L);
    }};

    /**
     * @return int[] @Description //可以用数据库操作 @Param [sportId, playType] @Author kimi @Date 2020/2/22
     */
    public static int[] get(long sportId, long playType) {
        //足球
        if (sportId == 1) {
            if (playType == 1) {
                return new int[]{19, 18, 17, 42, 20, 70};
            } else if (playType == 2) {
                return new int[]{26, 25, 75, 74, 72, 143};
            } else if (playType == 3) {
                return new int[]{4, 2, 1, 15, 6, 7, 104};
            } else if (playType == 4) {
                return new int[]{32, 33, 34};
            }
        }//篮球
        else if (sportId == 2) {
            if (playType == 3) {
                return new int[]{39, 38, 37, 40};
            } else if (playType == 13) {
                return new int[]{44, 45, 46, 47, 50, 51, 52, 53, 56, 57, 58, 59, 62, 63, 64, 65};
            } else if (playType == 1) {
                return new int[]{19, 18, 17, 42};
            } else if (playType == 2) {
                return new int[]{143, 26, 25, 75};
            }
        }//网球
        else if (sportId == 5) {
            if (playType == 3) {
                return new int[]{155, 169, 153, 160};
            } else if (playType == 14) {
                return new int[]{163, 164, 162, 165};
            } else if (playType == 17) {
                return new int[]{168};
            }
        }
        //乒乓球
        else if (sportId == 8) {
            if (playType == 3) {
                return new int[]{153, 173, 172};
            } else if (playType == 17) {
                return new int[]{175, 176, 177, 178};
            }
        }
        //羽毛球
        else if (sportId == 10) {
            if (playType == 3) {
                return new int[]{153, 173, 172};
            } else if (playType == 17) {
                return new int[]{175, 176, 177, 178, 179};
            }
        }
        //斯诺克
        else if (sportId == 7) {
            if (playType == 3) {
                return new int[]{181, 182, 183, 153};
            } else if (playType == 17) {
                return new int[]{185, 186, 184, 187};
            }
        }
        //棒球
        else if (sportId == 3) {
            return null;
        }
        return null;
    }


    public static final List<Integer> FULL_COURT_PLAYS = Arrays.asList(1, 4, 2, 15, 17, 19, 18, 42, 111, 113, 114, 118, 121, 122);
    public static final List<Integer> HALF_COURT_PLAYS = Arrays.asList(1, 4, 2, 15, 111, 113, 114, 118);

    public static final List<Long> OTHER_CATEGORY_ALL = Arrays.asList(6L, 28L, 12L, 2L, 3L, 27L, 16L, 14L, 68L, 8L, 9L, 104L, 13L, 7L, 31L, 24L, 30L, 43L, 29L, 69L, 21L, 22L, 23L, 341L, 34L, 32L, 33L, 25L);
}
