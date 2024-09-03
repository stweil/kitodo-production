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

package org.kitodo.api.dataformat.mets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.kitodo.api.dataformat.Workpiece;

/**
 * Interface for a service that handles access to the {@code <mets:mets>}
 * element.
 *
 * <p>
 * METS is a schema that describes possible structures of an XML file. Despite
 * many definitions, it is so variable that the same piece of information can be
 * stored in various ways in the file, so that an extended technical use of the
 * information is not readily possible. To fix this shortcoming, so-called
 * application profiles are defined for the individual uses of METS. This
 * interface implements a specific METS application profile, the
 * Kitodo.Production application profile, which is closely related to the ZVDD
 * DFG-Viewer METS Profile, which was defined by the Central Directory of
 * Digitized Prints (ZVDD), an institution of the Lower Saxony State and
 * University Library of the Georg-August-University Goettingen in Germany, and
 * is widely used in Germany. Although this interface uses METS terminology, it
 * can only be meaningfully used to read and write METS XML files that
 * correspond to the Kitodo.Production METS application profile. For this
 * purpose, only external functionality was made available, which is necessary
 * for editing METS XML files in the Kitodo.Production application profile. This
 * saves the user from the internal complexity and richness of the METS file
 * format.
 */
public interface MetsXmlElementAccessInterface {
    /**
     * Reads a METS file.
     *
     * @param in
     *            open input channel for reading the file
     * @return the read workpiece
     * @throws IOException
     *             if the reading fails
     */
    Workpiece read(InputStream in) throws IOException;

    /**
     * Writes the workpiece to a METS file.
     *
     * @param workpiece
     *            workpiece to save
     * @param out
     *            open output channel for writing the file
     * @throws IOException
     *             if the writing fails
     */
    void save(Workpiece workpiece, OutputStream out) throws IOException;
}
