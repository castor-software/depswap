package org.json;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class JSONArray {
	JArray json;

	public JSONArray(JArray o) {
		json = o;
	}

	//<init>()V
	public JSONArray() {
		json = JFactory.createJArray();
	}

	//<init>(Ljava/lang/String;)V
	public JSONArray(String in) throws JSONException {
		try {
			json = (JArray) JFactory.parse(in);
		} catch (JException e) {
			throw new JSONException();
		}
	}
	//<init>(Ljava/util/Collection;)V
	public JSONArray(Collection<?> collection) throws JSONException {
		if (collection == null) {
			json = JFactory.createJArray();
		} else {
			json = JFactory.createJArray();

			try {
				addAll(collection, false);
			} catch (JException e) {
				throw new JSONException();
			}
		}
	}

	public JSONArray(Object array) throws JSONException {
		this();
		if (!array.getClass().isArray() && !(array instanceof JSONArray)) {
			throw new JSONException();
		}
		if(array instanceof JSONArray) {
			JSONArray a = (JSONArray) array;
			try {
				for(int i = 0; i < a.length(); i++) {
					json.YASJF4J_add(JSONObject.unshield(a.get(i)));
				}
			} catch (JException e) {
				throw new JSONException();
			}
		} else {
			try {
				this.addAll(array, true);
			} catch (JException e) {
				throw new JSONException();
			}
		}
	}


	//put(Ljava/lang/Object;)Lorg/json/JSONArray;
	public JSONArray put(Object value) throws JSONException {
		try {
			json.YASJF4J_add(JSONObject.unshield(value));
		} catch (JException e) {
			throw new JSONException();
		}
		return this;
	}
	//put(Ljava/lang/Object;)Lorg/json/JSONArray;
	public JSONArray put( int index, Object value) throws JSONException {
		try {
			json.YASJF4J_set(index, JSONObject.unshield(value));
		} catch (JException e) {
			throw new JSONException();
		}
		return this;
	}



	//get(I)Ljava/lang/Object;
	public Object get(int index) {
		try {
			return JSONObject.shield(json.YASJF4J_get(index));
		} catch (JException e) {
			e.printStackTrace();
			return null;
		}
	}

	//get(Ljava/lang/String;)Z;
	public boolean getBoolean(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if (object.equals(Boolean.FALSE)
					|| (object instanceof String && ((String) object)
					.equalsIgnoreCase("false"))) {
				return false;
			} else if (object.equals(Boolean.TRUE)
					|| (object instanceof String && ((String) object)
					.equalsIgnoreCase("true"))) {
				return true;
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getDouble(Ljava/lang/String;)D
	public double getDouble(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof Number) {
				return ((Number)object).doubleValue();
			}
			try {
				return Double.parseDouble(object.toString());
			} catch (Exception e) {
				throw new JSONException();
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getFloat(Ljava/lang/String;)F
	public float getFloat(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof Number) {
				return ((Number)object).floatValue();
			}
			try {
				return Float.parseFloat(object.toString());
			} catch (Exception e) {
				throw new JSONException();
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getInt(Ljava/lang/String;)I
	public int getInt(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof Number) {
				return ((Number)object).intValue();
			}
			try {
				return Integer.parseInt(object.toString());
			} catch (Exception e) {
				throw new JSONException();
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getLong(Ljava/lang/String;)J
	public long getLong(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof Number) {
				return ((Number)object).longValue();
			}
			try {
				return Long.parseLong(object.toString());
			} catch (Exception e) {
				throw new JSONException();
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getString(Ljava/lang/String;)Ljava/lang/String;
	public String getString(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if (object instanceof String) {
				return (String) object;
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;
	public JSONObject getJSONObject(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if (object instanceof JObject) {
				return (JSONObject) JSONObject.shield(object);
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	//getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;
	public JSONArray getJSONArray(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if (object instanceof JArray) {
				return (JSONArray) JSONObject.shield(object);
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	public Object opt(int index) {
		return (index < 0 || index >= this.length()) ? null : get(index);
	}
	//optInt(Ljava/lang/String;)I
	public int optInt(int key, int defaultValue) {
		try {
			return getInt(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	//optInt(Ljava/lang/String;I)I
	public int optInt(int key) {
		return optInt(key, 0);
	}

	//optString(Ljava/lang/String;)Ljava/lang/String;
	public String optString(int key) {
		return optString(key,"");
	}

	//optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	public String optString(int key, String defaultValue) {
		try {
			return getString(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	//optBoolean(Ljava/lang/String;)Z
	public boolean optBoolean(int key) {
		try {
			return getBoolean(key);
		} catch (Exception e) {
		}
		return false;
	}
	//optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject
	public JSONObject optJSONObject(int key) {
		try {
			return getJSONObject(key);
		} catch (Exception e) {
		}
		return null;
	}

	//optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;
	public JSONArray optJSONArray(int key) {
		try {
			return getJSONArray(key);
		} catch (Exception e) {
		}
		return null;
	}

	//optLong(Ljava/lang/String;)I
	public long optLong(int key, long defaultValue) {
		try {
			return getLong(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	//optLong(Ljava/lang/String;J)J
	public long optLong(int key) {
		return optLong(key, 0);
	}

	//toString()Ljava/lang/String;
	@Override
	public String toString() {
		return json.YASJF4J_toString();
	}

	//length()I
	public int length() {
		return json.YASJF4J_size();
	}

	public boolean isNull(int index) {
		return JSONObject.NULL.equals(this.opt(index));
	}

	//iterator()Ljava/util/Iterator;
	public Iterator iterator() {
		return new Iterator<Object>() {
			int index = 0;
			JArray jsonA = json;
			@Override
			public boolean hasNext() {
				return index >= jsonA.YASJF4J_size();
			}

			@Override
			public Object next() {
				try {

					return JSONObject.shield(jsonA.YASJF4J_get(index++));
				} catch (JException e) {
					return null;
				}
			}
		};
	}

	private void addAll(Collection<?> collection, boolean wrap) throws JException {
		if (wrap) {
			for (Object o: collection){
				json.YASJF4J_add(JSONObject.unshield(JSONObject.wrap(o)));
			}
		} else {
			for (Object o: collection){
				json.YASJF4J_add(JSONObject.unshield(o));
			}
		}
	}
	private void addAll(Object array, boolean wrap) throws JException {
		if (array.getClass().isArray()) {
			int length = Array.getLength(array);
			if (wrap) {
				for (int i = 0; i < length; i += 1) {
					json.YASJF4J_add(JSONObject.unshield(JSONObject.wrap(Array.get(array, i))));
				}
			} else {
				for (int i = 0; i < length; i += 1) {
					json.YASJF4J_add(JSONObject.unshield(Array.get(array, i)));
				}
			}
		} else if (array instanceof JSONArray) {
			// use the built in array list `addAll` as all object
			// wrapping should have been completed in the original
			// JSONArray
			JSONArray ar = (JSONArray) array;
			for (int i = 0; i < ar.json.YASJF4J_size(); i += 1) {
				json.YASJF4J_add(ar.json.YASJF4J_get(i));
			}
		} else if (array instanceof Collection) {
			this.addAll((Collection<?>)array, wrap);
		} else if (array instanceof Iterable) {
			this.addAll((Iterable<?>)array, wrap);
		} else {
			throw new JException();
		}
	}


	public boolean similar(Object other) {
		if (!(other instanceof JSONArray)) {
			return false;
		}
		int len = this.length();
		if (len != ((JSONArray)other).length()) {
			return false;
		}
		for (int i = 0; i < len; i += 1) {
			Object valueThis = get(i);
			Object valueOther = ((JSONArray)other).get(i);
			if(valueThis == valueOther) {
				continue;
			}
			if(valueThis == null) {
				return false;
			}
			if (valueThis instanceof JSONObject) {
				if (!((JSONObject)valueThis).similar(valueOther)) {
					return false;
				}
			} else if (valueThis instanceof JSONArray) {
				if (!((JSONArray)valueThis).similar(valueOther)) {
					return false;
				}
			} else if (!valueThis.equals(valueOther)) {
				return false;
			}
		}
		return true;
	}


	public Object remove(int index) {
		Object value = null;
		try {
			value = json.YASJF4J_get(index);
			json.YASJF4J_remove(index);
		} catch (JException e) {
			e.printStackTrace();
		}
		return index >= 0 && index < this.length()
				? value
				: null;
	}

	public boolean isEmpty() {
		return json.YASJF4J_size() == 0;
	}

	public Writer write(StringWriter stringWriter) {
		stringWriter.write(this.toString());
		return stringWriter;
	}
}
