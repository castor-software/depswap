package org.json;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;
import se.kth.castor.yasjf4j.util.Utils;

import java.io.Closeable;

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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;


public class JSONObject {

    public JSONObject accumulate(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key,
                    value instanceof JSONArray ? new JSONArray().put(value)
                            : value);
        } else if (object instanceof JSONArray) {
            ((JSONArray) object).put(value);
        } else {
            this.put(key, new JSONArray().put(object).put(value));
        }
        return this;
    }

    public static void testValidity(Object o) throws JSONException {
        if (o instanceof Number && !numberIsFinite((Number) o)) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    private static boolean numberIsFinite(Number n) {
        if (n instanceof Double && (((Double) n).isInfinite() || ((Double) n).isNaN())) {
            return false;
        } else if (n instanceof Float && (((Float) n).isInfinite() || ((Float) n).isNaN())) {
            return false;
        }
        return true;
    }

    public JSONArray names() {
        if(this.isEmpty()) {
            return null;
        }
        return new JSONArray(this.keySet());
    }

    public JSONArray toJSONArray(JSONArray names) throws JSONException {
        if (names == null || names.isEmpty()) {
            return null;
        }
        JSONArray ja = new JSONArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    public void clear() {
        json = JFactory.createJObject();
    }

    /**
     * JSONObject.NULL is equivalent to the value that JavaScript calls null,
     * whilst Java's null is equivalent to the value that JavaScript calls
     * undefined.
     */
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
    //public static final Object NULL = new Null();
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
                        json.YASJF4J_put(String.valueOf(e.getKey()), JSONObject.unshield(wrap(value)));
                    } catch (JException e1) {
                        e1.printStackTrace();
                    }
                }
