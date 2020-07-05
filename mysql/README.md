# MySQL

## Versions

Alpine : `3.12`   
MySQL : `mariadb (10.4.13-r0)`

## Credentials

**Default :**  
* `MYSQL_ROOT_PASSWORD` : `root`,
* `MYSQL_DATABASE` : `app`,
* `MYSQL_DATABASE_TEST` : `app_test`,
* `MYSQL_USER` : `app`,
* `MYSQL_PASSWORD` : `app`,

**Custom :** In the `.env` file, change the different values to suit your needs.

## Commands

**Pull :** `docker pull quay.io/perriea/alpine-mysql:1.0`   
**Run :** `docker run -d -p 3306:3306 --env-file .env quay.io/perriea/alpine-mysql:1.0`



docker build -t pwc/db:latest ./db