package se.kth.assertteam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spark.Request;
import spark.Response;

import java.io.File;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class Server {

	public static void log(Request req) {
		System.out.println("[" + req.ip() + "] " + req.pathInfo() + " -> " + req.body());
	}


	public static void main(String[] args) throws Exception {
		//ParseArguments
		int SERVER_PORT = 8090;
		//String inputPath = "/home/nharrand/Documents/depswap/dispatcher/src/test/resources/test-2.csv";
		String inputPath = "/home/nharrand/Documents/depswap/dispatcher/src/test/resources/dataset-small-json-simple-1.1.1.csv";

		File logs;
		File results;
		JSONParser p = new JSONParser();
		ConfigurationGenerator generator = new CsvConfigurationGenerator(inputPath);
		ProgressManager progressManager = new ProgressManager();
		port(SERVER_PORT);


		//Init config generator

		//Setup routes
		get("/", (Request req, Response res) -> {
			res.type("text/html");
			res.status(200);
			return progressManager.getHtmlOverview();
		});

		get("/getHostName", (Request req, Response res) -> {
			log(req);
			res.status(200);
			JSONObject json = new JSONObject();
			json.put("workerName", progressManager.getNewHostName());

			res.type("application/json");
			return json.toString();
		});

		get("/getConfiguration", (Request req, Response res) -> {
			log(req);
			String worker = req.headers("workerName");

			Configuration cfg = generator.getConfiguration();
			if(cfg == null) {
				res.status(204);
				return new JSONObject();
			}

			res.status(200);

			progressManager.giveConfiguration(cfg, worker);

			JSONObject json = cfg.toJSON();
			System.out.println("[Server] Sent config: " + json.toJSONString());


			res.type("application/json");
			return json.toString();
		});
		post("/postResult", (Request req, Response res) -> {
			log(req);
			String worker = req.headers("workerName");
			String raw = req.body();
			Object parsed = p.parse(raw);
			if(!(parsed instanceof  JSONObject)) {
				res.status(400);
				return "Error, unable to parse JSON.";
			}
			JSONObject configResults = (JSONObject) parsed;
			progressManager.receiveConfigurationResults(worker, configResults);
			try {

			} catch (ClassCastException | NullPointerException e) {
				res.status(400);
				return "Error, unable to parse JSON.";
			}
			res.status(200);
			return "";
		});


		System.out.println("Unexpected");
	}
}
