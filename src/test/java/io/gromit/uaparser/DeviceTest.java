package io.gromit.uaparser;

import io.gromit.uaparser.model.Device;

public class DeviceTest extends DataTest<Device> {
	
	@Override
	protected Device getRandomInstance(long seed, StringGenerator g) {
		random.setSeed(seed);
		String family = g.getString(256);
		String brand = g.getString(256);
		String model = g.getString(256);
		return new Device(family, brand, model);
	}

	@Override
	protected Device getBlankInstance() {
		return new Device(null, null, null);
	}
}