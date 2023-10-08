package com.panda.sport.rcs.zuul.filter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.springframework.cloud.netflix.ribbon.RibbonHttpResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.panda.sport.rcs.zuul.contant.LogContext;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PostRequestLogFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		LogContext context = LogContext.getContext();
		try {
			context.setEndTime(System.currentTimeMillis());
			
			RequestContext ctx = RequestContext.getCurrentContext();
			Boolean isFileDown = false;
			Object zuulResponse = RequestContext.getCurrentContext().get("zuulResponse");
			
			log.info("zuulResponse:{}",zuulResponse);
			
			try {
				if (zuulResponse instanceof HttpResponse) {
					HttpResponse res = (HttpResponse) zuulResponse;
					if(res != null && res.getLastHeader("Content-Type") != null && res.getLastHeader("Content-Type").getValue() != null) {
						if(res.getLastHeader("Content-Type").getValue().contains(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
							log.info("文件下载，不处理：{}",ctx.getRequest().getRequestURI());
							isFileDown =  true;
						}
					} 
				} else if (zuulResponse instanceof RibbonHttpResponse) {
					RibbonHttpResponse res = (RibbonHttpResponse) zuulResponse;
					MediaType type = res.getHeaders().getContentType();
					
					if(type != null && type.toString().contains(MediaType.APPLICATION_OCTET_STREAM_VALUE)) {
						log.info("文件下载，不处理：{}",ctx.getRequest().getRequestURI());
						isFileDown =  true;
					}
				}else {
					List<Pair<String, String>> heads = ctx.getZuulResponseHeaders();
					if(heads != null) {
						for(Pair<String,String> pair : heads) {
							log.info("pair：{}，{}",pair.first(),pair.second());
							if("Content-Type".equals(pair.first()) && 
									MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(pair.second())) {
								log.info("文件下载，不处理：{}",ctx.getRequest().getRequestURI());
								isFileDown =  true;
							}
						}
					}
				}
			}catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			
			if(!isFileDown){
				InputStream stream = ctx.getResponseDataStream();
				String response = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
				
				context.setResult(response);
				ctx.setResponseDataStream(new ByteArrayInputStream(response.getBytes("UTF-8")));
			}
			
			log.info("[http-log-info] post :{}",context.getLogStr());
		}catch (Exception e) {
			log.error(e.getMessage(),e);
		}finally {
			context.remove();
		}
		
		return null;
	}

	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
