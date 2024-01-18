FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java11_1.6.2_3.1.1
WORKDIR /poker
ADD . /poker
CMD sbt run


