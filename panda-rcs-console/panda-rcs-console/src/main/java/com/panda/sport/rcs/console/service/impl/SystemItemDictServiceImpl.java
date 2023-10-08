package com.panda.sport.rcs.console.service.impl;

import com.github.pagehelper.PageHelper;
import com.panda.sport.rcs.console.dao.SystemItemDictMapper;
import com.panda.sport.rcs.console.pojo.SystemItemDict;
import com.panda.sport.rcs.console.response.PageDataResult;
import com.panda.sport.rcs.console.service.SystemItemDictService;
import com.panda.sport.rcs.console.vo.SystemItemDictVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: jstyChandler
 * @Package: com.panda.sport.rcs.console.service.impl
 * @ClassName: SystemItemDictServiceImpl
 * @Description: TODO
 * @Date: 2023/3/14 20:33
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class SystemItemDictServiceImpl implements SystemItemDictService {

    private static final Logger logger = LoggerFactory.getLogger(SystemItemDictServiceImpl.class);

    @Resource
    private SystemItemDictMapper systemItemDictMapper;

    @Override
    public PageDataResult getDictPage(SystemItemDictVo systemItemDictVo) {
        PageDataResult pageDataResult = new PageDataResult();
        PageHelper.startPage(systemItemDictVo.getPageNum(), systemItemDictVo.getPageSize());
        List<SystemItemDict> itemDictList = systemItemDictMapper.selectSystemItemDictList(systemItemDictVo);
        pageDataResult.setList(itemDictList);
        return pageDataResult;
    }

    @Override
    public Map<String, Object> delDict(Long id) {
        systemItemDictMapper.deleteSystemItemDictById(id);
        Map<String, Object> msg = new HashMap<>();
        msg.put("code",1);
        msg.put("msg","删除字典成功");
        return msg;
    }

    @Override
    public Map<String, Object> editDict(SystemItemDictVo systemItemDictVo) {
        SystemItemDict systemItemDict = new SystemItemDict();
        BeanUtils.copyProperties(systemItemDictVo, systemItemDict);
        systemItemDict.setModifyTime(System.currentTimeMillis());
        systemItemDictMapper.updateSystemItemDictById(systemItemDict);
        Map<String, Object> msg = new HashMap<>();
        msg.put("code",1);
        msg.put("msg","编辑字典成功");
        return msg;
    }

    @Override
    public Map<String, Object> addDict(SystemItemDictVo systemItemDictVo) {
        SystemItemDict systemItemDict = new SystemItemDict();
        BeanUtils.copyProperties(systemItemDictVo, systemItemDict);
        systemItemDict.setModifyTime(System.currentTimeMillis());
        systemItemDictMapper.insertSystemItemDict(systemItemDict);
        Map<String, Object> msg = new HashMap<>();
        msg.put("code",1);
        msg.put("msg","新增字典成功");
        return msg;
    }
}
