#!/usr/bin/env python3

import argparse
import tempfile
import os
import json
import time
import re
import xml.etree.ElementTree as xml

from Project import Project


def extract_surefire_reports(test_results_path):
    output = {
        'error': 0,
        'failing': 0,
        'passing': 0,
        'execution_time': 0
    }
    for test in os.listdir(test_results_path):
        if ".xml" not in test:
            continue
        test_path = os.path.join(test_results_path, test)
        try:
            test_results = xml.parse(test_path).getroot()
            if test_results.get('time') is not None:
                output['execution_time'] += float(test_results.get('time'))
            if test_results.get('errors') is not None:
                output['error'] += int(test_results.get('errors'))
            if test_results.get('failures') is not None:
                output['failing'] += int(test_results.get('failures'))
            if test_results.get('failed') is not None:
                output['failing'] += int(test_results.get('failed'))
            if test_results.get('tests') is not None:
                output['passing'] += int(test_results.get('tests')) - int(
                    test_results.get('errors')) - int(test_results.get('failures'))
            if test_results.get('passed') is not None:
                output['passing'] += int(test_results.get('passed'))
        except:
            pass
    return output


def readTestResults(path):
    surefire_name = "surefire-reports"

    output = {
        'error': 0,
        'failing': 0,
        'passing': 0,
        'execution_time': 0,
        'modules': {}
    }

    for root, dirs, files in os.walk(path, topdown=False):
        for name in dirs:
            if name == surefire_name:
                results = extract_surefire_reports(os.path.join(root, name))
                module = root.replace(path, "").replace("/target", "")[1:]

                output['error'] += results['error']
                output['failing'] += results['failing']
                output['passing'] += results['passing']
                output['execution_time'] += results['execution_time']

                output['modules'][module] = results
    return output

def getEnv():
    mvn_version_str = os.popen('mvn -version').read().strip()
    mvn_version = re.match(r'.*Apache Maven ([0-9\.]+) .*', mvn_version_str, re.DOTALL).group(1)
    mvn_java_version = re.match(r'.*Java version: ([0-9\._]+),.*', mvn_version_str, re.DOTALL).group(1)
    output = {
        "java_version": os.popen('java -version 2>&1 | awk -F[\\\"_] \'NR==1{print $2}\'').read().strip(),
        "mvn_version": mvn_version,
        "mvn_java_version": mvn_java_version,
    }

    return output

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-u', "--url", required=True,
                        help="The url to the git repository")
    parser.add_argument("-c", "--commit", required=True,
                        help="The commit of the lib to debloat")
    parser.add_argument("--iteration", type=int, default=3,
                        help="The number of test execution")
    parser.add_argument(
        "--timeout", help="Execution timeout of mvn compile/test/package", default=None)
    parser.add_argument(
        "--output", help="The output folder of the test-results")
    parser.add_argument(
        "--test-result", help="The output folder of the test-results")
    parser.add_argument("--coverage", nargs='?',
                        default=False, help="Get the coverage")

    args = parser.parse_args()

    if args.output:
        args.output = os.path.abspath(args.output)

    working_directory = tempfile.mkdtemp()

    project = Project(args.url)
    project.clone(working_directory)
    project.checkout_commit(args.commit)
    if len(project.pom.poms) == 0:
        os._exit(os.EX_OK)
    output = {
        "commit": project.get_commit(),
        "root": os.path.dirname(project.pom.poms[0]["path"]).replace(project.path, ""),
        "test_results": [],
        "start": time.time(),
        "env": getEnv()
    }
    if args.coverage:
        project.inject_jacoco_plugin()
    log_path = os.path.join(os.path.dirname(os.path.realpath(__file__)), "output.log")
    project.compile(clean=False, stdout=log_path, timeout="10m")
    output['compile-log'] = ""
    if os.path.exists(log_path):
        with open(log_path, 'r') as fd:
            output['compile-log'] = fd.read()
    output['test-logs'] = []
    for i in range(0, args.iteration):
        #project.test(clean=False)
        project.test(clean=False, stdout=log_path, timeout=args.timeout)
        if os.path.exists(log_path):
            with open(log_path, 'r') as fd:
                output['test-logs'].append(fd.read())
        if args.output is not None:
            project.copy_test_results(args.output)
        if args.coverage:
            project.copy_jacoco(args.output)
        output['test_results'].append(project.get_test_results())
    output['end'] = time.time()
    output['classpath'] = project.classpath()
    print(json.dumps(output))
