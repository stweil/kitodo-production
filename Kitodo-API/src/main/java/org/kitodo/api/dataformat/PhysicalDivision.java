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

package org.kitodo.api.dataformat;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.kitodo.api.dataformat.mets.KitodoUUID;

/**
 * A tree-shaped description of the physical division of the digital representation of
 * a digital medium. Each leaf of the physical structure tree represents a
 * single medium, possibly in several forms of digital representation. For
 * books, this is typically flat, meaning the {@link Workpiece#getPhysicalStructure()}
 * represents the book, and its immediate children represent the individual
 * views (front cover, inside cover, first page, second page, third page, …) For
 * other media, this may well be more complex, such as an archival box
 * containing envelopes with booklets and loose sheets and a dust jacket with a
 * phonographic record in it, etc.
 *
 * <p>
 * Media files that represent different representations of the same medium can
 * be attached to a physical division. This can be still images in different
 * resolutions with equal image content, but also the photography of the side of
 * a record along with its digitized soundtrack.
 */
public class PhysicalDivision extends Division<PhysicalDivision> {
    // TODO: we probably need a way to configure PhysicalDivision types to be considered for renumbering/pagination!
    public static final String TYPE_PAGE = "page";
    public static final String TYPE_TRACK = "track";
    public static final String TYPE_OTHER = "other";

    public static final List<String> TYPES = Arrays.asList(TYPE_PAGE, TYPE_TRACK, TYPE_OTHER);
    /**
     * Each physical division can be available in different variants, for each of which
     * a media file is available. This is in this map.
     */
    private Map<MediaVariant, URI> mediaFiles = new HashMap<>();

    private MediaPartial mediaPartial;

    /**
     * Saves the METS identifier for the division.
     */
    private String metsDivReferrerId;

    /**
     * List of LogicalDivisions this view is assigned to.
     */
    private transient List<LogicalDivision> logicalDivisions;

    /**
     * Creates a new PhysicalDivision.
     */
    public PhysicalDivision() {
        logicalDivisions = new LinkedList<>();
    }


    /**
     * Returns the map of available media variants with the corresponding media
     * file URIs.
     *
     * @return available media variants with corresponding media file URIs
     */
    public Map<MediaVariant, URI> getMediaFiles() {
        return mediaFiles;
    }

    /**
     * Returns the ID of div, or if unknown, creates a new one.
     *
     * @return the ID of div
     */
    public String getDivId() {
        if (Objects.isNull(metsDivReferrerId)) {
            metsDivReferrerId = KitodoUUID.randomUUID();
        }
        return metsDivReferrerId;
    }

    /**
     * Set the ID of div.
     *
     * @param divId
     *            ID of div to set
     */
    public void setDivId(String divId) {
        this.metsDivReferrerId = divId;
    }

    /**
     * The list is available to assist to render the front-end by holding the
     * elements of the logical structure that reference this physical division. It is
     * transient, meaning that its content is not saved and is not restored when
     * it is loaded.
     *
     * @return a list that you can use
     */
    public List<LogicalDivision> getLogicalDivisions() {
        return logicalDivisions;
    }

    /**
     * Set the media partial.
     *
     * @param mediaPartial
     *         The media partial
     */
    public void setMediaPartial(MediaPartial mediaPartial) {
        this.mediaPartial = mediaPartial;
    }

    /**
     * Check if physical division has media partial.
     *
     * @return True if has media partial
     */
    public boolean hasMediaPartial() {
        return Objects.nonNull(mediaPartial);
    }

    /**
     * Get the media partial.
     *
     * @return The media partial.
     */
    public MediaPartial getMediaPartial() {
        return mediaPartial;
    }

    @Override
    public String toString() {
        String fileName = "No file (";
        if (!mediaFiles.isEmpty()) {
            URI uri = mediaFiles.entrySet().iterator().next().getValue();
            fileName = FilenameUtils.getBaseName(uri.getPath()).concat(" (");
        }
        if (Objects.nonNull(getType())) {
            fileName = getType() + ' ' + fileName;
        }
        return mediaFiles.keySet().stream().map(MediaVariant::getUse)
                .collect(Collectors.joining(", ", fileName, ")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof PhysicalDivision)) {
            return false;
        }
        PhysicalDivision physicalDivision = (PhysicalDivision) o;
        return Objects.equals(mediaFiles, physicalDivision.mediaFiles) && Objects.equals(mediaPartial,
                physicalDivision.mediaPartial);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((mediaFiles == null) ? 0 : mediaFiles.hashCode());
        result = prime * result + ((Objects.isNull(mediaPartial) || Objects.isNull(mediaPartial.getBegin()))
                ? 0
                : mediaPartial.getBegin().hashCode());
        return result;
    }
}
