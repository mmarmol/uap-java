package io.gromit.uaparser;

import io.gromit.uaparser.model.OS;

public class OSTest extends DataTest<OS> {

	@Override
	protected OS getRandomInstance(long seed, StringGenerator g) {
		random.setSeed(seed);
		String family = g.getString(256), major = (random.nextBoolean() ? g.getString(8) : null),
				minor = (random.nextBoolean() ? g.getString(8) : null),
				patch = (random.nextBoolean() ? g.getString(8) : null),
				patchMinor = (random.nextBoolean() ? g.getString(8) : null);
		return new OS(family, major, minor, patch, patchMinor);
	}

	@Override
	protected OS getBlankInstance() {
		return new OS(null, null, null, null, null);
	}
}