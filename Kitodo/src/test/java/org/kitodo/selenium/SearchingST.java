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

package org.kitodo.selenium;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Browser;
import org.kitodo.selenium.testframework.Pages;
import org.kitodo.selenium.testframework.pages.DesktopPage;
import org.kitodo.selenium.testframework.pages.ExtendedSearchPage;
import org.kitodo.selenium.testframework.pages.ProcessesPage;
import org.kitodo.selenium.testframework.pages.SearchResultPage;
import org.kitodo.selenium.testframework.pages.TasksPage;
import org.openqa.selenium.WebElement;

public class SearchingST extends BaseTestSelenium {

    private static final String WAIT_FOR_FILTER_IS_APPLIED = "Wait until filter is applied";

    private static DesktopPage desktopPage;
    private static SearchResultPage searchResultPage;
    private static ExtendedSearchPage extendedSearchPage;
    private static ProcessesPage processesPage;
    private static TasksPage tasksPage;

    @BeforeClass
    public static void setup() throws Exception {
        desktopPage = Pages.getDesktopPage();
        searchResultPage = Pages.getSearchResultPage();
        extendedSearchPage = Pages.getExtendedSearchPage();
        processesPage = Pages.getProcessesPage();
        tasksPage = Pages.getTasksPage();
    }

    @Before
    public void login() throws Exception {
        Pages.getLoginPage().goTo().performLoginAsAdmin();
    }

    /**
     * Logout after every test.
     *
     * @throws Exception
     *             if topNavigationElement is not found
     */
    @After
    public void logout() throws Exception {
        Pages.getTopNavigation().logout();
        if (Browser.isAlertPresent()) {
            Browser.getDriver().switchTo().alert().accept();
        }
    }

    @Test
    public void searchForProcesses() throws Exception {
        desktopPage.searchInSearchField("process");
        int numberOfResults = searchResultPage.getNumberOfResults();
        assertEquals("There should be two processes found", 2, numberOfResults);

        searchResultPage.searchInSearchField("Second");
        await("Wait for visible search results").atMost(20, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(
            () -> assertEquals("There should be two processes found", 2, searchResultPage.getNumberOfResults()));

        searchResultPage.searchInSearchField("möhö");
        await("Wait for visible search results").atMost(20, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(
            () -> assertEquals("There should be no processes found", 0, searchResultPage.getNumberOfResults()));
    }

    @Test
    public void searchForProcessesAndSort() throws Exception {
        desktopPage.searchInSearchField("process");
        searchResultPage.clickTitleColumnForSorting();

        assertEquals("First process should be top result when sorting by title", "First process", 
            searchResultPage.getFirstSearchResultProcessTitle());

        searchResultPage.clickTitleColumnForSorting();

        assertEquals("Second process should be top result when reverse-sorting by title", "Second process", 
            searchResultPage.getFirstSearchResultProcessTitle());
    }

    @Test
    public void testExtendedSearch() throws Exception {
        processesPage.goTo();
        processesPage.navigateToExtendedSearch();
        SearchingST.extendedSearchPage.searchById("2");
        await("Wait for visible search results").atMost(20, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(
            () -> assertEquals("There should be one processes found", 1, processesPage.countListedProcesses()));
        List<String> processTitles = processesPage.getProcessTitles();
        assertEquals("Wrong process found", "Second process", processTitles.get(0));

        processesPage.navigateToExtendedSearch();
        processesPage = SearchingST.extendedSearchPage.seachByTaskStatus();
        await("Wait for visible search results").atMost(20, TimeUnit.SECONDS).ignoreExceptions().untilAsserted(
                () -> assertEquals("There should be one process found", 1, processesPage.countListedProcesses()));
        processTitles = processesPage.getProcessTitles();
        assertEquals("Wrong process found", "Second process", processTitles.get(0));
    }

    /**
     * Checks that a case insensitive search for process titles works.
     */
    @Test
    public void caseInsensitiveSearchForProcesses() throws Exception {
        desktopPage.searchInSearchField("PrOCeSs");
        assertEquals("Two processes should match case-insensitive search", 2, searchResultPage.getNumberOfResults());
    }

    /**
     * Checks that a case insensitive filter for task status works.
     */
    @Test
    public void caseInsensitiveFilterTaskStatus() throws Exception {
        processesPage.goTo();
        processesPage.applyFilter("\"stepinwork:pRoGrEsS\"");

        await(WAIT_FOR_FILTER_IS_APPLIED)
            .pollDelay(100, TimeUnit.MILLISECONDS)
            .atMost(10, TimeUnit.SECONDS).ignoreExceptions()
            .untilAsserted(() -> {
                List<String> processTitles = processesPage.getProcessTitles();
                assertEquals("Case insensitive filter should match only one process", 1, processTitles.size());
                assertEquals("Case insensitive filter should match \"First process\"", "First process", 
                    processTitles.get(0));
            });
    }

    /**
     * Checks whether adding and removing filters work on the processes page.
     */
    @Test
    public void addAndRemoveFilters() throws Exception {
        processesPage.goTo();
        processesPage.applyFilter("\"id:to be removed\"");
        processesPage.applyFilter("\"project:Example Project\"");
        assertEquals("Number of parsed filters does not match", 2, processesPage.getParsedFilters().size());

        processesPage.removeParsedFilter(0);
        assertEquals("Number of parsed filters does not match after removing filter", 1,
            processesPage.getParsedFilters().size());
    }

    /**
     * Checks whether suggestions and filtering work on the tasks page.
     */
    @Test
    public void filterTasksTest() throws Exception {
        tasksPage.goTo();

        tasksPage.typeCharactersIntoFilter("i");
        List<WebElement> suggestions = tasksPage.getSuggestions();
        assertEquals("Displayed wrong number of suggestions for input \"i\"", 1, suggestions.size());
        assertEquals("Displayed wrong suggestion for input \"i\"", "id:", suggestions.get(0).getText());

        tasksPage.selectSuggestion(0);
        assertEquals("Filter input value is wrong", "id:", tasksPage.getFilterInputValue());

        tasksPage.typeCharactersIntoFilter("1");
        tasksPage.submitFilter();
        assertEquals("Task list does not match filter", 2, tasksPage.countListedTasks());
    }
}
