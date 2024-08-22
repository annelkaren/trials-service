FROM eclipse-temurin:21-alpine

EXPOSE 8888

RUN apk update && \
    apk --no-cache add fontconfig \
    ttf-dejavu \
    freetype \
    freetype-dev \
    tzdata && \
    cp /usr/share/zoneinfo/America/Mexico_City /etc/localtime && \
    echo "America/Mexico_City" >  /etc/timezone

ENV TZ America/Mexico_City
ENV LANG es_MX.UTF-8
ENV LANGUAGE es_MX.UTF-8
ENV LC_ALL es_MX.UTF-8

RUN addgroup -g 1000 -S gobmx && adduser -u 1000 -S pjp -G gobmx

USER pjp:gobmx
WORKDIR /opt/pjp

COPY target/*.jar trials.jar

ENTRYPOINT ["java", "-jar", "trials.jar"]
