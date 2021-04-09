package se.kth.castor.yasjf4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NullWrapingTest {

	static double epsilon = 0.0000d;

	public static boolean equivalentModuloEpsilon(Object a, Object b) {
		if(a == null && b != null) return false;
		if(b==null) return false;
		if(a.toString().equals(b.toString())) return true;
		if(a instanceof Number && b instanceof Number) {
			return Math.abs(((Number) a).doubleValue() - ((Number) b).doubleValue()) <= epsilon;
		}
		return false;
	}


	@Test
	public void testArrayNullPrint() throws Exception {
		String sArray ="[null]";
		JArray a = (JArray) JFactory.parse(sArray);
		assertEquals(sArray, a.YASJF4J_toString());
		assertEquals(JNull.getInstance(), a.YASJF4J_get(0));
	}


	@Test
	public void testArrayDoublePrint() throws Exception {
		String sArray ="[23.45e-4]";
		JArray a = (JArray) JFactory.parse(sArray);
		assertTrue(equivalentModuloEpsilon(0.002345d, a.YASJF4J_get(0)));
	}
}
