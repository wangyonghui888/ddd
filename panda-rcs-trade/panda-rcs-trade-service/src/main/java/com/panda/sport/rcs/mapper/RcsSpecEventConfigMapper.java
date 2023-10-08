package com.panda.sport.rcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.sport.rcs.pojo.RcsSpecEventConfig;
import com.panda.sport.rcs.pojo.dto.SpecEventConfigDTO;
import com.panda.sport.rcs.pojo.param.RcsSpecEventConfigParam;
import com.panda.sport.rcs.pojo.param.UpdateSpecEventStatusParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Mapper AO特殊事件配置
 * </p>
 *
 * @author admin
 * @since 2023-04-10
 */
@Service
public interface RcsSpecEventConfigMapper extends BaseMapper<RcsSpecEventConfig> {
    
    /**
     * 根据ID修改
     * @param rcsSpecEventConfig
     * @return
     */
    int updateSpecEventConfigById(@Param("record") RcsSpecEventConfigParam rcsSpecEventConfig);
    
    
    /**
     * 根据赛事ID和时间编码修改激活时间、激活参数、激活次数
     * @param rcsSpecEventConfig
     * @return
     */
    int updateActiveByMatchId(@Param("record") RcsSpecEventConfig rcsSpecEventConfig);

    /**
     * 根据赛事ID和事件编码修改赔率
     * @param rcsSpecEventConfig
     * @return
     */
    int updateSpecEventConfigProbByMatchId(@Param("record") RcsSpecEventConfig rcsSpecEventConfig);
    
    
    /**
     * 根据赛事ID查询AO事件配置
     * @param specEventConfigDTO
     * @return
     */
    List<RcsSpecEventConfig> querySpecEventConfigList(@Param("record") SpecEventConfigDTO specEventConfigDTO);
    
    /**
     * 批量插入AO事件配置信息（等级级别）
     * @param eventConfigList
     * @return
     */
    int initSpecEventConfig(@Param("list") List<RcsSpecEventConfig> eventConfigList);
    
    
    /**
     * 批量插入AO事件配置信息（赛事级别、联赛级别）
     * @param eventConfigList
     * @return
     */
    int batchInsert(@Param("list") List<RcsSpecEventConfig> eventConfigList);
    
    /**
     * 根据赛事批量修改事件级别开关
     * @param specEventStatusParam
     * @return
     */
    int updateSpecEventStatusByMatchId(@Param("record") UpdateSpecEventStatusParam specEventStatusParam);
    
    /**
     * 获取自动开盘开关状态
     * @param businessKey
     * @return
     */
    String getAutoOpenMarketStatus(@Param("businessKey") String businessKey);
    
    /**
     * 修改自动开盘开关状态
     * @param businessKey
     * @param status
     * @return
     */
    int updateAutoOpenMarketStatus(@Param("businessKey") String businessKey, @Param("status") Integer status);
    
    /**
     * 初始化自动开盘开关
     * @param businessKey
     * @param status
     *  @return
     */
    int initAutoOpenMarketStatus(@Param("businessKey") String businessKey, @Param("status") Integer status);
}
