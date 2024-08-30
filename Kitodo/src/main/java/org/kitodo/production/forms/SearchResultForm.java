/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.kitodo.production.forms;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.enums.TaskStatus;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.production.dto.ProcessDTO;
import org.kitodo.production.dto.ProjectDTO;
import org.kitodo.production.dto.TaskDTO;
import org.kitodo.production.enums.ObjectType;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.ProcessService;
import org.kitodo.production.services.workflow.WorkflowControllerService;
import org.primefaces.PrimeFaces;

@Named("SearchResultForm")
@SessionScoped
public class SearchResultForm extends ProcessListBaseView {
    private static final Logger logger = LogManager.getLogger(SearchResultForm.class);

    private static final String SEARCH_RESULT_VIEW_ID = "/pages/searchResult.xhtml";
    private static final String SEARCH_RESULT_TABLE_ID = "searchResultTabView:searchResultForm:searchResultTable";

    private List<ProcessDTO> filteredList = new ArrayList<>();
    private List<ProcessDTO> resultList = new ArrayList<>();
    private String searchQuery;
    private String currentTaskFilter;
    private Integer currentProjectFilter;
    private Integer currentTaskStatusFilter;
    private final String searchResultListPath = MessageFormat.format(REDIRECT_PATH, "searchResult");
    private final WorkflowControllerService workflowControllerService = new WorkflowControllerService();

    /**
     * Searches for processes with the entered searchQuery.
     *
     * @return The searchResultPage
     */
    public String searchForProcessesBySearchQuery() {
        ProcessService processService = ServiceManager.getProcessService();
        HashMap<Integer, ProcessDTO> resultHash = new HashMap<>();
        List<ProcessDTO> results;
        try {
            results = processService.findByAnything(searchQuery);
            for (ProcessDTO processDTO : results) {
                resultHash.put(processDTO.getId(), processDTO);
            }
            this.resultList = new ArrayList<>(resultHash.values());
            refreshFilteredList();
        } catch (DataException e) {
            Helper.setErrorMessage("errorOnSearch", searchQuery);
            return this.stayOnCurrentPage;
        }
        setCurrentTaskStatusFilter(null);
        setCurrentProjectFilter(null);
        setCurrentTaskFilter(null);
        resetSearchResultTableViewState();
        return searchResultListPath;
    }

    /**
     * Filters the searchResults by project.
     */
    void filterListByProject() {
        if (Objects.nonNull(currentProjectFilter)) {
            this.filteredList.removeIf(result -> !result.getProject().getId()
                    .equals(currentProjectFilter));
        }
    }

    /**
     * Filters the searchResults by task and status.
     */
    void filterListByTaskAndStatus() {
        if (Objects.nonNull(currentTaskFilter) && Objects.nonNull(currentTaskStatusFilter)) {
            for (ProcessDTO processDTO : new ArrayList<>(this.filteredList)) {
                boolean remove = true;
                for (TaskDTO task : processDTO.getTasks()) {
                    if (task.getTitle().equalsIgnoreCase(currentTaskFilter)
                            && task.getProcessingStatus().getValue().equals(currentTaskStatusFilter)) {
                        remove = false;
                        break;
                    }
                }
                if (remove) {
                    this.filteredList.remove(processDTO);
                }

            }
        }
    }

    /**
     * Filters the searchResultList by the selected filters.
     */
    public void filterList() {
        refreshFilteredList();

        filterListByProject();
        filterListByTaskAndStatus();
    }

    /**
     * Get all Projects assigned to the search results.
     *
     * @return A list of projects for filter list
     */
    public Collection<ProjectDTO> getProjectsForFiltering() {
        HashMap<Integer, ProjectDTO> projectsForFiltering = new HashMap<>();
        for (ProcessDTO process : this.resultList) {
            projectsForFiltering.put(process.getProject().getId(), process.getProject());
        }
        return projectsForFiltering.values();
    }

    /**
     * Get all current Tasks from to the search results.
     *
     * @return A list of tasks for filter list
     */
    public Collection<TaskDTO> getTasksForFiltering() {
        HashMap<String, TaskDTO> tasksForFiltering = new HashMap<>();
        for (ProcessDTO processDTO : this.resultList) {
            for (TaskDTO currentTask : processDTO.getTasks()) {
                tasksForFiltering.put(currentTask.getTitle(), currentTask);
            }
        }
        return tasksForFiltering.values();
    }

