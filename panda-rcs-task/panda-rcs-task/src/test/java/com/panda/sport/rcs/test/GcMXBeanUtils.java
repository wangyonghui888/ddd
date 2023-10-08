package com.panda.sport.rcs.test;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.UUID;

public class GcMXBeanUtils {
	
	public static void main(String[] args) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					newStr();
				}
			}
		}).start();
		while(true) {
			try {
				for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
		            long count = gc.getCollectionCount();
		            long time = gc.getCollectionTime();
		            String name = gc.getName();
		            System.out.println(String.format("%s: %s times %s ms", name, count, time));
		        }
				
				System.gc();  
				Thread.currentThread().sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String newStr() {
		return UUID.randomUUID().toString();
	}

}
