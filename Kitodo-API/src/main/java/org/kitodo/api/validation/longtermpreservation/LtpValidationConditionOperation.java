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
 * Types of operations of a validation condition.
 */
public enum LtpValidationConditionOperation {

    /**
     * The condition value is verified to be exactly equal to the extracted
     * value.
     */
    EQUAL,

    /**
     * The extracted value is verified to be any one of the condition values.
     */
    ONE_OF,

    /**
     * The extracted value is verified to be none of the condition values.
     */
    NONE_OF,

    /**
     * The extracted value is verified to be smaller than the condition value.
     */
    SMALLER_THAN,

    /**
     * The extracted value is verified to be larger than the condition value.
     */
    LARGER_THAN,

    /**
     * The extracted value is verified to be in between the interval of two
     * condition values.
     */
    IN_BETWEEN,

    /**
     * The extracted value matches the provided regular expression without
     * considering letter casing.
     */
    MATCHES,

    /**
     * There is a non-empty value present. The exact value does not matter.
     */
    NON_EMPTY
}
