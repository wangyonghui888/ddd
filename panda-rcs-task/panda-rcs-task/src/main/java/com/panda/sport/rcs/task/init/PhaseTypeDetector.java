package com.panda.sport.rcs.task.init;

import com.panda.sport.rcs.core.utils.JsonFormatUtils;
import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.init
 * @Description :  TODO
 * @Date: 2020-02-07 11:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

@Component
@Slf4j
public class PhaseTypeDetector {
    @Autowired
    private RcsCodeService rcsCodeService;

    public static List<RcsCode> phases = new ArrayList<>();

    public static List<Long> haifOthers = new ArrayList<>();

    public static List<Long> zeroFifteen = new ArrayList<>();

    @PostConstruct
    public void initPhaseType() {
        phases = rcsCodeService.selectRcsCods("play_phase");
        haifOthers = JsonFormatUtils.fromJsonArray(rcsCodeService.getValue("other_category", "2"), Long.class);
        zeroFifteen= JsonFormatUtils.fromJsonArray(rcsCodeService.getValue("other_category", "4"), Long.class);
    }

    public static Integer getPhaseType(Long categoryId) {
        Integer result = 0;
        RcsCode rcsCode = phases.stream().filter(line -> String.valueOf(categoryId).equals(line.getChildKey())).findFirst().orElse(null);
        if (null != rcsCode) {
            result = Integer.parseInt(rcsCode.getRemark());
        }
        return result;
    }

    public static Integer getColumnType(Long categoryId) {
        Integer result = 0;
        RcsCode rcsCode = phases.stream().filter(line -> String.valueOf(categoryId).equals(line.getChildKey())).findFirst().orElse(null);
        if (null != rcsCode) {
            result = Integer.parseInt(rcsCode.getValue());
        }
        return result;
    }

    public static List<Integer> getAllCategories() {
        List<Integer> collect = phases.stream().map(map -> Integer.parseInt(map.getChildKey())).collect(Collectors.toList());
        if (null != collect && collect.size() > 0) {
            return collect;
        }
        return null;
    }

    public static List<Long> getHalfCategories() {
        List<Long> collect = phases.stream().filter(filter -> Arrays.asList(2L, 4L).contains(Long.parseLong(filter.getRemark()))).
                map(map -> Long.parseLong(map.getChildKey())).collect(Collectors.toList());
        if (null != collect && collect.size() > 0) {
            collect.addAll(haifOthers);
            return collect;
        }
        return null;
    }

    public static List<Long> getRemoveHalfCategories() {
        List<Long> collect = phases.stream().filter(filter -> Arrays.asList(1L, 3L).contains(Long.parseLong(filter.getRemark()))).
                map(map -> Long.parseLong(map.getChildKey())).collect(Collectors.toList());
        if (null != collect && collect.size() > 0) {
            return collect;
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getHalfCategories());
    }
}
