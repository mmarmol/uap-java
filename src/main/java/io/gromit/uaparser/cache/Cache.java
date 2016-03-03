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

import io.gromit.uaparser.model.Device;
import io.gromit.uaparser.model.OS;
import io.gromit.uaparser.model.UserAgent;

/**
 * The Interface ParserCache.
 */
public interface Cache {

	/**
	 * Gets the os.
	 *
	 * @param agentString the agent string
	 * @return the os
	 */
	OS getOs(String agentString);

	/**
	 * Put os.
	 *
	 * @param agentString the agent string
	 * @param object the object
	 */
	void putOS(String agentString, OS object);

	/**
	 * Gets the device.
	 *
	 * @param agentString the agent string
	 * @return the device
	 */
	Device getDevice(String agentString);

	/**
	 * Put device.
	 *
	 * @param agentString the agent string
	 * @param object the object
	 */
	void putDevice(String agentString, Device object);

	/**
	 * Gets the user agent.
	 *
	 * @param agentString the agent string
	 * @return the user agent
	 */
	UserAgent getUserAgent(String agentString);

	/**
	 * Put user agent.
	 *
	 * @param agentString the agent string
	 * @param object the object
	 */
	void putUserAgent(String agentString, UserAgent object);
	
	/**
	 * Clean.
	 */
	void clean();
}
