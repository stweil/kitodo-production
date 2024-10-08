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

import java.net.URI;

import org.kitodo.dataeditor.enums.FileLocationType;

/**
 * Data class to hold the information of media files which added as file group in mets document.
 */
public class MediaFile {
    private URI filePath;
    private String mimeType;
    private FileLocationType locationType;

    /**
     * The constructor for setting fields.
     *
     * @param filePath
     *            The file path as URI object.
     * @param locationType
     *            The location type.
     * @param mimeType
     *            The MIME-type
     */
    public MediaFile(URI filePath, FileLocationType locationType, String mimeType) {
        this.filePath = filePath;
        this.mimeType = mimeType;
        this.locationType = locationType;
    }

    /**
     * The empty constructor.
     */
    public MediaFile() {
    }

    /**
     * Gets filePath.
     *
     * @return The filePath.
     */
    public URI getFilePath() {
        return filePath;
    }

    /**
     * Sets filePath.
     *
     * @param filePath
     *            The filePath.
     */
    public void setFilePath(URI filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets mimeType.
     *
     * @return The mimeType.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets mimeType.
     *
     * @param mimeType
     *            The mimeType.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Gets locationType.
     *
     * @return The locationType.
     */
    public FileLocationType getLocationType() {
        return locationType;
    }

    /**
     * Sets locationType.
     *
     * @param locationType
     *            The locationType.
     */
    public void setLocationType(FileLocationType locationType) {
        this.locationType = locationType;
    }
}
