const pomParser = require("pom-parser");

const utils = require("./utils");

module.exports.Maven = function (projectPath) {
  this.projectPath = projectPath;
  this.pomPaths = utils.walkSync(projectPath).sort().reverse();
  this.poms = [];
  this.dependencyManagement = {};

  this.parse = () => {
    const promises = [];
    for (let path of this.pomPaths) {
      const pom = new module.exports.Pom(path, this);
      this.poms.push(pom);
      promises.push(pom.parse());
    }
    return Promise.all(promises);
  };

  this.dependencies = () => {
    const deps = {};
    for (let pom of this.poms) {
      if (pom.pom == null) {
        continue;
      }
      pom.dependencyManagement();
    }
    for (let pom of this.poms) {
      if (pom.pom == null) {
        continue;
      }
      deps[pom.module] = pom.dependencies();
    }
    return deps;
  };
  this.variableValue = (variable) => {
    if (variable == null) {
      return null;
    }
    if (Array.isArray(variable)) {
      variable = variable[0];
    }
    const props = this.properties();
    let processedVariable = variable.toLocaleLowerCase().trim();
    if (processedVariable[0] == "$") {
      processedVariable = processedVariable.substring(
        2,
        processedVariable.length - 1
      );
    }
    if (processedVariable == "project.groupid") {
      return this.poms[0].groupId();
    }
    if (processedVariable == "project.artifactid") {
      return this.poms[0].artifactId();
    }
    if (processedVariable == "project.version") {
      return this.poms[0].artifactId();
    }
    if (processedVariable == "project.version") {
      return this.poms[0].version();
    }
    if (props[processedVariable] && props[processedVariable] != variable) {
      return this.variableValue(props[processedVariable]);
    }
    return variable;
  };

  this.properties = () => {
    const props = {};
    for (let pom of this.poms) {
      const pomProps = pom.properties();
      for (let pro in pomProps) {
        props[pro] = pomProps[pro];
      }
    }
    return props;
  };
};

module.exports.Pom = function (pomPath, mavenProject) {
  this.pomPath = pomPath;
  this.mavenProject = mavenProject;
  this.module = pomPath
    .replace(mavenProject.projectPath + "/", "")
    .replace("/pom.xml", "");

  this.parse = async () => {
    const self = this;
    return new Promise((resolve, reject) => {
      pomParser.parse({ filePath: self.pomPath }, (err, pomResponse) => {
        if (err) {
          return reject();
        }
        self.pomResponse = pomResponse;
        self.pom = this.pomResponse.pomObject.project;
        return resolve();
      });
    });
  };

  this.properties = function () {
    const props = {};
    if (this.pom && this.pom.properties) {
      for (let property in this.pom.properties) {
        props[property.trim().toLocaleLowerCase()] = this.pom.properties[
          property
        ];
      }
    }
    return props;
  };

  this.version = function () {
    let version = this.pom.version;
    if (version == null && this.pom.parent != null) {
      version = this.pom.parent.version;
    }
    return version.toLocaleLowerCase();
  };

  this.groupId = function () {
    let groupId = this.pom.groupid;
    if (groupId == null && this.pom.parent != null) {
      groupId = this.pom.parent.groupid;
    }
    return groupId.toLocaleLowerCase();
  };

  this.artifactId = function () {
    let artifactId = this.pom.artifactid;
    if (artifactId == null && this.pom.parent != null) {
      artifactId = this.pom.parent.artifactId;
    }
    return artifactId.toLocaleLowerCase();
  };

  this.javaVersion = () => {
    return utils.getJavaVersion(this.pom);
  };
  this.dependencyManagement = () => {
    const projectDeps = [];

    if (
      this.pom &&
      this.pom.dependencymanagement &&
      this.pom.dependencymanagement.dependencies &&
      this.pom.dependencymanagement.dependencies.dependency
    ) {
      if (
        !Array.isArray(this.pom.dependencymanagement.dependencies.dependency)
      ) {
        this.pom.dependencymanagement.dependencies.dependency = [
          this.pom.dependencymanagement.dependencies.dependency,
        ];
      }
      for (let dependency of this.pom.dependencymanagement.dependencies
        .dependency) {
        dependency = this._cleanDep(dependency);
        this.mavenProject.dependencyManagement[
          dependency.groupid + ":" + dependency.artifactid
        ] = dependency.version;
      }
    }
    return projectDeps;
  };

  this.dependencies = () => {
    const projectDeps = [];
    if (this.pom && this.pom.dependencies && this.pom.dependencies.dependency) {
      if (!Array.isArray(this.pom.dependencies.dependency)) {
        this.pom.dependencies.dependency = [this.pom.dependencies.dependency];
      }
      for (let dependency of this.pom.dependencies.dependency) {
        dependency = this._cleanDep(dependency);
        projectDeps.push(dependency);
      }
    }
    return projectDeps;
  };

  this._cleanDep = function (dependency) {
    dependency.groupid = this.mavenProject
      .variableValue(dependency.groupid)
      .toLocaleLowerCase();
    if (dependency.artifactid != null) {
      dependency.artifactid = this.mavenProject
        .variableValue(dependency.artifactid)
        .toLocaleLowerCase();
    } else {
      console.log(dependency);
    }
    dependency.version = this._depVersion(dependency);
    return dependency;
  };

  this._depVersion = function (dependency) {
    let version = dependency.version;
    if (version == null) {
      version = this.mavenProject.dependencyManagement[
        dependency.groupid + ":" + dependency.artifactid
      ];
    }
    return this.mavenProject.variableValue(version);
  };
};
