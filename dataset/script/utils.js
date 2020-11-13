const Octokit = require("@octokit/rest")
  .plugin(require("@octokit/plugin-throttling"))
  .plugin(require("@octokit/plugin-retry"));

const pomParser = require("pom-parser");
const request = require("request");
const path = require("path");

const tokens = {};
module.exports.createOctokit = (token) => {
  const octokit = new Octokit({
    auth: token,
    throttle: {
      onRateLimit: (retryAfter, options) => {
        octokit.log.warn(
          `Request quota exhausted for request ${options.method} ${options.url} ${options.headers.authorization}`
        );

        if (options.request.retryCount === 0) {
          // only retries once
          console.log(`Retrying after ${retryAfter} seconds!`, token);
          return true;
        }
      },
      onAbuseLimit: (retryAfter, options) => {
        // does not retry, only logs a warning
        console.log(
          `Abuse detected for request ${options.method} ${options.url}`
        );
        return true;
      },
    },
  });
  octokit.hook.after("request", async (response, options) => {
    if (!tokens[token]) {
      tokens[token] = {};
    }
    tokens[token].remaining = parseInt(
      response.headers["x-ratelimit-remaining"]
    );
    tokens[token].reset = new Date(
      parseInt(response.headers["x-ratelimit-reset"]) * 1000
    );
  });
  return octokit;
};
/**
 * Shuffles array in place.
 * @param {Array} a items An array containing the items.
 */
module.exports.shuffle = function (a) {
  var j, x, i;
  for (i = a.length - 1; i > 0; i--) {
    j = Math.floor(Math.random() * (i + 1));
    x = a[i];
    a[i] = a[j];
    a[j] = x;
  }
  return a;
};

module.exports.walkSync = function (dir, filelist) {
  var fs = fs || require("fs"),
    files = fs.readdirSync(dir);
  filelist = filelist || [];
  files.forEach(function (file) {
    if (fs.statSync(path.join(dir, file)).isDirectory()) {
      filelist = module.exports.walkSync(path.join(dir, file), filelist);
    } else {
      filelist.push(path.join(dir, file));
    }
  });
  return filelist;
};

module.exports.clean_github_object = function (data) {
  if (typeof data == "object") {
    for (let key in data) {
      if (key.indexOf("_url") != -1 || data[key] == null) {
        delete data[key];
      }
      delete data._links;
      delete data.signature;
      data[key] = module.exports.clean_github_object(data[key]);
    }
  } else if (typeof data == "array") {
    for (let i in data) {
      data[i] = module.exports.clean_github_object(data[i]);
    }
  }
  return data;
};

module.exports.downloadGithubFile = function (repoName, commit, path, token) {
  return new Promise((resolve, reject) => {
    request.get(
      "https://cdn.jsdelivr.net/gh/" + repoName + "@" + commit + "/" + path,
      {},
      async (err, response, body) => {
        if (err) {
          return reject(err);
        }
        if (response.statusCode != 200 && response.statusCode != 404) {
          const owner = repoName.split("/")[0];
          const repo = repoName.split("/")[1];
          const octokit = module.exports.createOctokit(token);
          octokit.repos
            .getContents({
              owner,
              repo,
              path,
              ref: commit,
            })
            .then((response) => {
              resolve(Buffer.from(response.data.content, "base64"));
            }, reject);
        } else if (response.statusCode == 200) {
          resolve(body);
        }
      }
    );
  });
};

