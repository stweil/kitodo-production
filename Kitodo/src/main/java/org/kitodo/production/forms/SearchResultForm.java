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

import java.io.Serializable;
import java.text.MessageFormat;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

@Named("SearchResultForm")
@SessionScoped
public class SearchResultForm implements Serializable {

    private String searchQuery;

    private static final String REDIRECT_PATH = "/pages/{0}.xhtml?faces-redirect=true";

    /**
     * Searches for processes with the entered searchQuery.
     *
     * @return The redirect URL to the processes page with input parameter.
     */
    public String searchForProcessesBySearchQuery() {
        return MessageFormat.format(REDIRECT_PATH, "processes") + "&input=" + searchQuery;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}
