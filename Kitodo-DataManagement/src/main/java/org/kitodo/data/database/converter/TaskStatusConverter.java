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

package org.kitodo.data.database.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.kitodo.data.database.enums.TaskStatus;

@Converter
public class TaskStatusConverter implements AttributeConverter<TaskStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskStatus taskStatus) {
        return taskStatus.getValue();
    }

    @Override
    public TaskStatus convertToEntityAttribute(Integer taskStatusValue) {
        return TaskStatus.getStatusFromValue(taskStatusValue);
    }
}
