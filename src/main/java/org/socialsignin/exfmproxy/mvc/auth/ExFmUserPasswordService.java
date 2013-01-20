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
package org.socialsignin.exfmproxy.mvc.auth;

import org.socialsignin.exfmproxy.mvc.UserPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Implementation of UserPasswordService to be used in conjuction with ExFmUserDetailsService when
 * the user details service has been fully implemented in a system with access to ExFm user details store
 * 
 * @author Michael Lavelle
 */
@Component
public class ExFmUserPasswordService implements UserPasswordService {

	@Autowired
	private ExFmUserDetailsService exFmUserDetailsService;

	private String getAuthenticatedUserName() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return authentication == null
				|| authentication.getName().equals("anonymousUser") ? null
				: authentication.getName();
	}

	public String getAuthenticatedUserPassword() {
		UserDetails userDetails = exFmUserDetailsService
				.loadUserByUsername(getAuthenticatedUserName());
		return userDetails == null ? null : userDetails.getPassword();
	}

}
