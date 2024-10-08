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
-- Migration: Create table validationFolders_task_x_folder

-- 1. Create table validationFolders_task_x_folder

CREATE TABLE validationFolders_task_x_folder (
  task_id   int(11) NOT NULL
     COMMENT 'Task that triggers the validation of the contents of the folder',
  folder_id int(11) NOT NULL
     COMMENT 'Folder whose contents are to be validated in that task',
  PRIMARY KEY ( task_id, folder_id ),
  KEY FK_task_id   ( task_id ),
  KEY FK_folder_id ( folder_id ),
  CONSTRAINT FK_validationFolders_task_x_folder_task_id
    FOREIGN KEY ( task_id ) REFERENCES task ( id ),
  CONSTRAINT FK_validationFolders_task_x_folder_folder_id
    FOREIGN KEY ( folder_id ) REFERENCES folder ( id )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
