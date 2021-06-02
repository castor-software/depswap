package org.json.simple;


import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JNull;
import se.kth.castor.yasjf4j.JObject;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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


	public static String toJSONString(Object value) {
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

		if(value instanceof Character)
			return "\"" + value.toString() + "\"";


		if((value instanceof JSONAware))
			return ((JSONAware)value).toJSONString();

		if((value instanceof JSONAwareContainer))
			return ((JSONAwareContainer)value).get().toJSONString();

		if(value instanceof Map) {
			return new JSONObject((Map) value).toString();
			//return JSONObject.toJSONString((Map) value);;
		}

		if(value instanceof List) {
			return new JSONArray((List) value).toString();
			//return JSONArray.toJSONString((List) value);
		}

		if(value.getClass().isArray()) {
			//value.getClass().getComponentType().isPrimitive();
			//Object[] o = (Object[]) value;
			//Arrays.asList(value);
			return new JSONArray(autoBox(value)).toString();

		}

		return value.toString();
	}

	public static List autoBox(Object value) {
		if(value.getClass().getComponentType().isPrimitive()) {
			if(value.getClass().getComponentType() == boolean.class) {
				return Arrays.asList(ArrayUtils.toObject(((boolean[]) value)));
			} else if(value.getClass().getComponentType() == byte.class) {
				return Arrays.asList(ArrayUtils.toObject(((byte[]) value)));
			} else if(value.getClass().getComponentType() == char.class) {
				return Arrays.asList(ArrayUtils.toObject(((char[]) value)));
			} else if(value.getClass().getComponentType() == short.class) {
				return Arrays.asList(ArrayUtils.toObject(((short[]) value)));
			} else if(value.getClass().getComponentType() == int.class) {
				return Arrays.asList(ArrayUtils.toObject(((int[]) value)));
			} else if(value.getClass().getComponentType() == long.class) {
				return Arrays.asList(ArrayUtils.toObject(((long[]) value)));
			} else if(value.getClass().getComponentType() == float.class) {
				/*float[] fa = ((float[]) value);
				double[] da = new double[fa.length];
				for(int i = 0; i < fa.length; i++) da[i] = fa[i];
				return Arrays.asList(ArrayUtils.toObject(da));*/
				return Arrays.asList(ArrayUtils.toObject(((float[]) value)));
			} else {
				return Arrays.asList(ArrayUtils.toObject(((double[]) value)));
			}
		} else if (value.getClass().getComponentType().isArray()) {
			List<List> metalist = new ArrayList<>();
			Object[] ar = ((Object[]) value);
			for(int i = 0; i < ar.length; i++) {
				metalist.add(autoBox(ar[i]));
			}
			return metalist;
		} else {
			return Arrays.asList(((Object[]) value));
		}
	}

	public static Object parse(String s) {
		try {
			Object r = JFactory.parse(s);
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

	public static Object parse(Reader in) {
		JSONParser parser=new JSONParser();
		try {
			return parser.parse(in);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object parseWithException(Reader in) throws IOException, ParseException {
		JSONParser parser=new JSONParser();
		return parser.parse(in);
	}

	public static Object parseWithException(String s) throws ParseException{
		JSONParser parser=new JSONParser();
		return parser.parse(s);
	}

	public static void writeJSONString(Object o, Writer out) throws IOException {
		out.write(toJSONString(o));
	}


	public static Object shield(Object o) {
		if (o instanceof JObject) return new JSONObject((JObject) o);
		else if (o instanceof JArray) return new JSONArray((JArray) o);
		else if (o instanceof JNull) return null;
		//else if (o instanceof JSONAwareContainer) return ((JSONAwareContainer) o).get();
		else return o;
	}

	public static Object unshield(Object o) {
		if (o instanceof JSONObject) return ((JSONObject) o).json;
		else if (o instanceof JSONArray) return ((JSONArray) o).json;
		else if (o == null) return JNull.getInstance();
		//else if (o instanceof JSONAware) return JSONAwareContainer.create((JSONAware) o);
		else if (o instanceof JSONAware) {
			try {
				return JFactory.parse(((JSONAware) o).toJSONString());
			} catch (JException e) {
			}
			return o;
		}
		else return o;
	}


	public static JObject recO(Map m) {
		JObject o = JFactory.createJObject();
		for(Object er : m.entrySet()) {
			Map.Entry e = (Map.Entry) er;
			try {
				Object v = e.getValue();
				if(v == null) {
					o.YASJF4J_put(e.getKey().toString(),JNull.getInstance());
				} else if(v instanceof Map) {
					o.YASJF4J_put(e.getKey().toString(),recO((Map) v));
				} else if(v instanceof List) {
					o.YASJF4J_put(e.getKey().toString(),recA((List) v));
				} else if(v.getClass().isArray()) {
					o.YASJF4J_put(e.getKey().toString(),recA(autoBox(v)));
				} else {
					o.YASJF4J_put(e.getKey().toString(), unshield(v));
				}
			} catch (JException e1) {
				e1.printStackTrace();
			}
		}
		return o;
	}

	public static JArray recA(List m) {
		JArray a = JFactory.createJArray();
		for(Object e: m) {
			try {
				if(e == null) {
					a.YASJF4J_add(JNull.getInstance());
				} else if(e instanceof Map) {
					a.YASJF4J_add(recO((Map) e));
				} else if(e instanceof List) {
					a.YASJF4J_add(recA((List) e));
				} else if(e.getClass().isArray()) {
					a.YASJF4J_add(recA(autoBox(e)));
				} else {
					a.YASJF4J_add(unshield(e));
				}
			} catch (JException e1) {
				e1.printStackTrace();
			}
		}
		return a;
	}
}
