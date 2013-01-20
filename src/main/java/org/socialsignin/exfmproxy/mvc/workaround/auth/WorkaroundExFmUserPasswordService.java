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

import org.socialsignin.exfmproxy.mvc.UserPasswordService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

/**
 * This proxy to ExFm's API is an interim solution until such time that ExFm provide an oauth API.
 * If such an oauth API were provided by ExFm themselves, the ExFMUserPasswordService could be implemented
 * as it would have access to the username/passwords of ExFm users
 * As this proxy is implemented without access to the username/passwords of ExFm users, this
 * workaround UserPasswordService is provided which obtains the current authentication attempt from
 * the login attempt and returns password from this attempt .  
 * In this way we can simulate having access to the ExFm user store for
 * the currently authenticated user.
 * 
 * @author Michael Lavelle
 *
 */
@Component
public class WorkaroundExFmUserPasswordService implements UserPasswordService {

	public String getAuthenticatedUserPassword() {

		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return authentication == null
				|| authentication.getName().equals("anonymousUser") ? null
				: ((String) ((User)authentication.getPrincipal()).getPassword());
	}

}
