class LogAnalyser:
    def __init__(self, path: str):
        self.path = path
        with open(self.path, 'r', encoding="utf-8") as fd:
            self.content = fd.read()

    def parse(self):
        errors = []
        lines = self.content.split("\n")
        for line in lines:
            if "[ERROR]" in line:
                errors.append(line)
        return errors
