package se.kth.assertteam;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvConfigurationGenerator implements ConfigurationGenerator {
	Queue<Configuration> cfgs = new ArrayDeque<>();
	int totalCfgs;
	List<String> keys = new ArrayList<>();


	public CsvConfigurationGenerator(String srcPath) throws Exception {

		//parse header
		try (Stream<String> stream = Files.lines(Paths.get(srcPath), StandardCharsets.UTF_8)) {
			Spliterator<String> lines = stream.spliterator();

			lines.tryAdvance( h -> {
				String rawKeys[] = h.split(",");
					for (String k: rawKeys) {
						keys.add(k);
					}
				}
			);
			lines.forEachRemaining(s -> {
				String fields[] = s.split(",");
				SimpleConfiguration cfg = new SimpleConfiguration();
				for(int i = 0; i < keys.size(); i++) {
					cfg.data.put(keys.get(i), fields[i]);
					cfg.name = fields[0];
				}
				cfgs.add(cfg);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		totalCfgs = cfgs.size();
		//for line
		//create Cfg
		//add to list

	}

	public CsvConfigurationGenerator(File src, File results) {

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

	public class SimpleConfiguration implements Configuration {
		public JSONObject data = new JSONObject();
		public String name;

		@Override
		public JSONObject toJSON() {
			return data;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
