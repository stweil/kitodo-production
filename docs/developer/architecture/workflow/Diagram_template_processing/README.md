# Diagram - template processing

## Camunda BPMN model library

Kitodo uses the [Camunda BPMN model API](https://docs.camunda.org/manual/latest/model-apis/bpmn-model-api/) for reading BPMN diagrams as processes:

```java
public Reader(String diagramName) throws IOException {
    String diagramPath = ConfigCore.getKitodoDiagramDirectory() + diagramName + ".bpmn20.xml";
    loadProcess(ServiceManager.getFileService().read(Paths.get(diagramPath).toUri()));
}

private void loadProcess(InputStream diagramXmlContent) throws IOException {
    modelInstance = Bpmn.readModelFromStream(diagramXmlContent);
}
```

The diagram is iterated element by element (`Reader.readWorkflowTasks()`); for every task element the custom XML attributes in the namespace `http://www.kitodo.org/template` are read into the model beans `KitodoTask` and `KitodoScriptTask`:

```java
static final String NAMESPACE = "http://www.kitodo.org/template";
...
task.getAttributeValueNs(NAMESPACE, "typeMetadata");
```

The `Converter` class turns the model into database beans: `convertWorkflowToTemplate(template)` fills a `Template` with its `Task` objects (title, ordering, edit type, types, permissions, ...). Tasks behind a gateway carry a `WorkflowCondition` which is persisted to the `workflowcondition` table. The ordering of the tasks is derived from the sequence of the diagram: tasks on different branches after a gateway get the same ordering number.

## Database

The `template` table references the `workflow` table (a template can be created out of a workflow diagram), and the `task` table has a foreign key `workflowCondition_id` to the `workflowcondition` table, which stores the conditions defined in the diagram gateways.