//                else {
//                    try {
//                        json.YASJF4J_put(String.valueOf(e.getKey()), JNull.getInstance());
//                    } catch (JException e1) {
//                        e1.printStackTrace();
//                    }
//                }
            }
        }
    }

    public JSONObject(Object bean) {
        this();
        this.populateMap(bean);
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
            //return object;
            //return new JSONObject(object);
        } catch (Exception exception) {
            return null;
        }
        return null;
    }



    //get(Ljava/lang/String;)Ljava/lang/Object;
    public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
        try {
            return JSONObject.shield(json.YASJF4J_get(key));
        } catch (Exception e) {
            throw new JSONException();
        }
    }

    //get(Ljava/lang/String;)Z;
    public boolean getBoolean(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getDouble(Ljava/lang/String;)D
    public double getDouble(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }


    //getFloat(Ljava/lang/String;)F
    public float getFloat(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    public BigDecimal getBigDecimal(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
        try {
            Object object = json.YASJF4J_get(key);
            if(object instanceof Number) {
                return new BigDecimal(((Number)object).doubleValue());
            }
            try {
                return new BigDecimal(object.toString());
            } catch (Exception e) {
                throw new JSONException();
            }
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    public Number getNumber(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getInt(Ljava/lang/String;)I
    public int getInt(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getLong(Ljava/lang/String;)J
    public long getLong(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
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
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getString(Ljava/lang/String;)Ljava/lang/String;
    public String getString(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
        try {
            Object object = json.YASJF4J_get(key);
            if (object instanceof String) {
                return (String) object;
            }
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;
    public JSONObject getJSONObject(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
        try {
            Object object = json.YASJF4J_get(key);
            if (object instanceof JObject) {
                return (JSONObject) JSONObject.shield(object);
            }
        } catch (Exception e) {
        }
        throw new JSONException();
    }

    //getJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;
    public JSONArray getJSONArray(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        if(!json.YASJF4J_getKeys().contains(key)) throw new JSONException();
        try {
            Object object = json.YASJF4J_get(key);
            if (object instanceof JArray) {
                return (JSONArray) JSONObject.shield(object);
            }
        } catch (Exception e) {
        }
        throw new JSONException();
    }







    public Object opt(String key) {
        try {
            if(key==null) return null;
            if(!json.YASJF4J_getKeys().contains(key)) return null;
            return shield(json.YASJF4J_get(key));
        } catch (Exception e) {
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

    public BigDecimal optBigDecimal(String key) {
        return optBigDecimal(key, new BigDecimal(0d));
    }

    public double optDouble(String key, double defaultValue) {
        try {
            return getDouble(key);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public double optDouble(String key) {
        try {
            return getDouble(key);
        } catch (Exception e) {
        }
        return 0d;
    }

    //optBoolean(Ljava/lang/String;)Z
    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return getBoolean(key);
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




    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key, E defaultValue) {
        try {
            Object val = this.opt(key);
            if (NULL.equals(val)) {
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

    public <E extends Enum<E>> E optEnum(Class<E> clazz, String key) {
        return this.optEnum(clazz, key, null);
    }

    public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) throws JSONException {
        E val = optEnum(clazz, key);
        if(val==null) {
            // JSONException should really take a throwable argument.
            // If it did, I would re-implement this with the Enum.valueOf
            // method and place any thrown exception in the JSONException
            throw new JSONException();
        }
        return val;
    }

    //isNull(Ljava/lang/String;)Z
    public boolean isNull(String key) {
        return JSONObject.NULL.equals(this.opt(key));
    }

    //toString(I)Ljava/lang/String;
    public String toString(int ignored) {
        String str;
        try {
            str = json.YASJF4J_toString();
        } catch (Exception e) {
            throw new JSONException();
        }
        if(str == null) throw new JSONException();
        return str;
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
    public JSONObject put(String key, int value) {
        return put(key, Integer.valueOf(value));
    }
    //put(Ljava/lang/String;Z)Lorg/json/JSONObject;
    public JSONObject put(String key, boolean value) {
        return put(key, Boolean.valueOf(value));
    }
    //put(Ljava/lang/String;J)Lorg/json/JSONObject;
    public JSONObject put(String key, long value) {
        return put(key, Long.valueOf(value));
    }
    //put(Ljava/lang/String;Ljava/util/Collection;)Lorg/json/JSONObject;
    public JSONObject put(String key, Collection value) {
        if (value != null) {
            return put(key, new JSONArray(value));
        } else {
            return put(key, null);
        }
    }

    //put(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    public JSONObject put(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            //testValidity(value);
            try {
                json.YASJF4J_put(key, JSONObject.unshield(value));
            } catch (JException e) {
                throw new JSONException();
            }
        } else {
            try {
                json.YASJF4J_remove(key);
            } catch (JException e) {
            }
        }
        return this;
    }

    public JSONObject putOpt(String key, Object value) throws JSONException {
        if (key != null && value != null) {
            return this.put(key, value);
        }
        return this;
    }

    public static Object shield(Object o) {
        if (o instanceof JObject) return new JSONObject((JObject) o);
        else if (o instanceof JArray) return new JSONArray((JArray) o);
        //else if (o == null) return NULL;
        else if (o instanceof JNull) return NULL;
        else if (o instanceof Map) return new JSONObject(Utils.recO((Map) o));
        else if (o instanceof List) return new JSONArray(Utils.recA((List) o));
        else if (o != null && o.getClass().isArray())  return new JSONArray(Utils.recA(Utils.autoBox(o)));
        else return o;
    }

    public static Object unshield(Object o) {
        if (o instanceof JSONObject) return ((JSONObject) o).json;
        else if (o instanceof JSONArray) return ((JSONArray) o).json;
        else if (o instanceof Map) return new JSONObject(Utils.recO((Map) o)).json;
        else if (o instanceof List) return new JSONArray(Utils.recA((List) o)).json;
        else if (o != null && o.getClass().isArray())  return new JSONArray(Utils.recA(Utils.autoBox(o))).json;
        //else if (o instanceof Null) return null;
        else if (o instanceof Null) return JNull.getInstance();
        else return o;
    }

    public boolean isEmpty() {
        return json.YASJF4J_getKeys().size() == 0;
    }

    public void remove(String myKey) {
        try {
            json.YASJF4J_remove(myKey);
        } catch (JException e) {
            //e.printStackTrace();
        }
    }

    public Writer write(StringWriter stringWriter) {
        stringWriter.write(this.toString());
        return stringWriter;
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


    static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");
    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(number);

        // Shave off trailing zeros and decimal point, if possible.

        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public static String valueToString(Object value) throws JSONException {
        // moves the implementation to JSONWriter as:
        // 1. It makes more sense to be part of the writer class
        // 2. For Android support this method is not available. By implementing it in the Writer
        //    Android users can use the writer with the built in Android JSONObject implementation.
        return JSONWriter.valueToString(value);
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // will never happen - we are writing to a string writer
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    w.write('\\');
                    w.write(c);
                    break;
                case '/':
                    if (b == '<') {
                        w.write('\\');
                    }
                    w.write(c);
                    break;
                case '\b':
                    w.write("\\b");
                    break;
                case '\t':
                    w.write("\\t");
                    break;
                case '\n':
                    w.write("\\n");
                    break;
                case '\f':
                    w.write("\\f");
                    break;
                case '\r':
                    w.write("\\r");
                    break;
                default:
                    if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                            || (c >= '\u2000' && c < '\u2100')) {
                        w.write("\\u");
                        hhhh = Integer.toHexString(c);
                        w.write("0000", 0, 4 - hhhh.length());
                        w.write(hhhh);
                    } else {
                        w.write(c);
                    }
            }
        }
        w.write('"');
        return w;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> results = new HashMap<String, Object>();
        for (String key : json.YASJF4J_getKeys()) {
            try {
            Object v = json.YASJF4J_get(key);
            Object value;
            if (v == null || NULL.equals(v) || v instanceof JNull) {
                value = null;
            } else if (v instanceof JSONObject) {
                value = ((JSONObject) v).toMap();
            } else if (v instanceof JObject) {
                value = (new JSONObject((JObject) v)).toMap();
            } else if (v instanceof JSONArray) {
                value = ((JSONArray) v).toList();
            } else if (v instanceof JArray) {
                value = (new JSONArray((JArray) v)).toList();
            } else {
                value = v;
            }
            results.put(key, value);
            } catch (JException e) {
                //e.printStackTrace();
            }
        }
        return results;
    }

    public JSONObject(JSONObject jo, String ... names) {
        json = JFactory.createJObject();
        for (int i = 0; i < names.length; i += 1) {
            try {
                json.YASJF4J_put(names[i], JSONObject.unshield(jo.opt(names[i])));
            } catch (Exception ignore) {
            }
        }
    }

    public Object optQuery(String jsonPointer) {
        return query(new JSONPointer(jsonPointer));
    }

    public Object query(String jsonPointer) {
        return query(new JSONPointer(jsonPointer));
    }

    public Object optQuery(JSONPointer jsonPointer) {
        return jsonPointer.queryFrom(this);
    }

    public Object query(JSONPointer jsonPointer) {
        return jsonPointer.queryFrom(this);
    }

    public JSONObject append(String key, Object value) throws JSONException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key, new JSONArray().put(value));
        } else if (object instanceof JSONArray) {
            this.put(key, ((JSONArray) object).put(value));
        } else {
            throw new JSONException();
        }
        return this;
    }

    public BigDecimal optBigDecimal(String key, BigDecimal defaultValue) {
        Object val = this.opt(key);
        return objectToBigDecimal(val, defaultValue);
    }

    /**
     * @param val value to convert
     * @param defaultValue default value to return is the conversion doesn't work or is null.
     * @return BigDecimal conversion of the original value, or the defaultValue if unable
     *          to convert.
     */
    static BigDecimal objectToBigDecimal(Object val, BigDecimal defaultValue) {
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigDecimal){
            return (BigDecimal) val;
        }
        if (val instanceof BigInteger){
            return new BigDecimal((BigInteger) val);
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            return new BigDecimal(((Number) val).doubleValue());
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return new BigDecimal(((Number) val).longValue());
        }
        // don't check if it's a string in case of unchecked Number subclasses
        try {
            return new BigDecimal(val.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public BigInteger optBigInteger(String key, BigInteger defaultValue) {
        Object val = this.opt(key);
        return objectToBigInteger(val, defaultValue);
    }

    /**
     * @param val value to convert
     * @param defaultValue default value to return is the conversion doesn't work or is null.
     * @return BigInteger conversion of the original value, or the defaultValue if unable
     *          to convert.
     */
    static BigInteger objectToBigInteger(Object val, BigInteger defaultValue) {
        if (NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof BigInteger){
            return (BigInteger) val;
        }
        if (val instanceof BigDecimal){
            return ((BigDecimal) val).toBigInteger();
        }
        if (val instanceof Double || val instanceof Float){
            if (!numberIsFinite((Number)val)) {
                return defaultValue;
            }
            return new BigDecimal(((Number) val).doubleValue()).toBigInteger();
        }
        if (val instanceof Long || val instanceof Integer
                || val instanceof Short || val instanceof Byte){
            return BigInteger.valueOf(((Number) val).longValue());
        }
        // don't check if it's a string in case of unchecked Number subclasses
        try {
            // the other opt functions handle implicit conversions, i.e.
            // jo.put("double",1.1d);
            // jo.optInt("double"); -- will return 1, not an error
            // this conversion to BigDecimal then to BigInteger is to maintain
            // that type cast support that may truncate the decimal.
            final String valStr = val.toString();
            if(isDecimalNotation(valStr)) {
                return new BigDecimal(valStr).toBigInteger();
            }
            return new BigInteger(valStr);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String[] getNames(JSONObject jo) {
        if (jo.isEmpty()) {
            return null;
        }
        return jo.keySet().toArray(new String[jo.length()]);
    }


    private void populateMap(Object bean) {
        Class<?> klass = bean.getClass();

        // If klass is a System class then set includeSuperClass to false.

        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        for (final Method method : methods) {
            final int modifiers = method.getModifiers();
            if (Modifier.isPublic(modifiers)
                    && !Modifier.isStatic(modifiers)
                    && method.getParameterTypes().length == 0
                    && !method.isBridge()
                    && method.getReturnType() != Void.TYPE
                    && isValidMethodName(method.getName())) {
                final String key = getKeyNameFromMethod(method);
                if (key != null && !key.isEmpty()) {
                    try {
                        final Object result = method.invoke(bean);
                        if (result != null) {
                            json.YASJF4J_put(key, wrap(result));
                            // we don't use the result anywhere outside of wrap
                            // if it's a resource we should be sure to close it
                            // after calling toString
                            if (result instanceof Closeable) {
                                try {
                                    ((Closeable) result).close();
                                } catch (IOException ignore) {
                                }
                            }
                        }
                    } catch (IllegalAccessException ignore) {
                    } catch (IllegalArgumentException ignore) {
                    } catch (InvocationTargetException ignore) {
                    } catch (JException ignore) {
                    }
                }
            }
        }
    }

    private static boolean isValidMethodName(String name) {
        return !"getClass".equals(name) && !"getDeclaringClass".equals(name);
    }

    private static String getKeyNameFromMethod(Method method) {
        final int ignoreDepth = getAnnotationDepth(method, JSONPropertyIgnore.class);
        if (ignoreDepth > 0) {
            final int forcedNameDepth = getAnnotationDepth(method, JSONPropertyName.class);
            if (forcedNameDepth < 0 || ignoreDepth <= forcedNameDepth) {
                // the hierarchy asked to ignore, and the nearest name override
                // was higher or non-existent
                return null;
            }
        }
        JSONPropertyName annotation = getAnnotation(method, JSONPropertyName.class);
        if (annotation != null && annotation.value() != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        String key;
        final String name = method.getName();
        if (name.startsWith("get") && name.length() > 3) {
            key = name.substring(3);
        } else if (name.startsWith("is") && name.length() > 2) {
            key = name.substring(2);
        } else {
            return null;
        }
        // if the first letter in the key is not uppercase, then skip.
        // This is to maintain backwards compatibility before PR406
        // (https://github.com/stleary/JSON-java/pull/406/)
        if (key.length() == 0 || Character.isLowerCase(key.charAt(0))) {
            return null;
        }
        if (key.length() == 1) {
            key = key.toLowerCase(Locale.ROOT);
        } else if (!Character.isUpperCase(key.charAt(1))) {
            key = key.substring(0, 1).toLowerCase(Locale.ROOT) + key.substring(1);
        }
        return key;
    }


    private static <A extends Annotation> A getAnnotation(final Method m, final Class<A> annotationClass) {
        // if we have invalid data the result is null
        if (m == null || annotationClass == null) {
            return null;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return m.getAnnotation(annotationClass);
        }

        // if we've already reached the Object class, return null;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return null;
        }

        // check directly implemented interfaces for the method being checked
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                return getAnnotation(im, annotationClass);
            } catch (final SecurityException ex) {
                continue;
            } catch (final NoSuchMethodException ex) {
                continue;
            }
        }

        try {
            return getAnnotation(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
        } catch (final SecurityException ex) {
            return null;
        } catch (final NoSuchMethodException ex) {
            return null;
        }
    }


    private static int getAnnotationDepth(final Method m, final Class<? extends Annotation> annotationClass) {
        // if we have invalid data the result is -1
        if (m == null || annotationClass == null) {
            return -1;
        }

        if (m.isAnnotationPresent(annotationClass)) {
            return 1;
        }

        // if we've already reached the Object class, return -1;
        Class<?> c = m.getDeclaringClass();
        if (c.getSuperclass() == null) {
            return -1;
        }

        // check directly implemented interfaces for the method being checked
        for (Class<?> i : c.getInterfaces()) {
            try {
                Method im = i.getMethod(m.getName(), m.getParameterTypes());
                int d = getAnnotationDepth(im, annotationClass);
                if (d > 0) {
                    // since the annotation was on the interface, add 1
                    return d + 1;
                }
            } catch (final SecurityException ex) {
                continue;
            } catch (final NoSuchMethodException ex) {
                continue;
            }
        }

        try {
            int d = getAnnotationDepth(
                    c.getSuperclass().getMethod(m.getName(), m.getParameterTypes()),
                    annotationClass);
            if (d > 0) {
                // since the annotation was on the superclass, add 1
                return d + 1;
            }
            return -1;
        } catch (final SecurityException ex) {
            return -1;
        } catch (final NoSuchMethodException ex) {
            return -1;
        }
    }


    @Override
    public boolean equals(Object object) {
        if(object == null) return false;
        if(!(object instanceof JSONObject)) return false;
        JSONObject other = (JSONObject) object;
        if(other.keySet().size() != keySet().size()) return false;
        for(String key: keySet()) {
            Object val = get(key);
            if(!val.equals(other.get(key))) return false;
        }


        return true;
    }
}