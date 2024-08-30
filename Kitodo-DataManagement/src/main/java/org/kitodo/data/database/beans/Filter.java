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

package org.kitodo.data.database.beans;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Filter bean.
 */
@Entity
@Table(name = "filter")
public class Filter extends BaseIndexedBean {

    @Column(name = "value", columnDefinition = "longtext")
    private String value;

    @Column(name = "creationDate")
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_filter_user_id"))
    private User user;

    /**
     * Get filter value.
     *
     * @return filter value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set filter value.
     *
     * @param value
     *            filter
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get creation date.
     *
     * @return creation date as Date
     */
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Set creation date.
     *
     * @param creationDate
     *            as Date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get user.
     *
     * @return user
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Set user.
     *
     * @param user
     *            object
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof Filter) {
            Filter filter = (Filter) object;
            return Objects.equals(this.getId(), filter.getId());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, creationDate, user);
    }
}
