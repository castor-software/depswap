package se.kth.castor;

import se.kth.castor.yasjf4j.JArray;
import se.kth.castor.yasjf4j.JObject;

public interface JP {
	Object parseString(String in) throws Exception;
	String print(JObject in) throws Exception;
	String print(JArray in) throws Exception;
	String print(Object in) throws Exception;
	String getName();
	boolean equivalence(Object a, Object b);
}
