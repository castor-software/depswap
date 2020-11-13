const fs = require("fs");
const ProgressBar = require("progress");
const async = require("async");
var exec = require("child_process").exec;

const config = require("./config");
const utils = require("./utils");

let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "reposWithJSON_with_test_results.json")
);
let results = {};
if (fs.existsSync(config.output + "reposWithJSON_with_static_analysis.json")) {
  results = JSON.parse(
    fs.readFileSync(config.output + "reposWithJSON_with_static_analysis.json")
  );
}
function execTest(repo, commit) {
  return new Promise((resolve) => {
    exec(
      `docker run --rm runner analyze --url https://github.com/${repo} --commit ${commit} --timeout 20m`,
      (error, stdout, stderr) => {
        if (error == null && stdout) {
          try {
            return resolve(JSON.parse(stdout));
          } catch (error) {
            console.log(error)
          }
        }
        resolve(null);
      }
    );
  });
}

(async () => {
  const tasks = [];
  console.log(Object.keys(mavenGraph).length)
  for (let repo in mavenGraph) {
    const lib = mavenGraph[repo];
    const resultsLib = results[repo];
    if (resultsLib && resultsLib['static-usages'] != null) {
      lib['static-usages'] = resultsLib['static-usages'];
    }
    if (lib['static-usages'] != null) {
      continue;
    }
    if (!lib.commit || !utils.isGreen(lib.test_results)) {
      continue;
    }
    tasks.push({
      lib,
      repo,
    });
  }
  var bar = new ProgressBar(
    "[:bar] :current/:total (:percent) :rate/bps :etas :step",
    {
      complete: "=",
      incomplete: " ",
      width: 30,
      total: tasks.length,
    }
  );
  async.eachOfLimit(utils.shuffle(tasks), 25, async (task, index) => {
    const results = await execTest(task.repo, task.lib.commit);
    bar.tick({
      step: `${task.repo}`,
    });
    if (results != null) {
      task.lib['static-usages'] = results['static-usages'];
      console.log(results['static-usages']);
    }

    fs.writeFileSync(
      config.output + "reposWithJSON_with_static_analysis.json",
      JSON.stringify(mavenGraph)
    );
  });
})();
