package com.panda.sport.data.rcs.dto.tournament;

import lombok.Data;

/**
 * 根据联赛id查询相关联赛相关数据 请求参数VO
 *
 * @description:
 * @author: Waldkir
 * @date: 2022-01-02 14:14
 */
@Data
public class TournamentPropertyReqVo implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
     * 联赛ID
     */
    Long id;
    /**
     * 1.MTSOddsChangeValue
     * 2.orderDelayTime
     * 3.oddsChangeStatus
     */
    String type;
    
	public TournamentPropertyReqVo() {
		super();
	}
	
	public TournamentPropertyReqVo(Long id, String type) {
		super();
		this.id = id;
		this.type = type;
	}
    
    
    
}