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

package org.kitodo.dataeditor.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.kitodo.dataeditor.MetsKitodoObjectFactory;
import org.kitodo.dataformat.metskitodo.DivType;
import org.kitodo.dataformat.metskitodo.MetsType;
import org.kitodo.dataformat.metskitodo.StructLinkType;

public class StructLink extends MetsType.StructLink {

    private MetsKitodoObjectFactory objectFactory = new MetsKitodoObjectFactory();

    /**
     * Constructor to copy the data from parent class.
     *
     * @param structLink
     *            The MetsType.StructLink object.
     */
    public StructLink(MetsType.StructLink structLink) {
        super.id = structLink.getID();
        super.smLinkOrSmLinkGrp = structLink.getSmLinkOrSmLinkGrp();
    }

    /**
     * Adds a smLink to link a logical div to a physical div.
     *
     * @param logicalDiv
     *            The logical DivType object to link from.
     * @param physicalDiv
     *            The physical DivType object to link to.
     */
    private void addSmLink(DivType logicalDiv, DivType physicalDiv) {
        StructLinkType.SmLink smLink = objectFactory.createStructLinkTypeSmLink();
        smLink.setFrom(logicalDiv.getID());
        smLink.setTo(physicalDiv.getID());
        this.getSmLinkOrSmLinkGrp().add(smLink);
    }

    /**
     * Adds SmLinks for a list of physical divs to one logical div.
     * 
     * @param logicalDiv
     *            The logical DivType object to link from
     * @param physicalDivs
     *            The list of physical DivType objects to link to.
     */
    public void addSmLinks(DivType logicalDiv, List<DivType> physicalDivs) {
        for (DivType physicalDiv : physicalDivs) {
            addSmLink(logicalDiv, physicalDiv);
        }
    }

    /**
     * Removes a smLink which links a logical div to a physical div.
     *
     * @param logicalDiv
     *            The logical DivType object which links from.
     * @param physicalDiv
     *            The physical DivType object which links to.
     */
    public void removeSmLink(DivType logicalDiv, DivType physicalDiv) {
        StructLinkType.SmLink smLinkToDelete = objectFactory.createStructLinkTypeSmLink();
        smLinkToDelete.setFrom(logicalDiv.getID());
        smLinkToDelete.setTo(physicalDiv.getID());
        List<Object> smLinkOrSmLinkGrp = this.getSmLinkOrSmLinkGrp();
        smLinkOrSmLinkGrp.remove(smLinkToDelete);
    }

    /**
     * Returns a list of ids of physical div objects which are linked by a given
     * logical div.
     * 
     * @param logicalDiv
     *            The logical div element.
     * @return The list of ids.
     */
    public List<String> getPhysicalDivIdsByLogicalDiv(DivType logicalDiv) {
        List<SmLink> smLinks = getSmLinks();
        List<String> ids = new ArrayList<>();
        for (SmLink smLink : smLinks) {
            if (Objects.equals(smLink.getFrom(), logicalDiv.getID())) {
                ids.add(smLink.getTo());
            }
        }
        return ids;
    }

    /**
     * Returns a list of smLink objects. This method must be modified in case we
     * start to support smLinkGroups in mets structLink section.
     * 
     * @return The list of smLinks.
     */
    @SuppressWarnings("unchecked")
    public List<SmLink> getSmLinks() {
        return (List) getSmLinkOrSmLinkGrp();
    }
}
