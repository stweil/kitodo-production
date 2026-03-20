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

/**
 * The overall validation success or failure state for an image or file.
 */
public enum LtpValidationResultState {

    /**
     * The image was deemed valid.
     */
    VALID,

    /**
     * There were validation problems that are treated as warning.
     */
    WARNING,

    /**
     * There were validation problems that are treated as critical error.
     */
    ERROR

}
