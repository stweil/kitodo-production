# Create process out of template

## Copy process attributes

The attributes of the template (title, ruleset, project, ... and workpieces with their metadata and structure) are copied to the new process by `ProcessGenerator` (`org.kitodo.production.process.ProcessGenerator`).

## Copy tasks

All tasks of the template are copied to the new process (see `ProcessGenerator.copyTasks`). After the copy the tasks are ordered by their ordering number and title:

```java
public static void copyTasks(Template processTemplate, Process processCopy) {
    List<Task> tasks = new ArrayList<>();

    for (Task templateTask : processTemplate.getTasks()) {
        Task task = new Task(templateTask);
        task.setProcess(processCopy);
        tasks.add(task);
    }

    tasks.sort(Comparator.comparing(Task::getOrdering).thenComparing(Task::getTitle));
    processCopy.setTasks(tasks);
}
```

## Conditional task execution

Tasks which are behind a gateway in the workflow diagram carry a `WorkflowCondition` (see [Diagram template processing](../Diagram_template_processing/README.md)). Since all tasks are copied to the process, the condition is not used to select tasks but is evaluated at runtime, when the task is activated: `WorkflowControllerService.activateTask` checks `isWorkflowConditionFulfilled(process, condition)`.

* If the condition type is `NONE` or the condition evaluates to true, the task is opened and executed.
* If the condition evaluates to false, the task is closed immediately with the status *done* (it is skipped for this process) and the following tasks are activated.

The condition value can either be an XPath expression against the process' metadata file (`XPath`) or a script (`Script`); see `WorkflowConditionService` and `WorkflowControllerService`.
