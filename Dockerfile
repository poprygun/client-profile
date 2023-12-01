FROM harbor-repo.vmware.com/rapidportfoliomod/gradle@sha256:39b5b96f82b805810f9032b48d11451753d68d494a9faba473a72351fc5287ac AS build

RUN mkdir /project
COPY . /project
WORKDIR /project
RUN gradle build

FROM harbor-repo.vmware.com/dockerhub-proxy-cache/library/openjdk:17-jdk-alpine
RUN mkdir -p data
RUN apk update
RUN apk add --no-cache --upgrade busybox
RUN apk add --no-cache --upgrade dumb-init
RUN apk add --no-cache --upgrade expat
RUN apk add --no-cache --upgrade apk-tools
RUN apk add --no-cache --upgrade libcrypto1.1
RUN apk add --no-cache --upgrade libssl1.1
RUN apk add --no-cache --upgrade libtasn1
RUN apk add --no-cache --upgrade libretls
RUN apk add --no-cache --upgrade ssl_client
RUN apk add --no-cache --upgrade zlib
VOLUME /apps

RUN mkdir /app
RUN addgroup --system javauser && adduser -S -s /bin/false -G javauser javauser
COPY --from=build /project/build/libs/client-profile-0.0.1-SNAPSHOT.jar /app/java-application.jar

WORKDIR /app
# RUN chown -R javauser:javauser /app
# USER javauser

ENTRYPOINT [ "dumb-init", "sh", "-c", "java -Xms4096M -Xmx8192M -DMODE=${MODE} -DPLUGIN_CALL_BACK_URL=${PLUGIN_CALL_BACK_URL} -DPASSWORD=${PASSWORD} -Dspring.profiles.active=${PROFILE} -Dsecurity.allowed.ipaddress=${IP_ADDRESSES} -Djasypt.encryptor.password=${ENCRYPTION_KEY} -jar \"java-application.jar\"" ]
