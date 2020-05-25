package se.kth.assertteam;

import org.junit.Test;

import static org.junit.Assert.*;

public class CsvConfigurationGeneratorTest {

	@Test
	public void testCsvParsing() throws Exception {
		ConfigurationGenerator gen = new CsvConfigurationGenerator("src/test/resources/test-cfg-in.csv");

		assertEquals(gen.getTotal(), 6);

		assertEquals(gen.getCurrent(), 6);

		assertEquals(gen.getConfiguration().toJSON().size(),8);

		assertEquals(gen.getCurrent(), 5);
	}

}