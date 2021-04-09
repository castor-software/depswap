package se.kth.assertteam.depanalyzer;


import java.util.HashMap;
import java.util.Map;

public class API {
	int libID;

	Map<String, APIPackage> packages = new HashMap<>();

	public String toCsv() {
		StringBuilder sb = new StringBuilder();
		sb.append("Package" + "," +
				"Type" + "," +
				"isTypePublic" + "," +
				"isInterface" + "," +
				"isAnnotation" + "," +
				"isAbstract" + "," +
				"Element" + "," +
				"isPublic" + "," +
				"isField" + "," +
				"isStatic" + "\n");
		for(String pName: packages.keySet()) {
			APIPackage p = packages.get(pName);
			sb.append(p.toCsvLines());
		}
		return sb.toString();
	}


	class APIPackage {
		int id;
		String name;
		Map<String, APIClass> classes = new HashMap<>();

		public APIPackage(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public void insertType(String name, boolean isPublic, boolean isInterface, boolean isAnnotation, boolean isAbstract) {
			classes.putIfAbsent(name, new APIClass(name));
			APIClass cl = classes.get(name);
			cl.isPublic = isPublic;
			cl.isInterface =isInterface;
			cl.isAnnotation = isAnnotation;
			cl.isAbstract = isAbstract;
			cl.seen = true;
			classes.put(name,cl);
		}

		public void insertElement(String clazz, String element, boolean isStatic, boolean isPublic, boolean isField) {
			APIClass c = classes.computeIfAbsent(clazz, (str) -> new APIClass(str));
			c.insertElement(element,isStatic,isPublic,isField);
			//classes.put(clazz, c);
		}

		public String toCsvLines() {
			StringBuilder sb = new StringBuilder();
			for(String clName: classes.keySet()) {
				APIClass cl = classes.get(clName);
				sb.append(cl.toCsvLines(name));
			}
			return sb.toString();
		}
	}

	class APIClass {
		Integer id;
		String name;
		boolean isPublic;
		boolean isInterface;
		boolean isAnnotation;
		boolean isAbstract;
		boolean seen;
		Map<String, APIElement> elements = new HashMap<>();

		public APIClass(String name) {
			this.name = name;
		}

		public void insertElement(String element, boolean isStatic, boolean isPublic, boolean isField) {
			elements.putIfAbsent(element, new APIElement(element));
			APIElement el = elements.get(element);
			el.isStatic = isStatic;
			el.isPublic = isPublic;
			el.isField = isField;
			el.seen = true;
		}

		public String toCsvLines(String p) {
			StringBuilder sb = new StringBuilder();
			for(String elName: elements.keySet()) {
				APIElement el = elements.get(elName);
				sb.append(el.toCsvLine(p,name,isPublic,isInterface,isAnnotation,isAbstract));
			}
			return sb.toString();
		}
	}

	class APIElement {
		Integer id;
		String name;
		boolean isStatic;
		boolean isPublic;
		boolean isField;
		boolean seen;

		public APIElement(String name) {
			this.name = name;
		}

		public String toCsvLine(String p,
		                        String cl,
		                        boolean clIsPublic,
		                        boolean clIsInterface,
		                        boolean clIsAnnotation,
		                        boolean clIsAbstract
		) {
			StringBuilder sb = new StringBuilder();
			sb.append(p + "," +
					cl + "," +
					clIsPublic + "," +
					clIsInterface + "," +
					clIsAnnotation + "," +
					clIsAbstract + "," +
					name + "," +
					isPublic + "," +
					isField + "," +
					isStatic + "\n");
			return sb.toString();
		}
	}

	public void insertType(String pack, String name, boolean isPublic, boolean isInterface, boolean isAnnotation, boolean isAbstract) {
		APIPackage p = packages.computeIfAbsent(pack, (str) -> new APIPackage(0, str));
		p.insertType(name,isPublic,isInterface,isAnnotation,isAbstract);
		//packages.put(pack, p);
	}

	public void insertElement(String pack, String clazz, String element, boolean isStatic, boolean isPublic, boolean isField) {
		APIPackage p = packages.computeIfAbsent(pack, (str) -> new APIPackage(0, str));
		p.insertElement(clazz,element,isStatic,isPublic,isField);
		//packages.put(pack, p);
	}
}
