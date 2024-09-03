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

package org.kitodo.production.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class KitodoNamespaceContext implements NamespaceContext {

    private final Map<String, String> namespaces;

    /**
     * Constructor which creates the map of namespaces used on kitodo meta.xml.
     */
    public KitodoNamespaceContext() {
        namespaces = new HashMap<>();
        namespaces.put("kitodo", "http://meta.kitodo.org/v1/");
        namespaces.put("mets", "http://www.loc.gov/METS/");
        namespaces.put("mods", "http://www.loc.gov/mods/v3");
        namespaces.put("marc", "http://www.loc.gov/MARC21/slim");
    }

    @Override
    public Iterator getPrefixes(String arg) {
        return namespaces.keySet().iterator();
    }

    @Override
    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().equals(namespaceURI)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getKey().equals(prefix)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
