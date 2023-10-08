package com.panda.sport.rcs.task.context;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OddsChangeContext {
	
	private Map<String, Boolean > isChangeMap = new HashMap<String, Boolean>();
	
	private Map<String, BigDecimal > changeValMap = new HashMap<String, BigDecimal>();
	
	private static ThreadLocal<OddsChangeContext> context = new ThreadLocal<OddsChangeContext>() {

		@Override
		protected OddsChangeContext initialValue() {
			return new OddsChangeContext();
		}
	};
	
	public static OddsChangeContext getContext() {
		return context.get();
	}
	
	public static void remove() {
		context.remove();
	}

	public Map<String, Boolean> getIsChangeMap() {
		return isChangeMap;
	}

	public void setIsChangeMap(Map<String, Boolean> isChangeMap) {
		this.isChangeMap = isChangeMap;
	}

	public Map<String, BigDecimal> getChangeValMap() {
		return changeValMap;
	}

	public void setChangeValMap(Map<String, BigDecimal> changeValMap) {
		this.changeValMap = changeValMap;
	}

}
