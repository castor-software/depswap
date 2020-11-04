#!/usr/bin/env python3

from datetime import datetime
import argparse
import tempfile
import os
import shutil
import json
import subprocess
import xml.etree.ElementTree as xml
from pathlib import Path

from Project import Project
from LogAnalyser import LogAnalyser


json_map = {
    "org.json:json": [
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
        # "klaxon",
    ],
    "com.alibaba:fastjson": [
        "json-simple",
        "gson",
        "json",
        "cookjson",
        "jjson",
        "json-io",
        "json-lib",
        "jsonp",
        "jackson",
        "jsonutil",
        "mjson",
        # "klaxon",
    ],
    "com.googlecode.json-simple:json-simple": [
        "gson",
        "fastjson",
        "json",
        "cookjson",
        "jjson",
        "json-io",
        "json-lib",
        "jsonp",
        "jackson",
        "jsonutil",
        "mjson",
        # "klaxon",
    ],
    "com.google.code.gson:gson": [
        "json-simple",
        "fastjson",
        "json",
        "cookjson",
        "jjson",
        "json-io",
        "json-lib",
        "jsonp",
        "jackson",
        "jsonutil",
        "mjson",
        # "klaxon",
    ],
    "com.fasterxml.jackson.core:jackson-databind": [
        "json-simple",
        "fastjson",
        "json",
        "cookjson",
        "jjson",
        "json-io",
        "json-lib",
        "jsonp",
        "gson",
        "jsonutil",
        "mjson",
        # "klaxon",
    ]
}

lib_jar = {
    "cookjson": "yasjf4j-cookjson-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "com.alibaba:fastjson": "yasjf4j-fastjson-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "gson": "yasjf4j-gson-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "jackson": "yasjf4j-jackson-databind-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "jjson": "yasjf4j-jjson-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "org.json:json": "yasjf4j-json-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "json-io": "yasjf4j-json-io-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "json-lib": "yasjf4j-json-lib-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "com.googlecode.json-simple:json-simple": "yasjf4j-json-simple-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "jsonp": "yasjf4j-jsonp-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "jsonutil": "yasjf4j-jsonutil-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "klaxon": "yasjf4j-klaxon-1.0-SNAPSHOT-jar-with-dependencies.jar",
    "mjson": "yasjf4j-mjson-1.0-SNAPSHOT-jar-with-dependencies.jar",
}
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-u', "--url", required=True,
                        help="The url to the git repository")
    parser.add_argument('-l', "--lib", required=True,
                        help="The groupid:artifact id to replace")
    parser.add_argument("-c", "--commit", required=True,
                        help="The commit of the lib to debloat")
    parser.add_argument("-r", "--results", required=False,
                        help="The result folder", default="results")
    parser.add_argument(
        "--timeout", help="Execution timeout of mvn compile/test/package", default=None)
    
    parser.add_argument("-i", "--implementations", required=False, nargs='+',
                        help="The name of the implemts", default=None)

    args = parser.parse_args()

    print("[%s] Start %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), args.url), flush=True)

    working_directory = tempfile.mkdtemp()

    project = Project(args.url)
    project.clone(working_directory)
    print("[%s] Clone %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)
    project.checkout_commit(args.commit)

    project_path = working_directory
    log_path = os.path.join(project_path, "output.log")

    project.install(stdout=log_path, timeout=args.timeout)
    print("[%s] Install %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)

    

    if len(project.pom.poms) == 0:
        os._exit(os.EX_OK)

    results = {
        "url": project.url,
        "commit": args.commit,
        "lib": args.lib,
        "executions": []
    }

    project.test(stdout=log_path, clean=False, timeout=args.timeout)
    print("[%s] Test %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)
    log = LogAnalyser(log_path)

    results["executions"].append({
        "name": "original",
        "test": project.get_test_results(),
        "classpath": project.classpath(),
        "errors": log.parse()
    })
    os.rename(log_path, os.path.join(project_path, "origin.log"))

    implems = json_map[args.lib]
    if args.implementations is not None:
        implems = args.impls
    for implem in sorted(implems):
        print("[%s] Start %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)
        timeout_cmd = ''
        if args.timeout is not None:
            timeout_cmd = 'timeout -k 1m -s SIGKILL %s' % args.timeout
        cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/jars/;'
        subprocess.call(cmd, shell=True)
        print("[%s] Inject %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)

        project.test(stdout=log_path, timeout=args.timeout)
        print("[%s] Test %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)

        log = LogAnalyser(log_path)
        errors = log.parse()
        os.rename(log_path, os.path.join(project_path, "%s.log" % implem))

        results["executions"].append({
            "name": implem,
            "test": project.get_test_results(),
            "classpath": project.classpath(),
            "errors": errors
        })

        cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/jars/ r;'
        subprocess.call(cmd, shell=True)
        print("[%s] Restore %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)

        # cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/instrumented_jars/;'
        # subprocess.call(cmd, shell=True)

        # project.test(stdout=log_path, timeout=args.timeout)

        # log = LogAnalyser(log_path)
        # errors = log.parse()
        # os.rename(log_path, os.path.join(project_path, "%s_inst.log" % implem))
        
        # trace_dirs = []
        # for root, dirs, files in os.walk(project.path, topdown=False):
        #     for name in dirs:
        #         if name == "yajta-traceDir":
        #             trace_dirs.append(os.path.join(root, name))
        # timeout_cmd = ''
        # if args.timeout is not None:
        #     timeout_cmd = 'timeout -k 1m -s SIGKILL %s' % args.timeout
        # cmd = f'cd {project.path}; {timeout_cmd} java -cp $HOME/yajta.jar se.kth.castor.offline.RemoteUserReader -i {",".join(trace_dirs)} -o {os.path.join(project.path, "trace.json")} -f;'
        # subprocess.call(cmd, shell=True)

        # r = {
        #     "name": implem + "_inst",
        #     "test": project.get_test_results(),
        #     "classpath": project.classpath(),
        #     "errors": errors
        # }
        # results["executions"].append(r)

        # if os.path.exists(os.path.join(project.path, "trace.json")):
        #     with open(os.path.join(project.path, "trace.json"), 'r', encoding="utf-8") as fd:
        #         r['trace'] = json.load(fd)

        # for trace in trace_dirs:
        #   shutil.rmtree(trace)

        # cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/jars/ r;'
        # subprocess.call(cmd, shell=True)

        #project.test(clean=False, stdout="output.log", timeout=args.timeout)
    result_folder = os.path.join(args.results, "exp", project.repo)
    if not os.path.exists(result_folder):
        os.makedirs(result_folder)
    path_result = os.path.join(result_folder, args.lib + ".json")

    with open(path_result, 'w') as fd:
        json.dump(results, fd, indent=1)
