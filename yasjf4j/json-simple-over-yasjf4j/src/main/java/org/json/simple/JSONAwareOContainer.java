package org.json.simple;

import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

import java.util.Set;

public class JSONAwareOContainer extends JSONAwareContainer implements JObject {
	JSONAware o;
	JObject r;

	public JSONAwareOContainer(JSONAware j, JObject s) {
		super(j);
		o = j;
		r = s;
	}

	@Override
	public JSONAware get() {
		return o;
	}

	@Override
	public String toString() {
		return o.toJSONString();
	}

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof JSONAwareContainer && ((JSONAwareContainer) o).o != null) {
			return  ((JSONAwareContainer) o).o.equals(this.o);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return o.hashCode();
	}

	@Override
	public Set<String> YASJF4J_getKeys() {
		return r.YASJF4J_getKeys();
	}

	@Override
	public Object YASJF4J_get(String s) throws JException {
		return r.YASJF4J_get(s);
	}

	@Override
	public void YASJF4J_put(String s, Object o) throws JException {
		r.YASJF4J_put(s,o);
	}

	@Override
	public void YASJF4J_remove(String s) throws JException {
		r.YASJF4J_remove(s);
	}

	@Override
	public String YASJF4J_toString() {
		return r.YASJF4J_toString();
	}
}
