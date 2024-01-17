FROM hseeberger/scala-sbt
WORKDIR /poker
ADD . /poker
CMD sbt run


