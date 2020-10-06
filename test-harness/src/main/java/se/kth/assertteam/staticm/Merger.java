package se.kth.assertteam.staticm;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Merger {


	public static void main(String[] args) throws ParseException, IOException {
		if (args.length < 2) {
			System.err.println("Usage: java -jar myjar.jar path/to/json/array out.json");
			System.exit(-1);
		}

		File in = new File(args[0]);
		File out = new File(args[1]);

		boolean filter = args.length > 2;

		JSONParser p = new JSONParser();
		JSONArray a = (JSONArray) p.parse(readFile(in));

		Map<String, Map<String, Map<String, Integer>>> heatmap = new HashMap<>();

		int nbClientUsages = 0;

		for(int i = 0; i < a.size(); i++) {
			Object o = a.get(i);
			if(o instanceof JSONObject) {
				nbClientUsages++;
				for (Object packO : ((JSONObject) o).keySet()) {
					String pack = (String) packO;
					Map<String, Map<String, Integer>> existingPack = heatmap.computeIfAbsent(pack, s -> new HashMap<>());
					for(Object methodO: ((JSONObject) ((JSONObject) o).get(pack)).keySet()) {
						String mr = (String) methodO;
						String[] parts = mr.split("\\.");

						String cl = parts[0];

						String m = parts.length > 1 ? parts[1] : cl ;



						Map<String, Integer> c = existingPack.computeIfAbsent(cl, s -> new HashMap<>());
						Integer nb = c.computeIfAbsent(m, s -> 0);
						nb++;
						c.put(m,nb);
					}
				}
			}
		}
		int threshold = 50;

		int pcks = 0, classes = 0, methods = 0;
		int rpcks = 0, rclasses = 0, rmethods = 0;

		if(filter) {
			Set<String> paToRemove = new HashSet<>();
			for(String pa: heatmap.keySet()) {
				pcks++;
				Set<String> clToRemove = new HashSet<>();
				for(String cl: heatmap.get(pa).keySet()) {
					classes++;
					Set<String> mToRemove = new HashSet<>();
					for(String m: heatmap.get(pa).get(cl).keySet()) {
						methods++;
						if(heatmap.get(pa).get(cl).get(m) < threshold) {
							mToRemove.add(m);
							rmethods++;
						}
					}
					for(String m: mToRemove) heatmap.get(pa).get(cl).remove(m);

					if(heatmap.get(pa).get(cl).isEmpty()) {
						clToRemove.add(cl);
						rclasses++;
					}
				}
				//heatmap.get(pa).remove(clToRemove);
				for(String cl: clToRemove) heatmap.get(pa).remove(cl);

				if(heatmap.get(pa).isEmpty()) {
					paToRemove.add(pa);
					rpcks++;
				}
			}
			//heatmap.remove(paToRemove);
			for(String pa: paToRemove) heatmap.remove(pa);
		}


		//System.out.println(JSONObject.toJSONString(heatmap));
		Writer w = new BufferedWriter(new FileWriter(out));
		JSONObject.writeJSONString(heatmap, w);
		w.flush();
		w.close();

		System.out.println("Nb clients detected using API: " + nbClientUsages);
		System.out.println("Removed " + rpcks +    " / " + pcks +    " packages, remains: " + (pcks - rpcks));
		System.out.println("Removed " + rclasses + " / " + classes + " classes, remains: " +  (classes - rclasses));
		System.out.println("Removed " + rmethods + " / " + methods + " methods, remains: " +  (methods - rmethods));
	}

	public static String readFile(File f) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader b = new BufferedReader(new FileReader(f))) {
			String line;
			while((line = b.readLine()) != null) {
				sb.append(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
