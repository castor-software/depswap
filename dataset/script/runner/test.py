from Project import Project
import json

p = Project("https://github.com/castor-software/depswap/")
p.path = "/Users/tdurieux/git/jdbl"
print(json.dumps(p.get_test_results(), indent=1))