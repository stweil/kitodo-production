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

package org.kitodo.production.migration;

import java.util.Objects;

import org.kitodo.data.database.beans.Task;

public class TaskComparer {

    /**
     * Compares two tasks, if they are equal for migration purposes.
     * @return true, if they are equal, false if not
     */
    public boolean isEqual(Task firstTask, Task secondTask) {
        if (Objects.isNull(firstTask) || Objects.isNull(secondTask)) {
            return false;
        }
        if (Objects.isNull(firstTask.getTitle()) ? Objects.nonNull(secondTask.getTitle())
                : !firstTask.getTitle().equals(secondTask.getTitle())) {
            return false;
        }
        if (Objects.isNull(firstTask.getOrdering()) ? Objects.nonNull(secondTask.getOrdering())
                : !firstTask.getOrdering().equals(secondTask.getOrdering())) {
            return false;
        }
        if (firstTask.isTypeAutomatic() != secondTask.isTypeAutomatic()) {
            return false;
        }
        if (firstTask.isTypeMetadata() != secondTask.isTypeMetadata()) {
            return false;
        }
        if (firstTask.isTypeImagesRead() != secondTask.isTypeImagesRead()) {
            return false;
        }
        if (firstTask.isTypeImagesWrite() != secondTask.isTypeImagesWrite()) {
            return false;
        }
        if (firstTask.isTypeExportDMS() != secondTask.isTypeExportDMS()) {
            return false;
        }
        if (firstTask.isTypeAcceptClose() != secondTask.isTypeAcceptClose()) {
            return false;
        }
        if (firstTask.isTypeCloseVerify() != secondTask.isTypeCloseVerify()) {
            return false;
        }
        if (Objects.isNull(firstTask.getScriptPath()) ? Objects.nonNull(secondTask.getScriptPath())
                : !firstTask.getScriptPath().equals(secondTask.getScriptPath())) {
            return false;
        }
        return firstTask.isBatchStep() == secondTask.isBatchStep();
    }

    /**
     * Returns a hash value for which holds that is the same for two tasks if
     * the above comparator returns 0, otherwise different.
     *
     * @param task
     *            task to return hash value for
     * @return hash value
     */
    public static int hashCode(Task task) {
        if (Objects.isNull(task)) {
            return 0;
        }
        int hashCode = Objects.hash(task.getTitle(), task.getOrdering(), task.isTypeAutomatic(), task.isTypeMetadata(),
            task.isTypeImagesRead(), task.isTypeImagesWrite(), task.isTypeExportDMS(), task.isTypeAcceptClose(),
            task.isTypeCloseVerify(), task.getScriptPath(), task.isBatchStep());
        return hashCode;
    }
}
