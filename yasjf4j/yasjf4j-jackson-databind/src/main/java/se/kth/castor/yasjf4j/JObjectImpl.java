package se.kth.castor.yasjf4j;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.ArrayUtils;
import se.kth.castor.yasjf4j.util.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JObjectImpl extends ObjectNode implements JObject {
	public static ObjectMapper mapper = new ObjectMapper();

	public JObjectImpl() {
		super(mapper.getNodeFactory());
	}

	public JObjectImpl(ObjectNode json) throws JException {
		super(mapper.getNodeFactory());
		Iterator<Map.Entry<String,JsonNode>> it = json.fields();
		try {
			while(it.hasNext()) {
				Map.Entry<String,JsonNode> e = it.next();
				Object el = e.getValue();
				if(el instanceof ObjectNode) {
					put(e.getKey(), new JObjectImpl((ObjectNode) el));
				} else if (el instanceof ArrayNode) {
					put(e.getKey(), new JArrayImpl((ArrayNode) el));
				} else {
					put(e.getKey(), (JsonNode) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JObjectImpl(String json) throws JException {
		super(mapper.getNodeFactory());
		try {
			JsonNode o = mapper.readTree(json);
			Iterator<Map.Entry<String,JsonNode>> it = o.fields();
			while(it.hasNext()) {
				Map.Entry<String,JsonNode> e = it.next();
				Object el = e.getValue();
				if(el instanceof ObjectNode) {
					put(e.getKey(), new JObjectImpl((ObjectNode) el));
				} else if (el instanceof ArrayNode) {
					put(e.getKey(), new JArrayImpl((ArrayNode) el));
				} else {
					put(e.getKey(), (JsonNode) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public static JObjectImpl fromMap(Map json) throws JException {
		JObjectImpl r = new JObjectImpl();
		try {
			for(Object key: json.keySet()) {
				Object el = json.get(key);
				r.put((String) key, toJSONValue(el));
			}
		} catch (Exception e) {
			throw new JException();
		}
		return r;
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return _children.keySet();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		if(!_children.containsKey(s)) throw new JException();
		try {
			return toObject(get(s));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		try {
			put(s,  toJSONValue(o));
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
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JsonNode toJSONValue(Object o) throws JException {
		if(o == null) {
			return NullNode.getInstance();
		} else if (o instanceof JNull) {
			return NullNode.getInstance();
		} else if (o instanceof IntNode) {
			return (IntNode) o;
		} else if (o instanceof JsonNode) {
			return (JsonNode) o;
		} else if (o instanceof  Map) {
			return fromMap((Map) o);
		} else if (o instanceof List) {
			return JArrayImpl.fromList((List) o);
		} else if (o.getClass().isArray()) {
			return JArrayImpl.fromList(Utils.autoBox(o));
		} else if (o instanceof  String) {
			return new TextNode((String) o);
		} else if (o instanceof BigDecimal) {
			return new DecimalNode((BigDecimal) o);
		} else if (o instanceof  Double) {
			return new DoubleNode((Double) o);
		} else if (o instanceof  Float) {
			return new FloatNode((Float) o);
		} else if (o instanceof  Integer) {
			return new IntNode((Integer) o);
		} else if (o instanceof Byte) {
			return new ShortNode((Byte) o);
		} else if (o instanceof  Long) {
			return new LongNode((Long) o);
		} else if (o instanceof  Short) {
			return new ShortNode((Short) o);
		} else if (o instanceof BigInteger) {
			return new BigIntegerNode((BigInteger) o);
		} else if (o instanceof  Boolean) {
			return ((Boolean) o) ? BooleanNode.getTrue() : BooleanNode.getFalse();
		} else if (o instanceof  Character) {
			return new TextNode(((Character) o).toString());
		} else {
			return new POJONode(o);
			//return new TextNode(o.toString());
		}
	}

	public static Object toObject(Object o) throws ParseException {
		if(o instanceof TextNode) {
			return ((TextNode) o).asText();
		} else if (o instanceof DecimalNode) {
			return ((DecimalNode) o).decimalValue();
		} else if (o instanceof DoubleNode) {
			return ((DoubleNode) o).doubleValue();
		} else if (o instanceof FloatNode) {
			return ((FloatNode) o).floatValue();
		} else if (o instanceof IntNode) {
			return ((IntNode) o).intValue();
		} else if (o instanceof LongNode) {
			return ((LongNode) o).longValue();
		} else if (o instanceof ShortNode) {
			return ((ShortNode) o).shortValue();
		} else if (o instanceof BigIntegerNode) {
			return ((BigIntegerNode) o).bigIntegerValue();
		} else if (o instanceof POJONode) {
			return ((POJONode) o).getPojo();
		} else if (o instanceof BooleanNode) {
			return ((BooleanNode) o).booleanValue();
		} else if (o == NullNode.getInstance()) {
			return JNull.getInstance();
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
