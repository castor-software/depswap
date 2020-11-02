#!/usr/bin/env python3

import argparse
import tempfile
import os
import json
import subprocess
import xml.etree.ElementTree as xml
from pathlib import Path

from Project import Project


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-u', "--url", required=True,
                        help="The url to the git repository")
    parser.add_argument("-c", "--commit", required=True,
                        help="The commit of the lib to debloat")
    parser.add_argument(
        "--timeout", help="Execution timeout of mvn compile/test/package", default=None)

    args = parser.parse_args()

    working_directory = tempfile.mkdtemp()

    project = Project(args.url)
    project.clone(working_directory)
    project.checkout_commit(args.commit)

    if len(project.pom.poms) == 0:
        os._exit(os.EX_OK)

    project.compile(timeout=args.timeout, stdout="out.log")

    packages = ["org.json", "org.json.simple", "com.google.gson", "com.fasterxml.jackson.databind", "com.fasterxml.jackson.core", "org.eclipse.yasson", "org.glassfish.json", "com.alibaba.fastjson"]

    pathes = []

    for p in Path(project.path).rglob('target'):
        if os.path.exists(os.path.join(p, "classes")):
            pathes.append(os.path.join(p, "classes"))
    cmd = "java -cp depswap.jar se.kth.assertteam.depanalyzer.Analyzer %s %s" % (",".join(pathes), ",".join(packages))
    
    subprocess.call(cmd, shell=True)
