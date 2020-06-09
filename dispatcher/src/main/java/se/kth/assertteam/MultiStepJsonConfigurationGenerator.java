package se.kth.assertteam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Stream;

public class MultiStepJsonConfigurationGenerator implements ConfigurationGenerator {
	Queue<Configuration> cfgs = new ConcurrentLinkedDeque<>();
	int totalCfgs;
	List<String> keys = new ArrayList<>();


	public MultiStepJsonConfigurationGenerator(String srcIn) {
		try (Stream<String> stream = Files.lines(Paths.get(srcIn), StandardCharsets.UTF_8)) {
			Spliterator<String> lines = stream.spliterator();

			lines.tryAdvance(
					h -> {
						String rawKeys[] = h.split(",");
						for (String k: rawKeys) {
							keys.add(k);
						}
					}
			);

			lines.forEachRemaining(s -> {
				String fields[] = s.split(",");
				MultiStepJsonConfigurationGenerator.SimpleMultiStepConfiguration cfg = new MultiStepJsonConfigurationGenerator.SimpleMultiStepConfiguration();
				for(int i = 0; i < keys.size(); i++) {
					cfg.data.put(keys.get(i), fields[i]);
					cfg.name = fields[0];
				}
				JSONArray steps = new JSONArray();
				//steps.put("print_parameters", new JSONObject());

				//Add tasks
				JSONObject parameters = new JSONObject();
				parameters.put("commit", cfg.data.get("commit"));
				parameters.put("repo", cfg.data.get("repo"));
				parameters.put("url", cfg.data.get("url"));
				parameters.put("step", "clone_repo");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("repo", cfg.data.get("repo"));
				parameters.put("step", "cd");
				steps.add(parameters);


				parameters = new JSONObject();
				parameters.put("step", "mvn_compile");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("g", cfg.data.get("g"));
				parameters.put("a", cfg.data.get("a"));
				parameters.put("step", "count_dep_to_replace");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("step", "mvn_test");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("packages", cfg.data.get("packages"));
				parameters.put("step", "analyze_static_usages");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("g", cfg.data.get("g"));
				parameters.put("a", cfg.data.get("a"));
				parameters.put("v", cfg.data.get("v"));
				parameters.put("rg", cfg.data.get("rg"));
				parameters.put("ra", cfg.data.get("ra"));
				parameters.put("rv", cfg.data.get("rv"));
				parameters.put("step", "transform_pom");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("step", "mvn_test_again");
				steps.add(parameters);

				parameters = new JSONObject();
				parameters.put("g", cfg.data.get("g"));
				parameters.put("a", cfg.data.get("a"));
				parameters.put("v", cfg.data.get("v"));
				parameters.put("rg", cfg.data.get("rg"));
				parameters.put("ra", cfg.data.get("ra"));
				parameters.put("rv", cfg.data.get("rv"));
				parameters.put("step", "restore_pom");
				steps.add(parameters);



				cfg.data.put("steps", steps);
				cfg.results.put("steps", new JSONArray());
				//System.out.println("gen: " + cfg.toJSON().toJSONString());
				cfgs.add(cfg);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

		totalCfgs = cfgs.size();
	}



	@Override
	public Configuration getConfiguration() {
		return cfgs.poll();
	}

	@Override
	public int getTotal() {
		return totalCfgs;
	}

	@Override
	public int getCurrent() {
		return cfgs.size();
	}

	public class SimpleMultiStepConfiguration extends MultiStepConfiguration {

	}
}
