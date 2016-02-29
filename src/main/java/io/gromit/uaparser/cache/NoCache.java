/**
 * Copyright 2016 gromit.it
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gromit.uaparser.cache;

import io.gromit.uaparser.model.Client;
import io.gromit.uaparser.model.Device;
import io.gromit.uaparser.model.OS;
import io.gromit.uaparser.model.UserAgent;

/**
 * The Class NoCache.
 */
public class NoCache implements Cache{

	/** The Constant NO_CACHE. */
	public static final NoCache NO_CACHE = new NoCache();
	
	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getClient(java.lang.String)
	 */
	@Override
	public Client getClient(String userAgent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putClient(java.lang.String, io.gromit.uaparser.model.Client)
	 */
	@Override
	public void putClient(String userAgent, Client object) {
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getOs(java.lang.String)
	 */
	@Override
	public OS getOs(String userAgent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putOS(java.lang.String, io.gromit.uaparser.model.OS)
	 */
	@Override
	public void putOS(String userAgent, OS object) {	
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getDevice(java.lang.String)
	 */
	@Override
	public Device getDevice(String userAgent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putDevice(java.lang.String, io.gromit.uaparser.model.Device)
	 */
	@Override
	public void putDevice(String userAgent, Device object) {
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#getUserAgent(java.lang.String)
	 */
	@Override
	public UserAgent getUserAgent(String userAgent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see io.gromit.uaparser.cache.Cache#putUserAgent(java.lang.String, io.gromit.uaparser.model.UserAgent)
	 */
	@Override
	public void putUserAgent(String userAgent, UserAgent object) {
	}

}
