package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.beust.jcommander.internal.Lists;
import com.panda.sport.rcs.mapper.RcsMatchUserMemoRefMapper;
import com.panda.sport.rcs.pojo.RcsMatchUserMemoRef;
import com.panda.sport.rcs.task.wrapper.RcsMatchUserMemoRefService;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据字典接口
 *
 * @author riben
 * @since 2021-02-10
 */
@Service
public class RcsMatchUserMemoRefServiceImpl extends ServiceImpl<RcsMatchUserMemoRefMapper, RcsMatchUserMemoRef> implements RcsMatchUserMemoRefService {


    @Override
    public Map<String, List<Long>> getTradeRemindMatchMemos() {
        List<RcsMatchUserMemoRef> needRemindMemos = baseMapper.getNeedRemindMemos();
        if(CollectionUtils.isEmpty(needRemindMemos)) return null;
        //获取需要发送备忘录的赛事Id
        List<Long> unreadMatchIds = needRemindMemos.stream().map(RcsMatchUserMemoRef :: getStandardMatchId).distinct().collect(Collectors.toList());
        List<RcsMatchUserMemoRef> readMemoRefInfo = baseMapper.getReadMemoRefInfo(unreadMatchIds);
        if(CollectionUtils.isEmpty(readMemoRefInfo)){
            readMemoRefInfo = Lists.newArrayList();
        }

        Map<String,List<RcsMatchUserMemoRef>> readTraderMemos = readMemoRefInfo.stream().collect(Collectors.groupingBy(RcsMatchUserMemoRef :: getMemoId));
        Integer userNumber =  needRemindMemos.stream().map(RcsMatchUserMemoRef :: getTraderId).distinct().collect(Collectors.toList()).size();
        //遍历已读的操盘手对应备忘录关系记录集合。并判断应备忘录关系记录条数是否等于当前用户数量，若一样则表示当前的备忘录已全部阅读
        Map<String,List<RcsMatchUserMemoRef>> needRemindTraderMemos = needRemindMemos.stream().collect(Collectors.groupingBy(RcsMatchUserMemoRef :: getMemoId));
        readTraderMemos.forEach((memoId, readMemos) -> {
            if(readMemos.size() >= userNumber){
                needRemindTraderMemos.remove(memoId);
            }else{
                List<RcsMatchUserMemoRef> tempNeedRemindMemos = needRemindTraderMemos.get(memoId);
                List<String> readTraderIds = readMemos.stream().map(RcsMatchUserMemoRef :: getTraderId).collect(Collectors.toList());
                tempNeedRemindMemos = tempNeedRemindMemos.stream().filter(e -> !readTraderIds.contains(e.getTraderId())).collect(Collectors.toList());
                needRemindTraderMemos.put(memoId, tempNeedRemindMemos);
            }
        });

        Map<String,List<Long>> traderMatchIds = new HashMap<String,List<Long>>();
        needRemindTraderMemos.forEach((memoId, tempNeedRemindMemos) -> {
            for(RcsMatchUserMemoRef refInfo : tempNeedRemindMemos){
                List<Long> matchIds = traderMatchIds.get(refInfo.getTraderId());
                if(CollectionUtils.isEmpty(matchIds)){
                    matchIds = new ArrayList<Long>();
                    matchIds.add(refInfo.getStandardMatchId());
                    traderMatchIds.put(refInfo.getTraderId(), matchIds);
                }else{
                    if(matchIds.contains(refInfo.getStandardMatchId())){
                        continue;
                    }
                    matchIds.add(refInfo.getStandardMatchId());
                }
            }
        });
        return traderMatchIds;
    }

}
