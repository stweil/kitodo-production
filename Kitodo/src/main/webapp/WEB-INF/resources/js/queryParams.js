/**
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// eslint-disable-next-line no-use-before-define
var kitodo = kitodo || {};

/**
 * Update the current browser URL to include the selected query parameter and value.
 * 
 * @param str key the query parameter name
 * @param str value the query parameter value
 */
kitodo.updateQueryParameter = function (key, value) {
    const url = new URL(window.location.href);
    if (value === "") {
        url.searchParams.delete(key);
    } else {
        url.searchParams.set(key, value);
    }
    window.history.replaceState({}, "", url);
};

kitodo.removeQueryParameter = function (key) {
    const url = new URL(window.location.href);
    url.searchParams.delete(key);
    window.history.replaceState({}, "", url);
};
