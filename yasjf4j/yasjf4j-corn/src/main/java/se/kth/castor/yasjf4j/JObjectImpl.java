package se.kth.castor.yasjf4j;



import net.sf.corn.converter.ConversionException;
import net.sf.corn.converter.json.JsTypeComplex;
import net.sf.corn.converter.json.JsTypeList;
import net.sf.corn.converter.json.JsTypeNull;
import net.sf.corn.converter.json.JsTypeObject;
import net.sf.corn.converter.json.JsTypeSimple;
import net.sf.corn.converter.json.JsonConverter;
import net.sf.corn.converter.json.JsonStringParser;

import java.util.Set;

public class JObjectImpl extends JsTypeComplex implements JObject {

	public JObjectImpl() {
	}

	public JObjectImpl(JsTypeComplex json) throws JException {
		try {
			for(String key: json.keys()) {
				Object el = json.get(key);
				if(el instanceof JsTypeComplex) {
					put(key, new JObjectImpl((JsTypeComplex) el));
				} else if (el instanceof JsTypeList) {
					put(key, new JArrayImpl((JsTypeList) el));
				} else {
					put(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			JsTypeComplex o = (JsTypeComplex) JsonStringParser.parseJsonString(json);
			for(String key: o.keys()) {
				Object el = o.get(key);
				if(el instanceof JsTypeComplex) {
					put(key, new JObjectImpl((JsTypeComplex) el));
				} else if (el instanceof JsTypeList) {
					put(key, new JArrayImpl((JsTypeList) el));
				} else {
					put(key, shield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return keys();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		try {
			return unshield(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s,shield(o));
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
		return toString();
	}



	public static JsTypeObject shield(Object o) {
		if(o instanceof JsTypeObject) {
			return (JsTypeObject) o;
		} else if(o == null) {
			return JsTypeNull.NULL;
		} else {
			return new JsTypeSimple(o);
		}
	}

	public static Object unshield(JsTypeObject o) throws ConversionException {
		if(o instanceof JsTypeNull) {
			return null;
		} else if (o instanceof JObjectImpl) {
			return o;
		} else if (o instanceof JArrayImpl) {
			return o;
		} else if (o instanceof JsTypeSimple) {
			if(((JsTypeSimple) o).getSimpleValue().startsWith("\"")) return ((JsTypeSimple) o).toStringValue();
			JsonConverter converter = new JsonConverter();
			return converter.toJava(o);
		} else {
			return o;
		}
	}
}
