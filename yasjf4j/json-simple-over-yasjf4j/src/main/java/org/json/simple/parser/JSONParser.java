package org.json.simple.parser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class JSONParser {
	public JSONParser() {

	}

	public Object parse(String in) throws ParseException {
		char first = firstNonWhitChar(in);
		if(first == '{') {
			return new JSONObject(in);
		} else if (first == '[') {
			return new JSONArray(in);
		} else {
			throw new ParseException(0);
		}
	}



	public void parse(Reader in, ContentHandler contentHandler) throws ParseException, IOException {
		StringBuilder builder = new StringBuilder();

		BufferedReader br = new BufferedReader(in);
		String line;
		while ((line = br.readLine()) != null){
			builder.append(line);
		}
		String raw = builder.toString();
		char first = firstNonWhitChar(raw);
		Object json = null;
		//try {
			if (first == '{') {
				json = new JSONObject(raw);
			} else if (first == '[') {
				json = new JSONArray(raw);
			} else {
				throw new ParseException(0);
			}
		//} catch (JException e) {
		//	throw new ParseException(0);
		//}


		read(json, contentHandler);


	}

	private char firstNonWhitChar(String str) {
		return str.trim().charAt(0);
	}


	private boolean read(JSONObject o, ContentHandler contentHandler) throws ParseException, IOException {
		contentHandler.startJSON();
		for(Object k: o.keySet()) {
			String key = k.toString();
			if(!contentHandler.startObjectEntry(key))
				return false;
			if(!read(o.get(key), contentHandler))
				return false;
			if(!contentHandler.endObject())
				return false;
		}

		contentHandler.endJSON();
		return true;
	}

	private boolean read(JSONArray a, ContentHandler contentHandler) throws ParseException, IOException {
		if(!contentHandler.startArray())
			return false;
		for(int i = 0; i < a.size(); i++) {
			if(!read(a.get(i), contentHandler))
				return false;
		}
		if(!contentHandler.endArray())
			return false;
		return true;
	}

	private boolean read(Object obj, ContentHandler contentHandler) throws IOException, ParseException {
		if(obj instanceof JSONArray) {
			return read((JSONArray) obj, contentHandler);
		} else if(obj instanceof JSONObject) {
			return read((JSONObject) obj, contentHandler);
		} else if(obj instanceof Number) {
			return contentHandler.primitive(((Number) obj).longValue());
		} else if(obj instanceof String || obj instanceof Boolean || obj == null) {
			return contentHandler.primitive(obj);
		}
		return false;
	}
}
