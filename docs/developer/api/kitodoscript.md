# KitodoScript

KitodoScript is a small command language to perform a single action on a selection of processes, instead of processing them one by one. A script consists of an `action` parameter and, depending on the action, further parameters:

```
action:<command> [key:value ...]
```

Values containing spaces are enclosed in double quotes. Examples:

```
action:deleteData key:titleDocMain
action:exportDms exportImages:false
action:addData key:singleDigCollection "value:Drucke des 18. Jahrhunderts"
```

## Where KitodoScript is executed

* **Web UI**: in the process list, an action can be applied to the selected processes, to the processes of the current result page, or to the whole result set (see also `ProcessListViewKitodoScriptDialog`).
* **Workflow tasks**: a task configured as *script* task (a Camunda `ScriptTask`) is executed by `TaskService.executeScript`. If the script starts with `action:` it is interpreted as KitodoScript; otherwise it is executed as a shell command via `CommandService.runCommand`.
* **ActiveMQ**: the `KitodoScriptProcessor` (module `Kitodo`, package `org.kitodo.production.interfaces.activemq`) accepts scripts, see [ActiveMQ web services](activemq_jms_api.md).

All commands are implemented in `KitodoScriptService` (package `org.kitodo.production.services.command`).

## Metadata commands

The four metadata commands operate on the `meta.xml` file of each process. They share the parameters:

| Parameter | Meaning |
|---|---|
| `key:` | key of the metadata (as defined in the ruleset) |
| `value:` | literal value to add/overwrite (optional, values may contain spaces when quoted) |
| `source:` | take the value from another metadata with this key |
| `variable:` | take the value from a variable (e.g. `variable:(processid)`) |

* `action:addData` - add metadata value(s) to each process (existing values with the same key are kept)
* `action:overwriteData` - replace the value(s) of the metadata with the given key
* `action:deleteData` - delete metadata with the given key (optionally only when the value matches)
* `action:copyDataToChildren` - copy the given metadata from each process to its child processes

Examples:

```
action:addData key:singleDigCollection "value:Drucke des 18. Jahrhunderts"
action:addData key:PublicationYear source:PublicationYearSorting
action:addData key:KitodoID variable:(processid)
action:deleteData key:titleDocMain
action:overwriteData key:titleDocMain "value:neuerTitel"
```

## Process and workflow commands

| Command | Parameters | Effect |
|---|---|---|
| `action:exportDms` (alias `action:export`) | `exportImages:true\|false` | export the processes into the DMS, optionally without the images |
| `action:doit`, `action:doit2` | - | DMS export of the metadata only (same as `exportDms exportImages:false`) |
| `action:deleteProcess` | `contentOnly:true\|false` | by default delete only the process content (images, OCR); `false` deletes the whole process (parent processes with children are skipped) |
| `action:generateImages` | `folders:` (comma separated list of folder paths or `all`), `images:missing` or `images:missingordamaged` | generate the derivative images for the given folders from the project's generator source folder |
| `action:importProcesses` | `indir:`, `project:`, `template:`, `errors:` | import processes from a directory of metadata files; executed in the task manager |
| `action:importFromFileSystem` | `sourcefolder:` | read the images from the given source folder into the processes |
| `action:createFolders` | - | create missing process folders |
| `action:deleteTiffHeaderFile` | - | remove the `TiffStatus` / header files from the processes |
| `action:updateContentFiles` | - | rewrite the content files from the metadata |
| `action:resaveMetadataFile` | - | resave the `meta.xml` file of each process |
| `action:searchForMedia` | - | reconcile the media referenced in the metadata with the files on disk |
| `action:addRole` | `role:` | assign the given role to the processes |
| `action:setRuleset` | `ruleset:` (title) | assign the given ruleset to the processes |
| `action:setImportConfiguration` | `id:` (numeric id of the import configuration object) | assign the given import configuration to the processes |
| `action:setStepStatus` | `tasktitle:`, `status:` | set the status of the task with the given title |
| `action:setTaskProperty` | `tasktitle:`, `property:`, `value:true\|false` | change a boolean property of the task (`metadata`, `automatic`, `batch`, `readimages`, `writeimages`, `validate`, `exportdms`) |
| `action:addShellScriptToStep` | `tasktitle:`, `script:` (path), `label:` (name) | assign a shell script to the task with the given title |
| `action:runscript` | `stepname:` (task title), optionally `script:` (must match the task's script path) | execute the script of the task with the given title in each process |

## Notes

* The commands that a given web UI offers may be a subset of the commands available via scripts and ActiveMQ.
* Some actions (notably `exportDms` and `deleteProcess`) take a long time; they are executed synchronously, so consider smaller selections.
* Old documentation (from the Kitodo 2.x/3.x era) listed commands such as `addUser`, `addUserGroup`, `setProgress`, `setPriority` and `addTag`; those are no longer part of `KitodoScriptService`.
