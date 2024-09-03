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

--
-- Migration: Remove script columns from task table

-- 1. Remove columns

ALTER TABLE task
  DROP COLUMN scriptName2,
  DROP COLUMN typeAutomaticScriptPath2,
  DROP COLUMN scriptName3,
  DROP COLUMN typeAutomaticScriptPath3,
  DROP COLUMN scriptName4,
  DROP COLUMN typeAutomaticScriptPath4,
  DROP COLUMN scriptName5,
  DROP COLUMN typeAutomaticScriptPath5;

-- 2. Rename column scriptName1

ALTER TABLE task
  CHANGE scriptName1 scriptName VARCHAR(255);
