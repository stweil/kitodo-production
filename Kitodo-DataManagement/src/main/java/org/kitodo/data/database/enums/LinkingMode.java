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

package org.kitodo.data.database.enums;

/**
 * Different ways how to link the contents of a folder in a METS fileGrp.
 */
public enum LinkingMode {
    /**
     * A common fileGrp is created, all images will be linked, even if they have
     * not yet been physically added on the drive.
     */
    ALL,

    /**
     * The folder is validated, only images existing on the drive will be
     * linked.
     */
    EXISTING,

    /**
     * The folder will not be mapped to a fileGrp at all.
     */
    NO,

    /**
     * Only the selected preview image will be linked in the fileGrp.
     */
    PREVIEW_IMAGE
}
