const fs = require("fs");
const path = require("path");
const resolve = require("path").resolve;
const ProgressBar = require("progress");
const async = require("async");
var exec = require("child_process").exec;

const config = require("./config");
const utils = require("./utils");

const repos = utils.walkSync(config.output + "traces");
let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "json_dataset.json")
);

function execute(repo, commit, lib, impl) {
  const logPath = `${config.output}experimentv2_logs/${repo}_${lib}.log`;
  if (!fs.existsSync(path.dirname(logPath))) {
    fs.mkdirSync(path.dirname(logPath), { recursive: true });
  }
  const cmd = `docker run --rm -v ${resolve(
    config.output
  )}:/results runner experiment --url https://github.com/${repo} --commit ${commit} -r /results --lib ${lib} -i ${impl} --timeout 15m > ${logPath} 2>&1`;
  return new Promise((resolve) => {
    exec(cmd, (error, stdout, stderr) => {
      resolve();
    });
  });
}

const bridgeImplMap = {
  "org.json:json": [
    "nothing",
    "json-simple",
    "gson",
    "fastjson",
    "cookjson",
    "jjson",
    "json-io",
    "json-lib",
    "jsonp",
    "jackson",
    "jsonutil",
    "mjson",
    //"klaxon",
  ],
  "com.alibaba:fastjson": [
    "nothing",
    "json-simple",
    "gson",
    "json",
    "cookjson",
    "jjson",
    "json-io",
    "json-lib",
    "jsonp",
    "jackson",
    "jsonutil",
    "mjson",
    //"klaxon",
  ],
  "com.googlecode.json-simple:json-simple": [
    "nothing",
    "gson",
    "fastjson",
    "json",
    "cookjson",
    "jjson",
    "json-io",
    "json-lib",
    "jsonp",
    "jackson",
    "jsonutil",
    "mjson",
    //"klaxon",
  ],
  "com.google.code.gson:gson": [
    "nothing",
    "json-simple",
    "fastjson",
    "json",
    "cookjson",
    "jjson",
    "json-io",
    "json-lib",
    "jsonp",
    "jackson",
    "jsonutil",
    "mjson",
    //"klaxon",
  ],
  "com.fasterxml.jackson.core:jackson-databind": [
    "nothing",
    "json-simple",
    "fastjson",
    "json",
    "cookjson",
    "jjson",
    "json-io",
    "json-lib",
    "jsonp",
    "gson",
    "jsonutil",
    "mjson",
    // "klaxon",
  ],
};
(async () => {
  var bar = new ProgressBar(
    "[:bar] :current/:total (:percent) :rate/bps :etas :step",
    {
      complete: "=",
      incomplete: " ",
      width: 30,
      total: repos.length,
    }
  );

  function parseName(name) {
    const st = name.split(".");
    const method = st[st.length - 1];
    const cl = st[st.length - 2];
    const package = st.slice(0, st.length - 2).join(".");
    return { package, cl, method };
  }
  const stat = {};
  const csv = {};
  const output = {};
  const projects = new Set();
  const projectUsage = {};
  async.eachOfLimit(
    utils.shuffle(repos),
    25,
    (repo, index, callback) => {
      fs.readFile(repo, (err, data) => {
        const tmpR = repo.replace(".json", "").split("/");
        repo = tmpR[tmpR.length - 2] + "/" + tmpR[tmpR.length - 1];
        try {
          data = JSON.parse(data);
          for (let src in data) {
            const r = parseName(src);
            if (
              src.indexOf("org.json") == 0 ||
              src.indexOf("com.alibaba.fastjson") == 0 ||
              src.indexOf("com.fasterxml.jackson") == 0 ||
              src.indexOf("com.google.gson") == 0 ||
              src.indexOf("com.fasterxml.jackson.core.json") == 0
            ) {
              projects.add(repo);
            }
            // if (src.indexOf("org.json") == 0) {
            //   projects.add(repo);
            // }
            if (projectUsage[repo] == null) {
              projectUsage[repo] = new Set();
            }
            projectUsage[repo].add(r.package);
            if (csv[src] == null) {
              csv[src] = 0;
            }
            if (stat[r.package] == null) {
              stat[r.package] = {};
            }
            if (stat[r.package][r.cl] == null) {
              stat[r.package][r.cl] = {};
            }
            if (stat[r.package][r.cl][r.method] == null) {
              stat[r.package][r.cl][r.method] = 0;
            }
            if (output[src] == null) {
              output[src] = {};
            }
            let count = 0;
            for (let target in data[src]) {
              const nb = data[src][target];
              count += nb;
              if (output[src][target] == null) {
                output[src][target] = 0;
              }
              output[src][target] += nb;
            }
            stat[r.package][r.cl][r.method] += count;
            csv[src] += 1;
          }
        } finally {
          bar.tick({
            step: `${repo}`,
          });
          callback();
        }
      });
    },
    () => {
      const tasks = [];
      for (let project in projects) {
        const info = mavenGraph[project];
        if (info == null) {
          continue;
        }
        const packages = new Set();
        for (let dep of info.libs) {
          const lib = dep.groupid + ":" + dep.artifactid;
          packages.add(lib);
        }
        const libs = [
          "org.json:json",
          "com.google.code.gson:gson",
          "com.googlecode.json-simple:json-simple",
          "com.fasterxml.jackson.core:jackson-databind",
        ];
        for (let lib of libs) {
          for (let imp in bridgeImplMap[lib]) {
            if (
              fs.existsSync(
                path.join(config.output, "exp", project, lib, imp + ".json")
              )
            ) {
              continue;
            }
            if (packages.has(lib)) {
              tasks.push({
                project,
                commit: info.commit,
                lib,
                impl,
              });
            }
          }
        }
      }
      bar = new ProgressBar(
        "[:bar] :current/:total (:percent) :rate/bps :etas :step",
        {
          complete: "=",
          incomplete: " ",
          width: 30,
          total: tasks.length,
        }
      );
      async.eachLimit(tasks, 20, async (task) => {
        await execute(task.project, task.commit, task.lib, task.impl);
        bar.tick({
          step: `${task.project} -> ${task.lib} -> ${task.impl}`,
        });
      });
    }
  );
})();
