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

import java.util.concurrent.TimeUnit;

import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.selenium.testframework.Browser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        performLogin(user, password, true);
    }

    /**
     * Enter user name and password into the login form and submit it.
     *
     * @param user the user name
     * @param password the cleartext password
     * @param expectRedirect whether to wait until the browser is redirected away from the login page.
     *            Set this to false when the login is expected to fail (e.g. an invalid CSRF token),
     *            in which case the browser stays on the login page and no redirect ever occurs.
     * @throws InterruptedException in case there is an interruption
     */
    public void performLogin(User user, String password, boolean expectRedirect) throws InterruptedException {
        // wait until all processes that were added during database initialization are indexed
        await().ignoreExceptions().pollInterval(100, TimeUnit.MILLISECONDS).atMost(5, TimeUnit.SECONDS)
            .until(() -> !ServiceManager.getIndexingService().isIndexCorrupted());

        usernameInput.clear();
        usernameInput.sendKeys(user.getLogin());

        passwordInput.clear();
        passwordInput.sendKeys(password);

        loginButton.click();
        if (expectRedirect) {
            await("Wait for redirect after login to complete")
                    .pollDelay(Browser.getDelayAfterLogin(), TimeUnit.MILLISECONDS)
                    .pollInterval(500, TimeUnit.MILLISECONDS)
                    .atMost(30, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until(() -> !Browser.getCurrentUrl().contains("login"));
        } else {
            // a failed login (e.g. an invalid CSRF token) keeps the browser on the login page, so
            // there is no redirect to wait for; give the re-rendered page a moment to settle
            Thread.sleep(Browser.getDelayAfterLogin());
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

    /**
     * Login as admin user "kowal".
     *
     * @param expectRedirect whether to wait until the browser is redirected away from the login page
     * @throws InterruptedException in case there is an interruption
     * @throws DAOException in case user details can not be retrieved from the database
     */
    public void performLoginAsAdmin(boolean expectRedirect) throws InterruptedException, DAOException {
        performLogin(ServiceManager.getUserService().getById(1), MockDatabase.DEFAULT_USER_PASSWORD, expectRedirect);
    }
}
