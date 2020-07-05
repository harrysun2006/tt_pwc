#!/bin/sh
chown -R postgres "$PGDATA"
mkdir -p /run/postgresql/
chown -R postgres /run/postgresql/

if [ -z "$(ls -A "$PGDATA")" ]; then
    su-exec postgres initdb
    sed -ri "s/^#(listen_addresses\s*=\s*)\S+/\1'*'/" "$PGDATA"/postgresql.conf

    : ${POSTGRES_USER:="postgres"}
    : ${POSTGRES_DATABASE:=$POSTGRES_USER}

    if [ "$POSTGRES_PASSWORD" ]; then
        pass="PASSWORD '$POSTGRES_PASSWORD'"
        authMethod=md5
    else
        echo "==============================="
        echo "[i] !!! NO PASSWORD SET !!! (Use \$POSTGRES_PASSWORD env var)"
        echo "==============================="
        pass=
        authMethod=trust
    fi
    echo

	# create temp file
	tfile=$( mktemp )
	if [ ! -f "$tfile" ]; then
	    return 1
	fi

	# save sql
	echo "[i] Create temp file: $tfile"
	touch $tfile

    # create app user & database
    if [ "$POSTGRES_USER" != 'postgres' ]; then
        op=CREATE
    else
        op=ALTER
    fi

    echo "[i] $op app user: $POSTGRES_USER"
    echo "$op USER $POSTGRES_USER WITH PASSWORD '$POSTGRES_PASSWORD';" >> $tfile

    if [ "$POSTGRES_DATABASE" != 'postgres' ]; then
        echo "[i] create app database: $POSTGRES_DATABASE"
        echo "CREATE DATABASE $POSTGRES_DATABASE ENCODING 'UTF-8';" >> $tfile
        echo "ALTER DATABASE $POSTGRES_DATABASE SET TIMEZONE TO 'UTC';" >> $tfile
        echo "[i] grant app database $POSTGRES_DATABASE permissions to user $POSTGRES_USER"
        echo "GRANT ALL ON DATABASE $POSTGRES_DATABASE TO $POSTGRES_USER;" >> $tfile
    fi

    # create test user & database
    if [ ! -z "$POSTGRES_USER_TEST" ]; then
        echo "[i] create test user: $POSTGRES_USER_TEST"
        echo "CREATE USER $POSTGRES_USER_TEST WITH PASSWORD '$POSTGRES_PASSWORD_TEST';" >> $tfile
    fi

    if [ ! -z "$POSTGRES_DATABASE_TEST" ]; then
        echo "[i] create test database: $POSTGRES_DATABASE_TEST"
        echo "CREATE DATABASE $POSTGRES_DATABASE_TEST;" >> $tfile
        echo "ALTER DATABASE $POSTGRES_DATABASE_TEST SET TIMEZONE TO 'UTC';" >> $tfile
        echo "[i] grant test database $POSTGRES_DATABASE permissions to user $POSTGRES_USER_TEST"
        echo "GRANT ALL ON DATABASE $POSTGRES_DATABASE_TEST TO $POSTGRES_USER_TEST;" >> $tfile
    fi

    # cat $tfile | su-exec postgres postgres --single -jE

    su-exec postgres pg_ctl -D "$PGDATA" -o "-c listen_addresses=*" -w start
    for f in /initdb.d/*; do
        case "$f" in
            *.sh)  echo "$0: running $f"; . "$f" ;;
            *.sql) echo "$0: running $f"; psql --username "$POSTGRES_USER" --dbname "$POSTGRES_DATABASE" < "$f" && echo ;;
            *)     echo "$0: ignoring $f" ;;
        esac
        echo
    done
    psql -U postgres -f $tfile
    su-exec postgres pg_ctl -D "$PGDATA" -m fast -w stop

    { echo; echo "host all all 0.0.0.0/0 trust"; } >> "$PGDATA"/pg_hba.conf
fi

exec su-exec postgres "$@"

