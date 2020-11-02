FROM maven:3.6-jdk-8

RUN git clone https://github.com/castor-software/depswap.git --depth 1
RUN cd depswap/yasjf4j/yasjf4j-api; mvn install -DskipTests; 
RUN cd depswap/yasjf4j/yasjf4j-cookjson; mvn install -DskipTests;
COPY yasjf4j/build-jars.sh /depswap/yasjf4j/build-jars.sh
RUN cat /depswap/yasjf4j/build-jars.sh
RUN cd depswap/yasjf4j/; bash build-jars.sh;
RUN ls /depswap/yasjf4j/jars/