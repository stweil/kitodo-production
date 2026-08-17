# Testing

Kitodo.Production uses [JUnit 5](https://junit.org/junit5/) for unit tests. Unit tests of all modules are run by the standard Maven build (`mvn clean install`); integration tests are run with the `all-tests` profile (Failsafe) and the Selenium tests with the `selenium` profile (see [Building](../gettingstarted/README.md)).

## Mutation testing

To measure the quality of the tests, the project uses the mutation testing framework [Pitest](https://pitest.org/).

Pitest runs your unit tests against automatically modified versions of your application code. When the application code changes, it should produce different results and cause the unit tests to fail. If a unit test does not fail in this situation, it may indicate an issue with the test suite.

The [pitest-maven](https://pitest.org/maven/) plugin (version managed by the `pitest.version` property in the root `pom.xml`) is already configured in the module POMs of `Kitodo-Command` and `Kitodo-DataEditor`, limited to the corresponding packages:

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>${pitest.version}</version>
    <configuration>
        <targetClasses>
            <param>org.kitodo.command*</param>
        </targetClasses>
        <targetTests>
            <param>org.kitodo.command*</param>
        </targetTests>
    </configuration>
</plugin>
```

To add mutation testing to another module, add the plugin to `build/plugins` of the module's `pom.xml` with `targetClasses` and `targetTests` for the packages you want to mutate:

```xml
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>${pitest.version}</version>
    <configuration>
        <targetClasses>
            <param>com.your.package.root.want.to.mutate*</param>
        </targetClasses>
        <targetTests>
            <param>com.your.package.root*</param>
        </targetTests>
    </configuration>
</plugin>
```

The mutation test can be run directly from the command line, for example in the `Kitodo-Command` module:

```
mvn -pl Kitodo-Command pitest:mutationCoverage
```

This outputs an HTML report to `target/pit-reports` in the module's target directory.
