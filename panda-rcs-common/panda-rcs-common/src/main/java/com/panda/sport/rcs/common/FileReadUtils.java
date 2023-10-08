package com.panda.sport.rcs.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileReadUtils {

    public static String readFileContent(String fileName) {
        try {
        	ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        	Resource[] resources = resolver.getResources(fileName);
        	for(Resource resource : resources) {
        		InputStream stream = resource.getInputStream();
        		String result = IOUtils.toString(stream, "utf-8");
        		return result;
        	}
        } catch (IOException e) {
        	log.error(e.getMessage(),e);
        }
        return null;
    }
}
