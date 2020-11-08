angular
  .module("dep-website", ["ngRoute", "ui.bootstrap", "anguFixedHeaderTable"])
  .config(function ($routeProvider, $locationProvider) {
    $routeProvider
      .when("/:owner/:project/:lib", {
        controller: "bugController",
      })
      .when("/", {
        controller: "mainController",
      });
    // configure html5 to get links working on jsfiddle
    $locationProvider.html5Mode(false);
  })
  .directive("keypressEvents", [
    "$document",
    "$rootScope",
    function ($document, $rootScope) {
      return {
        restrict: "A",
        link: function () {
          $document.bind("keydown", function (e) {
            $rootScope.$broadcast("keypress", e);
            $rootScope.$broadcast("keypress:" + e.which, e);
          });
        },
      };
    },
  ])
  .directive("tests", [
    function () {
      return {
        restrict: "A",
        scope: {
          tests: "=tests",
        },
        link: function (scope, elem, attrs) {
          function printTests(tests) {
            $(elem).text("");

            let executionTime = 0;
            let count = {
              passing: 0,
              failing: 0,
              error: 0,
            };
            let errors = [];
            for (let cl in tests) {
              test = tests[cl];
              executionTime += test.execution_time;

              count.passing += test.passing;
              count.failing += test.failing;
              count.error += test.error;
              if (test.error > 0 || test.failing > 0) {
                for (let m of test.test_cases) {
                  if (m.error) {
                    errors.push({
                      name: cl + "#" + m.name,
                      error: m.error,
                    });
                  } else if (m.failure) {
                    errors.push({
                      name: cl + "#" + m.name,
                      error: m.failure,
                    });
                  }
                }
              }
            }
            executionTime = Math.round(executionTime * 100) / 100;
            let content = `P: ${count.passing} F: ${count.failing} E: ${count.error} (${executionTime}ms)<br>`;
            for (let error of errors) {
              content += `<div>${error.name}<pre class="stacktrace">${error.error.error}</pre></div>\n`;
            }
            $(elem).html(content);
          }
          scope.$watch("tests", function () {
            printTests(scope.tests);
          });
          printTests(scope.tests);
        },
      };
    },
  ])
  .controller("bugModal", function (
    $scope,
    $http,
    $rootScope,
    $uibModalInstance,
    bug
  ) {
    var $ctrl = this;
    $ctrl.bug = bug;

    function download() {
      $http
        .get(
          `data/exp/${$ctrl.bug.owner}/${$ctrl.bug.project}/${$ctrl.bug.lib}.json`
        )
        .then((res) => {
          $ctrl.info = res.data;
          for (let exec of $ctrl.info.executions) {
            for (let i in exec.classpath) {
              const cl = exec.classpath[i]
                .replace(/\/home\/runner\/\.m2\/repository\//g, "")
                .split(":");
              exec.classpath[i] = "";
              for (c of cl) {
                const pp = c.split("/");
                exec.classpath[i] += pp[pp.length - 1] + " ";
              }
            }
            exec.classpath = exec.classpath.join("\n");
            if (exec.errors && !exec.error) {
              exec.error = exec.errors.join("\n");
            }
          }
        });
    }
    download();

    $rootScope.$on("new_bug", function (e, bug) {
      $ctrl.bug = bug;
      download();
    });
    // u
    $scope.$on("keypress:85", function () {
      $ctrl.changeCategory("unknown", $ctrl.nextPatch);
    });
    // b
    $scope.$on("keypress:66", function () {
      $ctrl.changeCategory("bug", $ctrl.nextPatch);
    });
    // n
    $scope.$on("keypress:78", function () {
      $ctrl.changeCategory("nobug", $ctrl.nextPatch);
    });
    $ctrl.changeCategory = function (category, callback) {
      $http
        .post("/api/categories", {
          bug_id: $ctrl.bug.bugId,
          category: category,
        })
        .then((response) => {
          $ctrl.bug.failure_category = category;
          if (callback) {
            callback();
          }
        });
    };
    $ctrl.copy = (event) => {
      const el = document.createElement("textarea");
      el.value = event.target.innerText;
      document.body.appendChild(el);
      el.select();
      document.execCommand("copy");
      document.body.removeChild(el);
    };
    $ctrl.ok = function () {
      $uibModalInstance.close();
    };
    $ctrl.nextPatch = function () {
      $rootScope.$emit("next_bug", "next");
    };
    $ctrl.previousPatch = function () {
      $rootScope.$emit("previous_bug", "next");
    };
  })
  .controller("bugController", function (
    $scope,
    $location,
    $rootScope,
    $routeParams,
    $uibModal
  ) {
    var $ctrl = $scope;
    $ctrl.bugs = $scope.$parent.filteredBugs;
    $ctrl.index = -1;
    $ctrl.bug = null;

    $scope.$watch("$parent.filteredBugs", function () {
      $ctrl.bugs = $scope.$parent.filteredBugs;
      $ctrl.index = getIndex(
        $routeParams.owner,
        $routeParams.project,
        $routeParams.lib
      );
    });

    var getIndex = function (owner, project, lib) {
      if ($ctrl.bugs == null) {
        return -1;
      }
      for (var i = 0; i < $ctrl.bugs.length; i++) {
        if (
          $ctrl.bugs[i].owner == owner &&
          $ctrl.bugs[i].project == project &&
          $ctrl.bugs[i].lib == lib
        ) {
          return i;
        }
      }
      return -1;
    };

    $scope.$on("$routeChangeStart", function (next, current) {
      $ctrl.index = getIndex(
        current.params.owner,
        current.params.project,
        current.params.lib
      );
    });

    var modalInstance = null;
    $scope.$watch("index", function () {
      if ($scope.index != -1) {
        if (modalInstance == null) {
          modalInstance = $uibModal.open({
            animation: true,
            ariaLabelledBy: "modal-title",
            ariaDescribedBy: "modal-body",
            templateUrl: "modelPatch.html",
            controller: "bugModal",
            controllerAs: "$ctrl",
            size: "lg",
            resolve: {
              bug: function () {
                return $scope.bugs[$scope.index];
              },
            },
          });
          modalInstance.result.then(
            function () {
              modalInstance = null;
              $location.path("/");
            },
            function () {
              modalInstance = null;
              $location.path("/");
            }
          );
        } else {
          $rootScope.$emit("new_bug", $scope.bugs[$scope.index]);
        }
      }
    });
    var nextPatch = function () {
      var index = $scope.index + 1;
      if (index == $ctrl.bugs.length) {
        index = 0;
      }
      $location.path(
        "/" +
          $ctrl.bugs[index].owner +
          "/" +
          $ctrl.bugs[index].project +
          "/" +
          $ctrl.bugs[index].lib
      );
      return false;
    };
    var previousPatch = function () {
      var index = $scope.index - 1;
      if (index < 0) {
        index = $ctrl.bugs.length - 1;
      }
      $location.path(
        "/" +
          $ctrl.bugs[index].owner +
          "/" +
          $ctrl.bugs[index].project +
          "/" +
          $ctrl.bugs[index].lib
      );
      return false;
    };

    $scope.$on("keypress:39", function () {
      $scope.$apply(function () {
        nextPatch();
      });
    });
    $scope.$on("keypress:37", function () {
      $scope.$apply(function () {
        previousPatch();
      });
    });
    $rootScope.$on("next_bug", nextPatch);
    $rootScope.$on("previous_bug", previousPatch);
  })
  .controller("mainController", function ($scope, $location, $http) {
    $scope.sortType = ["owner"]; // set the default sort type
    $scope.sortReverse = false;
    $scope.match = "all";
    $scope.search = "";
    $scope.filters = {
      allvalid: true,
      allinvalid: true,
      libs: {},
      reasons: {},
    };
    $scope.reasons = [];
    $scope.libs = [];
    $scope.execs = [
      "json",
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
    ];

    // create the list of sushi rolls
    $scope.bugs = [];

    function downloadPatches() {
      $http.get("data/projects.json").then(function (response) {
        const bugs = response.data;
        for (let bug of bugs) {
          const tmp = bug.url.split("/");
          bug.owner = tmp[tmp.length - 2];
          bug.project = tmp[tmp.length - 1];

          if ($scope.libs.indexOf(bug.lib) == -1) {
            $scope.libs.push(bug.lib);
            $scope.filters.libs[bug.lib] = true;
          }
          for (let lib in bug.executions) {
            const exec = bug.executions[lib];
            if (exec.reason) {
              if ($scope.reasons.indexOf(exec.reason) == -1) {
                $scope.reasons.push(exec.reason);
                $scope.filters.reasons[exec.reason] = false;
              }
            }
          }
          $scope.bugs.push(bug);
        }
        var element = angular.element(document.querySelector("#menu"));
        var height = element[0].offsetHeight;

        angular
          .element(document.querySelector("#mainTable"))
          .css("height", height - 120 + "px");
      });
    }
    downloadPatches();

    $scope.openBug = function (bug) {
      $location.url("/" + bug.owner + "/" + bug.project + "/" + bug.lib);
    };

    $scope.sort = function (sort) {
      if (sort == $scope.sortType) {
        $scope.sortReverse = !$scope.sortReverse;
      } else {
        $scope.sortType = sort;
        $scope.sortReverse = false;
      }
      return false;
    };
    $scope.count = function (key) {
      let count = 0;
      let total = 0;
      for (let exec of $scope.filteredBugs) {
        if (exec.executions[key]) {
          total++;
          if (exec.executions[key].valid) {
            count++;
          }
        }
      }
      if (total == 0) {
        return "N.A";
      }
      return Math.floor((count * 100) / total) + "% " + count + "/" + total;
    };
    $scope.countBugs = function (key, filter) {
      if (filter == null) {
        filter = {};
      }
      if (filter.count) {
        return filter.count;
      }
      var count = 0;
      for (var i = 0; i < $scope.bugs.length; i++) {
        if ($scope.bugs[i].benchmark.toLowerCase() === key.toLowerCase()) {
          count++;
        } else if ($scope.bugs[i].benchmark === key) {
          count++;
        } else if (
          $scope.bugs[i].repairActions &&
          $scope.bugs[i].repairActions[key] != null &&
          $scope.bugs[i].repairActions[key] > 0
        ) {
          count++;
        } else if (
          $scope.bugs[i].repairPatterns &&
          $scope.bugs[i].repairPatterns[key] != null &&
          $scope.bugs[i].repairPatterns[key] > 0
        ) {
          count++;
        }
      }
      filter.count = count;
      return count;
    };

    $scope.naturalCompare = function (a, b) {
      if (a.type === "number") {
        return a.value - b.value;
      }
      return naturalSort(a.value, b.value);
    };
    $scope.bugsFilter = function (bug, index, array) {
      if ($scope.search) {
        var matchSearch =
          bug.owner.indexOf($scope.search) != -1 ||
          bug.project.indexOf($scope.search) != -1;
        if (matchSearch === false) {
          return false;
        }
      }
      for (let lib in $scope.filters.libs) {
        if (bug.lib == lib) {
          if (!$scope.filters.libs[lib]) {
            return false;
          }
        }
      }
      for (let reason in $scope.filters.reasons) {
        if ($scope.filters.reasons[reason]) {
          let match = false;
          for (let lib in bug.executions) {
            const exec = bug.executions[lib];
            if (exec.reason == reason) {
              match = true;
              break;
            }
          }
          if (match === false) {
            return false;
          }
        }
      }
      let allvalid = true;
      let allinvalid = true;
      for (let lib of $scope.execs) {
        if (!bug.executions[lib]) {
          continue;
        }
        if (
          bug.executions[lib].valid === true ||
          bug.executions[lib].valid === false
        ) {
          allvalid &= bug.executions[lib].valid === true;
          allinvalid &= bug.executions[lib].valid === false;
        }
      }
      if (!$scope.filters.allvalid && allvalid) {
        return false;
      }
      if (!$scope.filters.allinvalid && allinvalid) {
        return false;
      }
      return true;
    };
  });
