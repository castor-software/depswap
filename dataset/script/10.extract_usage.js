const fs = require("fs");
const path = require("path");
const resolve = require("path").resolve;
const ProgressBar = require("progress");
const async = require("async");
var exec = require("child_process").exec;

const config = require("./config");
const utils = require("./utils");

let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "json_dataset.json")
);

function execUsage(repo, commit) {
  const logPath = `${config.output}logs/${repo}.log`;
  if (!fs.existsSync(path.dirname(logPath))) {
    fs.mkdirSync(path.dirname(logPath));
  }
  const cmd = `docker run --rm -v ${resolve(
    config.output
  )}:/results runner usage --url https://github.com/${repo} --commit ${commit} -r /results --timeout 7m > ${logPath} 2>&1`;
  return new Promise((resolve) => {
    exec(cmd, (error, stdout, stderr) => {
      resolve();
    });
  });
}

(async () => {
  const tasks = [];
  for (let repo in mavenGraph) {
    const lib = mavenGraph[repo];
    if (fs.existsSync(config.output + "traces/" + repo + ".json")) {
      continue;
    }
    tasks.push({
      lib,
      repo,
    });
  }
  var bar = new ProgressBar(
    "[:bar] :current/:total (:percent) :rate/bps :etas :step (:duration sec)",
    {
      complete: "=",
      incomplete: " ",
      width: 30,
      total: tasks.length,
    }
  );
  async.eachOfLimit(utils.shuffle(tasks), 25, async (task, index) => {
    const start = new Date().getTime()
    await execUsage(task.repo, task.lib.commit);
    bar.tick({
      step: `${task.repo}`,
      duration: (new Date().getTime() - start) / 1000
    });
  });
})();
