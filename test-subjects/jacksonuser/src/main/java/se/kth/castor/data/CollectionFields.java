package se.kth.castor.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionFields {
	public List<Simple> l = new LinkedList<>();
	public Map<String, Composite> m = new HashMap<>();

	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		else if (!(o instanceof CollectionFields)) return false;
		CollectionFields c = (CollectionFields) o;
		if(l.size() != c.l.size()) return false;
		for(int i = 0; i < l.size(); i++) {
			if(!l.get(i).equals(c.l.get(i))) return false;
		}
		if(m.keySet().size() != c.m.entrySet().size()) return false;
		for(String key: m.keySet()) {
			if(!c.m.containsKey(key)) return false;
			if(!c.m.get(key).equals(m.get(key))) return false;
		}
		return true;
	}
}
