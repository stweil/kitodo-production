# Jersey WebAPI (removed)

> **Removed:** The Jersey based REST API (`/rest`) was removed from Kitodo.Production. The web application no longer contains a Jersey servlet or JAX-RS resources; the current interface for external software is the [ActiveMQ JMS interface](activemq_jms_api.md).

The content below is kept for historical reference only. It described the API as it existed in Kitodo 2.x / early 3.x:

The Jersey based Webapi has been introduced to quickly query process status and
configuration via Web interface. It exposed three resources:
Processes, Projects and CatalogConfiguration under the URL root /rest.

Implementation and Configuration
--------------------------------

The web resources belonged to the `org.goobi.webapi` package and used standard Java
JAX-RS API annotations to denote resource routing. The Jersey Container servlet was
configured in the application's `web.xml` file:

    <servlet>
        <servlet-name>Kitodo REST Service based on Jersey</servlet-name>
        <servlet-class>com.sun.jersey.spi.container.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>com.sun.jersey.config.property.packages</param-name>
            <param-value>org.goobi.webapi.resources; org.goobi.webapi.provider</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>Kitodo REST Service based on Jersey</servlet-name>
        <url-pattern>/rest/*</url-pattern>
    </servlet-mapping>

* GET `/rest/processes` - list of all processes (identifier and title) as JSON or XML
* GET `/rest/processes/<Identifier>` - a single process
* GET `/rest/process/<Identifier>/steps` - the steps of a process and their current state
