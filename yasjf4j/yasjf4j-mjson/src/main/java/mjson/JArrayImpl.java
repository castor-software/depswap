package mjson;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;

import java.util.List;
import java.util.Map;

public class JArrayImpl extends Json.ArrayJson implements JArray {

	public JArrayImpl(String json) throws JException {
		Json a = Json.read(json);
		fill(a.asList());
	}

	public void fill(List a) throws JException {
		for(Object el: a) {
			if(el instanceof Json) {
				if (((Json) el).isObject())
					add(new JObjectImpl((Json) el));
				else if (((Json) el).isArray())
					add(new JArrayImpl((Json) el));
				else
					add(el);
			} else if(el instanceof List) {
				add(new JArrayImpl((List) el));
			} else {
				add(el);
			}
		}
	}

	public JArrayImpl(List json) throws JException {
		fill(json);
	}

	public JArrayImpl(Json json) throws JException {
		fill(json.asList());
	}

	@Override
	public int YASJF4J_size() {
		return asList().size();
	}

	@Override
	public Object YASJF4J_get(int i) throws JException {
		return at(i);
	}

	@Override
	public void YASJF4J_set(int i, Object o) throws JException {
		set(i, o);
	}

	@Override
	public void YASJF4J_add(Object o) throws JException {
		add(o);
	}

	@Override
	public void YASJF4J_remove(int i) throws JException {
		remove(i);
	}

	@Override
	public String YASJF4J_toString() {
		return toString();
	}
}