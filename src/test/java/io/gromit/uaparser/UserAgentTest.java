package io.gromit.uaparser;

import io.gromit.uaparser.model.Browser;

public class UserAgentTest extends DataTest<Browser> {
	
	@Override
	protected Browser getRandomInstance(long seed, StringGenerator g) {
		random.setSeed(seed);
		String family = g.getString(256), major = (random.nextBoolean() ? g.getString(8) : null),
				minor = (random.nextBoolean() ? g.getString(8) : null),
				patch = (random.nextBoolean() ? g.getString(8) : null);
		return new Browser(family, major, minor, patch);
	}

	@Override
	protected Browser getBlankInstance() {
		return new Browser(null, null, null, null);
	}
}