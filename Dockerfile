FROM        trumanwoo/docker-jebian

MAINTAINER  Truman Woo <chunan.woo@gmail.com>

ENV         APP_VERSION 1.0.0
ENV 		APP_NAME play-compose-seed

# Install unzip
RUN         apt-get install -y unzip

COPY        target/universal/$APP_NAME-$APP_VERSION.zip /root/apps/
WORKDIR     /root/apps

# Setup database connections in environment variables
ENV 		BASE_DB_HOST jdbc:mysql://db:3306/play_compose_seed?characterEncoding=utf-8
ENV 		BASE_DB_USER root
ENV 		BASE_DB_PASSWD 123456

RUN         unzip $APP_NAME-$APP_VERSION.zip && \
            rm $APP_NAME-$APP_VERSION.zip
ENTRYPOINT  ["./play-compose-seed-1.0.0/bin/play-compose-seed", "-Dhttp.port=9000"]
EXPOSE      9000