const fs = require("fs");
const path = require("path");
const async = require("async");
const ProgressBar = require("progress");

const config = require("./config");
const Maven = require("./Pom").Maven;

let projects = ["jitsi/jitsi-videobridge"];
if (fs.existsSync("projects.json")) {
  projects = JSON.parse(fs.readFileSync("projects.json"));
} else {
  fs.readdirSync(config.output + "poms").forEach((owner) => {
    fs.readdirSync(config.output + "poms/" + owner).forEach((project) => {
      projects.push(owner + "/" + project);
    });
  });

  fs.writeFileSync("projects.json", JSON.stringify(projects, null, 1));
}

const jsonLibs = [
  "org.json:json",
  "com.googlecode.json-simple:json-simple",
  "com.google.code.gson:gson",
  // "com.fasterxml.jackson.core:jackson-core",
  "com.fasterxml.jackson.core:jackson-databind",
  "com.alibaba:fastjson",
  "org.eclipse:yasson",
  "org.glassfish:jakarta.json",
  "org.glassfish:javax.json",
];

var bar = new ProgressBar(
  "[:bar] :current/:total (:percent) :rate/bps :etas :match :repo",
  {
    complete: "=",
    incomplete: " ",
    width: 80,
    total: projects.length,
  }
);

const repos = [];
const stat = {};

function printStat() {
  for (let lib in stat) {
    console.log(lib, "total: ", stat[lib].total);
    for (let version of Object.keys(stat[lib]).sort(
      (a, b) => stat[lib][b] - stat[lib][a]
    )) {
      if (version == "total") {
        continue;
      }
      console.log("\t", version, stat[lib][version]);
    }
  }
}
// setInterval(printStat, 3000);
async.eachLimit(
  projects,
  13,
  async (repo) => {
    bar.tick({ repo, match: repos.length });
    repoPath = path.join(config.output, "poms", repo);
    try {
      const maven = new Maven(repoPath);
      await maven.parse();
      const deps = maven.dependencies();
      const libs = [];
      for (let module in deps) {
        for (let dep of deps[module]) {
          dep.module = module;
          const id = (dep.groupid + ":" + dep.artifactid).toLowerCase();
          if (jsonLibs.includes(id)) {
            libs.push(dep);
            if (!stat[id]) {
              stat[id] = {};
            }
            stat[id][dep.version] = (stat[id][dep.version] || 0) + 1;
            stat[id].total = (stat[id].total || 0) + 1;
          }
        }
      }
      if (libs.length > 0) {
        repos.push({
          repo,
          libs,
        });
      }
    } catch (error) {
      console.log("\n[Error]", error);
    }
  },
  async () => {
    // console.log(repos);
    printStat();
    fs.writeFileSync(
      config.output + "reposWithJSON.2.json",
      JSON.stringify(repos, null, 1)
    );
  }
);
