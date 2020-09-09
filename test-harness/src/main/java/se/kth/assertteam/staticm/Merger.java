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
import java.util.HashMap;
import java.util.Map;

public class Merger {


	public static void main(String[] args) throws ParseException, IOException {
		if (args.length < 2) {
			System.err.println("Usage: java -jar myjar.jar path/to/json/array out.json");
			System.exit(-1);
		}

		File in = new File(args[0]);
		File out = new File(args[1]);

		JSONParser p = new JSONParser();
		JSONArray a = (JSONArray) p.parse(readFile(in));

		Map<String, Map<String, Map<String, Integer>>> heatmap = new HashMap<>();



		for(int i = 0; i < a.size(); i++) {
			Object o = a.get(i);
			if(o instanceof JSONObject) {
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

		System.out.println(JSONObject.toJSONString(heatmap));
		Writer w = new BufferedWriter(new FileWriter(out));
		JSONObject.writeJSONString(heatmap, w);
		w.flush();
		w.close();
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
