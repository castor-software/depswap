package se.kth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateJson {
	public static JSONObject createSimpleJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("chaine", "chaine-val");
		obj.put("entier", 3);
		obj.put("flotant", 0.3);

		return obj;
	}

	public static JSONObject createNestedJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		JSONObject obj2 = new JSONObject();
		obj.put("chaine", "chaine-val");
		obj.put("entier", 3);
		obj.put("flotant", 0.3);

		obj2.put("chaine", "chaine-val");
		obj2.put("entier", 3);
		obj2.put("flotant", 0.3);

		obj.put("nested", obj2);


		return obj;
	}

	public static JSONArray createSimpleJSONArray()  throws JSONException {
		JSONArray array = new JSONArray();
		array.put(1);
		array.put(2);
		array.put(3);

		return array;
	}

	public static JSONArray createNestedJSONArray() throws JSONException {
		JSONArray array = new JSONArray();

		JSONArray array2 = new JSONArray();
		array2.put(0.1);
		array2.put(0.2);
		array2.put(0.3);

		JSONArray array3 = new JSONArray();
		array3.put("un");
		array3.put("deux");
		array3.put("trois");

		array.put(array2);
		array.put(array3);

		return array;
	}
}
