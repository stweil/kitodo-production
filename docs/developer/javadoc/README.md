# Javadoc

## Build Javadoc

1. Change to the root directory of your git clone.
2. Build the aggregated Javadoc for all modules in HTML format into the subdirectory `javadoc`:

```
mvn javadoc:aggregate -P generate_developer_docs -Ddoctarget=$(pwd) -Ddocdir=javadoc '-P!development'
```

(The `generate_developer_docs` profile configures the [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/aggregate-mojo.html); the build needs the same Java version as the project, see [Building](../gettingstarted/README.md).)

## Read Javadoc

Open `javadoc/index.html` and start at the package `org.kitodo`.
