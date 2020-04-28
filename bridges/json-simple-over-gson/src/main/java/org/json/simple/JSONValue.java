package org.json.simple;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class JSONValue {

	public static JsonElement lift(Object o) {

		if(o == null) {
			return new JsonNull();
		} else if(o instanceof JsonElement) {
			return (JsonElement) o;
		} else if(o instanceof String) {
			return new JsonPrimitive((String) o);
		} else if(o instanceof Integer) {
			return new JsonPrimitive((Integer) o);
		} else if(o instanceof Byte) {
			return new JsonPrimitive((Byte) o);
		} else if(o instanceof Float) {
			return new JsonPrimitive((Float) o);
		} else if(o instanceof Double) {
			return new JsonPrimitive((Double) o);
		} else if(o instanceof Character) {
			return new JsonPrimitive((Character) o);
		} else if(o instanceof Short) {
			return new JsonPrimitive((Short) o);
		} else if(o instanceof Long) {
			return new JsonPrimitive((Long) o);
		} else if(o instanceof Boolean) {
			return new JsonPrimitive((Boolean) o);
		} else if(o instanceof BigDecimal) {
			return new JsonPrimitive((BigDecimal) o);
		} else if(o instanceof BigInteger) {
			return new JsonPrimitive((BigInteger) o);
		} else if(o instanceof Map) {
			JsonObject obj = new JsonObject();
			for (Object key: ((Map) o).keySet()) {
				obj.add(key.toString(), lift(((Map) o).get(key)));
			}
			return obj;
		} else if(o instanceof List) {
			JsonArray obj = new JsonArray();
			for (Object el: (List) o) {
				obj.add(lift(el));
			}
			return obj;

		}
		return null;
	}

	public static Object down(Object o) {
		if(o instanceof JsonArray) {
			return new JSONArray((JsonArray) o);
		} else if(o instanceof JsonObject) {
			return new JSONObject((JsonObject) o);
		} else if(o instanceof JsonPrimitive) {

			JsonPrimitive p = (JsonPrimitive) o;
			if(p.isBoolean()) return p.getAsBoolean();
			if(p.isString()) return p.getAsString();
			if(p.isNumber()) return p.getAsNumber();
		}
		return o;
	}

	public static String escape(String s){
		if(s==null)
			return null;
		StringBuffer sb = new StringBuffer();
		escape(s, sb);
		return sb.toString();
	}

	public static void escape(String s, StringBuffer sb) {
		for(int i=0;i<s.length();i++){
			char ch=s.charAt(i);
			switch(ch){
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '/':
					sb.append("\\/");
					break;
				default:
					//Reference: http://www.unicode.org/versions/Unicode5.1.0/
					if((ch>='\u0000' && ch<='\u001F') || (ch>='\u007F' && ch<='\u009F') || (ch>='\u2000' && ch<='\u20FF')){
						String ss=Integer.toHexString(ch);
						sb.append("\\u");
						for(int k=0;k<4-ss.length();k++){
							sb.append('0');
						}
						sb.append(ss.toUpperCase());
					}
					else{
						sb.append(ch);
					}
			}
		}//for
	}


	public static String toJSONString(Object value){
		if(value == null)
			return "null";

		if(value instanceof String)
			return "\""+escape((String)value)+"\"";

		if(value instanceof Double){
			if(((Double)value).isInfinite() || ((Double)value).isNaN())
				return "null";
			else
				return value.toString();
		}

		if(value instanceof Float){
			if(((Float)value).isInfinite() || ((Float)value).isNaN())
				return "null";
			else
				return value.toString();
		}

		if(value instanceof Number)
			return value.toString();

		if(value instanceof Boolean)
			return value.toString();

		if((value instanceof JSONAware))
			return ((JSONAware)value).toJSONString();

		if(value instanceof Map) {
			return new JSONObject((Map) value).toString();
			//return JSONObject.toJSONString((Map) value);;
		}

		if(value instanceof List) {
			return new JSONArray((List) value).toString();
			//return JSONArray.toJSONString((List) value);
		}

		return value.toString();
	}
}
