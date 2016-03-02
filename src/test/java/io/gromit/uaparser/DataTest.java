package io.gromit.uaparser;

import java.util.HashSet;
import java.util.Random;

import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public abstract class DataTest<T> {
	
	protected Random random;
	protected Random seedRandom = new Random();

	@Before
	public void initialize() {
		random = new Random();
	}

	protected abstract T getRandomInstance(long seed, StringGenerator g);

	protected abstract T getBlankInstance();

	protected long nextSeed() {
		return seedRandom.nextLong();
	}

	protected class StringGenerator {
		private HashSet<String> generated = new HashSet<String>();
		private Random strRand;
		protected final boolean unique;

		public StringGenerator(long seed, boolean unique) {
			strRand = new Random(seed);
			this.unique = unique;
		}

		public String getString(int maxLen) {
			if (!unique) {
				return genString(maxLen);
			}
			while (true) {
				String ret = genString(maxLen);
				if (generated.add(ret)) {
					return ret;
				}
			}
		}

		private String genString(int maxLen) {
			int len = strRand.nextInt(maxLen + 1);
			StringBuilder sb = new StringBuilder(len);
			for (int i = len; i-- > 0;) {
				// ascii printable range 32 to 126
				sb.append((char) (strRand.nextInt(126 - 32 + 1) + 32));
			}
			return sb.toString();
		}
	}

	@Test
	public void testEqualsAndHashCode() {
		for (int i = 1000; i-- > 0;) {
			long seed = nextSeed();
			T first = getRandomInstance(seed, new StringGenerator(seed, false));
			T second = getRandomInstance(seed, new StringGenerator(seed, false));
			assertThat(first, is(second));
			assertThat(first.hashCode(), is(second.hashCode()));
		}
	}

	@Test
	public void testNotEquals() {
		for (int i = 1000; i-- > 0;) {
			StringGenerator uniqueGenerator = new StringGenerator(nextSeed(), true);
			T first = getRandomInstance(nextSeed(), uniqueGenerator);
			T second = getRandomInstance(nextSeed(), uniqueGenerator);
			assertThat(first, is(not(second)));
		}
	}

	@Test
	public void testBlankInstances() {
		T first = getBlankInstance(), second = getBlankInstance();
		assertThat(first, is(second));
		assertThat(first.hashCode(), is(second.hashCode()));
	}
}
