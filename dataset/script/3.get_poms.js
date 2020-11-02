const fs = require("fs");
const path = require("path");
const async = require("async");
const ProgressBar = require("progress");
const request = require("request");

const config = require("./config");
const utils = require("./utils");

const OUTPUT_dir = config.output + "poms/";
if (!fs.existsSync(OUTPUT_dir)) {
  fs.mkdirSync(OUTPUT_dir);
}

const repos = utils.walkSync(config.output + "maven_projects/");

var bar = new ProgressBar("[:bar] :current/:total (:percent) :rate/bps :etas :repo", {
  complete: "=",
  incomplete: " ",
  width: 80,
  total: repos.length,
});

function downloadGitHubFile(repo, file, commit, callback) {
  const token = config.github_tokens[Math.round(Math.random() * (config.github_tokens.length -1))]
  utils.downloadGithubFile(repo, commit, file, token).then(
    (res) => callback(null, res),
    (res) => callback(res, null)
  );
}

let nbMaven = 0;
async.eachLimit(
  repos,
  10,
  (file, callback) => {
    let repo =
      path.basename(path.dirname(file)) +
      "/" +
      path.basename(file).replace(".json", "");
    bar.tick({
      repo: repo,
    });

    fs.readFile(file, (err, data) => {
      if (err) {
        console.error(err);
        return callback();
      }
      const repositoryContent = JSON.parse(
        fs.readFileSync(config.output + "repo_files/" + repo + ".json")
      );
      const poms = JSON.parse(data);
      if (poms.length > 0) {
        nbMaven++;
      }

      async.forEachOfSeries(
        poms,
        (pom, key, cb) => {
          const pomPath = path.join(OUTPUT_dir, repo, pom);
          if (fs.existsSync(pomPath)) {
            nbMaven++;
            return cb();
          }
          if (!fs.existsSync(path.dirname(pomPath))) {
            fs.mkdirSync(path.dirname(pomPath), { recursive: true });
          }
          downloadGitHubFile(repo, pom, repositoryContent.sha, (err, body) => {
            if (err == null && body != null) {
              fs.writeFileSync(pomPath, body);
            }
            cb();
          });
        },
        callback
      );
    });
  },
  () => {
    console.log("# pom downloaded", nbMaven);
  }
);
