package com.panda.sport.rcs.data.service.impl;

import com.panda.sport.rcs.data.mapper.CommonMapper;
import com.panda.sport.rcs.data.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * ClassName: TestServicesImpl <br/>
 * Description: <br/>
 * date: 2019/9/24 21:27<br/>
 *
 * @author Administrator<br />
 * @since JDK 1.8
 */
@Service
public class CommonServicesImpl implements CommonService {

    @Autowired
    CommonMapper commonMapper;


    @Override
    public String getMatchScoreSource(Long standardMatchInfoId) {
        return commonMapper.getMatchScoreSource(standardMatchInfoId);
    }

    @Override
    public int insertRcsCategoryOddTemplet(List<Map> beans) {
        if(CollectionUtils.isEmpty(beans)){return 0;}
        for (Map bean : beans) {
            commonMapper.insertRcsCategoryOddTemplet(bean);
        }
        return 0;
    }
}
