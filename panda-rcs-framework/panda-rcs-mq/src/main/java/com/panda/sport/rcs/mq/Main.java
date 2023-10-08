package com.panda.sport.rcs.mq;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.panda.sport.rcs.mq.bean.RocketProperties;
import com.panda.sport.rcs.mq.db.SaveDbDelayUpdateConsumer;

public class Main {
	
	public static void main(String[] args) {
		Test1 test1 = new Test1("test one");
		Test2 test2 = new Test2("test second");
		
		RocketProperties p1 = new RocketProperties();
		p1.setGroup("test1 group");
		p1.setTopics("test1 topic");
		
		RocketProperties p2 = new RocketProperties();
		p2.setGroup("test2 group");
		p2.setTopics("test2 topic");
		
		try {
			test1.handleMs(p1, null);
			test1.handleMs(p1, null);
			
			test2.handleMs(p2, null);
			test2.handleMs(p2, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class Test1 extends SaveDbDelayUpdateConsumer<RocketProperties>{

	public Test1(String consumerConfig) {
		super(consumerConfig);
	}

	@Override
	public String getCacheKey(RocketProperties msg,Map<String, String> paramsMap) {
		return msg.getGroup() + msg.getTopics();
	}

	@Override
	public Boolean updateData(RocketProperties bean) {
		String tipic = bean.getTopics();
		System.out.println("Test1:  tipic=" + tipic + "  ,bean : " + JSONObject.toJSONString(bean));
		return true;
	}
}

class Test2 extends SaveDbDelayUpdateConsumer<RocketProperties>{

	public Test2(String consumerConfig) {
		super(consumerConfig);
	}

	@Override
	public String getCacheKey(RocketProperties msg,Map<String, String> paramsMap) {
		return msg.getGroup() + msg.getTopics();
	}

	@Override
	public Boolean updateData(RocketProperties bean) {
		String tipic = bean.getTopics();
		System.out.println("Test2:  tipic=" + tipic + "  ,bean : " + JSONObject.toJSONString(bean));
		return true;
	}

}
