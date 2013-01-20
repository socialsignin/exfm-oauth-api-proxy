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

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.NotAuthorizedException;
import org.springframework.social.exfm.api.ExFmProfile;
import org.springframework.social.exfm.api.impl.ExFmTemplate;
import org.springframework.stereotype.Component;

/**
 * This proxy to ExFm's API is an interim solution until such time that ExFm provide an oauth API.
 * If such an oauth API were provided by ExFm themselves, the ExFMUserDetailsService could be implemented
 * as it would have access to the username/passwords of ExFm users
 * As this proxy is implemented without access to the username/passwords of ExFm users, this
 * workaround UserDetailsService is provided which obtains the current authentication attempt from
 * the login attempt and uses the corresponding username/password combination to verify that user
 * with the ExFM api.  In this way we can simulate having access to the ExFm user store for
 * the currently authenticated user.
 * 
 * @author Michael
 *
 */
@Component("exFmUserDetailsService")
public class WorkaroundExFmUserDetailsService implements UserDetailsService {

	private String baseUrl = "http://ex.fm/api/v3";


	public UserDetails loadUserByUsername(String workaroundUserName)
			throws UsernameNotFoundException {

		String[] usernameAndPassword = workaroundUserName
				.split(WorkaroundUsernamePasswordAuthenticationFilter.USERNAME_PASSWORD_DELIMITER);
		if (usernameAndPassword.length != 2) {
			throw new UsernameNotFoundException(workaroundUserName);
		} else {
			String username = usernameAndPassword[0];
			String password = usernameAndPassword[1];
			ExFmTemplate exFmTemplate = new ExFmTemplate(baseUrl, username,
					password);
			ExFmProfile exfmProfile = null;
			try
			{
				exfmProfile = exFmTemplate.meOperations()
					.getUserProfile();
			}
			catch (NotAuthorizedException e)
			{
				
			}
			if (exfmProfile != null) {
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
				return new User(workaroundUserName, password, authorities);
			} else {
				throw new UsernameNotFoundException(username);
			}

		}

	}

}
