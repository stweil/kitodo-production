# System architecture

This documentation is work in progress. Please consult the [javadoc](../javadoc/README.md) or [contact the developer team](https://maillist.slub-dresden.de/cgi-bin/mailman/listinfo/kitodo-developer).

Below you will find some fragments regarding the system architecture of Kitodo.production.

## Overview

Kitodo.Production is a Java web application (WAR) that runs on an Apache Tomcat 10 Servlet container. In a simplified view, the layers are:

* **Frontend**: server-side pages written in [Jakarta Server Faces (JSF)](https://jakarta.ee/specifications/server-pages/) with [PrimeFaces](https://primefaces.org/) components. The backing beans are the *forms* in `org.kitodo.production.forms`.
* **Business logic**: services in `org.kitodo.production.services`, collected by the central `ServiceManager` (e.g. `ProcessService`, `UserService`, `MetadataService`).
* **Data layer**: JPA entities in `org.kitodo.data.database.beans` (module `Kitodo-DataManagement`), persisted with [Hibernate ORM](https://hibernate.org/orm/) to a MySQL/MariaDB database and mirrored for full-text search into an [OpenSearch](https://opensearch.org/) index managed by [Hibernate Search](https://hibernate.org/search/).
* **External integration**: [ActiveMQ](https://activemq.apache.org/) JMS services (see [ActiveMQ web services](../api/activemq_jms_api.md)).

## Data types

### Entity

Entities are JPA entity classes in `org.kitodo.data.database.beans` that are used for communication with the database. The abstract class `BaseBean` provides the primary key `id` and helpers to initialize lazy associations. Entities that belong to a template and its copies (e.g. `Template`, `Process`) additionally extend `BaseTemplateBean`.

```uml
[BaseBean] <|-- [BaseTemplateBean]
[BaseTemplateBean] <|-- [Template]
[BaseTemplateBean] <|-- [Process]
[BaseBean] <|-- [User]
[BaseBean] <|-- [Project]
[BaseBean] <|-- [Docket]
[BaseBean] <|-- [Client]
```

All entity classes are plain Java classes annotated with `@Entity`; persistence and the object-relational mapping are configured by JPA annotations and by `hibernate.cfg.xml`.

### Search index

Full-text search is implemented by [Hibernate Search](https://hibernate.org/search/) with the Elasticsearch backend, which speaks the HTTP API of an [OpenSearch](https://opensearch.org/) server (or of an Elasticsearch server). Hibernate Search keeps the index schema and the index documents in sync with the entity model; there is no separate document type per entity and no manual JSON handling in the application code.

* Which entities are indexed and how their fields are mapped is declared on the entity class itself with the `@Indexed` annotation and the `org.hibernate.search.mapper.pojo.mapping.definition.annotation` annotations. For example `Process` is annotated with `@Indexed(index = "kitodo-process")`.
* The search backend is configured in `hibernate.cfg.xml`:

```xml
<property name="hibernate.search.enabled">true</property>
<property name="hibernate.search.backend.type">elasticsearch</property>
<property name="hibernate.search.backend.hosts">localhost:9200</property>
<property name="hibernate.search.backend.protocol">http</property>
```

* Index names are defined per entity (for example `kitodo` for processes, as in `@Indexed(index = "kitodo-process")`); an optional prefix can be set with the `searchindex.prefix` parameter in `kitodo_config.properties`.
* Indexes are created, dropped and re-synced (reindexed) from the web application: menu *System*, tab *Indexing*.

### Transfer objects

Earlier versions of Kitodo used a layer of data transfer objects (DTOs) between the search index and the frontend. This layer was removed: the forms and services work with the entity classes and with the search results provided by Hibernate Search. The only remaining transfer object is `org.kitodo.production.dto.ProcessExportDTO`, which is used for process export.

## Modification of data

Scenario: a new column is needed in the database.

1. Add the new attribute (with getter and setter) to the entity class.
2. Write a [Flyway](https://flywaydb.org/) migration for the new column: add a new versioned SQL file to `Kitodo-DataManagement/src/main/resources/db/migration/` (for example `V2_103__Add_new_column.sql`).
3. If the attribute shall be searchable, add the corresponding mapping annotations (for example `@FullTextField`) to the entity class.
4. Adjust or add tests.
5. Adjust the frontend (form/page) where the new attribute is used.
6. Run the application and, if the index schema changed, recreate the index (via menu *System*, tab *Indexing*: delete the index, then start indexing again).

When deploying updates to an existing database, run the Flyway migrations (see [Build development version](../gettingstarted/development-version.md)).

## File management

All file access goes through the `FileManagementInterface` in the `Kitodo-API` module (`org.kitodo.api.filemanagement.FileManagementInterface`). Files are addressed by URI, not by file system path, which allows the process directories to be mapped to arbitrary backends:

* `create(URI parentFolder, String name, boolean file)` - create a file or folder
* `read(URI)` / `write(URI)` - retrieve an input stream / write an output stream
* `copy(URI source, URI target)` / `move(URI source, URI target)` / `rename(URI, String)`
* `delete(URI)`, `getNumberOfFiles(...)`, `getSize(URI)`

The concrete implementation is loaded with the Kitodo service loader (`KitodoServiceLoader`); the shipped local file system implementation is `org.kitodo.filemanagement.FileManagement`. File URIs are mapped against the data directory: relative URIs are resolved below the Kitodo data directory (`directory.metadata` in `kitodo_config.properties`) by the `FileMapper` class.
