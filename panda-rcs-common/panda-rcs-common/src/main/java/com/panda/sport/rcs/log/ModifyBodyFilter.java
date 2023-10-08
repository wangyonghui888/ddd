package com.panda.sport.rcs.log;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.IOException;

public class ModifyBodyFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//是否上传文件
		boolean isMultipartFlag = ServletFileUpload.isMultipartContent((HttpServletRequest) request);
		if(isMultipartFlag) {
			chain.doFilter(request, response);
		}else {
			MultiReadHttpServletRequest mParametersWrapper = new MultiReadHttpServletRequest((HttpServletRequest) request);
			chain.doFilter(mParametersWrapper, response);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("111111111111111111");
	}

}
