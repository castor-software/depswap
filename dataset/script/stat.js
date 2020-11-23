const fs = require("fs");
const path = require("path");
const config = require("./config");
const utils = require("./utils");

function readFile(fileName) {
  return JSON.parse(fs.readFileSync(path.join(config.output, fileName)));
}

console.log("Step 2. reposWithJSON.json");
let data = readFile("reposWithJSON.2.json");
console.log("Project with JSON", data.length);
console.log("Step 7. reposWithJSON_with_test_results.json");
data = readFile("reposWithJSON_with_test_results.json");
let nbRepo = 0;
let flaky = 0;
let libs = {};
repo: for (let repo in data) {
  const d = data[repo];
  if (d.test_results) {
    if (!utils.isGreen(d.test_results)) {
      if (utils.isFlaky(d.test_results)) {
        flaky++;
      }
      continue;
    }
    nbRepo++;
    for (let dep of d.libs) {
      const lib = dep.groupid + ":" + dep.artifactid;
      if (libs[lib] == null) {
        libs[lib] = {};
      }
      if (dep.version) {
        if (libs[lib][dep.version] == null) {
          libs[lib][dep.version] = 0;
        }
        libs[lib][dep.version]++;
      }
      libs[lib].count = (libs[lib].count || 0) + 1;
    }
  }
}
console.log(libs);
console.log("# repo", nbRepo);
console.log("# flaky", flaky);

console.log("Step 7. reposWithJSON_with_static_analysis.json");
data = readFile("reposWithJSON_with_static_analysis.json");
nbRepo = 0;
let packages = {};
repo: for (let repo in data) {
  const d = data[repo];
  if (!utils.isGreen(d.test_results)) {
    continue;
  }
  if (d.test_results) {
    if (!d["static-usages"]) {
      continue;
    }
    if (Object.keys(d["static-usages"]).length == 0) {
      continue;
    }
    for (let package in d["static-usages"]) {
      packages[package] =
        (packages[package] || 0) +
        Object.keys(d["static-usages"][package]).length;
    }
    nbRepo++;
  }
}
console.log(packages);
console.log("# repo", nbRepo);
