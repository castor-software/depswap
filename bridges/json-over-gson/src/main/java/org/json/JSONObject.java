package org.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONObject {
	static JsonParser parser = new JsonParser();
	JsonObject contained;

	public JSONObject() {
		contained = new JsonObject();

	}

	public JSONObject(String input) throws JSONException {
		try {
			JsonElement el = parser.parse(input);
			if (el.isJsonObject())
				contained = el.getAsJsonObject();
			else
				throw new JSONException();
		} catch (Exception e) {
			throw new JSONException();
		}
	}

	public JSONObject put(String key, Object val) throws JSONException {
		if(val instanceof String)
			contained.addProperty(key, (String) val);
		else if(val instanceof JSONObject)
			contained.add(key, ((JSONObject) val).contained);
		else if(val instanceof JSONArray)
			contained.add(key, ((JSONArray) val).contained);
		else
			throw new JSONException();
		return this;
	}

	public JSONObject put(String key, int val) throws JSONException {
		contained.addProperty(key,val);
		return this;
	}

	public JSONObject put(String key, float val) throws JSONException {
		contained.addProperty(key,val);
		return this;
	}

	public JSONObject put(String key, double val) throws JSONException {
		contained.addProperty(key,val);
		return this;
	}

	public Object get(String key) {
		JsonElement el = contained.get(key);
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
