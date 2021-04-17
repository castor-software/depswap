package se.kth.castor.yasjf4j;


import argo.jdom.JsonArrayNodeBuilder;
import argo.jdom.JsonNode;
import argo.jdom.JsonNodeBuilder;
import argo.jdom.JsonObjectNodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static argo.jdom.JsonNodeBuilders.aFalseBuilder;
import static argo.jdom.JsonNodeBuilders.aNullBuilder;
import static argo.jdom.JsonNodeBuilders.aNumberBuilder;
import static argo.jdom.JsonNodeBuilders.aStringBuilder;
import static argo.jdom.JsonNodeBuilders.aTrueBuilder;
import static argo.jdom.JsonNodeBuilders.anArrayBuilder;
import static argo.jdom.JsonNodeBuilders.anObjectBuilder;
import static se.kth.castor.yasjf4j.JObjectImpl.printer;
import static se.kth.castor.yasjf4j.JObjectImpl.shield;
import static se.kth.castor.yasjf4j.JObjectImpl.unshield;
import static se.kth.castor.yasjf4j.JObjectImpl.parser;

public class JArrayImpl extends ArrayList implements JArray {

	public JArrayImpl() {
		super();
	}

	public JArrayImpl(String json) throws JException {
		try {

			for(JsonNode el: parser.parse(json).getElements()) {
				if(el.hasFields()) {
					add(new JObjectImpl((JsonNode) el));
				} else if (el.hasElements()) {
					add(new JArrayImpl((JsonNode) el));
				} else {
					add(unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	public JArrayImpl(List json) {
		for(Object el: json) {
			add(unshield(el));
		}
	}

	public JArrayImpl(JsonNode json) throws JException {
		try {
			for(JsonNode el: json.getElements()) {
				if(el.hasFields()) {
					add(new JObjectImpl((JsonNode) el));
				} else if (el.hasElements()) {
					add(new JArrayImpl((JsonNode) el));
				} else {
					add(unshield(el));
				}
			}
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public int YASJF4J_size() {
		return size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		try {
			return shield(get(i));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		try {
			set(i, unshield(o));

		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		try {
			add(unshield(o));
		} catch (Exception e) {
			throw new JException();
		}
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}



	JsonArrayNodeBuilder getBuilder() {
		JsonArrayNodeBuilder builder = anArrayBuilder();
		for(Object valT: this) {
			Object val = shield(valT);
			JsonNodeBuilder b;
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
			builder.withElement(b);
		}

		return builder;
	}

	@Override
	public String YASJF4J_toString() {
		return printer.format(getBuilder().build());
	}

}
