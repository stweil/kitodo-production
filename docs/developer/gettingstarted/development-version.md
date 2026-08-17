# Build development version of Kitodo 4.x

This guide builds the deployment files (WAR, module JARs, SQL dump, configuration archive) from the current `main` branch and deploys them to a server.

It was tested on [Ubuntu 24.04 LTS](https://ubuntu.com/download/server) and [Debian 13 (trixie)](https://www.debian.org/releases/stable/); the [GitHub Actions build](https://github.com/kitodo/kitodo-production/blob/main/.github/workflows/main.yml) runs on `ubuntu-24.04`.

## 1. System environment

### Install Java 21, Maven and MariaDB

[MariaDB](https://mariadb.org/) is the recommended database server:

```
sudo apt update
sudo apt install -y openjdk-21-jdk maven mariadb-server unzip curl
```

To use [MySQL 8.x](https://dev.mysql.com/downloads/mysql/) instead, note that neither current Ubuntu nor Debian provides a `mysql-server` package in the standard repositories; install it from the [MySQL APT repository](https://dev.mysql.com/doc/refman/8.0/en/installing-mysql-using-apt.html).

### Change Java security config (for cloud environments)

```
sudo sed -i 's/securerandom.source=file:\/dev\/random/securerandom.source=file:\/dev\/urandom/' /etc/java-21-openjdk/security/java.security
```

## 2. Build files for deployment

### Download and build the sources

```
wget https://github.com/kitodo/kitodo-production/archive/main.zip
unzip main.zip && rm main.zip
(cd kitodo-production-main/ && mvn clean install -B '-P!development')
```

Note: If you want to build a release version, you may want to set the version in the `pom.xml` files before packaging.

### Create MariaDB database and user

Start the database server and create the database and user:

```
sudo service mariadb start
sudo mariadb -e "CREATE DATABASE kitodo; CREATE USER 'kitodo'@'localhost' IDENTIFIED BY 'kitodo'; GRANT ALL ON kitodo.* TO 'kitodo'@'localhost'; FLUSH PRIVILEGES;"
```

When using MySQL instead, start the server the same way and create the database and user with the `mysql` client.

### Generate SQL dump (schema, default data and Flyway migrations)

Load the schema and the default data, migrate the schema to the current version with [Flyway](https://flywaydb.org/) and dump the result:

```
cat kitodo-production-main/Kitodo/setup/schema.sql | mariadb -u kitodo -D kitodo --password=kitodo
cat kitodo-production-main/Kitodo/setup/default.sql | mariadb -u kitodo -D kitodo --password=kitodo
(cd kitodo-production-main/Kitodo-DataManagement && mvn flyway:baseline -Pflyway && mvn flyway:migrate -Pflyway)
mariadb-dump -u kitodo --password=kitodo kitodo > kitodo-4.sql
```

When using MySQL instead of MariaDB, replace `mariadb` with `mysql` and `mariadb-dump` with `mysqldump`.

The default Flyway configuration is in `Kitodo-DataManagement/src/main/resources/db/config/flyway.properties` (database `kitodo`, user `kitodo`/`kitodo` at `jdbc:mysql://localhost/kitodo`). For a MariaDB server, set `flyway.url=jdbc:mariadb://localhost/kitodo` before running the migration (see [Use MariaDB](use-mariadb.md)); adjust it likewise if your database is elsewhere.

### Create zip archive with directories and config files

```
mkdir zip zip/config zip/debug zip/diagrams zip/import zip/logs zip/messages zip/metadata zip/modules zip/rulesets zip/scripts zip/temp zip/users zip/xslt
install -m 444 kitodo-production-main/Kitodo/src/main/resources/kitodo_*.xml zip/config/
install -m 444 kitodo-production-main/Kitodo/src/main/resources/docket*.xsl zip/xslt/
install -m 444 kitodo-production-main/Kitodo/src/main/resources/xslt/*.xsl zip/xslt/
install -m 444 kitodo-production-main/Kitodo/rulesets/*.xml zip/rulesets/
install -m 554 kitodo-production-main/Kitodo/scripts/*.sh zip/scripts/
install -m 444 kitodo-production-main/Kitodo/diagrams/* zip/diagrams/
chmod -w zip/config zip/import zip/logs zip/messages zip/metadata zip/rulesets zip/scripts zip/xslt
(cd zip && zip -r ../kitodo-4-config.zip *)
```

Note: Create the `messages` directory only in the case when you want to add or edit your own translations. Only those translation files will be used then, not the ones from the class path.

### Create zip archive with the module JARs

```
zip -j kitodo-4-modules.zip kitodo-production-main/Kitodo/modules/*.jar
```

### Results

* war file: `kitodo-production-main/Kitodo/target/kitodo-<version>.war`
* modules: `kitodo-4-modules.zip`
* sql dump: `kitodo-4.sql`
* zip file: `kitodo-4-config.zip`

## 3. Deployment

### Install Tomcat 10 and ImageMagick

```
sudo apt update
sudo apt install -y tomcat10 imagemagick
```

### Add the OpenSearch 2.x repository and install OpenSearch

Add the [OpenSearch 2.x repository](https://opensearch.org/docs/install-and-configure/opensearch-repositories/) matching your distribution:

For Ubuntu 24.04:

```
wget -qO - https://artifacts.opensearch.org/publickey | sudo gpg --dearmor -o /usr/share/keyrings/opensearch.key
echo "deb [signed-by=/usr/share/keyrings/opensearch.key] https://artifacts.opensearch.org/releases/bundle/opensearch/2.x/ubuntu noble main" | sudo tee /etc/apt/sources.list.d/opensearch.list
```

For Debian:

```
wget -qO - https://artifacts.opensearch.org/publickey | sudo gpg --dearmor -o /usr/share/keyrings/opensearch.key
echo "deb [signed-by=/usr/share/keyrings/opensearch.key] https://artifacts.opensearch.org/releases/bundle/opensearch/2.x/debian stable main" | sudo tee /etc/apt/sources.list.d/opensearch.list
```

Then install OpenSearch:

```
sudo apt update
sudo apt install -y opensearch
```

### Configure Tomcat

Adjust the heap size (edit `/etc/default/tomcat10`):

```
sudo sed -i 's/JAVA_OPTS="-Djava.awt.headless=true/JAVA_OPTS="-Djava.awt.headless=true -Xmx1920m/' /etc/default/tomcat10
```

### Configure the database server (MySQL only)

MariaDB uses one file per table by default, so no configuration is required. MySQL must be told to do so (required for `FLUSH TABLES` based operations):

```
sudo sh -c "echo '[mysqld] innodb_file_per_table' >> /etc/mysql/mysql.conf.d/mysqld.cnf"
sudo service mysql restart
```

### Configure OpenSearch

Set data and log locations and a cluster name in `/etc/opensearch/opensearch.yml`:

```
sudo sed -i 's/#path.data: \/path\/to\/data/path.data: \/var\/lib\/opensearch/' /etc/opensearch/opensearch.yml
sudo sed -i 's/#path.logs: \/path\/to\/logs/path.logs: \/var\/log\/opensearch/' /etc/opensearch/opensearch.yml
sudo sed -i 's/#cluster.name: my-application/cluster.name: kitodo/' /etc/opensearch/opensearch.yml
sudo sed -i 's/#node.name: node-1/node.name: kitodo-1/' /etc/opensearch/opensearch.yml
sudo systemctl daemon-reload
sudo systemctl enable opensearch
sudo systemctl start opensearch
```

The application connects to the search index through Hibernate Search, configured in `hibernate.cfg.xml` (see [step below](#create-directories-and-set-permissions)). The relevant properties are:

```
hibernate.search.backend.type=elasticsearch
hibernate.search.backend.hosts=localhost:9200
hibernate.search.backend.protocol=http
```

(The value `elasticsearch` selects the [Hibernate Search Elasticsearch backend](https://docs.jboss.org/hibernate/stable/search/reference/en-US/html_single/#backend-elasticsearch-configuration-client), which speaks the OpenSearch HTTP API.)

### Create directories and set permissions

```
sudo mkdir /usr/local/kitodo
sudo unzip kitodo-4-config.zip -d /usr/local/kitodo
sudo chown -R tomcat10:tomcat10 /usr/local/kitodo
```

Adjust the following configuration files in `/usr/local/kitodo/config/`:

* `kitodo_config.properties` - the `directory.*` entries must point to the directories created above; set the script parameters (`script_createDirUserHome`, `script_createDirMeta`, `script_createSymLink`, `script_deleteSymLink`) to `/usr/local/kitodo/scripts/...`
* `hibernate.cfg.xml` - database URL, user and password and the search index host. The shipped defaults point at a local MySQL/OpenSearch; for MariaDB set `hibernate.connection.url` to `jdbc:mariadb://localhost/kitodo` (see [Use MariaDB](use-mariadb.md))

### Install modules

```
sudo unzip kitodo-4-modules.zip -d /usr/local/kitodo/modules
sudo chown -R tomcat10:tomcat10 /usr/local/kitodo
```

### Import the SQL dump and deploy the WAR file into Tomcat

```
mariadb -u kitodo -D kitodo --password=kitodo < kitodo-4.sql
sudo chown tomcat10:tomcat10 kitodo-<version>.war
sudo mv kitodo-<version>.war /var/lib/tomcat10/webapps/kitodo.war
sudo service tomcat10 restart
until curl -s "localhost:8080/kitodo/pages/login.jsf" | grep -q -o "KITODO.PRODUCTION" ; do sleep 1; done
```

(When using MySQL, import the dump with the `mysql` client instead of `mariadb`.)

### Login

<http://localhost:8080/kitodo/pages/login>

* user: `testAdmin`
* pass: `test`

### Index the data

Menu System, tab *Indexing*:

1. start indexing of all object types (the index schema is created automatically by Hibernate Search on first use)
2. wait until indexing has finished
3. log out and log in again

## 4. Updates

### Download sources

```
rm -rf kitodo-production-main
wget https://github.com/kitodo/kitodo-production/archive/main.zip
unzip main.zip && rm main.zip
```

### Migrate the database

```
(cd kitodo-production-main/Kitodo-DataManagement && mvn flyway:migrate -Pflyway)
```

### Rebuild and deploy the WAR file

```
(cd kitodo-production-main/ && mvn clean install -B '-P!development')
sudo rm -f /usr/local/kitodo/modules/*
sudo cp kitodo-production-main/Kitodo/modules/*.jar /usr/local/kitodo/modules/
sudo chown -R tomcat10:tomcat10 /usr/local/kitodo
sudo rm -rf /var/lib/tomcat10/webapps/kitodo
sudo chown tomcat10:tomcat10 kitodo-production-main/Kitodo/target/kitodo-<version>.war
sudo mv kitodo-production-main/Kitodo/target/kitodo-<version>.war /var/lib/tomcat10/webapps/kitodo.war
sudo service tomcat10 restart
until curl -s "localhost:8080/kitodo/pages/login.jsf" | grep -q -o "KITODO.PRODUCTION" ; do sleep 1; done
```

Note: If the update provides new example data (e.g. new default diagrams or rulesets), it has to be copied from `kitodo-production-main/Kitodo/...` to `/usr/local/kitodo/...` manually.

If the update changes the search index schema, recreate the index via menu System, tab *Indexing*.
