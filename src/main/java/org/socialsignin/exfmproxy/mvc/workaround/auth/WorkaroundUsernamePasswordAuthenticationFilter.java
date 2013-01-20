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
package org.socialsignin.exfmproxy.mvc.workaround.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
/**
 * This proxy to ExFm's API is an interim solution until such time that ExFm provide an oauth API.
 * If such an oauth API were provided by ExFm themselves, a standard UsernamePasswordAuthenticationFilter 
 * could be registered as it would have access to the username/passwords of ExFm users
 * As this proxy is implemented without access to the username/passwords of ExFm users, this
 * workaround UsernamePasswordAuthenticationFilter is provided which obtains the current authentication attempt from
 * the login attempt and stores the username *and* password in the authentication object in such
 * a way that the user's identity can be verified via the Ex.Fm API.
 * 
 * @author Michael Lavelle
 *
 */ class WorkaroundUsernamePasswordAuthenticationFilter extends
		UsernamePasswordAuthenticationFilter {

	public final static String USERNAME_PASSWORD_DELIMITER = ":";
	
	public WorkaroundUsernamePasswordAuthenticationFilter() {
		super();
		this.setFilterProcessesUrl("/login.do");
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		String userName = super.obtainUsername(request);
		String password = obtainPassword(request);
		return userName + USERNAME_PASSWORD_DELIMITER + password;
	
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		Authentication a = super.attemptAuthentication(request, response);
		String[] usernameAndPassword = ((User) a.getPrincipal()).getUsername()
				.split(USERNAME_PASSWORD_DELIMITER);
		User user = new User(usernameAndPassword[0], usernameAndPassword[1],
				a.getAuthorities());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user, usernameAndPassword[1], a.getAuthorities());
		setDetails(request, authentication);
		return authentication;


	}

	@Override
	@Autowired
	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

}
