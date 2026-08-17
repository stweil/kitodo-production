# Use MariaDB instead of MySQL

Kitodo.Production runs on MariaDB out of the box (the MariaDB JDBC driver is part of the build). To use a MariaDB server instead of a MySQL server, adjust the Hibernate and Flyway configuration.

## Hibernate configuration

Modifications must be made in `hibernate.cfg.xml`.

### hibernate.connection.url

Instead of `jdbc:mysql://...` use:

```
jdbc:mariadb://localhost/kitodo
```

### hibernate.connection.driver_class

Optional. The driver class is detected from the connection URL in most cases; when it is set explicitly, use `org.mariadb.jdbc.Driver` instead of `com.mysql.cj.jdbc.Driver`.

### hibernate.dialect

Optional. Hibernate detects the dialect from the database connection in most cases. If you need to set it explicitly, use `org.hibernate.dialect.MariaDBDialect` (see the [Hibernate docs on dialects](https://docs.jboss.org/hibernate/orm/6.4/reference/en-US/html_single/#appendix-dialects)).

## Flyway configuration

Only needed if you want to migrate your database with the help of Flyway. Modifications must be made in `flyway.properties` (see `Kitodo-DataManagement/src/main/resources/db/config/flyway.properties`).

### flyway.url

Instead of `jdbc:mysql://...` use `jdbc:mariadb://...`.
