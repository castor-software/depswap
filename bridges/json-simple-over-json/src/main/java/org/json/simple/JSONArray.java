package org.json.simple;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JSONArray implements List<Object>, JSONAware, JSONStreamAware {
	org.json.JSONArray json;

	public JSONArray(List in) {
		json = new org.json.JSONArray(in);
	}
	public JSONArray(String in) {
		json = new org.json.JSONArray(in);
	}

	public JSONArray() {
		json = new org.json.JSONArray();
	}

	public JSONArray(org.json.JSONArray a) {
		json = a;
	}

	@Override
	public int size() {
		return json.length();
	}

	@Override
	public boolean isEmpty() {
		return json.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		for(int i = 0; i < json.length(); i++) {
			if(json.get(i).equals(o))
				return true;
		}
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		return json.iterator();
	}

	@Override
	public Object[] toArray() {
		return json.toList().toArray();
	}

	@Override
	public boolean add(Object o) {
		json.put(o);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		for(int i = 0; i < json.length(); i++) {
			if(json.get(i).equals(o)) {
				json.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		for(Object o: c) {
			json.put(o);
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		json = new org.json.JSONArray();
	}

	@Override
	public Object set(int index, Object element) {
		return json.put(index, element);
	}

	@Override
	public void add(int index, Object element) {
		json.put(index,element);
	}

	@Override
	public Object remove(int index) {
		return json.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		for(int i = 0; i < json.length(); i++) {
			if(json.get(i).equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = -1;
		for(int i = 0; i < json.length(); i++) {
			if(json.get(i).equals(o)) {
				index =  i;
			}
		}
		return index;
	}

	@Override
	public ListIterator listIterator() {
		return (ListIterator) json.iterator();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray(Object[] a) {
		return json.toList().toArray(a);
	}

	@Override
	public String toJSONString() {
		return json.toString();
	}

	@Override
	public void writeJSONString(Writer out) throws IOException {
		out.write(json.toString());
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
		Object o = json.get(index);
		if(o instanceof org.json.JSONObject) o = new JSONObject((org.json.JSONObject) o);
		else if(o instanceof org.json.JSONArray) o = new JSONArray((org.json.JSONArray) o);
		return o;
	}

	@Override
	public String toString() {
		return json.toString();
	}
}
