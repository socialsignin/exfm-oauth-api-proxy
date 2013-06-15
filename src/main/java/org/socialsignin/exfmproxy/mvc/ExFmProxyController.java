package org.socialsignin.exfmproxy.mvc;

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.exfm.api.ExFm;
import org.springframework.social.exfm.api.impl.ExFmTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Controller which proxies requests to ExFm's API
 * Designed to support a subset of ExFm's API methods which can be proxied using
 * specific requestmapping patterns - these patterns are specified in Spring Security config - access for
 * non-matching patterns will be denied by Spring Security
 * 
 * @author Michael Lavelle
 *
 */
@RequestMapping("/api/v3")
@Controller
public class ExFmProxyController {

	private RestTemplate restTemplate;
	private String baseUrl = "http://ex.fm/api/v3/";

	@Autowired
	private UserPasswordService userPasswordService;

	public ExFmProxyController() {
		restTemplate = new RestTemplate();
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(Arrays.asList(new HttpMessageConverter<?>[] {stringHttpMessageConverter}));
	}
	
	protected ExFm getAuthenticatedExfm()
	{
		return new ExFmTemplate(baseUrl,getAuthenticatedUserName(), userPasswordService.getAuthenticatedUserPassword());
	}
	
	
	protected String getJson(HttpServletRequest request,String url)
	{
		String json = "";
		String method = request.getMethod().toLowerCase();
		if (method.equals("get"))
		{
			if (request.getQueryString() != null)
			{
				json = restTemplate.getForObject(url + "?" +  request.getQueryString(), String.class);
			}
			else
			{
				json = restTemplate.getForObject(url, String.class);
			}
		}
		else if (method.equals("post"))
		{
			HttpHeaders requestHeaders = new HttpHeaders();
			
			requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

			HttpEntity<?> httpEntity = new HttpEntity<Object>(requestHeaders);
			json =  restTemplate.postForEntity(url, httpEntity, String.class).getBody();
			
		}
		return json.trim();
	}
	
	
	public String addContextParams(String source,String context)
	{
		String contextParamString = "";
		if (source != null)
		{
			contextParamString = contextParamString + "&source=" + source;
		}
		if (context != null)
		{
			contextParamString = contextParamString + "&context=" + context;
		}
		return contextParamString;
	}
	

	@ResponseBody
	@RequestMapping("/song/{songId}/love")
	public String loveSong(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("songId") String songId,
		@RequestParam(required=false) String source,@RequestParam(required=false) String context) {
		response.setContentType("application/json");
		
		String url = baseUrl + "song/" + songId + "/love";
		
		return getJson(request,addAuthentication(url) + addContextParams(source,context));
	}
	
	
	@ResponseBody
	@RequestMapping("/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	public String proxy(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("path1") String path1,
			@PathVariable("path2") String path2,
			@PathVariable("path3") String path3,
			@PathVariable("path4") String path4,
			@PathVariable("path5") String path5,
			@PathVariable("path6") String path6) {
		response.setContentType("application/json");
		
		String url = baseUrl + path1 + "/" + path2 + "/"
		+ path3 + "/" + path4 + "/" + path5 + "/" + path6;
		
		return getJson(request,url);

	}
	
	@ResponseBody
	@RequestMapping("/{path1}/{path2}/{path3}/{path4}/{path5}")
	public String proxy(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("path1") String path1,
			@PathVariable("path2") String path2,
			@PathVariable("path3") String path3,
			@PathVariable("path4") String path4,
			@PathVariable("path5") String path5) {
		response.setContentType("application/json");
	
		String url = baseUrl + path1 + "/" + path2 + "/"
		+ path3 + "/" + path4 + "/" + path5;

		return getJson(request,url);

	}
	
	
	@ResponseBody
	@RequestMapping("/{path1}/{path2}/{path3}/{path4}")
	public String proxy(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("path1") String path1,
			@PathVariable("path2") String path2,
			@PathVariable("path3") String path3,
			@PathVariable("path4") String path4) {
		response.setContentType("application/json");
		String url = baseUrl + path1 + "/" + path2 + "/"
		+ path3 + "/" + path4;
		
		return getJson(request,url);

	}

	@ResponseBody
	@RequestMapping("/{path1}/{path2}/{path3}")
	public String proxy(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("path1") String path1,
			@PathVariable("path2") String path2,
			@PathVariable("path3") String path3) {
		response.setContentType("application/json");
		String url = baseUrl + path1 + "/" + path2 + "/"
		+ path3;
		return getJson(request,url);

	}

	@ResponseBody
	@RequestMapping("/{path1}/{path2}")
	public String proxy(HttpServletRequest request,HttpServletResponse response,
			@PathVariable("path1") String path1,
			@PathVariable("path2") String path2) {
		response.setContentType("application/json");
		

		String url = baseUrl + path1 + "/" + path2;
		
		return getJson(request,url);


	}
	
	private String addAuthentication(String url)
	{
		url = url + "?username="
		+ getAuthenticatedUserName() + "&password="
		+ userPasswordService.getAuthenticatedUserPassword();
		return url;
	}

	@ResponseBody
	@RequestMapping("/me")
	public String me(HttpServletRequest request,HttpServletResponse response) {
		String url = baseUrl + "me";

		response.setContentType("application/json");
		return getJson(request,addAuthentication(url));

	}

	private String getAuthenticatedUserName() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return authentication == null
				|| authentication.getName().equals("anonymousUser") ? null
				: authentication.getName();
	}

}