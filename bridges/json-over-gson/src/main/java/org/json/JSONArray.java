package org.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONArray {
	static JsonParser parser = new JsonParser();
	JsonArray contained;

	public JSONArray() {
		contained = new JsonArray();

	}

	public JSONArray(String input) throws JSONException {
		try {
			JsonElement el = parser.parse(input);
			if(el.isJsonArray())
				contained = el.getAsJsonArray();
			else
				throw new JSONException();
		} catch (Exception e) {
			throw new JSONException();
		}
	}

	public JSONArray put(Object val) throws JSONException {
		if(val instanceof String)
			contained.add((String) val);
		else if(val instanceof JSONObject)
			contained.add(((JSONObject) val).contained);
		else if(val instanceof JSONArray)
			contained.add(((JSONArray) val).contained);
		else
			throw new JSONException();
		return this;
	}

	public JSONArray put(int val) throws JSONException {
		contained.add(val);
		return this;
	}

	public JSONArray put(float val) throws JSONException {
		contained.add(val);
		return this;
	}

	public JSONArray put(double val) throws JSONException {
		contained.add(val);
		return this;
	}

	public Object get(int i) {
		JsonElement el = contained.get(i);
		if(el.isJsonPrimitive()) {
			JsonPrimitive p = el.getAsJsonPrimitive();
			if(p.isNumber()) {
				return p.getAsNumber();
			} else if(p.isString()) {
				return p.getAsString();
			} else if(p.isBoolean()) {
				return p.getAsBoolean();
			}
		} else if(el.isJsonArray()) {
			JSONArray ar = new JSONArray();
			ar.contained = el.getAsJsonArray();
			return ar;
		} else if(el.isJsonObject()) {
			JSONObject o = new JSONObject();
			o.contained = el.getAsJsonObject();
			return o;
		}
		return null;
	}


	@Override
	public String toString() {
		return contained.toString();
	}
}
