package com.panda.sport.rcs.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsBatchChangeDataSourceRecordMapper;
import com.panda.sport.rcs.pojo.RcsBatchChangeDataSourceRecord;
import com.panda.sport.rcs.vo.PreAllMarketDataSourceVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 批量切换数据源记录
 * magic
 * 2023.4.19
 */
@Slf4j
@Service
public class RcsBatchChangeDataSourceRecordService extends ServiceImpl<RcsBatchChangeDataSourceRecordMapper, RcsBatchChangeDataSourceRecord> {


    /**
     * 保存更新批量切换数据记录
     *
     * @param list
     * @return
     */
    public void batchSave(List<RcsBatchChangeDataSourceRecord> list) {
        baseMapper.batchSave(list);
    }

    /**
     * 获取最新几天的数据
     * <p>
     * 切换
     * Before              After
     * LS                   OA
     * old:LS              new:OA
     * <p>
     * 恢复
     * OA                   LS
     * old:after           new:before
     *
     * @param maxDay
     * @return
     */
    public List<RcsBatchChangeDataSourceRecord> findLastList(int maxDay, PreAllMarketDataSourceVo preAllMarketDataSourceVo) {
        LocalDateTime maxDayBefore = LocalDateTime.now().minusDays(maxDay);
        Date date = Date.from(maxDayBefore.atZone(ZoneId.systemDefault()).toInstant());
        return list(new LambdaQueryWrapper<RcsBatchChangeDataSourceRecord>()
                .gt(RcsBatchChangeDataSourceRecord::getCreateTime, date)
                .eq(RcsBatchChangeDataSourceRecord::getNewDataSourceCode, preAllMarketDataSourceVo.getBefore())
                .eq(RcsBatchChangeDataSourceRecord::getOldDataSourceCode, preAllMarketDataSourceVo.getAfter()));
    }
}
