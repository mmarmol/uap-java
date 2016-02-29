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
package io.gromit.uaparser.model;

/**
 * The Class Client.
 */
public class Client {

	/** The user agent. */
	public final UserAgent userAgent;

	/** The os. */
	public final OS os;

	/** The device. */
	public final Device device;

	/**
	 * Instantiates a new client.
	 *
	 * @param userAgent
	 *            the user agent
	 * @param os
	 *            the os
	 * @param device
	 *            the device
	 */
	public Client(UserAgent userAgent, OS os, Device device) {
		this.userAgent = userAgent;
		this.os = os;
		this.device = device;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Client))
			return false;

		Client o = (Client) other;
		return ((this.userAgent != null && this.userAgent.equals(o.userAgent)) || this.userAgent == o.userAgent)
				&& ((this.os != null && this.os.equals(o.os)) || this.os == o.os)
				&& ((this.device != null && this.device.equals(o.device)) || this.device == o.device);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int h = userAgent == null ? 0 : userAgent.hashCode();
		h += os == null ? 0 : os.hashCode();
		h += device == null ? 0 : device.hashCode();
		return h;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("{\"user_agent\": %s, \"os\": %s, \"device\": %s}", userAgent, os, device);
	}
}