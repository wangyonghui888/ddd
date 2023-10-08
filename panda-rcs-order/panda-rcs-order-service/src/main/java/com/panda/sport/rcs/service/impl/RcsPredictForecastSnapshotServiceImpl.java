package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.predict.RcsPredictForecastSnapshotMapper;
import com.panda.sport.rcs.pojo.vo.api.request.QueryForecastPlayReqVo;
import com.panda.sport.rcs.pojo.vo.api.response.ForecastSnapshotResVo;
import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastSnapshot;
import com.panda.sport.rcs.service.IRcsPredictForecastSnapshotService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RcsPredictForecastSnapshotServiceImpl extends ServiceImpl<RcsPredictForecastSnapshotMapper, RcsPredictForecastSnapshot>
        implements IRcsPredictForecastSnapshotService {

    @Override
    public List<ForecastSnapshotResVo> querySnapshot(QueryForecastPlayReqVo vo) {
        LambdaQueryWrapper<RcsPredictForecastSnapshot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!ObjectUtils.isEmpty(vo.getMatchId()), RcsPredictForecastSnapshot::getMatchId, vo.getMatchId());
        wrapper.eq(!ObjectUtils.isEmpty(vo.getPlayId()), RcsPredictForecastSnapshot::getPlayId, vo.getPlayId());
        wrapper.eq(RcsPredictForecastSnapshot::getDataType, 1);
        List<RcsPredictForecastSnapshot> rcsPredictForecastSnapshots = baseMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(rcsPredictForecastSnapshots)) {
            Map<Long, List<RcsPredictForecastSnapshot>> collect = rcsPredictForecastSnapshots.stream().collect(Collectors.groupingBy(RcsPredictForecastSnapshot::getSnapshotTime));
            List<ForecastSnapshotResVo> forecastSnapshotResVos = new ArrayList<>();
            collect.forEach((time, list) -> {
                ForecastSnapshotResVo forecastSnapshotResVo = new ForecastSnapshotResVo();
                forecastSnapshotResVo.setSnapshotTime(formatDate(time));
                RcsPredictForecastSnapshot rcsPredictForecastSnapshot = list.get(0);
                if (Arrays.asList(4L, 19L).contains(rcsPredictForecastSnapshot.getPlayId())) {
                    list.sort(Comparator.comparing(RcsPredictForecastSnapshot::getScore).reversed());
                }
                if (rcsPredictForecastSnapshot.getPlayId() == 2) {
                    list.sort(Comparator.comparing(RcsPredictForecastSnapshot::getScore));
                }
                forecastSnapshotResVo.setSnapshotList(list);
                forecastSnapshotResVos.add(forecastSnapshotResVo);
            });
            forecastSnapshotResVos.sort(Comparator.comparing(ForecastSnapshotResVo::getSnapshotTime).reversed());
            return forecastSnapshotResVos;

        }
        return null;
    }

    private String formatDate(Long date) {
        return LocalDateTime.ofInstant(new Date(date).toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
