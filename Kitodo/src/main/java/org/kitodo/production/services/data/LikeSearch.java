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

package org.kitodo.production.services.data;

/**
 * Specifies whether like search is enabled, or always enabled, on the database
 * field.
 */
enum LikeSearch {
    /**
     * Like search is possible, but must be activated with the search operator
     * {@code *}. A {@code *} in the search query is not filtered and is
     * converted to a {@code %} at the end.
     */
    ALLOWED,

    /**
     * A like right search is always carried out. A {@code *} in the search
     * query is filtered out, and a {@code %} is always appended at the end.
     */
    ALWAYS_RIGHT,

    /**
     * Like search is not allowed. A {@code *} in the search query is filtered
     * out.
     */
    NO
}
