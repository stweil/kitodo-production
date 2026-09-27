/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.selenium.testframework.pages;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.selenium.testframework.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends Page<LoginPage> {

    @SuppressWarnings(UNUSED)
    @FindBy(id = "login")
    private WebElement loginButton;

    @SuppressWarnings(UNUSED)
    @FindBy(id = "username")
    private WebElement usernameInput;

    @SuppressWarnings(UNUSED)
    @FindBy(id = "password")
    private WebElement passwordInput;

    public LoginPage() {
        super("pages/login");
    }

    /**
     * Goes to login page.
     *
     * @return The login page.
     */
    @Override
    public LoginPage goTo() {
        Browser.goTo(this.getUrl());
        return this;
    }

    /**
     * Enter user name and password into the login form and submit it for login.
     * 
     * @param user the user name
     * @param password the cleartext password 
     * @throws InterruptedException in case there is an interruption
     */
    public void performLogin(User user, String password) throws InterruptedException {
        // wait until all processes that were added during database initialization are indexed
        await().ignoreExceptions().pollInterval(100, TimeUnit.MILLISECONDS).atMost(5, TimeUnit.SECONDS)
            .until(() -> !ServiceManager.getIndexingService().isIndexCorrupted());

        usernameInput.clear();
        usernameInput.sendKeys(user.getLogin());

        passwordInput.clear();
        passwordInput.sendKeys(password);

        // mark the document the login form is submitted in; submitting the form triggers a
        // full page navigation that replaces it (both a successful and a deliberately failed
        // login navigate away from the submitted document), which is used to detect that the
        // login request has been processed
        Browser.getDriver().executeScript("window.kitodoLoginSubmitted = true;");
        loginButton.click();
        awaitLoginRequestProcessed();
    }

    /**
     * Waits until the login request has been processed.
     *
     * <p>Submitting the login form triggers a full page navigation. For a successful login
     * the browser is redirected to an authenticated page; for a deliberately failed login
     * (for example caused by an invalid CSRF token) it is redirected back to the login page.
     * In both cases the document the form was submitted in is replaced, which is indicated
     * by the sentinel set before the submission no longer existing. In the successful case
     * the top navigation must additionally be rendered before the login is considered
     * complete, so that its links can be used deterministically instead of relying on a
     * fixed delay.</p>
     */
    private void awaitLoginRequestProcessed() {
        new FluentWait<>(Browser.getDriver())
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofMillis(200))
                .withMessage("the login request has not been processed")
                .ignoring(WebDriverException.class)
                .until(driver -> Boolean.TRUE.equals(driver.executeScript(
                        "return window.kitodoLoginSubmitted !== true;")));

        // a successful login has left the login page; wait until the top navigation is
        // rendered before the login can be considered complete
        if (!Browser.getCurrentUrl().contains("login")) {
            new WebDriverWait(Browser.getDriver(), Duration.ofSeconds(30))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("dashboard-menu")));
        }
    }

    /**
     * Login as admin user "kowal".
     * 
     * @throws InterruptedException in case there is an interruption
     * @throws DAOException in case user details can not be retrieved from the database
     */
    public void performLoginAsAdmin() throws InterruptedException, DAOException {
        performLogin(ServiceManager.getUserService().getById(1), MockDatabase.DEFAULT_USER_PASSWORD);
    }
}
