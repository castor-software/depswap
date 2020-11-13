package org.json.simple;

import org.json.simple.parser.ParseException;
import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JException;
import se.kth.castor.yasjf4j.JFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class JSONArray implements List<Object>, JSONAware, JSONStreamAware {
	JArray json;

	public JSONArray(List in) {
		json = JFactory.createJArray();
		for(Object o: in) {
			try {
				json.YASJF4J_add(o);
			} catch (JException e) {
				e.printStackTrace();
			}
		}
	}

	public JSONArray(Set in) {
		json = JFactory.createJArray();
		for(Object o: in) {
			try {
				json.YASJF4J_add(o);
			} catch (JException e) {
				e.printStackTrace();
			}
		}
	}

	public JSONArray(String in) throws ParseException {
		try {
			json = (JArray) JFactory.parse(in);
		} catch (JException e) {
			throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
		}
	}

	public JSONArray() {
		json = JFactory.createJArray();
	}

	public JSONArray(JArray a) {
		json = a;
		/*try {
			json = (JArray) Utils.deepTranslate(a, JSONObject::new, JSONArray::new);
			System.out.println("json contains null? " + containsNull());
		} catch (JException e) {
			e.printStackTrace();
		}*/
	}

	public boolean containsNull() throws JException {
		for(int i =0; i < json.YASJF4J_size(); i++) {
			if(json.YASJF4J_get(i) == null) return true;
		}
		return false;
	}

	public static String toJSONString(List list) {
		JSONArray ar = new JSONArray(list);
		return ar.toJSONString();
	}

	public static String toJSONString(Object o) {
		return JSONValue.toJSONString(o);
	}

	public static void writeJSONString(Object o, StringWriter w) {
		JSONValue.writeJSONString(o,w);
	}

	@Override
	public int size() {
		return json.YASJF4J_size();
	}

	@Override
	public boolean isEmpty() {
		return json.YASJF4J_size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		for(int i = 0; i < json.YASJF4J_size(); i++) {
			try {
				if(json.YASJF4J_get(i).equals(o))
					return true;
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {
			int index = 0;
			JArray jsonA = json;
			@Override
			public boolean hasNext() {
				return index >= jsonA.YASJF4J_size();
			}

			@Override
			public Object next() {
				try {

					return JSONValue.shield(jsonA.YASJF4J_get(index++));
				} catch (JException e) {
					return null;
				}
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] res = new Object[json.YASJF4J_size()];
		for(int i = 0; i < json.YASJF4J_size(); i++) {
			try {
				res[i] = JSONValue.shield(json.YASJF4J_get(i));
			} catch (JException e) {
				e.printStackTrace();
			}
		}

		return res;
	}

	@Override
	public boolean add(Object o) {
		try {
			json.YASJF4J_add(o);
		} catch (JException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		for(int i = 0; i < json.YASJF4J_size(); i++) {
			try {
				if(json.YASJF4J_get(i).equals(o)) {
					json.YASJF4J_remove(i);
					return true;
				}
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		for(Object o: c) {
			try {
				json.YASJF4J_add(o);
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		json = JFactory.createJArray();
	}

	@Override
	public Object set(int index, Object element) {
		Object val = null;
		try {
			val = json.YASJF4J_get(index);
		} catch (JException e) {
			e.printStackTrace();
		}

		try {
			json.YASJF4J_set(index, element);
		} catch (JException e) {
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(int index) {
		Object val = null;
		try {
			val = json.YASJF4J_get(index);
		} catch (JException e) {
			e.printStackTrace();
		}
		try {
			json.YASJF4J_remove(index);
		} catch (JException e) {
			e.printStackTrace();
		}
		return val;
	}

	@Override
	public int indexOf(Object o) {
		for(int i = 0; i < json.YASJF4J_size(); i++) {
			try {
				if(json.YASJF4J_get(i).equals(o)) {
					return i;
				}
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = -1;
		for(int i = 0; i < json.YASJF4J_size(); i++) {
			try {
				if(json.YASJF4J_get(i).equals(o)) {
					index =  i;
				}
			} catch (JException e) {
				e.printStackTrace();
			}
		}
		return index;
	}

	@Override
	public ListIterator listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection c) {
		for(Object o: c) {
			if(!contains(o)) return false;
		}
		return true;
	}

	@Override
	public Object[] toArray(Object[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toJSONString() {
		return json.YASJF4J_toString();
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		out.write(json.YASJF4J_toString());
	}


	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(!(o instanceof JSONObject)) return false;
		JSONArray other = ((JSONArray) o);
		if(other.size() != size()) return false;
		for (Object e0: this) {
			if(!other.contains(e0)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Object get(int index) {
		try {
			return JSONValue.shield(json.YASJF4J_get(index));
		} catch (JException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return json.YASJF4J_toString();
	}
}
