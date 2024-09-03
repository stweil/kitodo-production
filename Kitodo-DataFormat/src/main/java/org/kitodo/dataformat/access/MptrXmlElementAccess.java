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

package org.kitodo.dataformat.access;

import java.net.URI;
import java.util.Objects;

import org.kitodo.api.dataformat.mets.LinkedMetsResource;
import org.kitodo.dataformat.metskitodo.DivType;
import org.kitodo.dataformat.metskitodo.DivType.Mptr;

/**
 * Provides access to the {@code <mets:mptr>} element.
 */
class MptrXmlElementAccess {
    /**
     * Set of allowed LOCTYPE values.
     */
    private enum AllowedLoctypeValues {
        ARK,
        URN,
        URL,
        PURL,
        HANDLE,
        DOI,
        OTHER;

        static boolean contains(String loctype) {
            if (Objects.isNull(loctype)) {
                return false;
            }
            try {
                valueOf(loctype);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    /**
     * Adds the information about a linked METS resource to a
     * {@code <mets:div>}.
     *
     * @param link
     *            Details of a linked METS resource
     * @param div
     *            {@code <mets:div>} to which the information is added
     */
    static void addMptrToDiv(LinkedMetsResource link, DivType div) {
        Mptr mptr = new Mptr();
        if (AllowedLoctypeValues.contains(link.getLoctype())) {
            mptr.setLOCTYPE(link.getLoctype());
        } else {
            mptr.setLOCTYPE(AllowedLoctypeValues.OTHER.toString());
            mptr.setOTHERLOCTYPE(Objects.toString(link.getLoctype()));
        }
        mptr.setHref(link.getUri().toASCIIString());
        div.getMptr().add(mptr);
    }

    /**
     * Reads the information about a link from a {@code <mets:div>}.
     *
     * @param div
     *            {@code <mets:div>}, which may contain information about a link
     * @return information to the link, if indicated
     */
    static LinkedMetsResource getLinkFromDiv(DivType div) {
        if (div.getMptr().isEmpty()) {
            return null;
        }
        LinkedMetsResource linkFromDiv = new LinkedMetsResource();
        Mptr mptr = div.getMptr().get(0);
        linkFromDiv.setLoctype(AllowedLoctypeValues.OTHER.toString().equals(mptr.getLOCTYPE()) ? mptr.getOTHERLOCTYPE()
                : mptr.getLOCTYPE());
        linkFromDiv.setUri(URI.create(mptr.getHref()));
        return linkFromDiv;
    }
}
