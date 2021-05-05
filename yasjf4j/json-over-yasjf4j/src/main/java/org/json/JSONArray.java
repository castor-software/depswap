package org.json;

/*
 Copyright (c) 2002 JSON.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 The Software shall be used for Good, not Evil.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A JSONArray is an ordered sequence of values. Its external text form is a
 * string wrapped in square brackets with commas separating the values. The
 * internal form is an object having <code>get</code> and <code>opt</code>
 * methods for accessing the values by index, and <code>put</code> methods for
 * adding or replacing values. The values can be any of these types:
 * <code>Boolean</code>, <code>JSONArray</code>, <code>JSONObject</code>,
 * <code>Number</code>, <code>String</code>, or the
 * <code>JSONObject.NULL object</code>.
 * <p>
 * The constructor can convert a JSON text into a Java object. The
 * <code>toString</code> method converts to JSON text.
 * <p>
 * A <code>get</code> method returns a value if one can be found, and throws an
 * exception if one cannot be found. An <code>opt</code> method returns a
 * default value instead of throwing an exception, and so is useful for
 * obtaining optional values.
 * <p>
 * The generic <code>get()</code> and <code>opt()</code> methods return an
 * object which you can cast or query for type. There are also typed
 * <code>get</code> and <code>opt</code> methods that do type checking and type
 * coercion for you.
 * <p>
 * The texts produced by the <code>toString</code> methods strictly conform to
 * JSON syntax rules. The constructors are more forgiving in the texts they will
 * accept:
 * <ul>
 * <li>An extra <code>,</code>&nbsp;<small>(comma)</small> may appear just
 * before the closing bracket.</li>
 * <li>The <code>null</code> value will be inserted when there is <code>,</code>
 * &nbsp;<small>(comma)</small> elision.</li>
 * <li>Strings may be quoted with <code>'</code>&nbsp;<small>(single
 * quote)</small>.</li>
 * <li>Strings do not need to be quoted at all if they do not begin with a quote
 * or single quote, and if they do not contain leading or trailing spaces, and
 * if they do not contain any of these characters:
 * <code>{ } [ ] / \ : , #</code> and if they do not look like numbers and
 * if they are not the reserved words <code>true</code>, <code>false</code>, or
 * <code>null</code>.</li>
 * </ul>
 *
 * @author JSON.org
 * @version 2016-08/15
 */
