# Building

Kitodo.Production is written in Java 21 and uses the [Jakarta Server Faces](https://jakarta.ee/specifications/server-pages/) web technology to run on a [Tomcat 10 Servlet container](https://tomcat.apache.org/). It is backed by a [MariaDB](https://mariadb.org/) (or [MySQL](https://www.mysql.com/)) database, accessed through the [Hibernate](https://hibernate.org/orm/) Object-Relational Mapping framework, and by an [OpenSearch](https://opensearch.org/) server for full-text search, accessed through [Hibernate Search](https://hibernate.org/search/).

The project structure is IDE independent. The tool for building, testing and packaging the application is [Apache Maven](https://maven.apache.org/).

## Prerequisites

* [Java SE Development Kit (JDK) 21](https://adoptium.net/temurin/releases/) (e.g. [Eclipse Temurin](https://adoptium.net/temurin/releases/))
* [Apache Maven](https://maven.apache.org/download.cgi)
* For local database development: [MariaDB](https://mariadb.org/download/) (recommended) or [MySQL 8.x](https://dev.mysql.com/downloads/mysql/)
* For full-text search: an [OpenSearch](https://opensearch.org/downloads/) 2.x server (except for the [demo mode](demo.md), which runs an embedded in-memory OpenSearch node)

## Modules

The build is a Maven multi-module project consisting of the following modules:

| Module | Purpose |
|---|---|
| `Kitodo-API` | API for plugins and modules of other Kitodo components |
| `Kitodo-DataManagement` | database beans, DAOs and the search index layer (Flyway migrations live here) |
| `Kitodo-Command` | command line interface to Kitodo.Production |
| `Kitodo-Query-URL-Import` | query URL based import of external data |
| `Kitodo-FileManagement` | file access and path mapping for process directories |
| `Kitodo-DataEditor` | structure- and metadata editor support |
| `Kitodo-DataFormat` | reading and writing METS, MODS and other metadata formats |
| `Kitodo-ImageManagement` | image processing (ImageMagick, jHOVE) |
| `Kitodo-LongTermPreservationValidation` | long term preservation validation (METS-LOM checks) |
| `Kitodo-PersistentIdentifier` | persistent identifier generation (URN) |
| `Kitodo-Docket` | docket (process slip) rendering |
| `Kitodo-XML-SchemaConverter` | conversion between XML schemas / metadata formats |
| `Kitodo-Validation` | metadata validation |
| `Kitodo` | the web application itself (WAR, Prime Faces UI) |

## Building

All dependencies are fetched from Maven Central.

Build the whole project (compile, unit tests, WAR, module JARs):

```
mvn clean install
```

(with a local configuration in `config-local/`, see [Local configuration](#local-configuration-config-local) below)

The [development profile](#profiles) is active by default. It expects a local configuration in `config-local/` (see below); use `-P '!development'` to build without one:

```
mvn clean install -P '!development'
```

During the build each module JAR is written to `Kitodo/modules/` with the `module` classifier, and the web application is packaged as `Kitodo/target/kitodo-<version>.war`.

### Profiles

| Profile | Effect |
|---|---|
| `development` (default) | reads `config-local/kitodo_config.properties` and copies the module JARs from `Kitodo/modules/` to the directory configured as `directory.modules` after the build |
| `demo` | starts an all-in-one demo environment (see [Build integrated demo](demo.md)) |
| `selenium` | starts Tomcat and the demo environment and runs the Selenium integration tests |
| `all-tests` | additionally runs the integration tests (Failsafe) of all modules |
| `checkstyle` | enforces the [Checkstyle](https://checkstyle.org/) rules from [`config/checkstyle.xml`](https://github.com/kitodo/kitodo-production/blob/main/config/checkstyle.xml) |
| `spotbugs` | runs [SpotBugs](https://spotbugs.github.io/) |
| `flyway` | runs Flyway baseline and migration against the database configured in `flyway.properties` |
| `generate_developer_docs` | builds the aggregated Javadoc (see [Javadoc](../javadoc/README.md)) |

The [build on GitHub Actions](https://github.com/kitodo/kitodo-production/blob/main/.github/workflows/main.yml) runs:

```
mvn clean install -B '-Pall-tests,flyway,checkstyle,!development'
```

## Local configuration (`config-local`)

Basic configuration files ship in the `Kitodo/src/main/resources/` directory. To provide a custom (local) configuration, create a directory `config-local` in the repository root and put your specific configuration files there. The directory `config-local` is listed last in the class path of the built WAR (after the bundled resources), so every default configuration file that is present in `config-local` replaces the bundled default.

The `development` profile (active by default) **requires** `config-local/kitodo_config.properties` to exist and reads it during the build (in the `initialize` phase). A fresh clone therefore does not build until you create that file, or you have to build with `-P '!development'`.

Most probably you will have to adjust these files:

* `kitodo_config.properties` - main configuration (data directories, ActiveMQ, catalogues, ...)
* `hibernate.cfg.xml` - database and search index connection (URL, user, password)
* `log4j2.xml` - logging configuration (e.g. the log file location in `<Property name="filename">`)
* `flyway.properties` - only when migrating the database with Flyway (`Kitodo-DataManagement/src/main/resources/db/config/flyway.properties`)

### Data directories

The application reads and writes its data in a set of directories (configured in `kitodo_config.properties`, all defaulting to `/usr/local/kitodo/...`). When setting up a local instance, create these directories:

```
config
debug
diagrams
logs
metadata
messages
modules
rulesets
temp
users
xslt
```

and copy the following files into them:

* all `kitodo_*.xml` files from `Kitodo/src/main/resources/` into `config`
* `docket*.xsl` from `Kitodo/src/main/resources/` into `xslt`
* the contents of `Kitodo/rulesets/` into `rulesets`
* the contents of `Kitodo/diagrams/` into `diagrams`
* the contents of `Kitodo/scripts/` into a `scripts` directory (adjust the `script_*` parameters in `kitodo_config.properties` accordingly)

### Adjust `kitodo_config.properties`

Point the `directory.*` and `script_*` parameters to the directories you created, using forward slashes and a trailing slash for directory entries:

```
directory.config=/path/to/config/
directory.rulesets=/path/to/rulesets/
directory.xslt=/path/to/xslt/
directory.metadata=/path/to/metadata/
directory.users=/path/to/users/
directory.temp=/path/to/temp/
directory.diagrams=/path/to/diagrams/
directory.modules=/path/to/modules/
directory.debug=/path/to/debug/
directory.messages=/path/to/messages/
script_createDirUserHome=/path/to/scripts/script_createDirUserHome.sh
script_createDirMeta=/path/to/scripts/script_createDirMeta.sh
script_createSymLink=/path/to/scripts/script_createSymLink.sh
script_deleteSymLink=/path/to/scripts/script_deleteSymLink.sh
```

## Further reading

* [Build integrated demo](demo.md)
* [Build development version](development-version.md)
* [Eclipse on Windows](eclipse-windows.md)
* [Create VirtualBox Appliance](virtualbox.md)
* [Use MariaDB (recommended)](use-mariadb.md)
* [Securing access to ActiveMQ](use-secured-activemq.md)

Setting up a Kitodo instance can be quite tricky. For more help on how to configure Kitodo, please check the [GitHub Wiki](https://github.com/kitodo/kitodo-production/wiki) or ask questions on the [mailing lists](https://github.com/kitodo/kitodo-production/wiki#Mailingliste).
