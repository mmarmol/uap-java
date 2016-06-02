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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import io.gromit.uaparser.cache.Cache;
import io.gromit.uaparser.cache.NoCache;
import io.gromit.uaparser.model.Client;
import io.gromit.uaparser.model.Device;
import io.gromit.uaparser.model.OS;
import io.gromit.uaparser.model.Browser;

// TODO: Auto-generated Javadoc
/**
 * The Class Parser.
 */
public class Parser {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(Parser.class);
	
	public static final String FAIL_SAFE_URL = "io.gromit.uaparser.fail.safe.url";
	
	/** The scheduled executor service. */
	private ScheduledExecutorService scheduledExecutorService;
	
	/** The ua regex yaml. */
	private String uaRegexYaml = "https://raw.githubusercontent.com/ua-parser/uap-core/master/regexes.yaml";
	
	/** The ua regex yaml m d5. */
	private String uaRegexYamlMD5 = null;
	
	/** The cache. */
	private Cache cache = NoCache.NO_CACHE;
	
	/** The parsers. */
	private Parsers parsers = null;
	
	/** The clean cache on update. */
	private Boolean cleanCacheOnUpdate = false;

	/**
	 * Instantiates a new parser.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Parser() throws IOException {
		initialize(uaRegexYaml);
	}

	/**
	 * Instantiates a new parser.
	 *
	 * @param regexYaml
	 *            the regex yaml
	 */
	public Parser(InputStream regexYaml) {
		initialize(regexYaml);
	}

	/**
	 * Ua regex yaml.
	 *
	 * @param url the url
	 * @return the parser
	 */
	public Parser uaRegexYaml(String url){
		this.uaRegexYaml=url;
		return this;
	}

	/**
	 * Cache.
	 *
	 * @param cache the cache
	 * @return the parser
	 */
	public Parser cache(Cache cache){
		this.cache = cache;
		return this;
	}
	
	/**
	 * Clean cache on update.
	 *
	 * @param cleanCacheOnUpdate the clean cache on update
	 * @return the parser
	 */
	public Parser cleanCacheOnUpdate(Boolean cleanCacheOnUpdate){
		this.cleanCacheOnUpdate = cleanCacheOnUpdate;
		return this;
	}
	
	/**
	 * Start schedule.
	 *
	 * @return the parser
	 */
	public Parser startSchedule(){
		scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				initialize(uaRegexYaml);
			}
		}, 0, 1, TimeUnit.HOURS);
		return this;
	}
	
	/**
	 * Stop schedule.
	 *
	 * @return the parser
	 */
	public Parser stopSchedule(){
		scheduledExecutorService.shutdown();
		return this;
	}
	
	/**
	 * Parses the.
	 *
	 * @param agentString
	 *            the agent string
	 * @return the client
	 */
	public Client parse(String agentString) {
		Parsers parsersForCall = parsers;
		Browser browser = parsersForCall.uaParser.parse(agentString, cache);
		OS os = parsersForCall.osParser.parse(agentString, cache);
		Device device = parsersForCall.deviceParser.parse(agentString, cache);
		return new Client(agentString, browser, os, device);
	}

	/**
	 * Parses the browser.
	 *
	 * @param agentString the agent string
	 * @return the browser
	 */
	public Browser parseBrowser(String agentString){
		return parsers.uaParser.parse(agentString, cache);
	}
	
	/**
	 * Parses the os.
	 *
	 * @param agentString the agent string
	 * @return the os
	 */
	public OS parseOS(String agentString){
		return parsers.osParser.parse(agentString, cache);
	}

	/**
	 * Parses the device.
	 *
	 * @param agentString the agent string
	 * @return the device
	 */
	public Device parseDevice(String agentString){
		return parsers.deviceParser.parse(agentString, cache);
	}

	/**
	 * Initialize.
	 *
	 * @param url the url
	 */
	private void initialize(String url){
		try{
			initialize(new URL(url).openStream());
			logger.info("reloaded ua-parser from remote url {}",url);
		}catch(Exception e){
			logger.error("error loading from remote",e);
			if(StringUtils.isNotBlank(System.getProperty(FAIL_SAFE_URL))
					&& !System.getProperty(FAIL_SAFE_URL).equalsIgnoreCase(url)){
				logger.info("re-loading from ua-parser fail safe url {}",url);
				initialize(System.getProperty(FAIL_SAFE_URL));
			}
		}
	}
	
	/**
	 * Initialize.
	 *
	 * @param regexYaml
	 *            the regex yaml
	 */
	private void initialize(InputStream regexYaml) {
		byte[] bytes = null;
		String newMD5 = null;
		try {
			bytes = IOUtils.toByteArray(regexYaml);
			newMD5 = new String(MessageDigest.getInstance("MD5").digest(bytes));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(newMD5.equals(uaRegexYamlMD5)){
			logger.info("same MD5 content for both files, do not load it");
			return;
		}
		uaRegexYamlMD5 = newMD5;
		Yaml yaml = new Yaml(new SafeConstructor());
		@SuppressWarnings("unchecked")
		Map<String, List<Map<String, String>>> regexConfig = (Map<String, List<Map<String, String>>>) yaml
				.load(new ByteArrayInputStream(bytes));

		List<Map<String, String>> uaParserConfigs = regexConfig.get("user_agent_parsers");
		if (uaParserConfigs == null) {
			throw new IllegalArgumentException("user_agent_parsers is missing from yaml");
		}
		UserAgentParser uaParser = UserAgentParser.fromList(uaParserConfigs);

		List<Map<String, String>> osParserConfigs = regexConfig.get("os_parsers");
		if (osParserConfigs == null) {
			throw new IllegalArgumentException("os_parsers is missing from yaml");
		}
		OSParser osParser = OSParser.fromList(osParserConfigs);

		List<Map<String, String>> deviceParserConfigs = regexConfig.get("device_parsers");
		if (deviceParserConfigs == null) {
			throw new IllegalArgumentException("device_parsers is missing from yaml");
		}
		DeviceParser deviceParser = DeviceParser.fromList(deviceParserConfigs);
		parsers = new Parsers(uaParser, osParser, deviceParser);
		if(cleanCacheOnUpdate){
			cache.clean();
		}
	}
	
	/**
	 * The Class Parsers.
	 */
	public static class Parsers {
		
		/** The ua parser. */
		public final UserAgentParser uaParser;

		/** The os parser. */
		public final OSParser osParser;

		/** The device parser. */
		public final DeviceParser deviceParser;

		/**
		 * Instantiates a new parsers.
		 *
		 * @param uaParser the ua parser
		 * @param osParser the os parser
		 * @param deviceParser the device parser
		 */
		public Parsers(UserAgentParser uaParser, OSParser osParser, DeviceParser deviceParser) {
			this.uaParser = uaParser;
			this.osParser = osParser;
			this.deviceParser = deviceParser;
		}
		
	}
}
