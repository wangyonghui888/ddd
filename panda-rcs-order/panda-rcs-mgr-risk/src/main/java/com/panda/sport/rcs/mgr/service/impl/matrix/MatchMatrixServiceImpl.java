package com.panda.sport.rcs.mgr.service.impl.matrix;

import com.panda.sport.data.rcs.api.matrix.MatchMatrixService;
import com.panda.sport.data.rcs.dto.matrix.MatrixBean;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.mgr.wrapper.ITOrderDetailService;
import com.panda.sport.rcs.vo.MatrixVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.service.impl.matrix
 * @Description :  赛事矩阵查询接口
 * @Date: 2019-11-06 11:19
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
@org.springframework.stereotype.Service
@Slf4j
public class MatchMatrixServiceImpl implements MatchMatrixService {
    /**
     * 矩阵查询
     */
    @Autowired
    private ITOrderDetailService orderDetailService;


    private MatrixBean[][] getMatrixBeans(MatrixVo[][] vos) {
        MatrixBean[][] beans = new MatrixBean[vos.length][vos[0].length];
        for (int i = 0; i < vos.length; i++) {
            for (int j = 0; j < vos[i].length; j++) {
                MatrixBean bean = new MatrixBean();
                bean.setValue(vos[i][j].getValue());
                bean.setLevel(vos[i][j].getLevel());
                beans[i][j] = bean;
            }
        }
        return beans;
    }



    /**
     *
     * @param item 参数（matchId 赛事ID，isSettlement： 1 未结算 2 已计算，matchType： 1 早盘 2 滚球盘）
     * @param matchStage 赛事阶段  1:比分全场矩阵；2:比分上半场矩阵
     * @param startTime 开始时间
     * @param endTime  结束时间
     * @param tenantIds 商户ID
     * @param playIds playId 玩法ID，
     * @param unit 单位 1 10 100 1000
     * @param size 矩阵大小 5*5 / 6*6
     * @return 赛事矩阵
     */
    @Override
    public MatrixBean[][] getMatrixByMatch(TOrderDetail item,String matchStage, Date startTime, Date endTime, List<Long> playIds, List<Long> tenantIds, Integer unit, Integer size) throws RpcException {
        MatrixVo[][] vo = null;
        if(matchStage.equals(2)) {
            vo = orderDetailService.getHalfMatrixByMatchId(item,
                                                            startTime,
                                                            endTime,
                                                            playIds,
                                                            tenantIds,
                                                            unit,
                                                            size);
        }else{
            vo = orderDetailService.queryMatrixByMatchId(tenantIds,
                                                            item.getMatchId(),
                                                            item.getMatchType(),
                                                            item.getIsSettlement(),
                                                            playIds,
                                                            unit,
                                                            size);
        }
        if (vo == null || vo.length == 0 || vo[0].length == 0) {
            throw new RpcException("没有相关矩阵相关信息：item=" + item.toString() + ":startTime=" + startTime + ";endTime=" + endTime + ";playIds=" + playIds + ";tenantId=" + tenantIds + ";unit=" + unit + ";size=" + size);
        }
        return getMatrixBeans(vo);
    }
}
