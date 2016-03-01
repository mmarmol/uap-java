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

package io.gromit.uaparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gromit.uaparser.cache.Cache;
import io.gromit.uaparser.cache.NoCache;
import io.gromit.uaparser.model.OS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Class OSParser.
 */
public class OSParser {

	/** The patterns. */
	private final List<OSPattern> patterns;

	/**
	 * Instantiates a new OS parser.
	 *
	 * @param patterns
	 *            the patterns
	 */
	public OSParser(List<OSPattern> patterns) {
		this.patterns = patterns;
	}

	/**
	 * From list.
	 *
	 * @param configList
	 *            the config list
	 * @return the OS parser
	 */
	public static OSParser fromList(List<Map<String, String>> configList) {
		List<OSPattern> configPatterns = new ArrayList<OSPattern>();

		for (Map<String, String> configMap : configList) {
			configPatterns.add(OSParser.patternFromMap(configMap));
		}
		return new OSParser(configPatterns);
	}

	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the os
	 */
	public OS parse(String agentString) {
		return this.parse(agentString, NoCache.NO_CACHE);
	}

	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @param cache
	 *            the cache
	 * @return the os
	 */
	public OS parse(String agentString, Cache cache) {
		if (agentString == null) {
			return null;
		}
		OS os = cache.getOs(agentString);
		if (os == null) {
			for (OSPattern p : patterns) {
				if ((os = p.match(agentString)) != null) {
					cache.putOS(agentString, os);
					return os;
				}
			}
			os = new OS("Other", null, null, null, null);
			cache.putOS(agentString, os);
		}
		return os;
	}

	/**
	 * Pattern from map.
	 *
	 * @param configMap
	 *            the config map
	 * @return the OS pattern
	 */
	protected static OSPattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("OS is missing regex");
		}

		return (new OSPattern(Pattern.compile(regex), configMap.get("os_replacement"),
				configMap.get("os_v1_replacement"), configMap.get("os_v2_replacement"),
				configMap.get("os_v3_replacement")));
	}

	/**
	 * The Class OSPattern.
	 */
	protected static class OSPattern {

		/** The pattern. */
		private final Pattern pattern;

		/** The v2 replacement. */
		private final String osReplacement, v1Replacement, v2Replacement, v3Replacement;

		/**
		 * Instantiates a new OS pattern.
		 *
		 * @param pattern
		 *            the pattern
		 * @param osReplacement
		 *            the os replacement
		 * @param v1Replacement
		 *            the v1 replacement
		 * @param v2Replacement
		 *            the v2 replacement
		 */
		public OSPattern(Pattern pattern, String osReplacement, String v1Replacement, String v2Replacement,
				String v3Replacement) {
			this.pattern = pattern;
			this.osReplacement = osReplacement;
			this.v1Replacement = v1Replacement;
			this.v2Replacement = v2Replacement;
			this.v3Replacement = v3Replacement;
		}

		/**
		 * Match.
		 *
		 * @param agentString
		 *            the agent string
		 * @return the os
		 */
		public OS match(String agentString) {
			String family = null, v1 = null, v2 = null, v3 = null, v4 = null;
			Matcher matcher = pattern.matcher(agentString);

			if (!matcher.find()) {
				return null;
			}

			int groupCount = matcher.groupCount();

			if (osReplacement != null) {
				if (groupCount >= 1) {
					family = Pattern.compile("(" + Pattern.quote("$1") + ")").matcher(osReplacement)
							.replaceAll(matcher.group(1));
				} else {
					family = osReplacement;
				}
			} else if (groupCount >= 1) {
				family = matcher.group(1);
			}

			if (v1Replacement != null) {
				v1 = v1Replacement;
			} else if (groupCount >= 2) {
				v1 = matcher.group(2);
			}
			if (v2Replacement != null) {
				v2 = v2Replacement;
			} else if (groupCount >= 3) {
				v2 = matcher.group(3);
			}
			if (v3Replacement != null) {
				v3 = v3Replacement;
			} else if (groupCount >= 4) {
				v3 = matcher.group(4);
			}
			if (groupCount >= 5) {
				v4 = matcher.group(5);
			}

			return family == null ? null : new OS(family, v1, v2, v3, v4);
		}
	}
}
