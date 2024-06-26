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

-- add authorities/permission for renaming media files
INSERT IGNORE INTO authority (title) VALUES ('renameMedia_clientAssignable');
INSERT IGNORE INTO authority (title) VALUES ('renameMedia_globalAssignable');

-- add "filenameLength" column to project table
ALTER TABLE project ADD filename_length INT DEFAULT 8;

SET SQL_SAFE_UPDATES = 1;
