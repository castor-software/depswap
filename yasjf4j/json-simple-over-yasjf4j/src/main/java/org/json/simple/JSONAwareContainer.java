package org.json.simple;


import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;
import se.kth.castor.yasjf4j.JObject;

public class JSONAwareContainer {
	JSONAware o;


	public static JSONAwareContainer create(JSONAware j) {
		try {
			Object p = JFactory.parse(j.toJSONString());
			if(p instanceof JObject) return new JSONAwareOContainer(j, (JObject) p);
			else if(p instanceof JArray) return new JSONAwareAContainer(j, (JArray) p);
			else return new JSONAwareContainer(j);
		} catch (JException e) {
		}
		return null;
	}

	public JSONAwareContainer(JSONAware j) {
		o = j;
	}

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
}
