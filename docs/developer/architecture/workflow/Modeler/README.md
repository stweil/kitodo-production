# Modeler

Kitodo uses the standard BPMN 2.0 model with a custom extension. The extension allows users to add Kitodo specific properties to the diagram elements. The attributes are read from the XML by the classes in `org.kitodo.production.workflow.model.beans` (`KitodoTask`, `KitodoScriptTask`), which look them up with `task.getAttributeValueNs(NAMESPACE, ...)` using the namespace `http://www.kitodo.org/template`.

## Extension

### BPMN Process

The process element is extended by the attribute:

* `outputName` - default title of the templates / processes created from this diagram

### BPMN Task

The task element is extended by the following attributes:

| attribute | meaning |
|---|---|
| `priority` | priority of the task (integer) |
| `editType` | task edit type (integer, mapped to `TaskEditType`) |
| `processingStatus` | initial processing status (integer, mapped to `TaskStatus`) |
| `concurrent` | whether the task can be worked on in parallel (`true` / `false`) |
| `typeMetadata` | task handles metadata (`true` / `false`) |
| `typeAutomatic` | task is executed automatically (`true` / `false`) |
| `typeImagesRead` | task needs read access to the images (`true` / `false`) |
| `typeImagesWrite` | task needs write access to the images (`true` / `false`) |
| `typeGenerateImages` | task generates images (`true` / `false`) |
| `typeValidateImages` | task validates images (`true` / `false`) |
| `typeExportDMS` | task exports data to the DMS (`true` / `false`) |
| `typeAcceptClose` | task can be closed by the accept/close functionality (`true` / `false`) |
| `typeCloseVerify` | task is verified when closing the process (`true` / `false`) |
| `batchStep` | task is executed for batches (`true` / `false`) |
| `repeatOnCorrection` | task is opened again when the process is corrected (`true` / `false`) |
| `kitodoConditionType` | type of the condition for tasks behind a gateway (e.g. `XPath`) |
| `kitodoConditionValue` | value of the condition for tasks behind a gateway |
| `permittedUserRole` | the comma separated list of role IDs allowed to perform the task |

They are mapped to the corresponding columns of the `task` table.

### BPMN ScriptTask

The script task element is extended by the two additional attributes:

* `scriptName` - the name of the script to run
* `scriptPath` - the path to the script

## Storage

Diagrams are stored in the user local directory, which is defined in `kitodo_config.properties`:

```
directory.diagrams=/usr/local/kitodo/diagrams/
```

Diagram files are named `<title>.bpmn20.xml`.

Additionally there is a `workflow` table with the columns `title`, `status` (`DRAFT` or `ACTIVE`), `client` and `separateStructure`. Templates that use a given workflow are linked via the `templates` table.
