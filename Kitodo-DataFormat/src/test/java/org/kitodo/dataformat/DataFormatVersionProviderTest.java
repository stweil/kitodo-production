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

package org.kitodo.dataformat;

import org.junit.Assert;
import org.junit.Test;

public class DataFormatVersionProviderTest {

    private DataFormatVersionProvider versionProvider = new DataFormatVersionProvider();

    @Test
    public void getDataFormatVersionTest() {
        Assert.assertEquals("Data format version was not correct", "1.0", versionProvider.getDataFormatVersion());
    }
}
