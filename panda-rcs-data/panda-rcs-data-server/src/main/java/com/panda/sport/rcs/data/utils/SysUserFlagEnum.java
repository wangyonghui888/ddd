package com.panda.sport.rcs.data.utils;

import com.panda.sports.api.vo.ShortSysUserVO;
import com.panda.sports.api.vo.SysTraderVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 用户标标识
 */
@Slf4j
public enum SysUserFlagEnum {

	TRADER("1"),
	RCS("2"),
	TRADER_MANGAGERS("3"),
	SENIOR_TRADERS("4"),
	TRADER_DIRECTOR("5"),
	OTHER("999");

	private String code;

	SysUserFlagEnum(String value) {
		this.code = value;
	}

	public String getCode() {
		return code;
	}
}