    /**
     * Get the values of taskStatus.
     * @return a list of status
     */
    public Collection<TaskStatus> getTaskStatusForFiltering() {
        return Arrays.asList(TaskStatus.values());
    }

    private void refreshFilteredList() {
        this.filteredList.clear();
        this.filteredList.addAll(this.resultList);
    }

    /**
     * Delete Process.
     *
     * @param processDTO
     *            process to delete.
     */
    @Override
    public void delete(ProcessDTO processDTO) {
        try {
            Process process = ServiceManager.getProcessService().getById(processDTO.getId());
            if (process.getChildren().isEmpty()) {
                try {
                    ProcessService.deleteProcess(process);
                    this.filteredList.remove(processDTO);
                } catch (DataException | IOException e) {
                    Helper.setErrorMessage(ERROR_DELETING, new Object[] {ObjectType.PROCESS.getTranslationSingular() },
                            logger, e);
                }
            } else {
                this.deleteProcessDialog = new DeleteProcessDialog();
                this.deleteProcessDialog.setProcess(process);
                PrimeFaces.current().executeScript("PF('deleteChildrenDialog').show();");
            }
        } catch (DAOException e) {
            Helper.setErrorMessage(ERROR_DELETING, new Object[] {ObjectType.PROCESS.getTranslationSingular() }, logger,
                    e);
        }
    }

    /**
     * Set up processing status selection.
     */
    public void setTaskStatusUpForSelection() {
        this.workflowControllerService.setTaskStatusUpForProcesses(this.getSelectedProcesses());
    }

    /**
     * Set down processing status selection.
     */
    public void setTaskStatusDownForSelection() {
        this.workflowControllerService.setTaskStatusDownForProcesses(this.getSelectedProcesses());
    }

    /**
     * Gets the filtered list.
     *
     * @return a list of ProcessDTO
     */
    public List<ProcessDTO> getFilteredList() {
        return this.filteredList;
    }

    /**
     * Sets the filtered list.
     *
     * @param filteredList
     *            a list of ProcessDTO
     */
    public void setFilteredList(List<ProcessDTO> filteredList) {
        this.filteredList = filteredList;
    }

    /**
     * Get currentTaskStatusFilter.
     *
     * @return value of currentTaskStatusFilter
     */
    public Integer getCurrentTaskStatusFilter() {
        return currentTaskStatusFilter;
    }

    /**
     * Set currentTaskStatusFilter.
     *
     * @param currentTaskStatusFilter as java.lang.Integer
     */
    public void setCurrentTaskStatusFilter(Integer currentTaskStatusFilter) {
        this.currentTaskStatusFilter = currentTaskStatusFilter;
    }

    /**
     * Gets the search query.
     *
     * @return the search query
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Sets the searchQuery.
     *
     * @param searchQuery
     *            the query to search for
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * Gets the current task filter.
     *
     * @return a List of Task title to filter
     */
    public String getCurrentTaskFilter() {
        return currentTaskFilter;
    }

    /**
     * Sets the current task filter.
     *
     * @param currentTaskFilter
     *            the task title to filter
     */
    public void setCurrentTaskFilter(String currentTaskFilter) {
        this.currentTaskFilter = currentTaskFilter;
    }

    /**
     * Gets the current project filter.
     *
     * @return a List of project id to filter
     */
    public Integer getCurrentProjectFilter() {
        return currentProjectFilter;
    }

    /**
     * Sets the current project filter.
     *
     * @param currentProjectFilter
     *            the project id to filter
     */
    public void setCurrentProjectFilter(Integer currentProjectFilter) {
        this.currentProjectFilter = currentProjectFilter;
    }

    /**
     * This method resets the view state of the search result table, which was
     * enabled by adding "multiViewState=true" to the DataTable component.
     * 
     * <p>This affects both the pagination state and the sort order of 
     * the table. It should be called whenever a new search is triggered such 
     * that the user is presented with the most relevant results (page 1) 
     * in the default order.</p>
     */
    public void resetSearchResultTableViewState() {
        if (!Objects.isNull(FacesContext.getCurrentInstance())) {
            // clear multi view state only if there is a state available
            Object mvs = PrimeFaces.current().multiViewState().get(SEARCH_RESULT_VIEW_ID, SEARCH_RESULT_TABLE_ID, false, null);
            if (Objects.nonNull(mvs)) {
                PrimeFaces.current().multiViewState().clear(SEARCH_RESULT_VIEW_ID, SEARCH_RESULT_TABLE_ID);
            }
        }
    }

}
