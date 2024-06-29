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

package org.kitodo.production.services.data;

import org.junit.Test;
import org.kitodo.production.services.ServiceManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserServiceTest {

    private static final UserService userService = ServiceManager.getUserService();

    @Test
    public void shouldCheckIfIsLoginValid() {
        assertTrue("Login is invalid!", userService.isLoginValid("validLogin"));

        assertFalse("Login is valid!", userService.isLoginValid("root"));
    }
}
