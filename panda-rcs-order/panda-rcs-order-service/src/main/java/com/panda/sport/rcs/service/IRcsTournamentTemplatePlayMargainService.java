package com.panda.sport.rcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplatePlayMargain;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lithan auto
 * @since 2020-09-15
 */
public interface IRcsTournamentTemplatePlayMargainService extends IService<RcsTournamentTemplatePlayMargain> {
    /**
     *  获取配置
     * @param templateId 模板id
     * @return
     */
    List<RcsTournamentTemplatePlayMargain> getTemplatePlayMargainList(Integer templateId);
}
