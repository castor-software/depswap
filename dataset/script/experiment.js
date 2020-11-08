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

function execute(repo, commit, lib) {
  const logPath = `${config.output}exec_logs/${repo}_${lib}.log`;
  if (!fs.existsSync(path.dirname(logPath))) {
    fs.mkdirSync(path.dirname(logPath), {recursive: true});
  }
  const cmd = `docker run --rm -v ${resolve(
    config.output
  )}:/results runner experiment --url https://github.com/${repo} --commit ${commit} -r /results --lib ${lib} --timeout 15m > ${logPath} 2>&1`;
  return new Promise((resolve) => {
    exec(cmd, (error, stdout, stderr) => {
      resolve();
    });
  });
}

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
              src.indexOf("com.google.gson") == 0  ||
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
      bar = new ProgressBar(
        "[:bar] :current/:total (:percent) :rate/bps :etas :step",
        {
          complete: "=",
          incomplete: " ",
          width: 30,
          total: projects.size,
        }
      );
      async.eachLimit(projects, 5, async (project) => {
        const info = mavenGraph[project];

        const packages = new Set()
        for (let module of info.poms) {
          for (let dep of module.deps) {
            packages.add(dep.lib);
            console.log(dep.lib)
          }
        }
        // if (packages.has("org.json:json")) {
        //   await execute(project, info.commit, "org.json:json");
        // }
        // if (packages.has("com.google.code.gson:gson")) {
        //   await execute(project, info.commit, "com.google.code.gson:gson");
        // }
        // if (packages.has("com.googlecode.json-simple:json-simple")) {
        //   await execute(project, info.commit, "com.googlecode.json-simple:json-simple");
        // }
        if (packages.has("com.fasterxml.jackson.core:jackson-databind")) {
          await execute(project, info.commit, "com.fasterxml.jackson.core:jackson-databind");
        }
        bar.tick({
          step: `${project}`,
        });
      });
    }
  );
})();
