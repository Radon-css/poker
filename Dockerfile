# ---------- 1) BUILDER -----------------------------------
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1 AS builder
WORKDIR /src

# Build‑Definition (bleibt meist stabil)
COPY build.sbt .
COPY project/ ./project/

# Ivy/SBT/Coursier-Caches
RUN --mount=type=cache,target=/root/.ivy2 \
    --mount=type=cache,target=/root/.sbt \
    --mount=type=cache,target=/root/.cache/coursier \
    sbt update

# Quellcode
COPY . .

# Drei Services in einem Rutsch bauen
RUN --mount=type=cache,target=/root/.ivy2 \
    --mount=type=cache,target=/root/.sbt \
    --mount=type=cache,target=/root/.cache/coursier \
    sbt coreService/stage evalService/stage dbService/stage

# ---------- 2) RUNTIME‑STAGES ----------------------------

FROM eclipse-temurin:21-jre AS core
WORKDIR /app
COPY --from=builder /src/coreService/target/universal/stage/ .
ENTRYPOINT ["bin/coreservice"]

FROM eclipse-temurin:21-jre AS eval
WORKDIR /app
COPY --from=builder /src/evalService/target/universal/stage/ .
ENTRYPOINT ["bin/evalservice"]

FROM eclipse-temurin:21-jre AS db
WORKDIR /app
COPY --from=builder /src/dbService/target/universal/stage/ .
ENTRYPOINT ["bin/dbservice"]
