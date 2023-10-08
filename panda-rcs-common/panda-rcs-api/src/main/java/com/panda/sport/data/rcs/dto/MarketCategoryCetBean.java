package com.panda.sport.data.rcs.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class MarketCategoryCetBean implements Serializable{

	private Long playId;
	
	private Long sportId;
	
	private Long setId;
}
