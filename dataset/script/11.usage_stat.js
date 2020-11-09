const fs = require("fs");
const ProgressBar = require("progress");
const async = require("async");

const config = require("./config");
const utils = require("./utils");

const repos = utils.walkSync(config.output + "traces");
let mavenGraph = JSON.parse(
  fs.readFileSync(config.output + "json_dataset.json")
);

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
            if (src.indexOf("org.json") == 0) {
              projects.add(repo);
            }
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
      for (let line of Object.entries(csv).sort(([, a], [, b]) => a - b)) {
        //console.log(line[0] + "," + line[1]);
      }
      for (let project of projects) {
        let packages = new Set();
        const info = mavenGraph[project];
        if (info == null) {
          console.log(project)
          continue;
        } 
        for (let dep of info.libs) {
          const lib = dep.groupid + ":" + dep.artifactid
          packages.add(lib);
        }
        console.log(project + "," + info.commit, [...packages].join(" "));
      }
    }
  );
})();
