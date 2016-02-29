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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.gromit.uaparser.cache.Cache;
import io.gromit.uaparser.cache.NoCache;
import io.gromit.uaparser.model.UserAgent;

/**
 * The Class UserAgentParser.
 */
public class UserAgentParser {

	/** The patterns. */
	private final List<UAPattern> patterns;
	
	/**
	 * Instantiates a new user agent parser.
	 *
	 * @param patterns
	 *            the patterns
	 */
	public UserAgentParser(List<UAPattern> patterns) {
		this.patterns = patterns;
	}

	/**
	 * From list.
	 *
	 * @param configList
	 *            the config list
	 * @return the user agent parser
	 */
	public static UserAgentParser fromList(List<Map<String, String>> configList) {
		List<UAPattern> configPatterns = new ArrayList<UAPattern>();

		for (Map<String, String> configMap : configList) {
			configPatterns.add(UserAgentParser.patternFromMap(configMap));
		}
		return new UserAgentParser(configPatterns);
	}
	
	/**
	 * Parses the.
	 *
	 * @param agentString the agent string
	 * @return the user agent
	 */
	public UserAgent parse(String agentString){
		return this.parse(agentString, NoCache.NO_CACHE);
	}
	
	/**
	 * Parses the.
	 *
	 * @param agentString            the agent string
	 * @param cache the cache
	 * @return the user agent
	 */
	public UserAgent parse(String agentString, Cache cache) {
		if (agentString == null) {
			return null;
		}
		UserAgent agent = cache.getUserAgent(agentString);
		if(agent==null){
			for (UAPattern p : patterns) {
				if ((agent = p.match(agentString)) != null) {
					cache.putUserAgent(agentString, agent);
					return agent;
				}
			}
			agent = new UserAgent("Other", null, null, null);
			cache.putUserAgent(agentString, agent);
		}
		return agent;
	}

	/**
	 * Pattern from map.
	 *
	 * @param configMap
	 *            the config map
	 * @return the UA pattern
	 */
	protected static UAPattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("User agent is missing regex");
		}

		return (new UAPattern(Pattern.compile(regex), configMap.get("family_replacement"),
				configMap.get("v1_replacement"), configMap.get("v2_replacement")));
	}

	/**
	 * The Class UAPattern.
	 */
	protected static class UAPattern {

		/** The pattern. */
		private final Pattern pattern;

		/** The v2 replacement. */
		private final String familyReplacement, v1Replacement, v2Replacement;

		/**
		 * Instantiates a new UA pattern.
		 *
		 * @param pattern
		 *            the pattern
		 * @param familyReplacement
		 *            the family replacement
		 * @param v1Replacement
		 *            the v1 replacement
		 * @param v2Replacement
		 *            the v2 replacement
		 */
		public UAPattern(Pattern pattern, String familyReplacement, String v1Replacement, String v2Replacement) {
			this.pattern = pattern;
			this.familyReplacement = familyReplacement;
			this.v1Replacement = v1Replacement;
			this.v2Replacement = v2Replacement;
		}

		/**
		 * Match.
		 *
		 * @param agentString
		 *            the agent string
		 * @return the user agent
		 */
		public UserAgent match(String agentString) {
			String family = null, v1 = null, v2 = null, v3 = null;
			Matcher matcher = pattern.matcher(agentString);

			if (!matcher.find()) {
				return null;
			}

			int groupCount = matcher.groupCount();

			if (familyReplacement != null) {
				if (familyReplacement.contains("$1") && groupCount >= 1 && matcher.group(1) != null) {
					family = familyReplacement.replaceFirst("\\$1", Matcher.quoteReplacement(matcher.group(1)));
				} else {
					family = familyReplacement;
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
				if (groupCount >= 4) {
					v3 = matcher.group(4);
				}
			}
			return family == null ? null : new UserAgent(family, v1, v2, v3);
		}
	}
}
