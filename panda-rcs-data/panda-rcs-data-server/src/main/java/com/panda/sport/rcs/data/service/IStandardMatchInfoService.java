package com.panda.sport.rcs.data.service;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.merge.dto.message.MatchEventInfoMessage;
import com.panda.sport.rcs.data.mqSerializaBean.StandardMatchMarketMessageDTO;
import com.panda.sport.rcs.pojo.StandardMatchInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2019-09-26
 */
public interface IStandardMatchInfoService extends IService<StandardMatchInfo> {
    /**
     * @MethodName:
     * @Description: 得到最后的插入时间
     * @Param:
     * @Return:
     * @Author: Vector
     * @Date: 2019/9/30
     **/
    Long getLastCrtTime();

    int updateBatch(List<StandardMatchInfo> list);

    int batchInsertOrUpdate(List<StandardMatchInfo> standardMatchInfos);

    List<StandardMatchInfo> listByListIds(ArrayList<Long> matchInfoDataLongs);

    int updateOperateMatchStatus(StandardMatchMarketMessageDTO data);

    /**
     * @param data
     * @param channel  1:事件 2：统计uof
     * @param linkId
     * @return
     */
    int updateMatchEventParam(MatchEventInfoMessage data, int channel, String linkId);

    int updateMatchStatus(StandardMatchInfo data, String s);

    /**
     * 批量更新早盘赛事是否是手动ManualRev字段信息
     * 1.matchIds里表示支持，没在matchIds里的表示不支持
     * 2.方法逻辑，先将ManualRev修改为0（不支持），再将支持的赛事更新为1（支持）
     * @param matchIds 支持ManualRev赛事id
     */
    void updateMatchMauManualRevStatus(List<Long> matchIds);

}
