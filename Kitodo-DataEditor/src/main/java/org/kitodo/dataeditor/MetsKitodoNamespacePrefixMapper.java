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

package org.kitodo.dataeditor;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Extension of NamespacePrefixMapper which holds the used namespaces and
 * corresponding prefixes of mets-kitodo format. This can be used as a property in
 * a jaxb context marshaller object at serialization of mets objects to xml files.
 */
public class MetsKitodoNamespacePrefixMapper extends NamespacePrefixMapper {

    private static final String METS_PREFIX = "mets";
    private static final String METS_URI = "http://www.loc.gov/METS/";

    private static final String XLINK_PREFIX = "xlink";
    private static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    private static final String KITODO_PREFIX = "kitodo";
    private static final String KITODO_URI = "http://meta.kitodo.org/v1/";

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (METS_URI.equals(namespaceUri)) {
            return METS_PREFIX;
        } else if (XLINK_URI.equals(namespaceUri)) {
            return XLINK_PREFIX;
        } else if (KITODO_URI.equals(namespaceUri)) {
            return KITODO_PREFIX;
        }
        return suggestion;
    }

    @Override
    public String[] getPreDeclaredNamespaceUris() {
        return new String[] {METS_URI, KITODO_URI, XLINK_URI };
    }
}
