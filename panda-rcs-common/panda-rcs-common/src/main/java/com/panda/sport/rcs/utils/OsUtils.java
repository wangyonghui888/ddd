package com.panda.sport.rcs.utils;

import java.util.Properties;

public class OsUtils {

    public static boolean isOSLinux() {
    	Properties prop = System.getProperties();

    	String os = prop.getProperty("os.name");
    	if (os != null && os.toLowerCase().indexOf("linux") > -1) {
    	return true;
    	} else {
		    return false;
		}
	}
}
