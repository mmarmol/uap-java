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
 * The Class OS.
 */
public class OS {

	/** The family. */
	public final String family, major, minor, patch, patchMinor;

	/**
	 * Instantiates a new os.
	 *
	 * @param family
	 *            the family
	 * @param major
	 *            the major
	 * @param minor
	 *            the minor
	 * @param patch
	 *            the patch
	 * @param patchMinor
	 *            the patch minor
	 */
	public OS(String family, String major, String minor, String patch, String patchMinor) {
		this.family = family;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.patchMinor = patchMinor;
	}

	/**
	 * From map.
	 *
	 * @param m
	 *            the m
	 * @return the os
	 */
	public static OS fromMap(Map<String, String> m) {
		return new OS(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"), m.get("patch_minor"));
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
		if (!(other instanceof OS))
			return false;

		OS o = (OS) other;
		return ((this.family != null && this.family.equals(o.family)) || this.family == o.family)
				&& ((this.major != null && this.major.equals(o.major)) || this.major == o.major)
				&& ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor)
				&& ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch)
				&& ((this.patchMinor != null && this.patchMinor.equals(o.patchMinor))
						|| this.patchMinor == o.patchMinor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int h = family == null ? 0 : family.hashCode();
		h += major == null ? 0 : major.hashCode();
		h += minor == null ? 0 : minor.hashCode();
		h += patch == null ? 0 : patch.hashCode();
		h += patchMinor == null ? 0 : patchMinor.hashCode();
		return h;
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