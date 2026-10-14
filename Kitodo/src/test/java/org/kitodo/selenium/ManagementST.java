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

package org.kitodo.selenium;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Browser;
import org.kitodo.selenium.testframework.Pages;

public class ManagementST extends BaseTestSelenium {

    @BeforeEach
    public void login() throws Exception {
        Pages.getLoginPage().goTo().performLoginAsAdmin();
    }

    @AfterEach
    public void logout() throws Exception {
        Pages.getTopNavigation().logout();
        if (Browser.isAlertPresent()) {
            Browser.getDriver().switchTo().alert().accept();
        }
    }

    @Test
    public void editUserConfigurationTest() throws Exception {
        Pages.getUserEditPage().changeUserSettings();

        // changeUserSettings() picks the language at a fixed position in the dropdown, which depends
        // on the locale order in faces-config.xml; the resulting page may therefore render in a
        // non-English language, so verify the users table is displayed instead of relying on the
        // localized "Users" header/link text.
        int usersDisplayed = Pages.getUsersPage().countListedUsers();
        assertTrue(usersDisplayed > 0, "Users page should display at least one user");
    }
}
