#!/usr/bin/env python3

import subprocess
import argparse
import sys
import os

parser = argparse.ArgumentParser(prog="Runner", description='Runner interface')

def run():
    program = None
    if sys.argv[1] == "build":
        program = "build.py"
    elif sys.argv[1] == "analyze":
        program = "analyze.py"
    elif sys.argv[1] == "usage":
        program = "usage.py"
    elif sys.argv[1] == "experiment":
        program = "experiment.py"
    subprocess.call("./%s %s" % (program, " ".join(sys.argv[2:])), shell=True)
    os._exit(0)

if __name__ == "__main__":
    run()