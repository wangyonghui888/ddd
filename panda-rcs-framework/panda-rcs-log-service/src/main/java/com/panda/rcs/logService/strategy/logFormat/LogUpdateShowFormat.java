package com.panda.rcs.logService.strategy.logFormat;
import com.panda.rcs.logService.Enum.CategorySetIdEnum;
import com.panda.rcs.logService.Enum.MatchTypeEnum;
import com.panda.rcs.logService.Enum.OperateLogOneEnum;
import com.panda.rcs.logService.mapper.StandardMatchInfoMapper;
import com.panda.rcs.logService.strategy.LogAllBean;
import com.panda.rcs.logService.strategy.LogFormatStrategy;
import com.panda.rcs.logService.vo.StandardMatchInfo;
import com.panda.sport.rcs.log.format.RcsOperateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogUpdateShowFormat extends LogFormatStrategy {
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;
    @Override
    public RcsOperateLog formatLogBean(RcsOperateLog rcsOperateLog, LogAllBean bean) {
        Long typeVal = bean.getMatchId();
        StandardMatchInfo matchMarketLiveBean = standardMatchInfoMapper.selectById(typeVal);
        String matchManageId = null == matchMarketLiveBean ? typeVal.toString() : matchMarketLiveBean.getMatchManageId();
        rcsOperateLog.setExtObjectId(matchManageId);
        if(MatchTypeEnum.EARLY.getId().equals(matchMarketLiveBean.getMatchStatus())){
            rcsOperateLog.setOperatePageCode(18);
        }else {
            rcsOperateLog.setOperatePageCode(15);
        }
        rcsOperateLog.setOperatePageName(OperateLogOneEnum.Update_Show.name());
        rcsOperateLog.setBehavior("客户端开关");
        rcsOperateLog.setObjectId(bean.getCategorySetId()+"");
        rcsOperateLog.setObjectNameByObj(montageEnAndZsIs(bean.getTeamList(),bean.getMatchId()));
        rcsOperateLog.setPlayId(bean.getCategorySetId());
        rcsOperateLog.setMatchId(bean.getMatchId());
        rcsOperateLog.setExtObjectNameByObj(OperateLogOneEnum.NONE.getName());
        rcsOperateLog.setParameterName(setCategorySetId(bean.getCategorySetId()));
        rcsOperateLog.setBeforeValByObj(bean.getClientShow()==0?"开":"关");
        rcsOperateLog.setAfterValByObj(bean.getClientShow()==0?"关":"开");
        return rcsOperateLog;
    }
    public String setCategorySetId(Long categorySetId) {
        return CategorySetIdEnum.getValue(categorySetId);
    }

}
