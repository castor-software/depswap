package se.kth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CreateJsonTest {

	@Test
	public void createSimpleJSONObject() throws JSONException {
		JSONObject o = CreateJson.createSimpleJSONObject();
		assertEquals("chaine-val", o.get("chaine"));
		assertEquals(0.3, o.get("flotant"));
		assertEquals(3, o.get("entier"));
		//assertEquals("{\"chaine\":\"chaine-val\",\"flotant\":0.3,\"entier\":3}", o.toString());
	}

	@Test
	public void createNestedJSONObject() throws JSONException {
		JSONObject o = CreateJson.createNestedJSONObject();
		assertEquals("chaine-val", o.get("chaine"));
		assertEquals(0.3, o.get("flotant"));
		assertEquals(3, o.get("entier"));

		JSONObject on = (JSONObject) o.get("nested");
		assertEquals("chaine-val", on.get("chaine"));
		assertEquals(0.3, on.get("flotant"));
		assertEquals(3, on.get("entier"));

		//assertEquals("{\"chaine\":\"chaine-val\",\"flotant\":0.3,\"entier\":3,\"nested\":{\"chaine\":\"chaine-val\",\"flotant\":0.3,\"entier\":3}}", o.toString());
	}

	@Test
	public void createSimpleJSONArray() throws JSONException {
		JSONArray array = CreateJson.createSimpleJSONArray();
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
		//assertEquals("[1,2,3]", array.toString());
	}

	@Test
	public void createNestedJSONArray() throws JSONException {
		JSONArray array = CreateJson.createNestedJSONArray();
		JSONArray array1 = (JSONArray) array.get(0);
		assertEquals(0.1, array1.get(0));
		assertEquals(0.2, array1.get(1));
		assertEquals(0.3, array1.get(2));
		JSONArray array2 = (JSONArray) array.get(1);
		assertEquals("un", array2.get(0));
		assertEquals("deux", array2.get(1));
		assertEquals("trois", array2.get(2));
		//assertEquals("[[0.1,0.2,0.3],[\"un\",\"deux\",\"trois\"]]", array.toString());
	}

	@Test
	public void serializationJSONObject() throws JSONException {
		JSONObject o = new JSONObject(CreateJson.createNestedJSONObject().toString());
		assertEquals("chaine-val", o.get("chaine"));
		assertTrue(compareNumber(0.3, (Number) o.get("flotant")));
		assertTrue(compareNumber(3, (Number) o.get("entier")));

		JSONObject on = (JSONObject) o.get("nested");
		assertEquals("chaine-val", on.get("chaine"));
		assertTrue(compareNumber(0.3, (Number) on.get("flotant")));
		assertTrue(compareNumber(3, (Number) on.get("entier")));

	}

	@Test
	public void serializationJSONArray() throws JSONException {

		JSONArray array = new JSONArray(CreateJson.createNestedJSONArray().toString());
		JSONArray array1 = (JSONArray) array.get(0);
		assertTrue(compareNumber(0.1, (Number)array1.get(0)));
		assertTrue(compareNumber(0.2, (Number)array1.get(1)));
		assertTrue(compareNumber(0.3, (Number)array1.get(2)));
		JSONArray array2 = (JSONArray) array.get(1);
		assertEquals("un", array2.get(0));
		assertEquals("deux", array2.get(1));
		assertEquals("trois", array2.get(2));
	}

	public static boolean compareNumber(Number n, Number m) {
		return new BigDecimal(n.toString()).compareTo(new BigDecimal(m.toString())) == 0;
	}
}