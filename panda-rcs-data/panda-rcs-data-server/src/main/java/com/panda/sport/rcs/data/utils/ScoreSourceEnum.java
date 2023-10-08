package com.panda.sport.rcs.data.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  holly
 * @Project Name :rcs-parent
 * @Package Name :com.panda.sport.rcs.constants
 * @Description :
 * @Date: 2019-10-24 15:02
 */
@Slf4j
public enum ScoreSourceEnum {

	STANDARD("0"),
	EVENT("1"),
	STATISTICS("2");

	private String code;

	ScoreSourceEnum(String value) {
		this.code = value;
	}

	public String getCode() {
		return code;
	}
}