module.exports.parsePom = function (filePath) {
  return new Promise((resolve, reject) => {
    pomParser.parse({ filePath }, (err, pomResponse) => {
      if (err) {
        return reject(err);
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

      const parentId = module.exports.removeEndNumber(
        groupId + ":" + artifactId
      );
      const output = {
        groupId,
        artifactId,
        parentId,
        version: module.exports.getVersion(pom, pom.version),
        java_version: module.exports.getJavaVersion(pom),
        dependencies: new Set(),
      };
      if (pom.dependencies && pom.dependencies.dependency) {
        if (!Array.isArray(pom.dependencies.dependency)) {
          pom.dependencies.dependency = [pom.dependencies.dependency];
        }
        for (let dependency of pom.dependencies.dependency) {
          let id = module.exports.removeEndNumber(
            dependency.groupid + ":" + dependency.artifactid
          );
          let version = module.exports.getVersion(
            pom,
            dependency.version,
            dependency.groupid,
            dependency.artifactid
          );
          output.dependencies.add({
            id,
            groupId: dependency.groupid,
            artifactId: dependency.artifactid,
            version,
          });
        }
      }
      return resolve(output);
    });
  });
};

module.exports.removeEndNumber = function (id) {
  while (!isNaN(id[id.length - 1])) {
    id = id.substring(0, id.length - 1);
  }
  return id;
};

module.exports.getVersion = (pom, version, groupId, artifactId) => {
  if (Array.isArray(version)) {
    version = version[0];
  }
  if (version == null || version.length == 0) {
    if (groupId != null && artifactId != null && pom.dependencymanagement) {
      let deps = pom.dependencymanagement.dependencies.dependency;
      if (!Array.isArray(deps)) {
        deps = [deps];
      }
      for (let dep of deps) {
        if (dep.groupid == groupId && dep.artifactid == artifactId) {
          return module.exports.getVersion(pom, dep.version);
        }
      }
    }
    return version;
  }
  if (version[0] == "$") {
    const variable = version.substring(2, version.length - 1);
    if (
      variable == "project.parent.version" &&
      pom.parent &&
      pom.parent.version
    ) {
      return module.exports.getVersion(pom, pom.parent.version);
    }
    if (pom.properties) {
      if (pom.properties[variable]) {
        return module.exports.getVersion(pom, pom.properties[variable]);
      }
      for (let property in pom.properties) {
        if (property.toLocaleLowerCase() == variable.toLocaleLowerCase()) {
          return module.exports.getVersion(pom, pom.properties[property]);
        }
      }
    }
    if (variable == "project.version") {
      if (pom.version == version) {
        return null;
      }
      //console.log(pom);
      return module.exports.getVersion(pom, pom.version);
    }
  }
  return version;
};
module.exports.getJavaVersion = function (pom) {
  if (pom.properties) {
    if (pom.properties["maven.compiler.source"]) {
      return pom.properties["maven.compiler.source"];
    }
    if (pom.properties["java.version"]) {
      return pom.properties["java.version"];
    }
    if (pom.properties["maven.compiler.release"]) {
      return pom.properties["maven.compiler.release"];
    }
  }
  if (pom.build && pom.build.plugins && pom.build.plugins.plugin) {
    if (!Array.isArray(pom.build.plugins.plugin)) {
      pom.build.plugins.plugin = [pom.build.plugins.plugin];
    }
    for (let plugin of pom.build.plugins.plugin) {
      if (plugin.artifactid == "maven-compiler-plugin") {
        if (plugin.configuration) {
          if (plugin.configuration.source) {
            return plugin.configuration.source;
          }
          if (plugin.configuration.release) {
            return plugin.configuration.release;
          }
        }
      }
    }
  }
  return undefined;
};
module.exports.isGreen = (test_results) => {
  if (test_results == null) {
    return false;
  }
  if (module.exports.isFlaky(test_results)) {
    return false;
  }
  return !test_results
    .map((x) => {
      if (x == null) return null;
      if (x.error == null) {
        const output = { error: 0, failing: 0, passing: 0 };
        for (let test in x) {
          output.error += x[test].error;
          output.failing += x[test].failing;
          output.passing += x[test].passing;
        }
        return output;
      }
      return { error: x.error, failing: x.failing, passing: x.passing };
    })
    .every((val) => val.error == 0 && val.failing == 0 && val.passing > 0);
};

module.exports.isFlaky = (test_results) =>
  !test_results
    .map((x) => {
      if (x == null) return null;
      if (x.error == null) {
        const output = { error: 0, failing: 0, passing: 0 };
        for (let test in x) {
          output.error += x[test].error;
          output.failing += x[test].failing;
          output.passing += x[test].passing;
        }
        return output;
      }
      return { error: x.error, failing: x.failing, passing: x.passing };
    })
    .every((val, _, arr) => {
      if (val == null) return val === arr[0];
      if (val != null && arr[0] == null) return false;
      return (
        val.error == arr[0].error &&
        val.failing == arr[0].failing &&
        val.passing == arr[0].passing
      );
    });
