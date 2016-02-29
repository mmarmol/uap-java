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

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The Class Device.
 */
public class Device {

	/** The family. */
	private String family;

	/**
	 * Instantiates a new device.
	 *
	 * @param family
	 *            the family
	 */
	public Device(String family) {
		this.family = family;
	}

	/**
	 * From map.
	 *
	 * @param m
	 *            the m
	 * @return the device
	 */
	public static Device fromMap(Map<String, String> m) {
		return new Device((String) m.get("family"));
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
		if (!(other instanceof Device))
			return false;

		Device o = (Device) other;
		return (this.family != null && this.family.equals(o.family)) || this.family == o.family;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return family == null ? 0 : family.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}