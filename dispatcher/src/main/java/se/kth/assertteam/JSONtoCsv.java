package se.kth.assertteam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Collectors;

public class JSONtoCsv {
	public static void main(String[] args) throws IOException, ParseException {
		File in = new File("../data/maven_graph.json");
		File out = new File("../data/maven_graph_json-simple.csv");

		JSONParser parser = new JSONParser();

		JSONObject raw = (JSONObject) parser.parse(readFile(in));
		JSONObject simple = (JSONObject) raw.get("com.googlecode.json-simple:json-simple");
		JSONArray clientsv111 = (JSONArray) ((JSONObject) simple.get("clients")).get("1.1.1");
		printResults(out,clientsv111);
	}

	public static String readFile(File f) throws IOException {
		return Files.lines(
				f.toPath(),
				Charset.forName("UTF-8")
			).collect(Collectors.joining("\n")
		);
	}

	public static void printResults(File output, JSONArray a) throws IOException {
		if(output.exists()) {
			output.delete();
		}
		output.createNewFile();
		try {
			Files.write(output.toPath(), "repo,url,commit,g,a,v,rg,ra,rv,packages\n".getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < a.size(); i++) {
			String repo = (String) ((JSONObject) a.get(i)).get("repo_name");
			String dirName = repo.split("/")[1];
			String line = dirName + ",https://github.com/" + repo + ".git,*,com.googlecode.json-simple,json-simple,*,se.kth.assert,json-simple-over-json,1.0-SNAPSHOT,org/json/simple\n";
			try {
				Files.write(output.toPath(), line.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
