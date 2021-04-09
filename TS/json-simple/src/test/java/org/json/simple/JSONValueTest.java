package org.json.simple;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import static org.json.simple.JSONTestUtils.assertEquivalent;

public class JSONValueTest extends TestCase {
	public void testByteArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((byte[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new byte[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", JSONValue.toJSONString(new byte[] { 12 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", JSONValue.toJSONString(new byte[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((byte[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[] { 12 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new byte[] { -7, 22, 86, -99 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}

	public void testShortArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((short[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new short[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", JSONValue.toJSONString(new short[] { 12 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", JSONValue.toJSONString(new short[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((short[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new short[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new short[] { 12 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new short[] { -7, 22, 86, -99 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testIntArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((int[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new int[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", JSONValue.toJSONString(new int[] { 12 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", JSONValue.toJSONString(new int[] { -7, 22, 86, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((int[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new int[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new int[] { 12 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new int[] { -7, 22, 86, -99 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testLongArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((long[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new long[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", JSONValue.toJSONString(new long[] { 12 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,9223372036854775807,-99]", JSONValue.toJSONString(new long[] { -7, 22, 9223372036854775807L, -99 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((long[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new long[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new long[] { 12 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new long[] { -7, 22, 86, -99 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7,22,86,-99]", writer.toString());
	}
	
	public void testFloatArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((float[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new float[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12.8]", JSONValue.toJSONString(new float[] { 12.8f }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", JSONValue.toJSONString(new float[] { -7.1f, 22.234f, 86.7f, -99.02f }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((float[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new float[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new float[] { 12.8f }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12.8]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new float[] { -7.1f, 22.234f, 86.7f, -99.02f }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testDoubleArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((double[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new double[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[12.8]", JSONValue.toJSONString(new double[] { 12.8 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", JSONValue.toJSONString(new double[] { -7.1, 22.234, 86.7, -99.02 }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((double[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new double[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new double[] { 12.8 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[12.8]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new double[] { -7.1, 22.234, 86.7, -99.02 }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[-7.1,22.234,86.7,-99.02]", writer.toString());
	}
	
	public void testBooleanArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((boolean[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new boolean[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[true]", JSONValue.toJSONString(new boolean[] { true }));
		//ARGO_EQUIVALENT
		assertEquivalent("[true,false,true]", JSONValue.toJSONString(new boolean[] { true, false, true }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((boolean[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[] { true }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[true]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new boolean[] { true, false, true }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[true,false,true]", writer.toString());
	}
	
	public void testCharArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((char[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new char[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[\"a\"]", JSONValue.toJSONString(new char[] { 'a' }));
		//ARGO_EQUIVALENT
		assertEquivalent("[\"a\",\"b\",\"c\"]", JSONValue.toJSONString(new char[] { 'a', 'b', 'c' }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((char[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new char[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new char[] { 'a' }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[\"a\"]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new char[] { 'a', 'b', 'c' }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[\"a\",\"b\",\"c\"]", writer.toString());
	}
	
	public void testObjectArrayToString() throws IOException {
		//ARGO_ORIGINAL
		assertEquals("null", JSONValue.toJSONString((Object[])null));
		//ARGO_EQUIVALENT
		assertEquivalent("[]", JSONValue.toJSONString(new Object[0]));
		//ARGO_EQUIVALENT
		assertEquivalent("[\"Hello\"]", JSONValue.toJSONString(new Object[] { "Hello" }));
		//ARGO_EQUIVALENT
		assertEquivalent("[1,2,3]", JSONValue.toJSONString(new int[] { 1, 2, 3 }));
		//ARGO_EQUIVALENT
		assertEquivalent("[[1,2,3]]", JSONValue.toJSONString(new Object[] {new int[] { 1, 2, 3 }}));
		//ARGO_EQUIVALENT
		assertEquivalent("[\"Hello\",12,[1,2,3]]", JSONValue.toJSONString(new Object[] { "Hello", new Integer(12), new int[] { 1, 2, 3 } }));
		
		StringWriter writer;
		
		writer = new StringWriter();
		JSONValue.writeJSONString((Object[])null, writer);
		//ARGO_ORIGINAL
		assertEquals("null", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[0], writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[] { "Hello" }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[\"Hello\"]", writer.toString());
		
		writer = new StringWriter();
		JSONValue.writeJSONString(new Object[] { "Hello", new Integer(12), new int[] { 1, 2, 3} }, writer);
		//ARGO_EQUIVALENT
		assertEquivalent("[\"Hello\",12,[1,2,3]]", writer.toString());
	}
	
	public void testArraysOfArrays() throws IOException {
		
		StringWriter writer;
		
		final int[][][] nestedIntArray = new int[][][]{{{1}, {5}}, {{2}, {6}}};
		final String expectedNestedIntString = "[[[1],[5]],[[2],[6]]]";

		//ARGO_EQUIVALENT
		assertEquivalent(expectedNestedIntString, JSONValue.toJSONString(nestedIntArray));
		
		writer = new StringWriter();
		JSONValue.writeJSONString(nestedIntArray, writer);
		//ARGO_EQUIVALENT
		assertEquivalent(expectedNestedIntString, writer.toString());

		final String[][] nestedStringArray = new String[][]{{"a", "b"}, {"c", "d"}};
		final String expectedNestedStringString = "[[\"a\",\"b\"],[\"c\",\"d\"]]";

		//ARGO_EQUIVALENT
		assertEquivalent(expectedNestedStringString, JSONValue.toJSONString(nestedStringArray));
		
		writer = new StringWriter();
		JSONValue.writeJSONString(nestedStringArray, writer);
		//ARGO_EQUIVALENT
		assertEquivalent(expectedNestedStringString, writer.toString());
	}
}