public class JSONArray implements Iterable<Object> {
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
        if (!array.getClass().isArray() && !(array instanceof JSONArray) && !(array instanceof JArray) && !(array instanceof Collection)) {
            throw new JSONException();
        }
        if(array instanceof JSONArray) {
            JSONArray a = (JSONArray) array;
            try {
                for (int i = 0; i < a.length(); i++) {
                    json.YASJF4J_add(JSONObject.unshield(a.get(i)));
                }
            } catch (JException e) {
                throw new JSONException();
            }
        } else if (array instanceof Collection) {
            Collection collection = (Collection)  array;
            json = JFactory.createJArray();

            try {
                addAll(collection, false);
            } catch (JException e) {
                throw new JSONException();
            }

        } else if (array instanceof JArray) {
            JArray collection = (JArray)  array;
            json = JFactory.createJArray();

            try {
                for(int i = 0; i < collection.YASJF4J_size(); i++) {
                    json.YASJF4J_add(collection.YASJF4J_get(i));
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
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            JSONObject.testValidity(value);
            try {
                json.YASJF4J_set(index, JSONObject.unshield(value));
            } catch (JException e) {
                throw new JSONException();
            }
            return this;
        }
        if(index == this.length()){
            // simple append
            return this.put(value);
        }
        // if we are inserting past the length, we want to grow the array all at once
        // instead of incrementally.
        //json.ensureCapacity(index + 1);
        while (index != this.length()) {
            // we don't need to test validity of NULL objects
            try {
                json.YASJF4J_add(JSONObject.unshield(JSONObject.NULL));
            } catch (JException e) {
                throw new JSONException();
            }
        }
        return this.put(value);
    }



    //get(I)Ljava/lang/Object;
    public Object get(int index) {
    	if(index >= json.YASJF4J_size() || index < 0) throw new JSONException();
        try {
            return JSONObject.shield(json.YASJF4J_get(index));
        } catch (JException e) {
            throw new JSONException();
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

	public Number getNumber(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof Number) {
				return ((Number)object).floatValue();
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

	public BigInteger getBigInteger(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof BigInteger) {
				return ((BigInteger)object);
			}
			try {
				return new BigInteger(object.toString());
			} catch (Exception e) {
				throw new JSONException();
			}
		} catch (JException e) {
		}
		throw new JSONException();
	}

	public BigDecimal getBigDecimal(int key) throws JSONException {
		try {
			Object object = json.YASJF4J_get(key);
			if(object instanceof BigInteger) {
				return ((BigDecimal)object);
			}
			try {
				return new BigDecimal(object.toString());
			} catch (Exception e) {
				throw new JSONException();
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
    public boolean optBoolean(int key, boolean defaultValue) {
        try {
            return getBoolean(key);
        } catch (Exception e) {
        }
        return defaultValue;
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

    public float optFloat(int key, float defaultValue) {
        try {
            return getFloat(key);
        } catch (Exception e) {
        }
        return defaultValue;
    }
    public float optFloat(int key) {
        return optFloat(key, 0f);
    }

    public double optDouble(int key, double defaultValue) {
        try {
            return getDouble(key);
        } catch (Exception e) {
        }
        return defaultValue;
    }
    public double optDouble(int key) {
        return optDouble(key, 0d);
    }

	public BigDecimal optBigDecimal(int key, BigDecimal defaultValue) {
		try {
			return getBigDecimal(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	public BigDecimal optBigDecimal(int key) {
		return optBigDecimal(key, new BigDecimal(0d));
	}

	public BigInteger optBigInteger(int key, BigInteger defaultValue) {
		try {
			return getBigInteger(key);
		} catch (Exception e) {
		}
		return defaultValue;
	}
	public BigInteger optBigInteger(int key) {
		return optBigInteger(key, BigInteger.valueOf(0));
	}

	public String toString(int i) {
		return toString();
	}

    //toString()Ljava/lang/String;
    @Override
    public String toString() {
        String str;
        try {
            str = json.YASJF4J_toString();
        } catch (Exception e) {
            throw new JSONException();
        }
        if(str == null) throw new JSONException();
        return str;
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
                return index < jsonA.YASJF4J_size();
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
            //} else if (!valueThis.equals(valueOther)) {
            } else if (!valueThis.toString().equals(valueOther.toString())) {
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
        } catch (Exception e) {
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

	public JSONObject toJSONObject(JSONArray names) throws JSONException {
		if (names == null || names.isEmpty() || this.isEmpty()) {
			return null;
		}
		JSONObject jo = new JSONObject();
		for (int i = 0; i < names.length(); i += 1) {
			jo.put(names.getString(i), this.opt(i));
		}
		return jo;
	}

    public JSONArray putAll(Collection<?> collection){
        try {
            this.addAll(collection, false);
        } catch (JException e) {
            //e.printStackTrace();
        }
        return this;
    }

    public JSONArray putAll(Iterable<?> iter) {
        try {
            this.addAll(iter, false);
        } catch (JException e) {
            //e.printStackTrace();
        }
        return this;
    }


    public JSONArray putAll(JSONArray array) {
        // directly copy the elements from the source array to this one
        // as all wrapping should have been done already in the source.
        for(int i = 0; i < array.length(); i++) {
            this.put(array.get(i));
        }
        return this;
    }

    public JSONArray putAll(Object array) throws JSONException {
        try {
            this.addAll(array, false);
        } catch (JException e) {
            throw new JSONException();
        }
        return this;
    }

	public String join(String separator) throws JSONException {
		try {
		int len = this.length();
		if (len == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(
				JSONObject.valueToString(json.YASJF4J_get(0)));

		for (int i = 1; i < len; i++) {
			sb.append(separator)
					.append(JSONObject.valueToString(json.YASJF4J_get(i)));
		}
		return sb.toString();
		} catch (JException e) {
			throw new JSONException();
		}
	}


	public Object query(String jsonPointer) {
		return query(new JSONPointer(jsonPointer));
	}

	public Object query(JSONPointer jsonPointer) {
		return jsonPointer.queryFrom(this);
	}

	public List<Object> toList() {
		List<Object> results = new ArrayList<Object>(json.YASJF4J_size());
		for (int i = 0; i < json.YASJF4J_size(); i++) {
			try {
			Object element = json.YASJF4J_get(i);
			if (element == null || JSONObject.NULL.equals(element) || element instanceof JNull) {
				results.add(null);
			} else if (element instanceof JArray) {
				results.add((new JSONArray((JArray) element)).toList());
			} else if (element instanceof JObject) {
				results.add((new JSONObject((JObject) element)).toMap());
			} else {
				results.add(element);
			}
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	public void clear() {
    	json = JFactory.createJArray();
	}



    public <E extends Enum<E>> E getEnum(Class<E> clazz, int index) throws JSONException {
        E val = optEnum(clazz, index);
        if(val==null) {
            // JSONException should really take a throwable argument.
            // If it did, I would re-implement this with the Enum.valueOf
            // method and place any thrown exception in the JSONException
            throw new JSONException();
        }
        return val;
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index) {
        return this.optEnum(clazz, index, null);
    }

    public <E extends Enum<E>> E optEnum(Class<E> clazz, int index, E defaultValue) {
        try {
            Object val = this.opt(index);
            if (JSONObject.NULL.equals(val)) {
                return defaultValue;
            }
            if (clazz.isAssignableFrom(val.getClass())) {
                // we just checked it!
                @SuppressWarnings("unchecked")
                E myE = (E) val;
                return myE;
            }
            return Enum.valueOf(clazz, val.toString());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }




    @Override
    public boolean equals(Object object) {
        if(object == null) return false;
        if(!(object instanceof JSONArray)) return false;
        JSONArray other = (JSONArray) object;
        if(other.length() != length()) return false;
        for(int i = 0; i < length(); i++) {
            Object val = get(i);
            if(!val.equals(other.get(i))) return false;
        }


        return true;
    }
}
