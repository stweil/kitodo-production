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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kitodo.MockDatabase;
import org.kitodo.data.database.beans.LdapServer;
import org.kitodo.production.services.ServiceManager;

public class LdapServerServiceIT {

    private static final LdapServerService ldapServerService = ServiceManager.getLdapServerService();

    @BeforeClass
    public static void prepareDatabase() throws Exception {
        MockDatabase.insertLdapGroups();
    }

    @AfterClass
    public static void cleanDatabase() {
        MockDatabase.cleanDatabase();
    }

    @Test
    public void shouldFindLdapServer() throws Exception {
        LdapServer ldapServer = ldapServerService.getById(1);
        assertEquals("LpadServer title is not matching", "FirstLdapServer", ldapServer.getTitle());
        assertFalse("LpadServer useSsl is not matching", ldapServer.isUseSsl());
        assertEquals("LdapServer password encoding is not matching", "SHA",
            ldapServer.getPasswordEncryption().getTitle());
    }
}
