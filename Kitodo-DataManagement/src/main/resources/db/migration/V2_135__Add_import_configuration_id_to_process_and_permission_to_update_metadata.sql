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

-- add column "import_configuration_id" to "process" table
ALTER TABLE process ADD import_configuration_id INT;

-- add new permissions to set import configuration for processes and for triggering re-import of catalog metadata
INSERT IGNORE INTO authority (title) VALUES ('setImportConfiguration_clientAssignable');
INSERT IGNORE INTO authority (title) VALUES ('reimportMetadata_clientAssignable');

SET SQL_SAFE_UPDATES = 1;
