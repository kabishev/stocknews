FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ADD target/scala-2.13/stocknews.jar /stocknews.jar
ADD docker/scripts/run.sh /run.sh
ADD docker/app.conf /app.conf

#VOLUME /app.conf

EXPOSE 8080

CMD /run.sh
