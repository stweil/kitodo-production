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

package org.kitodo.dataeditor.handlers;

import java.util.List;
import java.util.Objects;

import org.kitodo.dataformat.metskitodo.Mets;
import org.kitodo.dataformat.metskitodo.MetsType;

/**
 * General utilities for handling of generated mets-kitodo class content.
 */
public class MetsKitodoHeaderHandler {

    /**
     * Private constructor to hide the implicit public one.
     */
    private MetsKitodoHeaderHandler() {

    }

    /**
     * Adds a note to the first {@code agent} element in mets header. Does nothing
     * if no {@code agent} element exists.
     *
     * @param noteMessage
     *            The note message.
     * @param mets
     *            The Mets object.
     * @return The Mets object with added note.
     */
    public static Mets addNoteToMetsHeader(String noteMessage, Mets mets) {
        MetsType.MetsHdr metsHdr = mets.getMetsHdr();
        if (Objects.isNull(metsHdr)) {
            return mets;
        }

        List<MetsType.MetsHdr.Agent> agents = metsHdr.getAgent();
        if (!agents.isEmpty()) {
            agents.get(0).getNote().add(noteMessage);
        }
        return mets;
    }
}
