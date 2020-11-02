#!/usr/bin/env python3

import argparse
import tempfile
import os
import json
import subprocess
import xml.etree.ElementTree as xml
from pathlib import Path

from Project import Project


json_libs = [
    "org.json:json",
    "com.googlecode.json-simple:json-simple",
    "com.google.code.gson:gson",
    "com.fasterxml.jackson.core:jackson-core",
    "com.alibaba:fastjson",
    "org.eclipse:yasson",
    "org.glassfish:jakarta.json",
    "org.glassfish:javax.json"
]

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-u', "--url", required=True,
                        help="The url to the git repository")
    parser.add_argument("-c", "--commit", required=True,
                        help="The commit of the lib to debloat")
    parser.add_argument("-r", "--results", required=True,
                        help="The result folder")
    parser.add_argument(
        "--timeout", help="Execution timeout of mvn compile/test/package", default=None)

    args = parser.parse_args()

    working_directory = tempfile.mkdtemp()

    project = Project(args.url)
    project.clone(working_directory)
    project.checkout_commit(args.commit)

    project.install(stdout="install.log", timeout=args.timeout)

    if len(project.pom.poms) == 0:
        os._exit(os.EX_OK)

    to_instrument = set()

    cl = project.classpath()
    for p in cl:
        for dep in p.split(":"):
            for lib in json_libs:
                if lib.replace(".", "/").replace(":", "/") in dep:
                    to_instrument.add(dep)
                    break
    if len(to_instrument) == 0:
        os._exit(os.EX_OK)

    yajta_jar = os.path.join(os.path.dirname(os.path.realpath(__file__)), "yajta.jar")
    
    for lib in to_instrument:
        lib_file = os.path.basename(lib)
        cache_path = os.path.join(args.results, "instrumented_libs", lib_file)
        if not os.path.exists(os.path.dirname(cache_path)):
            os.makedirs(os.path.dirname(cache_path))
        cache_path = os.path.abspath(cache_path)
        if os.path.exists(cache_path):
            cmd = f"cp {cache_path} {lib}"
        else:
            timeout_cmd = ''
            if args.timeout is not None:
                timeout_cmd = 'timeout -k 1m -s SIGKILL %s' % args.timeout
            cmd = f'cd {project.path}; {timeout_cmd} java -cp {yajta_jar} se.kth.castor.offline.RemoteUserInstrumenter -i "{lib}" -o {project.path} -y; cp {lib_file} {lib};cp {lib_file} {cache_path};rm -rf yajta-tmp*;'
        print(cmd)
        subprocess.call(cmd, shell=True)

    project.test(clean=False, stdout="output.log", timeout=args.timeout)
    for lib in to_instrument:
        os.remove(lib)
    

    trace_dirs = []
    for root, dirs, files in os.walk(project.path, topdown=False):
        for name in dirs:
            if name == "traceDir":
                trace_dirs.append(os.path.join(root, name))

    result_path = os.path.join(args.results, "traces", project.repo + '.json')
    if not os.path.exists(os.path.dirname(result_path)):
        os.makedirs(os.path.dirname(result_path))
    result_path = os.path.abspath(result_path)

    if len(trace_dirs) == 0:
        with open(result_path, 'w') as fd:
            fd.write("{}")
    else:
        timeout_cmd = ''
        if args.timeout is not None:
            timeout_cmd = 'timeout -k 1m -s SIGKILL %s' % args.timeout
        cmd = f'cd {project.path}; {timeout_cmd} java -cp {yajta_jar} se.kth.castor.offline.RemoteUserReader -i {",".join(trace_dirs)} -o {result_path} -f;'
        print(cmd)
        subprocess.call(cmd, shell=True)
    
    print(project.path)