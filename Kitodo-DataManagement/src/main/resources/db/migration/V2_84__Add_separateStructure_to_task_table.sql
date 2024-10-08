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

-- 1. Add column for separateStructure to task table
--
ALTER TABLE task ADD separateStructure TINYINT(1);

-- 2. Switch off safe updates
--
SET SQL_SAFE_UPDATES = 0;

-- 3. Set default value for this column
--

UPDATE task SET separateStructure = 0 WHERE id IS NOT NULL;

-- 4. Switch on safe updates
--
SET SQL_SAFE_UPDATES = 1;
