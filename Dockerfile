FROM openjdk:12 

COPY target/msgpacktojson-1.0-SNAPSHOT-jar-with-dependencies.jar /

EXPOSE 4541

CMD java -jar /msgpacktojson-1.0-SNAPSHOT-jar-with-dependencies.jar
