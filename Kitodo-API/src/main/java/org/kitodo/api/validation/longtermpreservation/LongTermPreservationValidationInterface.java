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

package org.kitodo.api.validation.longtermpreservation;

import java.net.URI;
import java.util.List;

/** Validation for long-term preservation. */
public interface LongTermPreservationValidationInterface {

    /**
     * Validates a file for long-term preservation.
     *
     * @param fileUri
     *            The uri to the image, which should be validated.
     * @param fileType
     *            The fileType of the image at the given path.
     * @return A validation result.
     */
    LtpValidationResult validate(URI fileUri, FileType fileType,
            List<? extends LtpValidationConditionInterface> conditions);

}
