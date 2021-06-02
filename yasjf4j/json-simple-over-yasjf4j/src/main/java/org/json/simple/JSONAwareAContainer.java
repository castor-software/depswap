package org.json.simple;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

public class JSONAwareAContainer extends JSONAwareContainer implements JArray {
	JSONAware o;
	JArray r;

	public JSONAwareAContainer(JSONAware j, JArray s) {
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
	public int YASJF4J_size() {
		return r.YASJF4J_size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		return r.YASJF4J_get(i);
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		r.YASJF4J_set(i,o);
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		r.YASJF4J_add(o);
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		r.YASJF4J_remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return r.YASJF4J_toString();
	}
}
