package org.json;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JSONObject {
	JObject json;

	//<init>()V
	public JSONObject() {
		json = JFactory.createJObject();
	}

	public JSONObject(JObject o) {
		json = o;
	}

	//<init>(Ljava/lang/String;)V
	public JSONObject(String in) throws JSONException {
		try {
			json = (JObject) JFactory.parse(in);
		} catch (JException e) {
			throw new JSONException();
		}
	}

	//<init>(Ljava/util/Map;)V
	public JSONObject(Map<?, ?> m) {
		json = JFactory.createJObject();
		if (m != null) {
			for (final Map.Entry<?, ?> e : m.entrySet()) {
				if(e.getKey() == null) {
					throw new NullPointerException("Null key.");
				}
				final Object value = e.getValue();
				if (value != null) {
					try {
						json.YASJF4J_put(String.valueOf(e.getKey()), wrap(value));
					} catch (JException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	public static Object wrap(Object object) {
		try {
			if (object == null) {
				return NULL;
			}
			if (object instanceof JSONObject || object instanceof JSONArray
					|| NULL.equals(object) //|| object instanceof JSONString
					|| object instanceof Byte || object instanceof Character
					|| object instanceof Short || object instanceof Integer
					|| object instanceof Long || object instanceof Boolean
					|| object instanceof Float || object instanceof Double
					|| object instanceof String || object instanceof BigInteger
					|| object instanceof BigDecimal || object instanceof Enum) {
				return object;
			}

			if (object instanceof Collection) {
				Collection<?> coll = (Collection<?>) object;
				return new JSONArray(coll);
			}
			if (object.getClass().isArray()) {
				return new JSONArray(object);
			}
			if (object instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) object;
				return new JSONObject(map);
			}
			Package objectPackage = object.getClass().getPackage();
			String objectPackageName = objectPackage != null ? objectPackage
					.getName() : "";
			if (objectPackageName.startsWith("java.")
					|| objectPackageName.startsWith("javax.")
					|| object.getClass().getClassLoader() == null) {
				return object.toString();
			}
			//return new JSONObject(object);
		} catch (Exception exception) {
			return null;
		}
		return null;
	}



	//get(Ljava/lang/String;)Ljava/lang/Object;
	public Object get(String key) throws JSONException {
		try {
			return JSONObject.shield(json.YASJF4J_get(key));
		} catch (JException e) {
			throw new JSONException();
		}
	}

	//get(Ljava/lang/String;)Z;
	public boolean getBoolean(String key) throws JSONException {
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
	public double getDouble(String key) throws JSONException {
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
	public float getFloat(String key) throws JSONException {
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
	public int getInt(String key) throws JSONException {
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
	public long getLong(String key) throws JSONException {
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
	public String getString(String key) throws JSONException {
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
	public JSONObject getJSONObject(String key) throws JSONException {
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
	public JSONArray getJSONArray(String key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if (object instanceof JArray) {
				return (JSONArray) JSONObject.shield(object);
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}







	public Object opt(String key) {
		try {
			return key == null ? null : unshield(json.YASJF4J_get(key));
		} catch (JException e) {
			return null;
		}
	}

	//optInt(Ljava/lang/String;)I
	public int optInt(String key, int defaultValue) {
		try {
			return getInt(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	//optInt(Ljava/lang/String;I)I
	public int optInt(String key) {
		return optInt(key, 0);
	}

	//optString(Ljava/lang/String;)Ljava/lang/String;
	public String optString(String key) {
		return optString(key,"");
	}

	//optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	public String optString(String key, String defaultValue) {
		try {
			return getString(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	//optBoolean(Ljava/lang/String;)Z
	public boolean optBoolean(String key) {
		try {
			return getBoolean(key);
		} catch (Exception e) {
		}
		return false;
	}
	//optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject
	public JSONObject optJSONObject(String key) {
		try {
			return getJSONObject(key);
		} catch (Exception e) {
		}
		return null;
	}

	//optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;
	public JSONArray optJSONArray(String key) {
		try {
			return getJSONArray(key);
		} catch (Exception e) {
		}
		return null;
	}

	//optLong(Ljava/lang/String;)I
	public long optLong(String key, long defaultValue) {
		try {
			return getLong(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	//optLong(Ljava/lang/String;J)J
	public long optLong(String key) {
		return optLong(key, 0);
	}

	//isNull(Ljava/lang/String;)Z
	//toString(I)Ljava/lang/String;

	//toString()Ljava/lang/String;
	@Override
	public String toString() {
		return json.YASJF4J_toString();
	}


	//length()I
	public int length() {
		return json.YASJF4J_getKeys().size();
	}


	//keys()Ljava/util/Iterator;
	public Iterator keys() {
		return json.YASJF4J_getKeys().iterator();
	}
	//keySet()Ljava/util/Set;
	public Set<String> keySet() {
		return json.YASJF4J_getKeys();
	}


	//has(Ljava/lang/String;)Z
	public boolean has(String key) {
		return json.YASJF4J_getKeys().contains(key);
	}



	//put(Ljava/lang/String;I)Lorg/json/JSONObject;
	//put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
	//put(Ljava/lang/String;Z)Lorg/json/JSONObject;
	//put(Ljava/lang/String;J)Lorg/json/JSONObject;
	//put(Ljava/lang/String;Ljava/util/Collection;)Lorg/json/JSONObject;
	public JSONObject put(String key, Object value) {
		if (key == null) {
			throw new NullPointerException("Null key.");
		}
		if (value != null) {
			//testValidity(value);
			try {
				json.YASJF4J_put(key.toString(), JSONObject.unshield(value));
			} catch (JException e) {
			}
		} else {
			try {
				json.YASJF4J_remove(key);
			} catch (JException e) {
			}
		}
		return this;
	}

	public static Object shield(Object o) {
		if (o instanceof JObject) return new JSONObject((JObject) o);
		else if (o instanceof JArray) return new JSONArray((JArray) o);
		else return o;
	}

	public static Object unshield(Object o) {
		if (o instanceof JSONObject) return ((JSONObject) o).json;
		else if (o instanceof JSONArray) return ((JSONArray) o).json;
		else return o;
	}

	public boolean isEmpty() {
		return json.YASJF4J_getKeys().size() == 0;
	}

	public void remove(String myKey) {
		try {
			json.YASJF4J_remove(myKey);
		} catch (JException e) {
			e.printStackTrace();
		}
	}

	public Writer write(StringWriter stringWriter) {
		stringWriter.write(this.toString());
		return stringWriter;
	}

	private static final class Null {

		/**
		 * There is only intended to be a single instance of the NULL object,
		 * so the clone method returns itself.
		 *
		 * @return NULL.
		 */
		@Override
		protected final Object clone() {
			return this;
		}

		/**
		 * A Null object is equal to the null value and to itself.
		 *
		 * @param object
		 *            An object to test for nullness.
		 * @return true if the object parameter is the JSONObject.NULL object or
		 *         null.
		 */
		@Override
		public boolean equals(Object object) {
			return object == null || object == this;
		}
		/**
		 * A Null object is equal to the null value and to itself.
		 *
		 * @return always returns 0.
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * Get the "null" string value.
		 *
		 * @return The string "null".
		 */
		@Override
		public String toString() {
			return "null";
		}
	}
	//NULLLjava/lang/Object;
	public static final Object NULL = new Null();
	protected static boolean isDecimalNotation(final String val) {
		return val.indexOf('.') > -1 || val.indexOf('e') > -1
				|| val.indexOf('E') > -1 || "-0".equals(val);
	}

	public boolean similar(Object other) {
		try {
			if (!(other instanceof JSONObject)) {
				return false;
			}
			if (!this.keySet().equals(((JSONObject)other).keySet())) {
				return false;
			}
			for (String name : this.keySet()) {
				Object valueThis = get(name);
				Object valueOther = ((JSONObject)other).get(name);
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
					if(valueThis instanceof Number && valueOther instanceof Number) {
						Number nThis = (Number) valueThis;
						Number nOther = (Number) valueOther;
						if(((Number) valueOther).intValue() == ((Number) valueThis).intValue()) {
							return true;
						}
					}
					return false;
				}
			}
			return true;
		} catch (Throwable exception) {
			return false;
		}
	}
}
