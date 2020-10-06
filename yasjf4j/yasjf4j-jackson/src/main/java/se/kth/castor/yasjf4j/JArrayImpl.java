package se.kth.castor.yasjf4j;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JArrayImpl extends ArrayNode implements JArray {

	public JArrayImpl() {
		super(JObjectImpl.mapper.getNodeFactory());
	}

	public JArrayImpl(String json) throws JException {
		super(JObjectImpl.mapper.getNodeFactory());
		try {
			JsonNode o = JObjectImpl.mapper.readTree(json);
			Iterator<JsonNode> it = o.iterator();
			while(it.hasNext()) {
				JsonNode el = it.next();
				if(el instanceof ObjectNode) {
					add(new JObjectImpl((ObjectNode) el));
				} else if (el instanceof ArrayNode) {
					add(new JArrayImpl((ArrayNode) el));
				} else {
					add((JsonNode) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(ArrayNode json) throws JException {
		super(JObjectImpl.mapper.getNodeFactory());
		try {
			Iterator<JsonNode> it = json.iterator();
			while(it.hasNext()) {
				JsonNode el = it.next();
				if(el instanceof ObjectNode) {
					add(new JObjectImpl((ObjectNode) el));
				} else if (el instanceof ArrayNode) {
					add(new JArrayImpl((ArrayNode) el));
				} else {
					add((JsonNode) el);
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public static JArrayImpl fromList(List json) throws JException {
		JArrayImpl r = new JArrayImpl();
		try {
			for(Object el: json) {
				r.add(JObjectImpl.toJSONValue(el));
			}
		} catch (Exception e) {
			throw new JException();
		}
		return r;
	}

	@Override
	public int YASJF4J_size() {
		return size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			return JObjectImpl.toObject(get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, JObjectImpl.toJSONValue(o));

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(JObjectImpl.toJSONValue(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		try {
			return JObjectImpl.mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
