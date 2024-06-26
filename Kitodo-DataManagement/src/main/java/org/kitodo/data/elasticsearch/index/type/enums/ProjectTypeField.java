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

package org.kitodo.data.elasticsearch.index.type.enums;

public enum ProjectTypeField implements TypeInterface {

    ID("id"),
    TITLE("title"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    NUMBER_OF_PAGES("numberOfPages"),
    NUMBER_OF_VOLUMES("numberOfVolumes"),
    METS_RIGTS_OWNER("metsRightsOwner"),
    ACTIVE("active"),
    HAS_PROCESSES("hasProcesses"),
    TEMPLATES("templates"),
    USERS("users"),
    CLIENT_ID("client.id"),
    CLIENT_NAME("client.name"),
    FOLDER("folder"),
    FOLDER_FILE_GROUP("fileGroup"),
    FOLDER_URL_STRUCTURE("urlStructure"),
    FOLDER_MIME_TYPE("mimeType"),
    FOLDER_PATH("path");

    private String name;

    ProjectTypeField(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
