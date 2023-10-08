package com.panda.sport.rcs.credit.enums;

import com.google.common.collect.Lists;
import com.panda.sport.rcs.enums.SportIdEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用玩法分类
 * @Author : Paca
 * @Date : 2021-05-05 16:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface PlayClassify {

    @Getter
    @AllArgsConstructor
    enum Football implements PlayClassify {
        SINGLE_PLAY(0, new Integer[]{4, 2, 1, 114, 122, 18, 19, 15, 128, 127, 126}, "单个玩法分一组"),
        CORRECT_SCORE(100001, new Integer[]{7, 20, 74, 103}, "波胆玩法"),
        OVERTIME_OTHER(100002, new Integer[]{129, 130, 234, 235, 236, 330, 331, 332}, "加时赛其他玩法"),
        PENALTY_SHOOTOUT(100003, new Integer[]{132, 133, 134, 237, 238, 239, 240, 241, 333, 334, 335}, "点球大战"),
        OTHER(-1, new Integer[]{}, "其它玩法");

        private Integer classType;
        private Integer[] playIdArray;
        private String name;

        public List<Integer> getPlayIds() {
            return Lists.newArrayList(this.getPlayIdArray());
        }

        public static Integer getPlayClassify(Integer playId) {
            for (Football football : Football.values()) {
                if (football.getPlayIds().contains(playId)) {
                    if (football.getClassType().equals(SINGLE_PLAY.getClassType())) {
                        return playId;
                    } else {
                        return football.getClassType();
                    }
                }
            }
            return OTHER.getClassType();
        }
    }

    @Getter
    @AllArgsConstructor
    enum Basketball implements PlayClassify {
        SINGLE_PLAY(0, new Integer[]{39, 38, 37, 40}, "单个玩法分一组"),
        Halftime_TOTAL(200001, new Integer[]{18, 26}, "半场大小"),
        Halftime_HANDICAP(200002, new Integer[]{19, 143}, "半场让分"),
        section_HANDICAP(200003, new Integer[]{46, 52, 58, 64}, "单节让分"),
        section_TOTAL(200004, new Integer[]{45, 51, 57, 63}, "单节大小"),
        OTHER(-1, new Integer[]{}, "其它玩法");

        private Integer classType;
        private Integer[] playIdArray;
        private String name;

        public List<Integer> getPlayIds() {
            return Lists.newArrayList(this.getPlayIdArray());
        }

        public static Integer getPlayClassify(Integer playId) {
            for (Basketball basketball : Basketball.values()) {
                if (basketball.getPlayIds().contains(playId)) {
                    if (basketball.getClassType().equals(SINGLE_PLAY.getClassType())) {
                        return playId;
                    } else {
                        return basketball.getClassType();
                    }
                }
            }
            return OTHER.getClassType();
        }
    }

    @Getter
    @AllArgsConstructor
    enum Tennis implements PlayClassify {
        SINGLE_PLAY(0, new Integer[]{154, 155, 153, 162, 163, 202, 164}, "单个玩法分一组"),
        OTHER(-1, new Integer[]{}, "其它玩法");

        private Integer classType;
        private Integer[] playIdArray;
        private String name;

        public List<Integer> getPlayIds() {
            return Lists.newArrayList(this.getPlayIdArray());
        }

        public static Integer getPlayClassify(Integer playId) {
            for (Tennis tennis : Tennis.values()) {
                if (tennis.getPlayIds().contains(playId)) {
                    if (tennis.getClassType().equals(SINGLE_PLAY.getClassType())) {
                        return playId;
                    } else {
                        return tennis.getClassType();
                    }
                }
            }
            return OTHER.getClassType();
        }
    }

    @Getter
    @AllArgsConstructor
    enum Other implements PlayClassify {
        WIN_ALONE(1, new Integer[]{153, 175, 1, 184, 162, 242, 273, 275, 283, 5, 3, 259, 261, 17, 37, 44, 50, 56, 62}, "独赢"),
        HANDICAP(2, new Integer[]{172, 176, 181, 185, 253, 243, 249, 280, 4, 268, 294, 19, 39, 46, 52, 58, 64}, "让分"),
        TOTAL(3, new Integer[]{173, 177, 182, 186, 254, 244, 245, 246, 250, 251, 252, 276, 281, 282, 284, 285, 286, 287, 288, 289, 290, 291, 292, 2, 257, 258, 262, 263, 264, 295, 18, 38, 198, 199, 87, 97, 45, 51, 57, 63, 305}, "大小"),
        ODD_EVEN(4, new Integer[]{178, 183, 187, 255, 247, 15, 42, 40}, "单双"),
        OTHER(-1, new Integer[]{}, "其它玩法");

        private Integer classType;
        private Integer[] playIdArray;
        private String name;

        public List<Integer> getPlayIds() {
            return Lists.newArrayList(this.getPlayIdArray());
        }

        public static Integer getPlayClassify(Integer playId) {
            for (Other other : Other.values()) {
                if (other.getPlayIds().contains(playId)) {
                    return other.getClassType();
                }
            }
            return OTHER.getClassType();
        }
    }

    static Integer getPlayClassify(Integer sportId, Integer playId) {
        if (SportIdEnum.isFootball(sportId)) {
            return Football.getPlayClassify(playId);
        }
        if (SportIdEnum.isBasketball(sportId)) {
            return Basketball.getPlayClassify(playId);
        }
        if (SportIdEnum.isTennis(sportId)) {
            return Tennis.getPlayClassify(playId);
        }
        return Other.getPlayClassify(playId);
    }
}
