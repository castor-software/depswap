package se.kth.castor.yasjf4j;

public class MyNull {
	static MyNull instance = new MyNull();

	public MyNull() {
	}

	public static MyNull getInstance() {
		return instance;
	}

	public String toString() {
		return "null";
	}
	public String toJSON() {
		return "null";
	}
}
