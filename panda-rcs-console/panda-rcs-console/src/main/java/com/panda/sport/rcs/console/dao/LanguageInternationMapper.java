package com.panda.sport.rcs.console.dao;

import com.panda.sport.rcs.console.pojo.LanguageInternation;
import com.panda.sport.rcs.console.pojo.SystemServiceInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import tk.mapper.MyMapper;

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
public interface LanguageInternationMapper extends MyMapper<LanguageInternation> {

    int batchInsert(@Param("list") List<SystemServiceInfo> list);

    @Select("SELECT name_code nameCode, concat('{', GROUP_CONCAT(\tconcat( '\"', language_type, '\":\"', REPLACE(REPLACE(text,'\\\\','\\\\\\\\'),'\\\"','\\\\\"'),'\"' )) ,'}') text FROM language_internation  WHERE name_code IS NOT NULL GROUP BY name_code ORDER BY name_code desc LIMIT #{start},#{pageSize}")
    List<LanguageInternation> getLanguageInternations(@Param("start") int start ,@Param("pageSize") int pageSize);

}
