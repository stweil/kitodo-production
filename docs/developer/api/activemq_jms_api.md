# ActiveMQ web services for Kitodo

## JMS

Apache [ActiveMQ](https://activemq.apache.org/) is an open source Java messaging (JMS) implementation provided by the Apache Software Foundation. It is intended to be used to connect software components in a flexible way. The core is the ActiveMQ server, which can be pictured like a post office. The mail boxes are named "queue" or "topic". Queues work as expected: a producer sends a message where a consumer can pick it up. Topics can be pictured like black boards: the main difference is that a message read from a queue is removed from the queue, while a message read from a topic is still available to others. Consumer clients can actively check the server or may register listeners with the server to be notified of new messages.

Kitodo.Production acts as a *consumer*: it picks up orders from the queues configured in `kitodo_config.properties`, executes them, and - if a result topic is configured - reports the outcome there.

## API implementation

The behaviour has already been implemented in Kitodo: `org.kitodo.production.interfaces.activemq.ActiveMQDirector` connects to the server configured in `activeMQ.hostURL` (the connection is opened asynchronously at application start by the `KitodoProduction` listener) and registers all consumers from its `services` variable.

The elements of this variable are classes extending the abstract class `ActiveMQProcessor`. This class implements the JMS `MessageListener` and provides facilities to handle exceptions and to store the consumer, which is required on shutdown to disconnect. The currently implemented processors are:

| class | queue parameter | purpose |
|---|---|---|
| `CreateNewProcessesProcessor` | `activeMQ.createNewProcesses.queue` | create new processes out of templates, optionally with data imported from catalogues |
| `FinalizeStepProcessor` | `activeMQ.finalizeStep.queue` | finalize (close) a task of a process, optionally setting properties and adding a comment |
| `TaskActionProcessor` | `activeMQ.taskAction.queue` | execute task actions (for example `COMMENT`, `ERROR_OPEN`, ...) |
| `KitodoScriptProcessor` | `activeMQ.kitodoScript.queue` | run Kitodo scripts (create folders, export, ...) on a set of processes |

To implement another web service processor, you have to implement a class which extends `ActiveMQProcessor` and implements its abstract `void process(MapMessageObjectReader)`. In this method do what your processor is intended to do; use the `MapMessageObjectReader` class to type-safely retrieve the payload from the incoming `MapMessage`. You must add your new class to the `services` variable of `ActiveMQDirector`.

The Kitodo server administrator is in control of which processors are started and which queue names they listen on. The implementing class passes its queue name to the constructor of the parent class, read from `kitodo_config.properties`. If the queue name is not configured, the constructor passes `null` to the parent class, which prevents the `ActiveMQDirector` from registering the processor to the server. Inside the class, the queue name is available in the global variable `queueName` which is set by the parent class.

### Service processor skeleton sample

```java
package org.kitodo.production.interfaces.activemq;

public class MyServiceProcessor extends ActiveMQProcessor {

    public MyServiceProcessor() {
        super(ConfigCore.getOptionalString(ParameterCore.MY_SERVICE_QUEUE).orElse(null));
    }

    @Override
    protected void process(MapMessageObjectReader ticket) throws ProcessorException, JMSException {
        // TODO: process the ticket
    }
}
```

### Processor response

Responses from processors are handled as `WebServiceResult` objects. Those objects are `MapMessage`s which send themselves to the topic configured in `activeMQ.results.topic`. They consist of the strings `queue` (the name of the queue the job ticket was sent to), `id` (a string "id" in the `MapMessage` which is mandatory), `level` and an optional `message`. When designing the `MapMessage` layout to parameterise your web service processor, keep in mind that a string element `id` is mandatory.

If `process()` terminates without error, it is meant to have done its job successfully and a result with the level `success` is sent. If `process()` throws an exception, a result with the level `fatal` is sent; the exception is returned as the `message` string. You may also use the `WebServiceResult` class to send messages with the other levels of `ReportLevel` (`error`, `warn`, `info`, `debug`, `verbose`), which are meant to be informative only:

```java
new WebServiceResult(queueName, ticket.getMandatoryString("id"), ReportLevel.INFO,
        "Remote host is down, trying again later.")
    .send();
```

## Create new processes service

Kitodo.Production is equipped with a web service interface to automatically create new processes based on a given process template. This allows the digitization process to be initiated from outside the application, for example by assigning a new digital ID to a record in a library catalogue and then running a script.

The web service infrastructure is provided by an ActiveMQ server, which needs to be downloaded and started. Without further configuration it provides everything necessary on port 61616 of the machine in question.

`activeMQ.hostURL` must be set in `kitodo_config.properties` to point to this server. `activeMQ.createNewProcesses.queue` must be set to a queue name (default example: `KitodoProduction.CreateNewProcesses.Queue`) from which Kitodo.Production picks up orders to create new processes.

Orders must be `javax.jms.MapMessage` objects with the following key-value pairs:

| key | type | meaning |
|---|---|---|
| `project` | `Integer` | **mandatory** - the id of the project the new process belongs to |
| `template` | `Integer` | **mandatory** - the id of the process template to use |
| `imports` | `List<Map<String,String>>` | optional - zero or more catalog imports; each map must contain `importconfiguration` (the id of an import configuration) and `value` (the search value in its default search field) |
| `title` | `String` | optional - the process title; if not given, the title is generated according to the configured rule |
| `parent` | `String` | optional - the process id (all digits) or title of a parent process; the new process is added as its last child |
| `metadata` | `Map<String,String>` | optional - additional metadata entries for the process |

If an import cannot find exactly one hit, the order fails with an error message.

## Finalize steps service

Kitodo.Production is equipped with a web service interface to automatically finalize tasks. This allows external software contributing to a workflow to report their success from outside the application. Additionally, properties can be set and a comment can be added to the process.

`activeMQ.finalizeStep.queue` must be set to a queue name (default example: `KitodoProduction.FinalizeStep.Queue`) from which Kitodo.Production picks up orders to finalize tasks.

Orders must be `javax.jms.MapMessage` objects with the following key-value pairs:

| key | type | meaning |
|---|---|---|
| `id` | `Integer` | **mandatory** - the id of the task (step) to close (do not mix up with the process id) |
| `properties` | `Map<String,String>` | optional - properties to set on the task |
| `message` | `String` | optional - a comment to add to the process |

## Task action service

`activeMQ.taskAction.queue` (default example: `KitodoProduction.TaskAction.Queue`) accepts orders to execute task actions:

| key | type | meaning |
|---|---|---|
| `id` | `Integer` | **mandatory** - the id of the task |
| `action` | `String` | **mandatory** - a `TaskAction`, for example `COMMENT` or `ERROR_OPEN` |
| `message` | `String` | as required by the action (for example the comment text) |
| `correctionTaskId` | `Integer` | for `ERROR_OPEN` - the id of a correction task |

## Kitodo script service

`activeMQ.kitodoScript.queue` (default example: `KitodoProduction.KitodoScript.Queue`) accepts orders to run Kitodo scripts:

| key | type | meaning |
|---|---|---|
| `script` | `String` | **mandatory** - the script, for example `script_createDirMeta <process path>` |
| `processes` | `Collection<Integer>` | the process ids the script is executed for |

Only the script commands named in the `activeMQ.kitodoScript.allow` parameter are authorized to be executed.
