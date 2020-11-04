const fs = require("fs");
const ProgressBar = require("progress");
const async = require("async");
var exec = require("child_process").exec;

const config = require("./config");
const utils = require("./utils");

let mavenGraph = {}

const tmp = JSON.parse(
  fs.readFileSync(config.output + "reposWithJSON.2.json")
);
for (let lib of tmp) {
  mavenGraph[lib.repo] = lib;
}

let results = {};
if (fs.existsSync(config.output + "reposWithJSON_with_test_results.json")) {
  results = JSON.parse(
    fs.readFileSync(config.output + "reposWithJSON_with_test_results.json")
  );
}
function execTest(repo, commit) {
  return new Promise((resolve) => {
    exec(
      `docker run --rm runner build --url https://github.com/${repo} --commit ${commit} --timeout 7m`,
      (error, stdout, stderr) => {
        if (!error && stdout) {
          try {
            const results = JSON.parse(stdout);
            return resolve(results);
          } catch (error) {}
        }
        resolve(null);
      }
    );
  });
}

(async () => {
  const tasks = [];
  for (let repo in mavenGraph) {
    const lib = mavenGraph[repo];
    const resultsLib = results[repo];
    if (resultsLib && resultsLib.test_results) {
      lib.test_results = resultsLib.test_results;
    }
    if (resultsLib && resultsLib.commit != null) {
      lib.commit = resultsLib.commit;
    }
    if (lib.test_results && lib.test_results.length >= 3 && lib.commit && !utils.isFlaky(lib.test_results)) {
      if (lib.test_results[0] !=  null) {
        continue;
      }
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
  async.eachOfLimit(utils.shuffle(tasks), 5, async (task, index) => {
    bar.tick({
      step: `${task.repo}`,
    });
    const results = await execTest(task.repo, "HEAD");
    if (results != null) {
      task.lib.commit = results.commit;
      task.lib.test_results = results.test_results;
      console.log(results.test_results[0])
    }

    fs.writeFileSync(
      config.output + "reposWithJSON_with_test_results.json",
      JSON.stringify(mavenGraph)
    );
  });
})();
