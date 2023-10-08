package com.panda.sport.rcs.mapper.tourTemplate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.tourTemplate.RcsTournamentTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lithan auto
 * @since 2020-09-15
 */
@Repository
public interface RcsTournamentTemplateMapper extends BaseMapper<RcsTournamentTemplate> {
    /**
     * 根据id修改模板数据
     *
     * @param param
     * @return
     */
    int updateTemplateById(RcsTournamentTemplate param);

    List<RcsTournamentTemplate> getTournamentTemplateList();

}
