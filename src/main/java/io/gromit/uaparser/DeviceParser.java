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
	private final List<DevicePattern> patterns;

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
		Device device = cache.getDevice(agentString);
		if (device != null) {
			return device;
		}
		for (DevicePattern p : patterns) {
			if ((device = p.match(agentString)) != null) {
				cache.putDevice(agentString, device);
				return device;
			}
		}
		device = new Device("Other", null, null);
		cache.putDevice(agentString, device);
		return device;
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
		String regex_flag = configMap.get("regex_flag");

		if (regex == null) {
			throw new IllegalArgumentException("Device is missing regex");
		}

		Pattern pattern;
		if (regex_flag != null && regex_flag.equals("i")) {
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(regex);
		}

		/**
		 * To maintains backwards compatibility the familyReplacement field has
		 * been named "device_replacement"
		 */
		return new DevicePattern(pattern, configMap.get("device_replacement"), configMap.get("brand_replacement"),
				configMap.get("model_replacement"));
	}

	/**
	 * The Class DevicePattern.
	 */
	protected static class DevicePattern {

		/** The Constant GROUP_PATTERN. */
		private static final Pattern GROUP_PATTERN = Pattern.compile("\\$(\\d+)");

		/** The pattern. */
		private final Pattern pattern;
		
		/** The family replacement. */
		private final String familyReplacement;
		
		/** The brand replacement. */
		private final String brandReplacement;
		
		/** The model replacement. */
		private final String modelReplacement;

		/**
		 * Instantiates a new device pattern.
		 *
		 * @param pattern the pattern
		 * @param familyReplacement the family replacement
		 * @param brandReplacement the brand replacement
		 * @param modelReplacement the model replacement
		 */
		public DevicePattern(Pattern pattern, String familyReplacement, String brandReplacement,
				String modelReplacement) {
			this.pattern = pattern;
			this.familyReplacement = familyReplacement;
			this.brandReplacement = brandReplacement;
			this.modelReplacement = modelReplacement;
		}

		/**
		 * Perform replacement.
		 *
		 * @param matcher the matcher
		 * @param replacement the replacement
		 * @return the string
		 */
		private String performReplacement(Matcher matcher, String replacement) {
			int count = matcher.groupCount();
			StringBuffer buffer = new StringBuffer();
			Matcher replaceMatcher = GROUP_PATTERN.matcher(replacement);
			while (replaceMatcher.find()) {
				String group = null;
				try {
					int id = Integer.parseInt(replaceMatcher.group(1));
					if (id >= 0 && id <= count) {
						group = matcher.group(id);
					}
				} catch (NumberFormatException ignored) {
				} catch (IllegalArgumentException ignored) {
				} catch (IndexOutOfBoundsException ignored) {
				}
				replaceMatcher.appendReplacement(buffer, group == null ? "" : Matcher.quoteReplacement(group));
			}
			replacement = buffer.toString();
			return replacement;
		}

		/**
		 * Replace.
		 *
		 * @param matcher the matcher
		 * @param replacement the replacement
		 * @param position the position
		 * @return the string
		 */
		private String replace(Matcher matcher, String replacement, int position) {
			if (replacement == null) {
				if (position > 0) {
					replacement = "$" + position;
				} else {
					return null;
				}
			}

			if (replacement.contains("$")) {
				replacement = performReplacement(matcher, replacement);
			}
			replacement = replacement.trim();
			if (replacement.length() == 0) {
				return null;
			} else {
				return replacement;
			}
		}

		/**
		 * Match.
		 *
		 * @param agentString the agent string
		 * @return the device
		 */
		public Device match(String agentString) {

			Matcher matcher = pattern.matcher(agentString);

			if (!matcher.find()) {
				return null;
			}

			String family = replace(matcher, familyReplacement, 1);
			String brand = replace(matcher, brandReplacement, -1);
			String model = replace(matcher, modelReplacement, 1);

			if (family != null) {
				return new Device(family, brand, model);
			} else {
				return null;
			}

		}

	}

}