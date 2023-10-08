package com.panda.sport.rcs.mgr.utils;

import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.mgr.utils
 * @Description :  TODO
 * @Date: 2020-07-18 15:52
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
    public static int[]get(long sportId) {
        //足球
        if (sportId == 1) {
            return new int[]{19, 18, 17, 42, 20, 70,26, 25, 75, 74, 72, 143,4, 2, 1, 15, 6, 7, 104,32, 33, 34,-1};
        }
        //篮球
        else if (sportId == 2) {
            return new int[]{39, 38, 37, 40,45, 46, 47, 48, 51, 52, 53, 54, 57, 58, 59, 60, 63, 64, 65, 66,19, 18, 43, 42,143, 26, 142, 75,-1};
        }
        //棒球
        else if (sportId == 3) {
            return new int[]{242, 243, 244,247,249, 250,-1};
        }
        //冰球
        else if (sportId == 4) {
            return new int[]{1, 4, 2,204,267, 261, 268,262,-1};
        }
        //网球
        else if (sportId == 5) {
            return new int[]{153, 154, 155, 169,202, 160, 171, 204,166,170,162,163,164,165,-1};
        }
        //美式足球
        else if (sportId == 6) {
            return new int[]{37, 39, 38,40,17, 19, 18,42,-1};
        }
        //斯诺克
        else if (sportId == 7) {
            return new int[]{153,181, 182, 183, 204,184, 185, 186, 187,-1};
        }
        //乒乓球
        else if (sportId == 8) {
            return new int[]{153, 172, 173,204,175, 176, 177, 178,-1};
        }
        //排球
        else if (sportId == 9) {
            return new int[]{153,172,173,204, 162, 253, 254,255,-1};
        }
        //羽毛球
        else if (sportId == 10) {
            return new int[]{153, 172, 173,204,175, 176, 177, 178, -1};
        }
        return  new int[]{-1};
    }

    /**
     * @return int[] @Description //可以用数据库操作 @Param [sportId, playType] @Author kimi @Date 2020/2/22
     */
    public static int[]get(long sportId, long playType) {
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
        }
        //篮球
        else if (sportId == 2) {
            if (playType == 3) {
                return new int[]{39, 38, 37, 40};
            } else if (playType == 13) {
                return new int[]{45, 46, 47, 48, 51, 52, 53, 54, 57, 58, 59, 60, 63, 64, 65, 66};
            } else if (playType == 1) {
                return new int[]{19, 18, 43, 42};
            } else if (playType == 2) {
                return new int[]{143, 26, 142, 75};
            }
        }
        //棒球
        else if (sportId == 3) {
            if (playType == 3) {
                return new int[]{242, 243, 244};
            } else if (playType == 1) {
                return new int[]{249, 250, 251,252};
            }
        }
        //冰球
        else if (sportId == 4) {
            if (playType == 3) {
                return new int[]{1, 4, 2};
            } else if (playType == 15) {
                return new int[]{261, 262, 268};
            }
        }
        //网球
        else if (sportId == 5) {
            if (playType == 3) {
                return new int[]{155, 169, 153, 160};
            } else if (playType == 14) {
                return new int[]{163, 164, 162, 165};
            } else if (playType == 17) {
                return new int[]{168};
            }
        }
        //美式足球
        else if (sportId == 6) {
            if (playType == 3) {
                return new int[]{37, 38, 39};
            } else if (playType == 1) {
                return new int[]{17, 18, 19,42};
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
        //乒乓球
        else if (sportId == 8) {
            if (playType == 3) {
                return new int[]{153, 173, 172};
            } else if (playType == 17) {
                return new int[]{175, 176, 177, 178};
            }
        }
        //排球
        else if (sportId == 9) {
            if (playType == 3) {
                return new int[]{153, 172, 173};
            } else if (playType == 17) {
                return new int[]{162, 253, 254,255,256};
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
        return null;
    }


    public static final List<Integer> FULL_COURT_PLAYS = Arrays.asList(1, 4, 2, 15, 17, 19, 18, 42, 111, 113, 114, 118, 121, 122);
    public static final List<Integer> HALF_COURT_PLAYS = Arrays.asList(1, 4, 2, 15, 111, 113, 114, 118);

    public static final List<Long> OTHER_CATEGORY_ALL = Arrays.asList(6L, 28L, 12L, 2L, 3L, 27L, 16L, 14L, 68L, 8L, 9L, 104L, 13L, 7L, 31L, 24L, 30L, 43L, 29L, 69L, 21L, 22L, 23L, 341L, 34L, 32L, 33L, 25L);
}

