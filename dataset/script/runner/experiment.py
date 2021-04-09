#!/usr/bin/env python3

from datetime import datetime
import argparse
import tempfile
import os
import json
import subprocess

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
        "jackson-databind",
        "jsonutil",
        "mjson",
        "nothing",
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
        "jackson-databind",
        "jsonutil",
        "mjson",
        "nothing",
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
        "jackson-databind",
        "jsonutil",
        "mjson",
        "nothing",
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
        "jackson-databind",
        "jsonutil",
        "mjson",
        "nothing",
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
        "nothing",
        # "klaxon",
    ]
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
    print("[%s] [STARTED]  Clone %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)
    status = project.clone(working_directory)
    print("[%s] [FINISHED] Clone %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name, status), flush=True)
    print("[%s] [STARTED]  Checkout %s %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name, args.commit), flush=True)
    project.checkout_commit(args.commit)
    print("[%s] [FINISHED]  Checkout %s %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name, project.get_commit()), flush=True)

    project_path = working_directory
    log_path = os.path.join(project_path, "output.log")

    print("[%s] [STARTED]  Install %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)
    status = project.install(stdout=log_path, timeout=args.timeout)
    print("[%s] [FINISHED] Install %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name, status), flush=True)

    

    if len(project.pom.poms) == 0:
        os._exit(os.EX_OK)

    results = {
        "url": project.url,
        "commit": args.commit,
        "lib": args.lib,
        "executions": []
    }

    print("[%s] [STARTED]  Test %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name), flush=True)
    status = project.test(stdout=log_path, clean=False, timeout=args.timeout)
    print("[%s] [FINISHED] Test %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), project.name, status), flush=True)
    log = LogAnalyser(log_path)

    o = {
        "url": project.url,
        "commit": args.commit,
        "lib": args.lib,
        "name": "original",
        "test": project.get_test_results(),
        "classpath": project.classpath(),
        "errors": log.parse()
    }
    print(o["errors"])

    result_folder = os.path.join(args.results, "experimentv2", project.repo, args.lib)
    if not os.path.exists(result_folder):
        os.makedirs(result_folder)
    path_result = os.path.join(result_folder, "original.json")

    with open(path_result, 'w') as fd:
        json.dump(o, fd, indent=1)
    
    results["executions"].append(o)
    os.rename(log_path, os.path.join(project_path, "origin.log"))

    implems = json_map[args.lib]
    if args.implementations is not None:
        implems = args.implementations
    for implem in sorted(implems):
        print("[%s] Start %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)
        timeout_cmd = ''
        if args.timeout is not None:
            timeout_cmd = 'timeout -k 1m -s SIGKILL %s' % args.timeout
        cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/jars/;'
        print("[%s] [STARTED]  Inject %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)
        subprocess.call(cmd, shell=True)
        print("[%s] [FINISHED] Inject %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem, status), flush=True)

        print("[%s] [STARTED]  Test %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)
        status = project.test(stdout=log_path, timeout=args.timeout)
        print("[%s] [FINISHED] Test %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem, status), flush=True)

        log = LogAnalyser(log_path)
        errors = log.parse()
        print(errors)
        os.rename(log_path, os.path.join(project_path, "%s.log" % implem))

        o = {
            "url": project.url,
            "commit": args.commit,
            "lib": args.lib,
            "name": implem,
            "test": project.get_test_results(),
            "classpath": project.classpath(),
            "errors": errors
        }
        results["executions"].append(o)

        cmd = f'cd {project.path}; {timeout_cmd} java -jar $HOME/depswap.jar . "{args.lib}:*" "se.kth.castor:yasjf4j-{implem}:1.0-SNAPSHOT" $HOME/depswap/yasjf4j/jars/ r;'
        print("[%s] [STARTED]  Restore %s" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem), flush=True)
        subprocess.call(cmd, shell=True)
        print("[%s] [FINISHED] Restore %s (status: %s)" % (datetime.now().strftime("%d/%m/%Y %H:%M:%S"), implem, status), flush=True)

        result_folder = os.path.join(args.results, "experimentv2", project.repo, args.lib)
        if not os.path.exists(result_folder):
            os.makedirs(result_folder)
        path_result = os.path.join(result_folder, implem + ".json")

        with open(path_result, 'w') as fd:
            json.dump(o, fd, indent=1)

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
