const fs = require("fs");
const utils = require("./utils");
const config = require("./config");

let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "reposWithJSON_with_static_analysis.json")
);

(async () => {
  repo: for (let repo in mavenGraph) {
    const lib = mavenGraph[repo];
    if (!lib.commit || !utils.isGreen(lib.test_results)) {
      delete mavenGraph[repo];
      continue;
    }
  }

  
  console.log(Object.keys(mavenGraph).length);
  fs.writeFileSync(
    config.output + "json_dataset.json",
    JSON.stringify(mavenGraph)
  );
})();
