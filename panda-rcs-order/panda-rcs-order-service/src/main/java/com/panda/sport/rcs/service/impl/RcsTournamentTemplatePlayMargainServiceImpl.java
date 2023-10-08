package com.panda.sport.rcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.tourTemplate.RcsTournamentTemplatePlayMargainMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;
import com.panda.sport.rcs.service.IRcsTournamentTemplatePlayMargainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lithan auto
 * @since 2020-09-15
 */
@Service
public class RcsTournamentTemplatePlayMargainServiceImpl extends ServiceImpl<RcsTournamentTemplatePlayMargainMapper, RcsTournamentTemplatePlayMargain> implements IRcsTournamentTemplatePlayMargainService {

    @Autowired
    private RcsTournamentTemplatePlayMargainMapper rcsTournamentTemplatePlayMargainMapper;

    @Override
    public List<RcsTournamentTemplatePlayMargain> getTemplatePlayMargainList(Integer templateId) {
        return rcsTournamentTemplatePlayMargainMapper.getTemplatePlayMargainList(templateId);
    }
}
