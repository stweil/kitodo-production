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

package org.kitodo.production.forms;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.kitodo.data.database.beans.SearchField;

@Named
@RequestScoped
public class AddSearchFieldDialogView {

    private SearchField searchField;

    /**
     * Default constructor.
     */
    public AddSearchFieldDialogView() {
        init();
    }

    /**
     * Get searchField.
     *
     * @return value of searchField
     */
    public SearchField getSearchField() {
        return searchField;
    }

    /**
     * Set searchField.
     *
     * @param searchField as org.kitodo.data.database.beans.SearchField
     */
    public void setSearchField(SearchField searchField) {
        this.searchField = searchField;
    }

    /**
     * Initialize search field.
     */
    public void init() {
        searchField = new SearchField();
    }
}
