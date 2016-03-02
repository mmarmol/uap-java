package io.gromit.uaparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.Before;
import org.yaml.snakeyaml.Yaml;

import io.gromit.uaparser.model.Client;
import io.gromit.uaparser.model.Device;
import io.gromit.uaparser.model.OS;
import io.gromit.uaparser.model.UserAgent;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ParserTest {
	
	final String TESTS_PATH = "https://raw.githubusercontent.com/ua-parser/uap-core/master/tests/";
	final String TEST_RESOURCE_PATH = "https://raw.githubusercontent.com/ua-parser/uap-core/master/test_resources/";

	Yaml yaml = new Yaml();
	Parser parser;

	@Before
	public void initParser() throws Exception {
		parser = new Parser();
	}

	@Test
	public void testParseUserAgent() {
		testUserAgentFromYaml(TESTS_PATH+"test_ua.yaml");
	}

	@Test
	public void testParseOS() {
		testOSFromYaml(TESTS_PATH+"test_os.yaml");
	}

	@Test
	public void testParseAdditionalOS() {
		testOSFromYaml(TEST_RESOURCE_PATH+"additional_os_tests.yaml");
	}

	@Test
	public void testParseDevice() {
		testDeviceFromYaml(TESTS_PATH+"test_device.yaml");
	}

	@Test
	public void testParseFirefox() {
		testUserAgentFromYaml(TEST_RESOURCE_PATH+"firefox_user_agent_strings.yaml");
	}

	@Test
	public void testParsePGTS() {
		testUserAgentFromYaml(TEST_RESOURCE_PATH+"pgts_browser_list.yaml");
	}

	@Test
	public void testParseAll() {
		String agentString1 = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; fr; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5,gzip(gfe),gzip(gfe)";
		String agentString2 = "Mozilla/5.0 (iPhone; CPU iPhone OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3";

		Client expected1 = new Client(new UserAgent("Firefox", "3", "5", "5"),
				new OS("Mac OS X", "10", "4", null, null), new Device("Other", null, null));
		Client expected2 = new Client(new UserAgent("Mobile Safari", "5", "1", null),
				new OS("iOS", "5", "1", "1", null), new Device("iPhone", "Apple", "iPhone"));

		assertThat(parser.parse(agentString1), is(expected1));
		assertThat(parser.parse(agentString2), is(expected2));
	}

	@Test
	public void testReplacementQuoting() throws Exception {
		String testConfig = "user_agent_parsers:\n" + "  - regex: 'ABC([\\\\0-9]+)'\n"
				+ "    family_replacement: 'ABC ($1)'\n" + "os_parsers:\n"
				+ "  - regex: 'CatOS OH-HAI=/\\^\\.\\^\\\\='\n" + "    os_replacement: 'CatOS 9000'\n"
				+ "device_parsers:\n" + "  - regex: 'CashPhone-([\\$0-9]+)\\.(\\d+)\\.(\\d+)'\n"
				+ "    device_replacement: 'CashPhone $1'\n"
				+ "    brand_replacement: 'CashPhone'\n"
				+ "    model_replacement: '$1'\n";

		Parser testParser = parserFromStringConfig(testConfig);
		Client result = testParser.parse("ABC12\\34 (CashPhone-$9.0.1 CatOS OH-HAI=/^.^\\=)");
		assertThat(result.userAgent.family, is("ABC (12\\34)"));
		assertThat(result.os.family, is("CatOS 9000"));
		assertThat(result.device.family, is("CashPhone $9"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConfigThrows() throws Exception {
		parserFromStringConfig("user_agent_parsers:\n  - family_replacement: 'a'");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testUserAgentFromYaml(String url) {
		InputStream yamlStream;
		try {
			yamlStream = new URL(url).openStream();
		} catch (IOException e) {
			throw new IllegalArgumentException(url);
		}
		List<Map> testCases = (List<Map>) ((Map) yaml.load(yamlStream)).get("test_cases");
		for (Map<String, String> testCase : testCases) {
			// Skip tests with js_ua as those overrides are not yet supported in
			// java
			if (testCase.containsKey("js_ua"))
				continue;

			String uaString = testCase.get("user_agent_string");
			assertThat(uaString, parser.parseUserAgent(uaString), is(UserAgent.fromMap(testCase)));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testOSFromYaml(String url) {
		InputStream yamlStream;
		try {
			yamlStream = new URL(url).openStream();
		} catch (IOException e) {
			throw new IllegalArgumentException(url);
		}
		List<Map> testCases = (List<Map>) ((Map) yaml.load(yamlStream)).get("test_cases");
		for (Map<String, String> testCase : testCases) {
			// Skip tests with js_ua as those overrides are not yet supported in
			// java
			if (testCase.containsKey("js_ua"))
				continue;

			String uaString = testCase.get("user_agent_string");
			assertThat(uaString, parser.parseOS(uaString), is(OS.fromMap(testCase)));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	void testDeviceFromYaml(String url) {
		InputStream yamlStream;
		try {
			yamlStream = new URL(url).openStream();
		} catch (IOException e) {
			throw new IllegalArgumentException(url);
		}
		List<Map> testCases = (List<Map>) ((Map) yaml.load(yamlStream)).get("test_cases");
		for (Map<String, String> testCase : testCases) {

			String uaString = testCase.get("user_agent_string");
			assertThat(uaString, parser.parseDevice(uaString), is(Device.fromMap(testCase)));
		}
	}

	Parser parserFromStringConfig(String configYamlAsString) throws Exception {
		InputStream yamlInput = new ByteArrayInputStream(configYamlAsString.getBytes("UTF8"));
		return new Parser(yamlInput);
	}
}
