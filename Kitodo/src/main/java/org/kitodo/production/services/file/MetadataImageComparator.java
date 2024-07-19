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

package org.kitodo.production.services.file;

import java.net.URI;
import java.util.Comparator;

import org.kitodo.config.ConfigCore;
import org.kitodo.config.enums.ParameterCore;
import org.kitodo.exceptions.NotImplementedException;

public class MetadataImageComparator implements Comparator<Object> {

    private final FileService fileService;

    MetadataImageComparator(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public int compare(Object firstObject, Object secondObject) {
        String firstName;
        String secondName;

        if (firstObject instanceof String && secondObject instanceof String) {
            firstName = (String) firstObject;
            secondName = (String) secondObject;
        } else if (firstObject instanceof URI && secondObject instanceof URI) {
            URI firstUri = (URI) firstObject;
            URI secondUri = (URI) secondObject;

            firstName = fileService.getFileName(firstUri);
            secondName = fileService.getFileName(secondUri);
        } else {
            throw new NotImplementedException();
        }

        return compareImages(firstName, secondName);
    }

    private int compareImages(String firstName, String secondName) {
        String imageSorting = ConfigCore.getParameter(ParameterCore.IMAGE_SORTING, "number");

        if (imageSorting.equalsIgnoreCase("number")) {
            try {
                Integer firstIterator = Integer.valueOf(firstName);
                Integer secondIterator = Integer.valueOf(secondName);
                return firstIterator.compareTo(secondIterator);
            } catch (NumberFormatException e) {
                return firstName.compareToIgnoreCase(secondName);
            }
        } else if (imageSorting.equalsIgnoreCase("alphanumeric")) {
            return firstName.compareToIgnoreCase(secondName);
        } else {
            return firstName.compareToIgnoreCase(secondName);
        }
    }
}
