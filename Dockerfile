FROM gradle:8.4.0-jdk17-jammy AS BUILD

WORKDIR /appbuild

COPY . .

RUN gradle build -Dquarkus.package.type=uber-jar --no-daemon

#FROM amazoncorretto:17-alpine as CORRETTO-DEPS
#
#WORKDIR /app
#
#COPY --from=build /appbuild/build/paddy-backend-runner.jar .
#
## Get modules list
#RUN unzip paddy-backend-runner.jar -d temp &&  \
#    jdeps  \
#      --print-module-deps \
#      --ignore-missing-deps \
#      --recursive \
#      --multi-release 17 \
#      --class-path="./temp/BOOT-INF/lib/*" \
#      --module-path="./temp/BOOT-INF/lib/*" \
#      paddy-backend-runner.jar > modules.txt
#
#FROM amazoncorretto:17-alpine as CORRETTO-JDK
#
#WORKDIR /app
#
#COPY --from=corretto-deps /app/modules.txt .
#
## Output a custom jre built from the modules list
#RUN apk add --no-cache binutils && \
#    jlink \
#     --verbose \
#     --add-modules "$(cat modules.txt)" \
#     --strip-debug \
#     --no-man-pages \
#     --no-header-files \
#     --compress=2 \
#     --output /jre

FROM amazoncorretto:17-alpine as CORRETTO-JDK

RUN apk add curl # For Health Checks

#COPY --from=corretto-jdk /jre /app/jre
COPY --from=build /appbuild/build/paddy-backend-runner.jar /app/paddy-backend-runner.jar

WORKDIR /app

ARG DEBUG_OPT
ENV DEBUG_API_OPT=$DEBUG_OPT

CMD java $DEBUG_API_OPT -jar paddy-backend-runner.jar