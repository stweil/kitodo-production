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

package org.kitodo.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigCoreTest {

    @Test
    public void shouldGetKitodoConfigDirectory() {
        String expected = "src/test/resources/";

        assertEquals("Directory was queried incorrectly!", expected, ConfigCore.getKitodoConfigDirectory());
    }

    @Test
    public void shouldGetKitodoDataDirectory() {
        String expected = "src/test/resources/metadata/";

        assertEquals("Directory was queried incorrectly!", expected, ConfigCore.getKitodoDataDirectory());
    }
}
