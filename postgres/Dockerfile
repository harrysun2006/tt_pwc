# Alpine 2.22 (LNX 395 2020-01-19) built Sun May 3 16:50:26 UTC 2020 on build-3-12-x86_64
# postgres (PostgreSQL) 12.3
#
# Alpine 3.12 - postgresql 12.3
# Alpine 3.11 - postgresql 12.2
# Alpine 3.10 - postgresql 11.7
# Alpine 3.9  - postgresql 11.7
# Alpine 3.8  - postgresql 10.12
# Alpine 3.7  - postgresql 10.10
# Alpine 3.6  - postgresql 9.6.13
#

FROM alpine:3.8

LABEL MAINTAINER="Harry Sun <harrysun2006@gmail.com>"

# ARG POSTGRES_VERSION=10.12-r0
ENV PGDATA /var/lib/postgresql/data

# Install PostgreSQL
RUN apk add --update su-exec tzdata libpq postgresql postgresql-client postgresql-contrib && \
    mkdir /initdb.d && \
    rm -f /var/cache/apk/*

# Copy startup script
COPY start.sh /start.sh
RUN chmod uog+x start.sh

# VOLUME /var/lib/postgresql/data

EXPOSE 5432
ENTRYPOINT [ "/start.sh" ]
CMD [ "postgres" ]
