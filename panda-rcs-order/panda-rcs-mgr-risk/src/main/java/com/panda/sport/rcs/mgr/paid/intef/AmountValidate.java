package com.panda.sport.rcs.mgr.paid.intef;

import com.panda.sport.data.rcs.dto.ExtendBean;

import java.util.Map;

/**
 * @author :  black
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.rpc.paid.intef.impl
 * @Description :  检验
 * @Date: 2019-10-04 11:15
 * @ModificationHistory Who    When    What
 * toney     2019-10-10 10:38      优化代码，添加注释
 * --------  ---------  --------------------------
 */
public interface AmountValidate {
	/**
	 * 获取剩余最大赔付金额
	 * 用户点击投注项，recType 未空，rec为空
	 * rec 为空的时候按照0计算   （计算当前项最大下注金额时）
	 * @param order
	 * @param rec
	 * @return
	 */
	public Long getSurplusAmount(ExtendBean order, Long[][] rec);

	/**
	 * 订单各个维度计算并保存（是矩阵做累加）
	 * @param order
	 * @param rec
	 * @return
	 */
	public Boolean saveOrderAndValidate(ExtendBean order, Long[][] rec, Map<String, Object> data);

	/**
	 * 对于缓存做回滚操作 ,数据库操作不用处理
	 * @param order
	 * @param rec
	 * @param data
	 */
	public default void rollBack(ExtendBean order, Long[][] rec, Map<String, Object> data) {
		
	}
	
	/**
	 * 派奖后的处理流程
	 * 
	 */
	public default void prizeHandle(ExtendBean orderItem) {
		
	}
	
	public default AmountValidate next() {
		return null;
	}
	
	public default void setNext(AmountValidate validate) {
	}
}
