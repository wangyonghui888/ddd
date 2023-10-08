package com.panda.sport.rcs.zuul.order;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;

@Component
public class RouteOrderConfig {
	
	
	RouteOrderConfig(ZuulProperties zuulProperties){
		Map<String, ZuulRoute> map = zuulProperties.getRoutes();
		if(map == null ) return;
		
		Map<String, ZuulRoute> routes = new LinkedHashMap<String, ZuulProperties.ZuulRoute>();
		
		Set<String> treeSet = new TreeSet<String>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		treeSet.addAll(map.keySet());
		treeSet.forEach(key -> routes.put(key, map.get(key)));
		zuulProperties.setRoutes(routes);

		System.out.println( zuulProperties);
	}

}
