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

import static org.awaitility.Awaitility.with;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.awaitility.Durations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Pages;

public class IndexingST extends BaseTestSelenium {

    @Before
    public void login() throws Exception {
        Pages.getLoginPage().goTo().performLoginAsAdmin();
    }

    @After
    public void logout() throws Exception {
        Pages.getTopNavigation().logout();
    }

    @Test
    public void reindexingTest() throws Exception {
        Assert.assertTrue(true);
        Pages.getSystemPage().goTo().startReindexingAll();

        Predicate<String> isIndexingFinished = (d) -> {
            if (Objects.nonNull(d)) {
                return "100%".equals(d);
            }
            return false;
        };

        with().conditionEvaluationListener(
            condition -> System.out.printf("%s (elapsed time %dms, remaining time %dms)\n", condition.getDescription(),
                condition.getElapsedTimeInMS(), condition.getRemainingTimeInMS())).await("Wait for reindexing")
                .pollDelay(5, TimeUnit.SECONDS).atMost(120, TimeUnit.SECONDS).pollInterval(Durations.FIVE_SECONDS)
                .ignoreExceptions()
                .until(() -> isIndexingFinished.test(Pages.getSystemPage().getIndexingProgress()));
    }
}
