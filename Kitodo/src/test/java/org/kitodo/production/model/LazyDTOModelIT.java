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

package org.kitodo.production.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kitodo.MockDatabase;
import org.kitodo.SecurityTestUtils;
import org.kitodo.data.database.beans.Client;
import org.kitodo.production.dto.DocketDTO;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.ClientService;
import org.primefaces.model.SortOrder;

public class LazyDTOModelIT {

    private static final ClientService clientService = ServiceManager.getClientService();
    private static LazyDTOModel lazyDTOModel = null;

    @BeforeAll
    public static void setUp() throws Exception {
        MockDatabase.startNode();
        MockDatabase.insertClients();
        lazyDTOModel = new LazyDTOModel(clientService);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        MockDatabase.stopNode();
        MockDatabase.cleanDatabase();
    }

    @Test
    public void shouldGetRowData() throws Exception {
        List clients = clientService.getAll();
        Client firstClient = (Client) clients.get(0);
        Client lazyClient = (Client) lazyDTOModel.getRowData(String.valueOf(firstClient.getId()));
        assertEquals(firstClient.getName(), lazyClient.getName());
    }

    @Test
    public void shouldLoadFromDatabase() {
        List clients = lazyDTOModel.load(0, 2, "name", SortOrder.ASCENDING, null);
        assertEquals(2, clients.size());

        clients = lazyDTOModel.load(0, 10, "name", SortOrder.ASCENDING, null);
        assertEquals(3, clients.size());

        Client client = (Client) clients.get(0);
        assertEquals("First client", client.getName());

        clients = lazyDTOModel.load(0, 2, "name", SortOrder.DESCENDING, null);
        client = (Client) clients.get(0);
        assertEquals("Second client", client.getName());
    }

    @Test
    public void shouldLoadFromIndex() throws Exception {
        MockDatabase.cleanDatabase();
        MockDatabase.insertForAuthenticationTesting();
        MockDatabase.insertDockets();
        SecurityTestUtils.addUserDataToSecurityContext(ServiceManager.getUserService().getById(1), 1);

        LazyDTOModel lazyDTOModelDocket = new LazyDTOModel(ServiceManager.getDocketService());

        List dockets = lazyDTOModelDocket.load(0, 2, "title", SortOrder.ASCENDING, null);
        assertEquals(2, dockets.size());

        DocketDTO docket = (DocketDTO) dockets.get(0);
        assertEquals("default", docket.getTitle());

        dockets = lazyDTOModelDocket.load(0, 2, "title", SortOrder.DESCENDING, null);
        docket = (DocketDTO) dockets.get(0);
        assertEquals("tester", docket.getTitle());
    }
}
