package org.json.simple;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import junit.framework.TestCase;

import static org.json.simple.JSONTestUtils.assertEquivalent;

public class JSONArrayTest extends TestCase {

	public void testJSONArray() {
		final JSONArray jsonArray = new JSONArray();

		assertEquivalent("[]", jsonArray.toJSONString());
	}

	public void testJSONArrayCollection() {
		final ArrayList testList = new ArrayList();
		testList.add("First item");
		testList.add("Second item");
		
		final JSONArray jsonArray = new JSONArray(testList);

		assertEquivalent("[\"First item\",\"Second item\"]", jsonArray.toJSONString());
	}

	public void testWriteJSONStringCollectionWriter() throws IOException, ParseException {
		final HashSet testSet = new HashSet();
		testSet.add("First item");
		testSet.add("Second item");
		
		final JSONArray jsonArray = new JSONArray(testSet);
		final StringWriter writer = new StringWriter();
		
		jsonArray.writeJSONString(writer);
		
		final JSONParser parser = new JSONParser();
		final JSONArray parsedArray = (JSONArray)parser.parse(writer.toString());
		
		assertTrue(parsedArray.containsAll(jsonArray));
		assertTrue(jsonArray.containsAll(parsedArray));
		assertEquals(2, jsonArray.size());
	}

	public void testToJSONStringCollection() throws ParseException {
		final HashSet testSet = new HashSet();
		testSet.add("First item");
		testSet.add("Second item");
		
		final JSONArray jsonArray = new JSONArray(testSet);
		
		final JSONParser parser = new JSONParser();
		final JSONArray parsedArray = (JSONArray)parser.parse(jsonArray.toJSONString());
		
		assertTrue(parsedArray.containsAll(jsonArray));
		assertTrue(jsonArray.containsAll(parsedArray));
		assertEquals(2, jsonArray.size());
	}

	public void testByteArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((byte[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new byte[0]));
		assertEquivalent("[12]", JSONArray.toJSONString(new byte[] { 12 }));
		assertEquivalent("[-7,22,86,-99]", JSONArray.toJSONString(new byte[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((byte[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new byte[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new byte[] { 12 }, writer);
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new byte[] { -7, 22, 86, -99 }, writer);
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testShortArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((short[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new short[0]));
		assertEquivalent("[12]", JSONArray.toJSONString(new short[] { 12 }));
		assertEquivalent("[-7,22,86,-99]", JSONArray.toJSONString(new short[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((short[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new short[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new short[] { 12 }, writer);
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new short[] { -7, 22, 86, -99 }, writer);
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testIntArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((int[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new int[0]));
		assertEquivalent("[12]", JSONArray.toJSONString(new int[] { 12 }));
		assertEquivalent("[-7,22,86,-99]", JSONArray.toJSONString(new int[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((int[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new int[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new int[] { 12 }, writer);
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new int[] { -7, 22, 86, -99 }, writer);
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testLongArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((long[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new long[0]));
		assertEquivalent("[12]", JSONArray.toJSONString(new long[] { 12 }));
		assertEquivalent("[-7,22,9223372036854775807,-99]", JSONArray.toJSONString(new long[] { -7, 22, 9223372036854775807L, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((long[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new long[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new long[] { 12 }, writer);
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new long[] { -7, 22, 86, -99 }, writer);
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testFloatArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((float[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new float[0]));
		assertEquivalent("[12.8]", JSONArray.toJSONString(new float[] { 12.8f }));
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", JSONArray.toJSONString(new float[] { -7.1f, 22.234f, 86.7f, -99.02f }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((float[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new float[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new float[] { 12.8f }, writer);
		assertEquivalent("[12.8]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new float[] { -7.1f, 22.234f, 86.7f, -99.02f }, writer);
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testDoubleArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((double[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new double[0]));
		assertEquivalent("[12.8]", JSONArray.toJSONString(new double[] { 12.8 }));
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", JSONArray.toJSONString(new double[] { -7.1, 22.234, 86.7, -99.02 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((double[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new double[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new double[] { 12.8 }, writer);
		assertEquivalent("[12.8]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new double[] { -7.1, 22.234, 86.7, -99.02 }, writer);
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testBooleanArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((boolean[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new boolean[0]));
		assertEquivalent("[true]", JSONArray.toJSONString(new boolean[] { true }));
		assertEquivalent("[true,false,true]", JSONArray.toJSONString(new boolean[] { true, false, true }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((boolean[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new boolean[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new boolean[] { true }, writer);
		assertEquivalent("[true]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new boolean[] { true, false, true }, writer);
		assertEquivalent("[true,false,true]", writer.toString());
	}
	
	public void testCharArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((char[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new char[0]));
		assertEquivalent("[\"a\"]", JSONArray.toJSONString(new char[] { 'a' }));
		assertEquivalent("[\"a\",\"b\",\"c\"]", JSONArray.toJSONString(new char[] { 'a', 'b', 'c' }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((char[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new char[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new char[] { 'a' }, writer);
		assertEquivalent("[\"a\"]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new char[] { 'a', 'b', 'c' }, writer);
		assertEquivalent("[\"a\",\"b\",\"c\"]", writer.toString());
	}
	
	public void testObjectArrayToString() throws IOException {
		assertEquals("null", JSONArray.toJSONString((Object[])null));
		assertEquivalent("[]", JSONArray.toJSONString(new Object[0]));
		assertEquivalent("[\"Hello\"]", JSONArray.toJSONString(new Object[] { "Hello" }));
		assertEquivalent("[\"Hello\",12,[1,2,3]]", JSONArray.toJSONString(new Object[] { "Hello", new Integer(12), new int[] { 1, 2, 3 } }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONArray.writeJSONString((Object[])null, writer);
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new Object[0], writer);
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new Object[] { "Hello" }, writer);
		assertEquivalent("[\"Hello\"]", writer.toString());
		
		writer = new StringWriter();
		JSONArray.writeJSONString(new Object[] { "Hello", new Integer(12), new int[] { 1, 2, 3} }, writer);
		assertEquivalent("[\"Hello\",12,[1,2,3]]", writer.toString());
	}


	public void testIteration() throws ParseException {
		JSONArray a = new JSONArray("[1,2,3]");
		int i = 0;
		for (Object child : a) {
			i++;
		}
		assertEquals(3, i);
	}

}
