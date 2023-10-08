package com.panda.sport.rcs.task.mq.bean;

import java.util.List;

import com.panda.sport.rcs.mongo.MarketCategory;

import lombok.Data;

@Data
public class MatchMarketLiveUpdateBean {
	
	private Long id;
	
	private  List<MarketCategory> list ;

	public MatchMarketLiveUpdateBean() {}
	
	public MatchMarketLiveUpdateBean(Long id ) {
		this.id = id;
	}
}
