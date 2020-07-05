# Alpine 2.22 (LNX 395 2020-01-19) built Sun May 3 16:50:26 UTC 2020 on build-3-12-x86_64
# mariadb-10.4.13
#

FROM alpine:3.12

LABEL MAINTAINER="Harry Sun <harrysun2006@gmail.com>"

# Install MYSQL
RUN apk add --update mysql mysql-client && rm -f /var/cache/apk/*

# Copy MySQL startup script and config
COPY start.sh /start.sh
COPY my.cnf /etc/my.cnf
RUN chmod uog+x start.sh

RUN mkdir -p /var/mysql/data
# VOLUME [ "/var/lib/mysql" ]

EXPOSE 3306
ENTRYPOINT [ "/start.sh" ]
