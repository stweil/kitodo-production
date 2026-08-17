# How to create a developer workspace for Kitodo.Production with Eclipse on Windows

## Prerequisites

* [Java SE Development Kit (JDK) 21](https://adoptium.net/temurin/releases/)
* [Git](https://git-scm.com/downloads)
* [Maven](https://maven.apache.org/download.cgi)
* [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/)
* A database server: [MariaDB (recommended, see [Use MariaDB](use-mariadb.md) for the Hibernate/Flyway configuration)](https://mariadb.org/download/) or [MySQL 8.x](https://dev.mysql.com/downloads/mysql/)
* An [OpenSearch](https://opensearch.org/downloads/) 2.x server (for full-text search; except when using the [demo mode](demo.md))

## Configure the `PATH`s

Make sure the Maven `bin` directory is on your `PATH`, and that `JAVA_HOME` points to the root directory of the JDK (not the JRE). Right-click the start menu, select *System*, *Extended system settings*, *Environment variables* to do so. Be careful when you try to add an entry to the path: Click *New*, then type at least one character into the appearing text box before clicking *Browse…*. Otherwise, the last entry in the list will be replaced. You need to restart an open `cmd.exe` window for the changes to take effect.

Make sure the path of your Eclipse Workspace does not contain any dot. To check this, right-click on your Eclipse workspace folder in Windows Explorer, select Properties, Security, and examine the object name. If necessary, move your workspace or create a new one in a suitable location.

## Get the source code

If you have not done so yet, [fork](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo) the [project repository](https://github.com/kitodo/kitodo-production) on GitHub. Use Git (using a tool like [TortoiseGit](https://tortoisegit.org/), or the command line) to clone the repository as a subdirectory into your Eclipse workspace.

## Create the data-file structure

Create the data-file structure required by Kitodo.Production (outside the Eclipse workspace):

```
mkdir config debug diagrams logs metadata messages modules rulesets scripts temp users xslt
```

Copy all `kitodo_*.xml` from `Kitodo\src\main\resources\` into the `config` directory.

Copy `docket*.xsl` and the contents of `Kitodo\src\main\resources\xslt\` to the `xslt` directory.

Copy the contents of `Kitodo\rulesets\` to the `rulesets` directory.

Copy the contents of `Kitodo\diagrams\` to the `diagrams` directory.

Copy the contents of `Kitodo\scripts\` to the `scripts` directory.

## Create your `config-local`

In the source directory that you just cloned, create a folder named `config-local`. Copy the following files there:

*From `Kitodo\src\main\resources\`:*

* `hibernate.cfg.xml`
* `kitodo_config.properties`
* `log4j2.xml`

*From `Kitodo-DataManagement\src\main\resources\db\config\`:*

* `flyway.properties`

Edit the following parameters in your copy of `kitodo_config.properties` to point to the data directories you created. Use forward slashes as separators, and end the path entries with a forward slash:

* `directory.config=D:/path to/config/`
* `directory.rulesets=D:/path to/rulesets/`
* `directory.xslt=D:/path to/xslt/`
* `directory.metadata=D:/path to/metadata/`
* `directory.users=D:/path to/users/`
* `directory.temp=D:/path to/temp/`
* `directory.diagrams=D:/path to/diagrams/`
* `directory.modules=D:/path to/modules/`
* `directory.messages=D:/path to/messages/`
* `directory.debug=D:/path to/debug/`
* `script_createDirUserHome=D:/path to/scripts/script_createDirUserHome.bat`
* `script_createDirMeta=D:/path to/scripts/script_createDirMeta.bat`
* `script_createSymLink=D:/path to/scripts/script_createSymLink.bat`
* `script_deleteSymLink=D:/path to/scripts/script_deleteSymLink.bat`

Edit your copy of `log4j2.xml` so that the `<Property name="filename">` points to your `D:/path to/logs`.

Edit `hibernate.cfg.xml` so that `hibernate.connection.url`, `hibernate.connection.username` and `hibernate.connection.password` point to your database, and `hibernate.search.backend.hosts` points to your OpenSearch server.

## Run Maven

On the command line, change to the directory you just cloned and execute the following command:

```
mvn clean install
```

The `development` profile (active by default) reads `config-local/kitodo_config.properties` and copies the newly built module JARs into the directory configured as `directory.modules`.

## Set up the database

Create the database and the user (MariaDB or MySQL):

```
CREATE DATABASE kitodo;
CREATE USER 'kitodo'@'localhost' IDENTIFIED BY 'kitodo';
GRANT ALL ON kitodo.* TO 'kitodo'@'localhost';
FLUSH PRIVILEGES;
```

First load `schema.sql`, then `default.sql` from the folder `Kitodo\setup` into the database. There will be warnings because the scripts handle some cases for backward compatibility. You can safely ignore them.

Then, change into the subfolder `Kitodo-DataManagement` and execute the following command to migrate your database to the current schema version with Flyway:

```
mvn flyway:baseline -Pflyway
mvn flyway:migrate -Pflyway
```

## Exclude the Eclipse `.project` and runtime files from Git

* Go to the file system and find the `.git` folder in the project folder in your Eclipse workspace. By default, this folder is hidden, so you need to configure your Explorer to show hidden files and folders to see it.
* Find the `info` subfolder in that folder, or create it if it is missing.
* Find or create a file named `exclude` (no extension).
* List any files and directories you need to exclude in that file.
* Sometimes you need to restart Eclipse for changes to take effect.

Example content:

```
**/.classpath
**/.project
**/.settings
```

## Configure Eclipse

Eclipse's Maven import will create a lot of projects in your workspace which all need a bit of configuration, as listed below. You can simplify this by creating a new workspace and only configure the workspace settings once accordingly.

Add projects: In Eclipse, select *File*, *Import*, *Maven*, *Existing Maven project*. Point the *root directory* to your project directory and import all projects.

**Tomcat:** You have to increase the Tomcat startup time-out. You can do so from the server in the *Servers* view. If the Tomcat is not yet listed, select *Window*, *Preferences*, *Server*, *Runtime Environments* and add a Tomcat 10 there. Double-click the server in the *Servers* view, extend the time-out on the appearing page under *Timeouts*. You have to click *save* for the changes to take effect. I also recommend to open the *launch configuration* (from the same page), *Arguments*, and add `-Xmx3g` to *VM arguments*.

**Code formatter:** Import `config\Kitodo-IDE-formatting-Eclipse.xml` and set the code formatter to use the imported *Kitodo-Java Formatter Settings*.

**Imports:** The project has decided not to import packages, but only to explicitly import used classes, and to sort them alphabetically. Go to the Organize Imports page, remove all packages to be handled specially from the list, and set the both *Number of imports needed* values to something like `2147483647`.

**Checkstyle:** Get the Checkstyle plug-in and configure it to use the config file `config\checkstyle.xml`.

## Prepare the web application

Throughout all development, make sure your OpenSearch server is running (for example by starting `opensearch.bat` in the `bin` folder of the installation) and that your database server is running.

In Eclipse, right-click on the `Kitodo` project, select *Run As*, *Run on Server*, to launch Kitodo.Production in your Tomcat.

Access the web application under <http://localhost:8080/kitodo/pages/login> and log in as user `testAdmin` with password `test`.

From the navigation menu, select *System* and open the *Indexing* tab. Start indexing of all object types (the index schema is created automatically on first use). When this has finished, you have to log out and back into the web application.
