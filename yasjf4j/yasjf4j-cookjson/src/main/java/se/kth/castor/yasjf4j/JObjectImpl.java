package se.kth.castor.yasjf4j;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.TextJsonGenerator;
import org.yuanheng.cookjson.TextJsonParser;
import org.yuanheng.cookjson.value.CookJsonArray;
import org.yuanheng.cookjson.value.CookJsonBigDecimal;
import org.yuanheng.cookjson.value.CookJsonBinary;
import org.yuanheng.cookjson.value.CookJsonDouble;
import org.yuanheng.cookjson.value.CookJsonInt;
import org.yuanheng.cookjson.value.CookJsonLong;
import org.yuanheng.cookjson.value.CookJsonObject;
import org.yuanheng.cookjson.value.CookJsonString;
import se.kth.castor.yasjf4j.util.Utils;

import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends CookJsonObject implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(CookJsonObject json) throws JException {
		try {
			for(String key: json.keySet()) {
				JsonValue el = json.get(key);
				if(el instanceof CookJsonObject) {
					put(key, new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					put(key, new JArrayImpl((CookJsonArray) el));
				} else {
					put(key, el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(Map json) throws JException {
		try {
			for(Object okey: json.keySet()) {
				String key = okey.toString();
				Object el = json.get(key);
				if(el instanceof CookJsonObject) {
					put(key, new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					put(key, new JArrayImpl((CookJsonArray) el));
				} else {
					put(key, toJSONValue(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			TextJsonParser p = new TextJsonParser(new StringReader(json));
			p.next();
			CookJsonObject o = (CookJsonObject) p.getValue();
			for(String key: o.keySet()) {
				Object el = o.get(key);
				if(el instanceof CookJsonObject) {
					put(key, new JObjectImpl((CookJsonObject) el));
				} else if (el instanceof CookJsonArray) {
					put(key, new JArrayImpl((CookJsonArray) el));
				} else {
					put(key, toJSONValue(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!containsKey(s)) throw new JException();
		try {
			return toObject(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s, toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		StringWriter a = new StringWriter();
		TextJsonGenerator g = new TextJsonGenerator(a);
		g.write(this);
		g.flush();
		return a.toString();
	}

	public static JsonValue toJSONValue(Object o) throws JException {
		//null
		if(o == null) {
			return JsonValue.NULL;
		} else if (o instanceof JNull) {
			return JsonValue.NULL;
		} else if (o instanceof JsonValue) {
			return (JsonValue) o;
		} else if (o instanceof  Map) {
			return new JObjectImpl((Map) o);
		} else if (o instanceof  List) {
			return new JArrayImpl((List) o);
		} else if (o.getClass().isArray()) {
			return new JArrayImpl(Utils.autoBox(o));
		} else if (o instanceof  String) {
			return new CookJsonString((String) o);
		} else if (o instanceof  Number) {
			if(o instanceof Double) {
				return new CookJsonDouble((Double) o);
			} else if (o instanceof Float) {
				return new CookJsonDouble((Float) o);
			} else if (o instanceof BigDecimal) {
				return new CookJsonBigDecimal((BigDecimal) o);
			} else if (o instanceof Integer) {
				return new CookJsonInt((Integer) o);
			} else if (o instanceof Long) {
				return new CookJsonLong((Long) o);
			} else {
				return new CookJsonInt(((Number) o).intValue());
			}
		} else if (o instanceof  Boolean) {
			return ((Boolean) o) ? JsonValue.TRUE : JsonValue.FALSE;
		} else if (o instanceof  Character) {
			return new CookJsonString(((Character) o) .toString());
		} else {
			return new CookJsonString(o.toString());
		}
	}

	public static Object toObject(JsonValue o) throws ParseException {
		if(o instanceof CookJsonDouble) {
			return ((CookJsonDouble) o).doubleValue();
		} else if (o == JsonValue.TRUE) {
			return true;
		} else if (o == JsonValue.FALSE) {
			return false;
		} else if (o == JsonValue.NULL) {
			return JNull.getInstance();
		} else if (o instanceof CookJsonBigDecimal) {
			return ((CookJsonBigDecimal) o).bigDecimalValue();
		} else if (o instanceof CookJsonInt) {
			return ((CookJsonInt) o).intValue();
		} else if (o instanceof CookJsonLong) {
			return ((CookJsonLong) o).longValue();
		} else if (o instanceof JsonString) {
			return ((JsonString) o).getString();
		} else if (o instanceof CookJsonBinary) {
			return ((CookJsonBinary) o).getBytes();
		} else {
			return o;
		}
	}

//	public static List autoBox(Object value) {
//		if(value.getClass().getComponentType().isPrimitive()) {
//			//ClassUtils.primitivesToWrappers(value.getClass().getComponentType());
//			//value.getClass().getComponentType().
//			if(value.getClass().getComponentType() == boolean.class) {
//				return Arrays.asList(ArrayUtils.toObject(((boolean[]) value)));
//			} else if(value.getClass().getComponentType() == byte.class) {
//				return Arrays.asList(ArrayUtils.toObject(((byte[]) value)));
//			} else if(value.getClass().getComponentType() == char.class) {
//				return Arrays.asList(ArrayUtils.toObject(((char[]) value)));
//			} else if(value.getClass().getComponentType() == short.class) {
//				return Arrays.asList(ArrayUtils.toObject(((short[]) value)));
//			} else if(value.getClass().getComponentType() == int.class) {
//				return Arrays.asList(ArrayUtils.toObject(((int[]) value)));
//			} else if(value.getClass().getComponentType() == long.class) {
//				return Arrays.asList(ArrayUtils.toObject(((long[]) value)));
//			} else if(value.getClass().getComponentType() == float.class) {
//				return Arrays.asList(ArrayUtils.toObject(((float[]) value)));
//			} else {
//				return Arrays.asList(ArrayUtils.toObject(((double[]) value)));
//			}
//		} else if (value.getClass().getComponentType().isArray()) {
//			List<List> metalist = new ArrayList<>();
//			Object[] ar = ((Object[]) value);
//			for(int i = 0; i < ar.length; i++) {
//				metalist.add(autoBox(ar[i]));
//			}
//			return metalist;
//		} else {
//			return Arrays.asList(((Object[]) value));
//		}
//	}
}
