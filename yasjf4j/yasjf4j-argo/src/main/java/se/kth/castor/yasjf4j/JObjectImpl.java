package se.kth.castor.yasjf4j;





import argo.format.CompactJsonFormatter;
import argo.format.JsonNumberUtils;
import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeBuilder;
import argo.jdom.JsonNodeBuilders;
import argo.jdom.JsonObjectNodeBuilder;
import argo.jdom.JsonStringNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static argo.jdom.JsonNodeBuilders.aFalseBuilder;
import static argo.jdom.JsonNodeBuilders.aNullBuilder;
import static argo.jdom.JsonNodeBuilders.aNumberBuilder;
import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.aTrueBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;

public class JObjectImpl extends LinkedHashMap<String, Object> implements JObject {

	public static JdomParser parser = new JdomParser();
	public static CompactJsonFormatter printer = new CompactJsonFormatter();


	public JObjectImpl() {
	}

	public JObjectImpl(JsonNode json) throws JException {
		try {
			Map<JsonStringNode, JsonNode> fields = json.getFields();
			for(JsonStringNode key: fields.keySet()) {
				JsonNode el = fields.get(key);
				if(el.hasFields()) {
					put(key.getText(), new JObjectImpl((JsonNode) el));
				} else if (el.hasElements()) {
					put(key.getText(), new JArrayImpl((JsonNode) el));
				} else {
					put(key.getText(), unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(Map fields) {
		for(Object rawKey: fields.keySet()) {
			Object el = fields.get(rawKey);
			put(rawKey.toString(), unshield(el));
		}
	}

	public JObjectImpl(String json) throws JException {
		try {
			Map<JsonStringNode, JsonNode> fields = parser.parse(json).getFields();
			for(JsonStringNode key: fields.keySet()) {
				JsonNode el = fields.get(key);
				if(el.hasFields()) {
					put(key.getText(), new JObjectImpl((JsonNode) el));
				} else if (el.hasElements()) {
					put(key.getText(), new JArrayImpl((JsonNode) el));
				} else {
					put(key.getText(), unshield(el));
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
			return shield(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s, unshield(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		remove(s);
	}

	JsonObjectNodeBuilder getBuilder() {
		JsonObjectNodeBuilder builder = anObjectBuilder();
		for(String key: keySet()) {
			JsonNodeBuilder b;
			Object val = shield(get(key));
			if(val == null) {
				b = aNullBuilder();
			} else if(val instanceof JNull) {
				b = aNullBuilder();
			} else if(val instanceof JObjectImpl) {
				b = ((JObjectImpl) val).getBuilder();
			} else if(val instanceof JArrayImpl) {
				b = ((JArrayImpl) val).getBuilder();
			} else if(val instanceof Number) {
				b = aNumberBuilder(((Number) val).toString());
			} else if(val instanceof Boolean) {
				b = ((Boolean) val).booleanValue() ? aTrueBuilder() : aFalseBuilder();
			} else {
				b = aStringBuilder(val.toString());
			}
			builder.withField(key, b);
		}

		return builder;
	}


	@Override
	public String YASJF4J_toString() {
		return printer.format(getBuilder().build());
	}

	public static Object unshield(Object o) {

		if(o instanceof JNull) {
			//return null;
			return JsonNodeBuilders.aNullBuilder().build();
		} else if(o instanceof JsonNode) {
			JsonNode n = (JsonNode) o;
//			if(n.isNullNode()) {
//				return JNull.getInstance();
			if(n == null) {
				return null;
				//return JsonNodeBuilders.aNullBuilder().build();
			} else if (n.isNumberValue()) {
				String val = n.getNumberValue();
				if(val.contains("e-") || val.contains("E-") || val.contains(".")) {
					BigDecimal bd = JsonNumberUtils.asBigDecimal(val);
					Double d = JsonNumberUtils.asDouble(n.getNumberValue());
					if(bd.toString().equals(d.toString())) return d;
					return bd;
				} else {
					BigInteger bi = JsonNumberUtils.asBigInteger(n.getNumberValue());
					if(bi.bitLength() > 32) return bi;
					return JsonNumberUtils.asInteger(n.getNumberValue());
				}
			} else if(n.isBooleanValue()) {
				return n.getBooleanValue();
			} else if (n.isStringValue()) {
				return n.getStringValue();
			}
		}/* else if (o instanceof Map) {
			return new JObjectImpl((Map) o);
		} else if (o instanceof List) {
			return new JArrayImpl((List) o);
		}*/
		return o;
	}


	public static Object shield(Object o) {
		if(o instanceof JsonNode) {
			JsonNode n = (JsonNode) o;
			if (n.isNullNode()) {
				return JNull.getInstance();
			}
			else return o;
		}
		//if (o instanceof JNull) return aNullBuilder();
		else return o;
	}
}
