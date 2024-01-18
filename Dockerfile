FROM hseeberger/scala-sbt:11.0.7_1.3.13_2.12.12
WORKDIR /poker
ADD . /poker
CMD sbt run


