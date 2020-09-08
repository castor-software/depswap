package org.json.simple;


import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class JSONValue {

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

	public static Object parse(String s) {
		try {
			Object r = JFactory.parse(s);;
			if(r instanceof JObject) {
				return new JSONObject((JObject) r);
			} else if (r instanceof JArray) {
				return new JSONArray((JArray) r);
			} else {
				return r;
			}
		} catch (JException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void writeJSONString(Object o, StringWriter out) {
		out.write(toJSONString(o));
	}
}
