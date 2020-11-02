const fs = require("fs");

const config = require("./config");

let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "reposWithJSON_with_static_analysis.json")
);

(async () => {
  repo: for (let repo in mavenGraph) {
    const lib = mavenGraph[repo];
    if (!lib.commit || !lib.test_results || lib.test_results[0] == null || lib.test_results[0].passing == 0 ||(lib.test_results[0].modules && lib.test_results[0].modules.length == 0)) {
      delete mavenGraph[repo];
      continue;
    }
    for (test of lib.test_results) {
      if (test == null) {
        continue repo;
      }
      if (test.error != 0 || test.failing != 0 || test.passing <= 0) {
        delete mavenGraph[repo];
        continue repo;
      }
    }
  }

  
  console.log(Object.keys(mavenGraph).length);
  fs.writeFileSync(
    config.output + "json_dataset.json",
    JSON.stringify(mavenGraph)
  );
})();
