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

package org.kitodo.selenium.stress;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.kitodo.data.database.beans.User;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.selenium.testframework.BaseTestSelenium;
import org.kitodo.selenium.testframework.Browser;
import org.kitodo.selenium.testframework.Pages;

/**
 * Stress test for the flaky login steps of the selenium test suite.
 *
 * <p>This class is deliberately not named with the "ST" suffix, so the
 * default failsafe include pattern (all classes ending in "ST") never pick
 * it up in regular CI runs. It is meant to be started explicitly with
 * "-Dit.test=LoginStress", for example by the "stress-flaky-selenium" GitHub
 * workflow. The number of login/logout cycles can be controlled with the
 * environment variable "KITODO_STRESS_ITERATIONS" (default: 50). Every cycle
 * that needs more than "KITODO_STRESS_STALL_MS" milliseconds (default: 8000)
 * is reported as "LOGIN STALL", so the test log gives a grep-able record of
 * the observed stalls.</p>
 */
public class LoginStress extends BaseTestSelenium {

    private static final Logger logger = LogManager.getLogger(LoginStress.class);
    private static final String ENV_ITERATIONS = "KITODO_STRESS_ITERATIONS";
    private static final String ENV_STALL_MS = "KITODO_STRESS_STALL_MS";
    private static final int DEFAULT_ITERATIONS = 50;
    private static final long DEFAULT_STALL_MS = 8000;

    /**
     * Performs the login and logout cycle repeatedly to provoke the stalls
     * observed in the regular selenium runs.
     */
    @Test
    public void repeatedLoginAndLogout() throws Exception {
        int iterations = readInt(ENV_ITERATIONS, DEFAULT_ITERATIONS);
        long stallMs = readInt(ENV_STALL_MS, (int) DEFAULT_STALL_MS);
        User user = ServiceManager.getUserService().getByLogin("kowal");
        int stalls = 0;
        for (int i = 1; i <= iterations; i++) {
            long start = System.nanoTime();
            try {
                logger.info("Login stress iteration {}/{}", i, iterations);
                Pages.getLoginPage().goTo().performLogin(user);
                await("Wait for redirect after login to complete")
                        .pollInterval(500, TimeUnit.MILLISECONDS)
                        .atMost(60, TimeUnit.SECONDS)
                        .ignoreExceptions()
                        .until(() -> !Browser.getCurrentUrl().contains("login"));
                Pages.getTopNavigation().logout();
            } catch (Exception e) {
                logger.error("Login stress iteration {} failed: {}", i, e.toString());
            }
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            if (elapsedMs > stallMs) {
                stalls++;
                logger.warn("LOGIN STALL: iteration {} took {} ms (threshold {} ms)", i, elapsedMs, stallMs);
            }
        }
        logger.warn("Login stress finished: {} iterations, {} stalls above {} ms", iterations, stalls, stallMs);
    }

    private static int readInt(String environmentVariable, int defaultValue) {
        String value = System.getenv(environmentVariable);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid {} value '{}', falling back to {}", environmentVariable, value, defaultValue);
            return defaultValue;
        }
    }
}
