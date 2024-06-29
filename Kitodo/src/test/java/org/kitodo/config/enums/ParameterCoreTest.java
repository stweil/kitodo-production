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

package org.kitodo.config.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class ParameterCoreTest {

    @Test
    public void shouldGetParameterWithoutDefaultValueTest() {
        assertNull("Default value for param exists!",
            ParameterCore.DIR_USERS.getParameter().getDefaultValue());
    }

    @Test
    public void shouldOverrideToStringWithParameterKeyTests() {
        ParameterCore parameterCore = ParameterCore.DIR_USERS;
        assertEquals("Methods toString() was not overridden!", parameterCore.getParameter().getKey(),
                String.valueOf(parameterCore));
    }
}
