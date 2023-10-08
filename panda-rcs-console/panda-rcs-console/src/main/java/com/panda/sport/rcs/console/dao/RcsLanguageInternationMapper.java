package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.LanguageInternation;
import com.panda.sport.rcs.console.pojo.UpdateRcsMarketCategorySetBO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mapper.MyMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.console.dao
 * @Description :  TODO
 * @Date: 2020-02-10 15:31
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
public interface RcsLanguageInternationMapper extends MyMapper<LanguageInternation> {

    int batchInsertOrUpdate(List<LanguageInternation> list);


    @Select("SELECT * from rcs_language_internation WHERE (name_code REGEXP '[^0-9]') =1")
    List<LanguageInternation> qureyVacahrNamecode();

    List<LanguageInternation> selectIds(@Param("list") List<String> strings);

    void updateRLIN(@Param("list") List<LanguageInternation> list);

    void updatesRMCSN(@Param("list") ArrayList<UpdateRcsMarketCategorySetBO> list);
}
