package com.panda.sport.rcs.pojo.vo.api.response;


import com.panda.sport.rcs.pojo.vo.predict.RcsPredictForecastSnapshot;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ForecastSnapshotResVo {

    private String snapshotTime;

    private List<RcsPredictForecastSnapshot> snapshotList;
}
