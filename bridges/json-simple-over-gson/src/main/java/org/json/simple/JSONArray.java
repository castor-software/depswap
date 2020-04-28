package org.json.simple;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class JSONArray implements List<Object>, JSONAware, JSONStreamAware {
	JsonArray json;

	public JSONArray(List in) {
		json = (JsonArray) JSONValue.lift(in);
	}

	public JSONArray(String in) {
		json = (JsonArray) JsonParser.parseString(in);
	}

	public JSONArray() {
		json = new JsonArray();
	}

	public JSONArray(JsonArray a) {
		json = a;
	}

	@Override
	public int size() {
		return json.size();
	}

	@Override
	public boolean isEmpty() {
		return json.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		json.contains(JSONValue.lift(o));
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		Object[] res = new Object[json.size()];
		for(int i = 0; i < json.size(); i++) {
			res[i] = JSONValue.down(json.get(i));
		}
		return res;
	}

	@Override
	public boolean add(Object o) {
		json.add(JSONValue.lift(o));
		return true;
	}

	@Override
	public boolean remove(Object o) {
		for(int i = 0; i < json.size(); i++) {
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
			json.add(JSONValue.lift(o));
		}
		return true;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		json = new JsonArray();
	}

	@Override
	public Object set(int index, Object element) {
		return json.set(index, JSONValue.lift(element));
	}

	@Override
	public void add(int index, Object element) {
		json.set(index,JSONValue.lift(element));
	}

	@Override
	public Object remove(int index) {
		return json.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		for(int i = 0; i < json.size(); i++) {
			if(json.get(i).equals(o)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		int index = -1;
		for(int i = 0; i < json.size(); i++) {
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
		throw new UnsupportedOperationException();
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
		if(!(o instanceof JSONArray)) return false;
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
		return JSONValue.down(json.get(index));
	}

	@Override
	public String toString() {
		return json.toString();
	}
}
