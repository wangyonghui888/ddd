package com.panda.sport.rcs.utils;

import java.util.ArrayList;
import java.util.List;

public class SpliteOrderUtils {
	
    public interface ApiCall<T>{
    	
    	public void execute(List<T> list);
    }

	public static <T> void spliteOrder(List<T> arr, int length, int start, int index,
			List<T> list, SpliteOrderUtils.ApiCall<T> apiCall) {
		for(;index < arr.size() ; ) {
    		List<T> tempList = new ArrayList<T>();
        	tempList.addAll(list);
    		tempList.add(arr.get(index));
    		if(tempList.size() == length) {
    			apiCall.execute(tempList);
    			++ index;
    			continue;
    		}
    		++ index;
    		spliteOrder(arr, length, start, index, tempList,apiCall);
    	}
	}

}
