package com.panda.sport.rcs.test;

import java.util.Map;

public class StackDataInfo {
	
	public static void main(String[] args) {
		System.out.println(getJavaStackTrace());
	}
	
	/**
     * 获取线程快照信息
     *
     * @return
     */
    public static String getJavaStackTrace() {
        StringBuffer msg = new StringBuffer();
        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()) {
            Thread thread = (Thread) stackTrace.getKey();
            StackTraceElement[] stack = (StackTraceElement[]) stackTrace.getValue();
            if (thread.equals(Thread.currentThread())) {
                continue;
            }
            msg.append("\n 线程:").append(thread.getName()).append(",  Priority:" + thread.getPriority())
            .append(",  id:" + thread.getId()).append(",  group:" + thread.getThreadGroup().getName()).append("\n")
            .append(" java.lang.Thread.State:" + thread.getState()).append("\n");
            for (StackTraceElement element : stack) {
                msg.append("\t").append(element).append("\n");
            }
        }
        return msg.toString();
    }


}
