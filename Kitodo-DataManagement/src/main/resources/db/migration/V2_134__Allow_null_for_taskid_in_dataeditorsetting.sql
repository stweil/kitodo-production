--
-- (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
--
-- This file is part of the Kitodo project.
--
-- It is licensed under GNU General Public License version 3 or later.
--
-- You should have received a copy of the GNU General Public License
-- along with this program.  If not, see <https://www.gnu.org/licenses/>.
--

SET SQL_SAFE_UPDATES = 0;

-- allow null in column task_id of table dataeditor_setting to store task-independent layout
ALTER TABLE dataeditor_setting MODIFY COLUMN task_id INT(11) NULL;

SET SQL_SAFE_UPDATES = 1;