# How to run Kitodo.Production in demo mode

The demo mode builds Kitodo.Production and starts everything that is needed to try it out: a Tomcat 10 web server (downloaded and started automatically by Maven), an in-memory OpenSearch node, an in-memory H2 database pre-populated with example data.

No further services need to be installed.

## Prerequisites

* [Java SE Development Kit (JDK) 21](https://adoptium.net/temurin/releases/)
* [Git](https://git-scm.com/downloads)
* [Maven](https://maven.apache.org/download.cgi)

## Get the source code

If you have not done so yet, [fork](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo) the [project repository](https://github.com/kitodo/kitodo-production) on GitHub and use Git (using a tool like [TortoiseGit](https://tortoisegit.org/) or the command line) to clone the repository.

## Start the application

On the command line, change to the directory you just cloned and execute:

```
mvn clean install -Pdemo,!development
```

(on Windows command prompt, use `mvn clean install -Pdemo,!development` without quotes)

The command does the following:

1. builds all modules and the web application,
2. downloads and starts a Tomcat 10 instance and deploys the web application to it,
3. starts the in-memory OpenSearch node and the in-memory H2 database and inserts the example data.

After the log message `Kitodo is running now` has been printed, access the application at:

<http://localhost:8080/kitodo/pages/login>

and log in with:

* user: `kowal`
* password: `test`

Keep the command line window open: the Maven process stays in the foreground and holds the OpenSearch node and the database. To stop the application, terminate the Maven process (Ctrl + C in the command line window). Be aware that all data inserted during the demo run is lost when the application is stopped (it is stored in memory and in the `target` directory only).

Do not use the demo mode in a production system!

## Troubleshooting

* If a directory `config-local` exists in the repository root (for example because you configured a development instance), the configuration files in it take precedence over the demo configuration in the class path. If the demo then behaves unexpectedly (for example it connects to a different database or misses module code), move the directory aside or adjust it, and rebuild with `mvn clean ...`.
* If the build stops with an error that port 8080 is already in use, stop the other application using the port and start again.
