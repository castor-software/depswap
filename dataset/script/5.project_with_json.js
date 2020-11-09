const pomParser = require("pom-parser");
const async = require("async");
const ProgressBar = require("progress");

const config = require("./config");
const utils = require("./utils");

const poms = utils.walkSync(config.output + "poms");
// const poms = [config.output + "poms/jitsi/jitsi-videobridge/pom.xml"]
const junitLibs = ["junit:junit", "org.junit.jupiter:junit-jupiter"];
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
    total: poms.length,
  }
);

const repos = {};
const stat = {};
const jsonLibsss = {};

async.eachLimit(
  poms,
  25,
  (file, callback) => {
    file = file.replace(/\/\//g, "/");
    const repoPath = file.substring(
      file.indexOf(config.output + "poms") + (config.output + "poms").length + 1
    );
    const splitPath = repoPath.split("/");
    let repo = splitPath[0] + "/" + splitPath[1];
    const pomPath = repoPath.replace(repo + "/", "");
    bar.tick({
      repo: repo,
      match: Object.keys(repos).length,
    });
    pomParser.parse(
      {
        filePath: file,
      },
      function (err, pomResponse) {
        if (err || pomResponse.pomObject.project == null) {
          // fs.unlinkSync(file)
          // console.log(repo, err)
          return callback();
        }
        const pom = pomResponse.pomObject.project;
        let groupId = pom.groupid;
        if (groupId == null && pom.parent != null) {
          groupId = pom.parent.groupid;
        }
        let artifactId = pom.artifactid;
        if (artifactId == null && pom.parent != null) {
          artifactId = pom.parent.artifactId;
        }
        const projectDeps = new Set();
        const javaVersion = utils.getJavaVersion(pom);
        console.log(groupId,artifactId )

        if (pom.dependencies && pom.dependencies.dependency) {
          if (!Array.isArray(pom.dependencies.dependency)) {
            pom.dependencies.dependency = [pom.dependencies.dependency];
          }
          const projectDepVersions = {};
          for (let dependency of pom.dependencies.dependency) {
            let id = utils
              .removeEndNumber(dependency.groupid + ":" + dependency.artifactid)
              .toLowerCase();
            if (id.indexOf("json") > -1) {
              jsonLibsss[id] = (jsonLibsss[id] || 0) + 1;
            }
            projectDeps.add(id);
            projectDepVersions[id] = utils.getVersion(
              pom,
              dependency.version,
              dependency.groupid,
              dependency.artifactid
            );
          }
          const json = [...projectDeps].filter((value) =>
            jsonLibs.includes(value)
          );
          if (json.length > 0) {
            if (repos[repo] == null) {
              repos[repo] = {
                poms: [],
              };
            }
            for (let l of json) {
              stat[l] = (stat[l] || 0) + 1;
            }
            repos[repo].poms.push({
              group_id: groupId,
              artifact_id: artifactId,
              repo_name: repo,
              java_version: javaVersion,
              pom_path: pomPath,
              deps: json.map((lib) => {
                return { lib, version: projectDepVersions[lib] };
              }),
            });
          }
        }
        callback();
      }
    );
  },
  async () => {
    console.log(jsonLibsss);
    console.log(stat);
    // fs.writeFileSync(
    //   config.output + "reposWithJSON.json",
    //   JSON.stringify(repos, null, 1)
    // );
  }
);
