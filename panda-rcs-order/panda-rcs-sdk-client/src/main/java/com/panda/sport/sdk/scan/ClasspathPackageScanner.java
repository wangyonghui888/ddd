package com.panda.sport.sdk.scan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import com.panda.sport.sdk.category.IMatrixForecast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panda.sport.sdk.util.StringUtil;

public class ClasspathPackageScanner {
    private static Logger logger = LoggerFactory.getLogger(ClasspathPackageScanner.class);
    private String basePackage;
    private ClassLoader cl;
    
    private List<String> allClassList ;

    public ClasspathPackageScanner(){}
    
    /**
     * 初始化
     * @param basePackage
     */
    public ClasspathPackageScanner(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }
    public ClasspathPackageScanner(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
    
    /**
     *获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }
    
    /**
     *doScan函数
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        URL url = cl.getResource(splashPath);   //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
        String filePath = StringUtil.getRootPath(url);
        List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个JAR包", filePath);
            }
            
            names = readFromJarFile(filePath, splashPath);
            if(names == null || names.size() <= 0 ) {
            	names = readFromJarFile(url,splashPath);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("{} 是一个目录", filePath);
            }
            names = readFromDirectory(filePath);
        }
        for (String name : names) {
            if (isClassFile(name)) {
                nameList.add(toFullyQualifiedName(name, basePackage));
            } else {
                doScan(basePackage + "." + name, nameList);
            }
        }
        if (logger.isDebugEnabled()) {
            for (String n : nameList) {
                logger.debug("找到{}", n);
            }
        }
        return nameList;
    }

    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
        return sb.toString();
    }
    
    private List<String> readFromJarFile(URL url, String splashedPackageName) throws IOException {
    	List<String> nameList = new ArrayList<String>();
    	try {
            URLConnection connection = url.openConnection();
            if (connection instanceof JarURLConnection) {
                  JarFile jarFile = ((JarURLConnection)connection).getJarFile();
                  Enumeration enu = jarFile.entries();
                  while (enu.hasMoreElements()) {
                	  JarEntry element = (JarEntry) enu.nextElement();
                	  String name = element.getName();
                	  if(name.startsWith(splashedPackageName) && isClassFile(name)){
                		  nameList.add(name.replace(splashedPackageName, "").replace("/", ".").substring(1));
                	  }
            	  }

            } 
          } catch (IOException iOException) {
        	  iOException.printStackTrace();
          }

        return nameList;
    }

    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("从JAR包中读取类: {}", jarPath);
        }
        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<String>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name.replace(splashedPackageName, "").replace("/", ".").substring(1));
            }

            entry = jarIn.getNextJarEntry();
        }

        return nameList;
    }

    private List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }
    
    public List<String> getAllMatchByAnnotion(Class anno){
    	List<String> result = new ArrayList<String>();
    	try {
    		if(allClassList == null) {
        		allClassList = getFullyQualifiedClassNameList();
        	}
    		for(String name : allClassList) {
            	if(Class.forName(name).getAnnotation(anno) != null) {
            		result.add(name);
            	}
            }
    		
    	}catch (Exception e) {
    		logger.error(e.getMessage(),e);
    		throw new RuntimeException(e.getMessage());
    	}
    	
    	return result;
    }
    
    public List<String> getAllMatchByInterface(Class inter){
    	List<String> result = new ArrayList<String>();
    	try {
    		if(allClassList == null) {
        		allClassList = getFullyQualifiedClassNameList();
        	}
    		for(String name : allClassList) {
    			for(Class tempClazz : Class.forName(name).getInterfaces()) {
    				if(!tempClazz.getName().equals(inter.getName())) continue;
    				result.add(name);
    			}
            }
    		
    	}catch (Exception e) {
    		logger.error(e.getMessage(),e);
    		throw new RuntimeException(e.getMessage());
    	}
    	
    	return result;
    }

    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
    	ClasspathPackageScanner scan = new ClasspathPackageScanner("com.panda.sport.sdk.category");
        System.out.println(scan.getAllMatchByInterface(IMatrixForecast.class));
    	
    }
}
