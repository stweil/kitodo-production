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

-- Add column "show_logical_page_number_below_thumbnail" to "user" table
ALTER TABLE user ADD show_logical_page_number_below_thumbnail TINYINT(1) DEFAULT 0;

-- Add column "default_pagination_type" to "user" table
ALTER TABLE user ADD default_pagination_type VARCHAR(12) DEFAULT NULL;
