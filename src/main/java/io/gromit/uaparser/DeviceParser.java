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
import io.gromit.uaparser.model.Device;

/**
 * The Class DeviceParser.
 */
public class DeviceParser {

	/** The patterns. */
	List<DevicePattern> patterns;

	/**
	 * Instantiates a new device parser.
	 *
	 * @param patterns
	 *            the patterns
	 */
	public DeviceParser(List<DevicePattern> patterns) {
		this.patterns = patterns;
	}

	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the device
	 */
	public Device parse(String agentString) {
		return this.parse(agentString, NoCache.NO_CACHE);
	}

	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @param cache
	 *            the cache
	 * @return the device
	 */
	public Device parse(String agentString, Cache cache) {
		if (agentString == null) {
			return null;
		}
		Device inCache = cache.getDevice(agentString);
		if (inCache != null) {
			return inCache;
		}
		String device = null;
		for (DevicePattern p : patterns) {
			if ((device = p.match(agentString)) != null) {
				break;
			}
		}
		if (device == null) {
			device = "Other";
		}
		Device deviceToReturn = new Device(device);
		cache.putDevice(agentString, deviceToReturn);
		return deviceToReturn;
	}

	/**
	 * From list.
	 *
	 * @param configList
	 *            the config list
	 * @return the device parser
	 */
	public static DeviceParser fromList(List<Map<String, String>> configList) {
		List<DevicePattern> configPatterns = new ArrayList<DevicePattern>();
		for (Map<String, String> configMap : configList) {
			configPatterns.add(DeviceParser.patternFromMap(configMap));
		}
		return new DeviceParser(configPatterns);
	}

	/**
	 * Pattern from map.
	 *
	 * @param configMap
	 *            the config map
	 * @return the device pattern
	 */
	protected static DevicePattern patternFromMap(Map<String, String> configMap) {
		String regex = configMap.get("regex");
		if (regex == null) {
			throw new IllegalArgumentException("Device is missing regex");
		}
		Pattern pattern = "i".equals(configMap.get("regex_flag")) // no ohter
																	// flags
																	// used (by
																	// now)
				? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
		return new DevicePattern(pattern, configMap.get("device_replacement"));
	}

	/**
	 * The Class DevicePattern.
	 */
	protected static class DevicePattern {

		/** The Constant SUBSTITUTIONS_PATTERN. */
		private static final Pattern SUBSTITUTIONS_PATTERN = Pattern.compile("\\$\\d");

		/** The pattern. */
		private final Pattern pattern;

		/** The device replacement. */
		private final String deviceReplacement;

		/**
		 * Instantiates a new device pattern.
		 *
		 * @param pattern
		 *            the pattern
		 * @param deviceReplacement
		 *            the device replacement
		 */
		public DevicePattern(Pattern pattern, String deviceReplacement) {
			this.pattern = pattern;
			this.deviceReplacement = deviceReplacement;
		}

		/**
		 * Match.
		 *
		 * @param agentString
		 *            the agent string
		 * @return the string
		 */
		public String match(String agentString) {
			Matcher matcher = pattern.matcher(agentString);
			if (!matcher.find()) {
				return null;
			}
			String device = null;
			if (deviceReplacement != null) {
				if (deviceReplacement.contains("$")) {
					device = deviceReplacement;
					for (String substitution : getSubstitutions(deviceReplacement)) {
						int i = Integer.valueOf(substitution.substring(1));
						String replacement = matcher.groupCount() >= i && matcher.group(i) != null
								? Matcher.quoteReplacement(matcher.group(i)) : "";
						device = device.replaceFirst("\\" + substitution, replacement);
					}
					device = device.trim();
				} else {
					device = deviceReplacement;
				}
			} else if (matcher.groupCount() >= 1) {
				device = matcher.group(1);
			}

			return device;
		}

		/**
		 * Gets the substitutions.
		 *
		 * @param deviceReplacement
		 *            the device replacement
		 * @return the substitutions
		 */
		private List<String> getSubstitutions(String deviceReplacement) {
			Matcher matcher = SUBSTITUTIONS_PATTERN.matcher(deviceReplacement);
			List<String> substitutions = new ArrayList<String>();
			while (matcher.find()) {
				substitutions.add(matcher.group());
			}
			return substitutions;
		}

	}

}