FROM maven:3.6-jdk-8

RUN apt-get update
RUN apt-get install -y python3 python3-pip git

ARG USER_ID
ARG GROUP_ID

RUN groupadd -r --gid $GROUP_ID runner; exit 0
RUN useradd -rm -d /home/runner -s /bin/bash -G sudo --uid $USER_ID --gid $GROUP_ID runner
USER runner

WORKDIR /home/runner

ARG LIB

RUN git clone https://github.com/castor-software/depswap.git --depth 1
RUN cd depswap; git checkout $LIB
RUN cd depswap/test-harness; mvn package; cp target/depswap-test-harness-0.1-SNAPSHOT-jar-with-dependencies.jar /home/runner/depswap.jar
RUN cat depswap/yasjf4j/build-jars.sh
RUN cd depswap/yasjf4j/; bash build-jars.sh;

COPY requirements.txt requirements.txt
RUN pip3 install --requirement requirements.txt 

RUN mkdir results
ENV M2_HOME=/usr/share/maven

COPY maven-proxy.xml maven-proxy.xml
COPY yajta.jar yajta.jar

COPY instruments-jars.sh instruments-jars.sh
RUN cd depswap/yasjf4j; bash /home/runner/instruments-jars.sh
RUN ls depswap/yasjf4j/instrumented_jars;

COPY __init__.py /__init__.py
COPY main.py main.py
COPY build.py build.py
COPY analyze.py analyze.py
COPY usage.py usage.py
COPY experiment.py experiment.py
COPY Project.py Project.py
COPY PomExtractor.py PomExtractor.py
COPY LogAnalyser.py LogAnalyser.py

ENTRYPOINT [ "./main.py" ]
