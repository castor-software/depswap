package se.kth.assertteam.staticm;

import com.google.gson.JsonObject;
import org.json.simple.JSONObject;
import se.kth.assertteam.jsonbench.parser.GsonParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ReadStaticUsages {
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: java -jar myjar.jar path/to/json/array out.json");
			System.exit(-1);
		}

		File in = new File(args[0]);
		File out = new File(args[1]);



		Map<String, PackageInfo> heatmap = new HashMap<>();

		GsonParser gsonParser = new GsonParser();

		JsonObject json = (JsonObject) gsonParser.parseString(Merger.readFile(in));

		for(String repo: json.keySet()) {
			JsonObject repoData = json.getAsJsonObject(repo);

			if(repoData.has("static-usages")) {
				JsonObject staticUsages = repoData.getAsJsonObject("static-usages");
				if(staticUsages.size() > 0) {
					for(String p: staticUsages.keySet()) {
						JsonObject rawP = staticUsages.getAsJsonObject(p);
						PackageInfo pi = heatmap.computeIfAbsent(p, (str) -> new PackageInfo());
						pi.nbClient++;

						for(String m: rawP.keySet()) {
							String[] r = m.split("\\.");
							if(r.length < 2) {
								r = new String[]{m,m};
							}
							String cl = r[0];
							String meth = r[1];
							Map<String, Integer> ms = pi.classesInfo.computeIfAbsent(cl, str -> new HashMap<>());
							Integer nbU = ms.computeIfAbsent(meth, str -> 0);
							ms.put(meth, (nbU + 1));
						}
					}
				}
			}
		}


		Writer w = new BufferedWriter(new FileWriter(out));
		//w.write(gsonParser.print(heatmap));
		w.append("[");
		boolean isFirst = true;
		for(String p: heatmap.keySet()) {
			if(isFirst) {
				isFirst = false;
			} else {
				w.append(",");
			}

			heatmap.get(p).writeJson(w, p);

		}
		w.append("]");
		w.flush();
		w.close();
		System.out.println("Done");
	}

	static class PackageInfo {
		int nbClient = 0;
		Map<String, Map<String, Integer>> classesInfo = new HashMap<>();

		public void writeJson(Writer w, String pack) throws IOException {
			w.append("{");
			w.append("\"package\": \"" + pack + "\",");
			w.append("\"nbClients\": " + nbClient + "," + "\"classes\":");
			JSONObject.writeJSONString(classesInfo, w);
			w.append("}");
		}
	}
}
