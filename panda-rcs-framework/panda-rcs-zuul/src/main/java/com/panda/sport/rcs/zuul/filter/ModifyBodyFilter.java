package com.panda.sport.rcs.zuul.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ModifyBodyFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		MultiReadHttpServletRequest mParametersWrapper = new MultiReadHttpServletRequest((HttpServletRequest) request);
		chain.doFilter(mParametersWrapper, response);
	}

}
