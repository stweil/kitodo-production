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


/**
 * Base class for a tab view inside an edit view.
 * 
 * <p>Defines two methods "load" and "save" that should be implemented to handle
 * how data is loaded and saved from the perspective of the individual edit tab.
 */
public class BaseTabEditView<T> extends BaseForm {
    
    /**
     * Method that is called from viewAction of base edit form after loading some 
     * entity that is being edited, e.g. a User.
     *
     * @param object
     *            the entity currently being edited
     */
    public void load(T object) {
        return;
    }

    /**
     * Return true if tab view information can be saved. 
     *
     * @return true if user clients data was saved
     */
    public boolean save() {
        return true;
    }
}
